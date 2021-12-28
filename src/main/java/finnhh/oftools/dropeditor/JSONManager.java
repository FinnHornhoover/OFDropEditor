package finnhh.oftools.dropeditor;

import com.google.gson.*;
import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Preferences;
import javafx.util.Pair;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class JSONManager {
    public static final String PREFERENCE_PATH = "drop_editor_preferences.json";
    public static final String[] PATCH_NAMES = new String[] {
            "drops",
            "NPCs",
            "mobs",
            "eggs",
            "paths",
    };
    public static final String[] ITEM_TYPES = new String[] {
            "m_pWeaponItemTable",
            "m_pShirtsItemTable",
            "m_pPantsItemTable",
            "m_pShoesItemTable",
            "m_pHatItemTable",
            "m_pGlassItemTable",
            "m_pBackItemTable",
            "m_pGeneralItemTable",
            "",
            "m_pChestItemTable",
            "m_pVehicleItemTable",
    };
    public static final String[] ITEM_ICON_NAMES = new String[] {
            "wpnicon",
            "cosicon",
            "cosicon",
            "cosicon",
            "cosicon",
            "cosicon",
            "cosicon",
            "generalitemicon",
            "error",
            "generalitemicon",
            "vehicle",
    };

    private final Gson gson;

    private final Map<String, JsonObject> prePatchObjects;
    private final Map<String, JsonObject> postPatchObjects;

    private final List<File> patchDirectories;
    private File saveDirectory;
    private boolean standaloneSave;

    private Preferences preferences;

    public JSONManager() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
        prePatchObjects = new HashMap<>();
        postPatchObjects = new HashMap<>();
        patchDirectories = new ArrayList<>();
        standaloneSave = true;
        preferences = new Preferences();
        preferences.patchDirectories = new ArrayList<>();
    }

    private void patch(JsonObject originalObject, JsonObject patchObject) {
        for (var entry : patchObject.entrySet()) {
            String key = entry.getKey();
            JsonElement patchValue = entry.getValue();

            // case 1: override the key value
            if (key.startsWith("!")) {
                originalObject.add(key.substring(1), patchValue);

                // case 2: if key already exists
            } else if (originalObject.has(key)) {
                JsonElement originalValue = originalObject.get(key);

                // case 2.1: delete the key
                if (patchValue.isJsonNull()) {
                    originalObject.remove(key);

                    // case 2.2: override the primitive value
                } else if (patchValue.isJsonPrimitive()) {
                    // no type check here
                    originalObject.add(key, patchValue);

                    // case 2.3: for arrays, add the values in the patch
                } else if (patchValue.isJsonArray()) {
                    originalValue.getAsJsonArray().addAll(patchValue.getAsJsonArray());

                    // case 2.4: recurse for objects
                } else {
                    patch(originalValue.getAsJsonObject(), patchValue.getAsJsonObject());
                }

                // case 3: if key not present, add patch
            } else {
                originalObject.add(key, patchValue);
            }
        }
    }

    private JsonObject getChangedTree(JsonObject baseObject, JsonObject changedObject) {
        JsonObject targetObject = new JsonObject();

        Set<String> baseKeys = baseObject.keySet();
        Set<String> changedKeys = changedObject.keySet();

        Set<String> allKeys = new HashSet<>(baseKeys);
        allKeys.addAll(changedKeys);

        for (String key : allKeys) {
            // case 1: base and difference both contain the key (usually true)
            if (baseKeys.contains(key) && changedKeys.contains(key)) {
                JsonElement baseValue = baseObject.get(key);
                JsonElement changedValue = changedObject.get(key);

                // case 1.1: if the elements are json objects, recurse down
                //           and add if the difference is not empty
                if (changedValue.isJsonObject()) {
                    JsonObject targetValue = this.getChangedTree(baseValue.getAsJsonObject(),
                            changedValue.getAsJsonObject());
                    if (targetValue.size() > 0)
                        targetObject.add(key, targetValue);

                // case 1.2: if elements are anything else, and they are not equal,
                //           override previous value explicitly
                } else if (!baseValue.equals(changedValue)) {
                    targetObject.add("!" + key, changedValue);
                }

                // case 1.3: if elements are equal, no-op

            // case 2: only base contains the key, add a null value for deletion
            } else if (baseKeys.contains(key)) {
                targetObject.add(key, null);

            // case 3: only difference contains the key, add the value
            } else {
                targetObject.add(key, changedObject.get(key));
            }
        }

        return targetObject;
    }

    private void readItemData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException {

        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap = staticDataStore.getItemInfoMap();

        for (int i = 0; i < ITEM_TYPES.length; i++) {
            if (i == 8)
                continue;

            JsonObject typedItemObject = xdt.getAsJsonObject(ITEM_TYPES[i]);
            JsonArray itemDataArray = typedItemObject.getAsJsonArray("m_pItemData");
            JsonArray itemStringArray = typedItemObject.getAsJsonArray("m_pItemStringData");
            JsonArray itemIconArray = typedItemObject.getAsJsonArray("m_pItemIconData");

            for (JsonElement itemElement : itemDataArray) {
                JsonObject itemData = itemElement.getAsJsonObject();
                JsonObject itemStringData = itemStringArray
                        .get(itemData.get("m_iItemName").getAsInt())
                        .getAsJsonObject();

                String name = itemStringData.get("m_strName").getAsString();

                String comment = (i == 9) ?
                        itemStringData.get("m_strComment").getAsString() :
                        itemStringArray
                                .get(itemData.get("m_iComment").getAsInt())
                                .getAsJsonObject()
                                .get("m_strComment")
                                .getAsString();

                String iconName = ITEM_ICON_NAMES[i] + "_" + itemIconArray
                        .get(itemData.get("m_iIcon").getAsInt())
                        .getAsJsonObject()
                        .get("m_iIconNumber")
                        .getAsInt();

                int id = itemData.get("m_iItemNumber").getAsInt();

                ItemInfo itemInfo = new ItemInfo(
                        id,
                        i,
                        itemData.get("m_iTradeAble").getAsInt() == 1,
                        itemData.get("m_iSellAble").getAsInt() == 1,
                        itemData.get("m_iItemPrice").getAsInt(),
                        itemData.get("m_iItemSellPrice").getAsInt(),
                        itemData.get("m_iStackNumber").getAsInt(),
                        (i == 7 || i == 9) ? 1 : itemData.get("m_iRarity").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iMinReqLev").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iPointRat").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iGroupRat").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iDelayTime").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iDefenseRat").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iReqSex").getAsInt(),
                        (i == 7 || i == 9) ? 0 : itemData.get("m_iEquipType").getAsInt(),
                        name,
                        comment,
                        iconName
                );

                itemInfoMap.put(new Pair<>(id, i), itemInfo);
            }
        }
    }

    private void readMobData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, JsonSyntaxException {

        Map<Integer, MobTypeInfo> mobTypeInfoMap = staticDataStore.getMobTypeInfoMap();
        Map<Integer, List<MobInfo>> mobInfoMap = staticDataStore.getMobInfoMap();

        JsonObject npcTableObject = xdt.getAsJsonObject("m_pNpcTable");
        JsonArray npcDataArray = npcTableObject.getAsJsonArray("m_pNpcData");
        JsonArray npcStringArray = npcTableObject.getAsJsonArray("m_pNpcStringData");
        JsonArray npcIconArray = npcTableObject.getAsJsonArray("m_pNpcIconData");

        for (int type = 1; type < npcDataArray.size(); type++) {
            JsonObject npcData = npcDataArray.get(type).getAsJsonObject();

            String name = npcStringArray
                    .get(npcData.get("m_iNpcName").getAsInt())
                    .getAsJsonObject()
                    .get("m_strName")
                    .getAsString();

            String iconName = "mobicon_" + npcIconArray
                    .get(npcData.get("m_iIcon1").getAsInt())
                    .getAsJsonObject()
                    .get("m_iIconNumber")
                    .getAsInt();

            MobTypeInfo mobTypeInfo = new MobTypeInfo(
                    type,
                    npcData.get("m_iNpcLevel").getAsInt(),
                    name,
                    iconName
            );

            mobTypeInfoMap.put(type, mobTypeInfo);
            mobInfoMap.put(type, new ArrayList<>());
        }

        JsonObject mobDataObject = getPatchedObject("mobs", JsonObject.class);
        List<JsonObject> mobObjectList = Stream.of("mobs", "groups")
                .flatMap(key -> mobDataObject.getAsJsonObject(key).entrySet().stream())
                .map(e -> e.getValue().getAsJsonObject())
                .toList();

        for (JsonObject mobObject : mobObjectList) {
            int type = mobObject.get("iNPCType").getAsInt();

            if (!mobTypeInfoMap.containsKey(type))
                continue;

            MobTypeInfo mobTypeInfo = mobTypeInfoMap.get(type);

            MobInfo mobInfo = new MobInfo(
                    mobTypeInfo,
                    Optional.empty(),
                    mobObject.get("iX").getAsInt(),
                    mobObject.get("iY").getAsInt(),
                    !mobObject.has("iMapNum") ? 0 : mobObject.get("iMapNum").getAsInt()
            );

            mobInfoMap.get(type).add(mobInfo);

            if (!mobObject.has("aFollowers"))
                continue;

            for (JsonElement followerElement : mobObject.getAsJsonArray("aFollowers")) {
                JsonObject followerObject = followerElement.getAsJsonObject();
                int followerType = followerObject.get("iNPCType").getAsInt();

                if (!mobTypeInfoMap.containsKey(followerType))
                    continue;

                MobTypeInfo followerTypeInfo = mobTypeInfoMap.get(followerType);

                MobInfo followerInfo = new MobInfo(
                        followerTypeInfo,
                        Optional.of(mobTypeInfo),
                        mobInfo.x() + followerObject.get("iOffsetX").getAsInt(),
                        mobInfo.y() + followerObject.get("iOffsetY").getAsInt(),
                        mobInfo.instanceID()
                );

                mobInfoMap.get(followerType).add(followerInfo);
            }
        }

        // remove npc types that did not appear in the mobs object
        List<Integer> nonMobKeys = mobTypeInfoMap.keySet().stream()
                .filter(key -> !mobInfoMap.containsKey(key))
                .toList();
        nonMobKeys.forEach(mobTypeInfoMap::remove);
    }

    private void readEggData(StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, JsonSyntaxException {

        Map<Integer, EggTypeInfo> eggTypeInfoMap = staticDataStore.getEggTypeInfoMap();
        Map<Integer, EggInfo> eggInfoMap = staticDataStore.getEggInfoMap();

        JsonObject eggDataObject = getPatchedObject("eggs", JsonObject.class);
        JsonArray eggTypeArray = eggDataObject.getAsJsonArray("EggTypes");
        JsonArray eggArray = eggDataObject.getAsJsonArray("Eggs");

        for (JsonElement eggTypeElement : eggTypeArray) {
            JsonObject eggTypeObject = eggTypeElement.getAsJsonObject();

            int type = eggTypeObject.get("Id").getAsInt();
            EggTypeInfo eggTypeInfo = new EggTypeInfo(type, eggTypeObject.get("DropCrateId").getAsInt());

            eggTypeInfoMap.put(type, eggTypeInfo);
        }

        for (JsonElement eggElement : eggArray) {
            JsonObject eggObject = eggElement.getAsJsonObject();

            int type = eggObject.get("iType").getAsInt();

            if (!eggTypeInfoMap.containsKey(type))
                continue;

            EggInfo eggInfo = new EggInfo(
                    eggTypeInfoMap.get(type),
                    eggObject.get("iX").getAsInt(),
                    eggObject.get("iY").getAsInt(),
                    !eggObject.has("iMapNum") ? 0 : eggObject.get("iMapNum").getAsInt()
            );

            eggInfoMap.put(type, eggInfo);
        }
    }

    public Optional<Preferences> getPreferences() {
        File preferenceFile = new File(PREFERENCE_PATH);

        try (FileReader fileReader = new FileReader(preferenceFile)) {
            return Optional.ofNullable(gson.fromJson(fileReader, Preferences.class));
        } catch (JsonSyntaxException | IOException e) {
            return Optional.empty();
        }
    }

    public void setFromPreferences(Preferences preferences, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, JsonSyntaxException, IOException {

        setDropsDirectory(new File(preferences.dropDirectory));

        for (String patchName : preferences.patchDirectories)
            addPatchPath(new File(patchName));

        setXDT(new File(preferences.xdtFile), staticDataStore);

        setSavePreferences(new File(preferences.saveDirectory), preferences.standaloneSave);

        if (preferences.iconDirectory != null)
            setIconDirectory(new File(preferences.iconDirectory));

        this.preferences = preferences;
    }

    public void setDropsDirectory(File dropsDirectory) throws NullPointerException, JsonSyntaxException, IOException {
        Path dropsPath = dropsDirectory.toPath();

        for (String name : PATCH_NAMES) {
            try (FileReader fileReader = new FileReader(dropsPath.resolve(name + ".json").toFile())) {
                JsonObject jsonObject = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class),
                        "Object in file \"" + name + "\" must be a JSON object.");

                prePatchObjects.put(name, jsonObject);
                postPatchObjects.put(name, jsonObject.deepCopy());
            }
        }

        preferences.dropDirectory = dropsDirectory.getAbsolutePath();
    }

    public void addPatchPath(File patchDirectory) throws IOException {
        boolean patchedOnce = false;

        for (String name : PATCH_NAMES) {
            try (FileReader fileReader = new FileReader(patchDirectory.toPath().resolve(name + ".json").toFile())) {
                JsonObject jsonObject = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class));

                patch(postPatchObjects.get(name), jsonObject);
                patchedOnce = true;
            } catch (NullPointerException | JsonSyntaxException | IOException ignored) {
            }
        }

        if (patchedOnce) {
            patchDirectories.add(patchDirectory);
            preferences.patchDirectories.add(patchDirectory.getAbsolutePath());
        } else {
            throw new IOException("No valid patch files present.");
        }
    }

    public void setXDT(File xdtFile, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, JsonSyntaxException, IOException {

        try (FileReader fileReader = new FileReader(xdtFile)) {
            JsonObject xdt = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class),
                    "Invalid XDT file.");

            readItemData(xdt, staticDataStore);
            readMobData(xdt, staticDataStore);
            readEggData(staticDataStore);
        }

        preferences.xdtFile = xdtFile.getAbsolutePath();
    }

    public void setSavePreferences(File saveDirectory, boolean standaloneSave) throws IllegalArgumentException {
        if (!standaloneSave && patchDirectories.contains(saveDirectory))
            throw new IllegalArgumentException("Cannot save edits to a loaded patch folder without standalone mode.");

        this.saveDirectory = saveDirectory;
        this.standaloneSave = standaloneSave;

        preferences.saveDirectory = saveDirectory.getAbsolutePath();
        preferences.standaloneSave = standaloneSave;
    }

    public void setIconDirectory(File iconDirectory) {
        preferences.iconDirectory = iconDirectory.getAbsolutePath();
    }

    public boolean isStandaloneSave() {
        return standaloneSave;
    }

    public void setStandaloneSave(boolean standaloneSave) {
        this.standaloneSave = standaloneSave;
    }

    public List<File> getPatchDirectories() {
        return patchDirectories;
    }

    public <T> T getPatchedObject(String name, Class<T> tClass) throws JsonSyntaxException {
        return gson.fromJson(postPatchObjects.get(name), tClass);
    }

    public void save(Drops drops) throws IOException {
        for (String name : PATCH_NAMES) {
            // shortcut
            if (!standaloneSave && !name.equals("drops"))
                continue;

            JsonObject baseObject = standaloneSave ?
                    prePatchObjects.get(name) :
                    postPatchObjects.get(name);

            JsonObject changedObject = name.equals("drops") ?
                    gson.toJsonTree(drops).getAsJsonObject() :
                    postPatchObjects.get(name);

            JsonObject objectToSave = getChangedTree(baseObject, changedObject);

            if (objectToSave.size() > 0) {
                try (FileWriter writer = new FileWriter(saveDirectory.toPath().resolve(name + ".json").toFile())) {
                    gson.toJson(objectToSave, writer);
                }
            }
        }
    }

    public void savePreferences() throws IOException {
        try (FileWriter writer = new FileWriter(PREFERENCE_PATH)) {
            gson.toJson(preferences, writer);
        }
    }
}
