package finnhh.oftools.dropeditor;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Preferences;
import javafx.util.Pair;
import org.hildan.fxgson.FxGson;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    public static final String[] CONSTANT_NAMES = new String[] {
            "areas",
    };
    public static final String[] ITEM_TYPE_NAMES = new String[] {
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
    public static final String[] NPC_ICON_NAMES = new String[] {
            "error",
            "error",
            "error",
            "error",
            "npcicon",
            "error",
            "error",
            "error",
            "mobicon",
            "error",
            "hnpcicon",
    };
    public static final String[] MISSION_TASK_NAMES = {
            "Mission",
            "Guide Mission",
            "Nano Mission",
            "World Mission"
    };
    public static final String VALIDATION_ERR = "[ERR]";
    public static final String VALIDATION_WARN = "[WARN]";

    private final Gson gson;

    private final Map<String, JsonObject> prePatchObjects;
    private final Map<String, JsonObject> postPatchObjects;

    private Preferences preferences;

    public JSONManager() {
        gson = FxGson.coreBuilder()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();
        prePatchObjects = new HashMap<>();
        postPatchObjects = new HashMap<>();
        preferences = new Preferences();
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

        Set<String> allKeys = new LinkedHashSet<>(baseKeys);
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
            throws NullPointerException, IllegalStateException, ClassCastException {

        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap = staticDataStore.getItemInfoMap();

        for (ItemType itemType : ItemType.values()) {
            if (itemType == ItemType.NONE)
                continue;

            JsonObject typedItemObject = xdt.getAsJsonObject(ITEM_TYPE_NAMES[itemType.getTypeID()]);
            JsonArray itemDataArray = typedItemObject.getAsJsonArray("m_pItemData");
            JsonArray itemStringArray = typedItemObject.getAsJsonArray("m_pItemStringData");
            JsonArray itemIconArray = typedItemObject.getAsJsonArray("m_pItemIconData");

            for (JsonElement itemElement : itemDataArray) {
                JsonObject itemData = itemElement.getAsJsonObject();
                JsonObject itemStringData = itemStringArray
                        .get(itemData.get("m_iItemName").getAsInt())
                        .getAsJsonObject();

                String name = itemStringData.get("m_strName").getAsString();

                String comment = (itemType == ItemType.CRATE) ?
                        itemStringData.get("m_strComment").getAsString() :
                        itemStringArray
                                .get(itemData.get("m_iComment").getAsInt())
                                .getAsJsonObject()
                                .get("m_strComment")
                                .getAsString();

                String iconName = String.format("%s_%02d", ITEM_ICON_NAMES[itemType.getTypeID()], itemIconArray
                        .get(itemData.get("m_iIcon").getAsInt())
                        .getAsJsonObject()
                        .get("m_iIconNumber")
                        .getAsInt());

                int id = itemData.get("m_iItemNumber").getAsInt();

                int requiredLevel = (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                        0 : itemData.get("m_iMinReqLev").getAsInt();
                int contentLevel = requiredLevel;
                if (itemType == ItemType.CRATE) {
                    try {
                        contentLevel = Integer.parseInt(name.split("Lv")[0]);
                    } catch (NumberFormatException ignored) {
                    }
                }

                ItemInfo itemInfo = new ItemInfo(
                        id,
                        itemType,
                        WeaponType.forType((itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iTargetMode").getAsInt()),
                        itemData.get("m_iTradeAble").getAsInt() == 1,
                        itemData.get("m_iSellAble").getAsInt() == 1,
                        itemData.get("m_iItemPrice").getAsInt(),
                        itemData.get("m_iItemSellPrice").getAsInt(),
                        itemData.get("m_iStackNumber").getAsInt(),
                        Rarity.forType((itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                1 : itemData.get("m_iRarity").getAsInt()),
                        Gender.forType((itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iReqSex").getAsInt()),
                        requiredLevel,
                        contentLevel,
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iPointRat").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iGroupRat").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iInitalTime").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iDeliverTime").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iDelayTime").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iDurationTime").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iAtkRange").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iAtkAngle").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iTargetNumber").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iDefenseRat").getAsInt(),
                        (itemType == ItemType.GENERAL_ITEM || itemType == ItemType.CRATE) ?
                                0 : itemData.get("m_iUp_runSpeed").getAsInt(),
                        name,
                        comment,
                        iconName
                );

                itemInfoMap.put(new Pair<>(id, itemType.getTypeID()), itemInfo);
            }
        }
    }

    private void readNPCMobData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap = staticDataStore.getItemInfoMap();
        Map<Integer, NPCTypeInfo> npcTypeInfoMap = staticDataStore.getNpcTypeInfoMap();
        Map<Integer, List<NPCInfo>> npcInfoMap = staticDataStore.getNpcInfoMap();
        Map<Integer, MobTypeInfo> mobTypeInfoMap = staticDataStore.getMobTypeInfoMap();
        Map<Integer, List<MobInfo>> mobInfoMap = staticDataStore.getMobInfoMap();

        JsonObject vendorTableObject = xdt.getAsJsonObject("m_pVendorTable");
        JsonArray vendorItemArray = vendorTableObject.getAsJsonArray("m_pItemData");

        JsonObject npcTableObject = xdt.getAsJsonObject("m_pNpcTable");
        JsonArray npcDataArray = npcTableObject.getAsJsonArray("m_pNpcData");
        JsonArray npcStringArray = npcTableObject.getAsJsonArray("m_pNpcStringData");
        JsonArray npcIconArray = npcTableObject.getAsJsonArray("m_pNpcIconData");

        Map<Integer, List<VendorItemInfo>> vendorItemMap = new HashMap<>();

        for (JsonElement vendorItemElement : vendorItemArray) {
            JsonObject vendorItemObject = vendorItemElement.getAsJsonObject();

            int npcType = vendorItemObject.get("m_iNpcNumber").getAsInt();
            ItemInfo itemInfo = itemInfoMap.get(new Pair<>(
                    vendorItemObject.get("m_iitemID").getAsInt(),
                    vendorItemObject.get("m_iItemType").getAsInt()
            ));

            vendorItemMap.putIfAbsent(npcType, new ArrayList<>());

            if (Objects.nonNull(itemInfo)) {
                vendorItemMap.get(npcType).add(new VendorItemInfo(
                        itemInfo,
                        npcType,
                        vendorItemObject.get("m_iSortNumber").getAsInt(),
                        itemInfo.buyPrice()
                ));
            }
        }

        for (int type = 1; type < npcDataArray.size(); type++) {
            JsonObject npcData = npcDataArray.get(type).getAsJsonObject();

            String name = npcStringArray
                    .get(npcData.get("m_iNpcName").getAsInt())
                    .getAsJsonObject()
                    .get("m_strName")
                    .getAsString();

            JsonObject mobIconObject = npcIconArray
                    .get(npcData.get("m_iIcon1").getAsInt())
                    .getAsJsonObject();
            int npcIconType = Math.max(0, mobIconObject.get("m_iIconType").getAsInt()) % NPC_ICON_NAMES.length;
            int npcIconNumber = mobIconObject.get("m_iIconNumber").getAsInt();
            String iconName = npcIconType != 4 && npcIconType != 8 && npcIconType != 10 ?
                    "unknown" :
                    String.format("%s_%02d", NPC_ICON_NAMES[npcIconType], npcIconNumber);

            NPCTypeInfo npcTypeInfo = new NPCTypeInfo(
                    type,
                    name,
                    iconName,
                    Optional.ofNullable(vendorItemMap.get(type)).orElse(List.of())
            );

            MobTypeInfo mobTypeInfo = new MobTypeInfo(
                    type,
                    npcData.get("m_iNpcLevel").getAsInt(),
                    name,
                    iconName
            );

            npcTypeInfoMap.put(type, npcTypeInfo);
            npcInfoMap.put(type, new ArrayList<>());
            mobTypeInfoMap.put(type, mobTypeInfo);
            mobInfoMap.put(type, new ArrayList<>());
        }

        JsonObject npcDataObject = getPatchedObject("NPCs", JsonObject.class);
        List<JsonObject> npcObjectList = npcDataObject
                .getAsJsonObject("NPCs")
                .entrySet().stream()
                .map(e -> e.getValue().getAsJsonObject())
                .toList();

        for (JsonObject npcObject : npcObjectList) {
            int type = npcObject.get("iNPCType").getAsInt();

            if (!npcTypeInfoMap.containsKey(type))
                continue;

            NPCTypeInfo npcTypeInfo = npcTypeInfoMap.get(type);

            NPCInfo npcInfo = new NPCInfo(
                    npcTypeInfo,
                    npcObject.get("iX").getAsInt(),
                    npcObject.get("iY").getAsInt(),
                    !npcObject.has("iMapNum") ? 0 : npcObject.get("iMapNum").getAsInt()
            );

            npcInfoMap.get(type).add(npcInfo);
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

        // remove npc types that did not appear in the NPCs object
        List<Integer> nonNPCKeys = npcTypeInfoMap.keySet().stream()
                .filter(key -> !npcInfoMap.containsKey(key))
                .toList();
        nonNPCKeys.forEach(npcTypeInfoMap::remove);
        // remove npc types that did not appear in the mobs object
        List<Integer> nonMobKeys = mobTypeInfoMap.keySet().stream()
                .filter(key -> !mobInfoMap.containsKey(key))
                .toList();
        nonMobKeys.forEach(mobTypeInfoMap::remove);
    }

    private void readEggData(StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        Map<Integer, EggTypeInfo> eggTypeInfoMap = staticDataStore.getEggTypeInfoMap();
        Map<Integer, List<EggInfo>> eggInfoMap = staticDataStore.getEggInfoMap();

        JsonObject eggDataObject = getPatchedObject("eggs", JsonObject.class);
        JsonObject eggTypeData = eggDataObject.getAsJsonObject("EggTypes");
        JsonObject eggData = eggDataObject.getAsJsonObject("Eggs");

        for (var eggTypeEntry : eggTypeData.entrySet()) {
            JsonObject eggTypeObject = eggTypeEntry.getValue().getAsJsonObject();

            int type = eggTypeObject.get("Id").getAsInt();
            EggTypeInfo eggTypeInfo = new EggTypeInfo(type, eggTypeObject.get("DropCrateId").getAsInt());

            eggTypeInfoMap.put(type, eggTypeInfo);
            eggInfoMap.put(type, new ArrayList<>());
        }

        for (var eggEntry : eggData.entrySet()) {
            JsonObject eggObject = eggEntry.getValue().getAsJsonObject();

            int type = eggObject.get("iType").getAsInt();

            if (!eggTypeInfoMap.containsKey(type))
                continue;

            EggInfo eggInfo = new EggInfo(
                    eggTypeInfoMap.get(type),
                    eggObject.get("iX").getAsInt(),
                    eggObject.get("iY").getAsInt(),
                    !eggObject.has("iMapNum") ? 0 : eggObject.get("iMapNum").getAsInt()
            );

            eggInfoMap.get(type).add(eggInfo);
        }
    }

    private void readMissionData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap = staticDataStore.getItemInfoMap();
        Map<Integer, MissionTaskInfo> missionTaskInfoMap = staticDataStore.getMissionTaskInfoMap();
        Map<Integer, MissionInfo> missionInfoMap = staticDataStore.getMissionInfoMap();

        JsonObject missionTableObject = xdt.getAsJsonObject("m_pMissionTable");
        JsonArray missionDataArray = missionTableObject.getAsJsonArray("m_pMissionData");
        JsonArray missionStringDataArray = missionTableObject.getAsJsonArray("m_pMissionStringData");
        JsonArray rewardDataArray = missionTableObject.getAsJsonArray("m_pRewardData");

        Map<Integer, Set<MissionTaskInfo>> taskMap = new HashMap<>();

        for (int i = 1; i < missionDataArray.size(); i++) {
            JsonObject missionDataObject = missionDataArray.get(i).getAsJsonObject();

            int missionID = missionDataObject.get("m_iHMissionID").getAsInt();
            int taskID = missionDataObject.get("m_iHTaskID").getAsInt();
            String missionName = missionStringDataArray
                    .get(missionDataObject.get("m_iHMissionName").getAsInt())
                    .getAsJsonObject()
                    .get("m_pstrNameString")
                    .getAsString();

            JsonObject rewardDataObject = rewardDataArray
                    .get(missionDataObject.get("m_iSUReward").getAsInt())
                    .getAsJsonObject();
            JsonArray rewardItemIDs = rewardDataObject.get("m_iMissionRewardItemID").getAsJsonArray();
            // spelled with a typo in the actual XDT file
            JsonArray rewardItemTypes = rewardDataObject.get("m_iMissionRewarItemType").getAsJsonArray();
            Set<ItemInfo> itemRewards = IntStream.range(0, 4)
                    .filter(idx -> rewardItemTypes.get(idx).getAsInt() > 0 || rewardItemIDs.get(idx).getAsInt() > 0)
                    .mapToObj(idx -> itemInfoMap.get(
                            new Pair<>(rewardItemIDs.get(idx).getAsInt(), rewardItemTypes.get(idx).getAsInt())))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            int missionType = Math.max(0, missionDataObject.get("m_iHMissionType").getAsInt()) % MISSION_TASK_NAMES.length;

            MissionTaskInfo missionTaskInfo = new MissionTaskInfo(
                    taskID,
                    missionDataObject.get("m_iHTaskType").getAsInt(),
                    missionID,
                    missionType,
                    missionDataObject.get("m_iHJournalNPCID").getAsInt(),
                    missionName,
                    MISSION_TASK_NAMES[missionType],
                    rewardDataObject.get("m_iCash").getAsInt(),
                    rewardDataObject.get("m_iFusionMatter").getAsInt(),
                    itemRewards
            );

            missionTaskInfoMap.put(taskID, missionTaskInfo);

            if (!taskMap.containsKey(missionID))
                taskMap.put(missionID, new HashSet<>());

            taskMap.get(missionID).add(missionTaskInfo);
        }

        taskMap.forEach((missionID, taskSet) -> taskSet.stream()
                .max(Comparator.comparingInt(mto -> mto.itemRewards().size()))
                .ifPresent(mto -> missionInfoMap.put(missionID, new MissionInfo(
                        missionID,
                        mto.missionType(),
                        mto.npc(),
                        mto.missionName(),
                        mto.missionTypeName(),
                        taskSet,
                        mto.taroReward(),
                        mto.fmReward(),
                        mto.itemRewards()
                ))));
    }

    private void readInstanceData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        Map<Integer, MissionTaskInfo> missionTaskInfoMap = staticDataStore.getMissionTaskInfoMap();
        Map<Integer, WarpInfo> warpInfoMap = staticDataStore.getWarpInfoMap();
        Map<Long, InstanceInfo> instanceInfoMap = staticDataStore.getInstanceInfoMap();

        JsonObject instanceTableObject = xdt.getAsJsonObject("m_pInstanceTable");
        JsonArray instanceDataArray = instanceTableObject.getAsJsonArray("m_pInstanceData");
        JsonArray warpDataArray = instanceTableObject.getAsJsonArray("m_pWarpData");
        JsonArray warpNameDataArray = instanceTableObject.getAsJsonArray("m_pWarpNameData");

        for (int i = 1; i < warpDataArray.size(); i++) {
            JsonObject warpDataObject = warpDataArray.get(i).getAsJsonObject();
            int warpID = warpDataObject.get("m_iWarpNumber").getAsInt();

            warpInfoMap.put(warpID, new WarpInfo(
                    warpID,
                    warpDataObject.get("m_iToMapNum").getAsLong(),
                    warpDataObject.get("m_iToX").getAsInt(),
                    warpDataObject.get("m_iToY").getAsInt(),
                    warpDataObject.get("m_iNpcNumber").getAsInt(),
                    Optional.ofNullable(missionTaskInfoMap.get(warpDataObject.get("m_iLimit_TaskID").getAsInt()))
            ));
        }

        for (int i = 1; i < instanceDataArray.size(); i++) {
            JsonObject instanceDataObject = instanceDataArray.get(i).getAsJsonObject();
            long instanceID = i;
            String name = warpNameDataArray
                    .get(instanceDataObject.get("m_iInstanceNameID").getAsInt())
                    .getAsJsonObject()
                    .get("m_pstrNameString")
                    .getAsString();
            Set<WarpInfo> warpSet = warpInfoMap.values().stream()
                    .filter(wi -> wi.toInstanceID() == instanceID)
                    .collect(Collectors.toSet());
            Optional<MissionTaskInfo> entryTask = warpSet.stream()
                    .reduce((wi1, wi2) -> wi1.requiredTask().isPresent() ? wi1 : wi2)
                    .flatMap(WarpInfo::requiredTask);

            instanceInfoMap.put(instanceID, new InstanceInfo(
                    instanceID,
                    instanceDataObject.get("m_iZoneX").getAsInt(),
                    instanceDataObject.get("m_iZoneY").getAsInt(),
                    instanceDataObject.get("m_iIsEP").getAsInt(),
                    name,
                    warpSet,
                    entryTask
            ));
        }
    }

    private void readNanoData(JsonObject xdt, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        Map<Integer, NanoPowerInfo> nanoPowerInfoMap = staticDataStore.getNanoPowerInfoMap();
        Map<Integer, NanoInfo> nanoInfoMap = staticDataStore.getNanoInfoMap();

        JsonObject nanoObject = xdt.getAsJsonObject("m_pNanoTable");
        JsonArray nanoDataArray = nanoObject.getAsJsonArray("m_pNanoData");
        JsonArray nanoStringArray = nanoObject.getAsJsonArray("m_pNanoStringData");
        JsonArray nanoIconArray = nanoObject.getAsJsonArray("m_pNanoIconData");
        JsonArray nanoPowerDataArray = nanoObject.getAsJsonArray("m_pNanoTuneData");
        JsonArray nanoPowerStringArray = nanoObject.getAsJsonArray("m_pNanoTuneStringData");

        JsonObject skillObject = xdt.getAsJsonObject("m_pSkillTable");
        JsonArray skillDataArray = skillObject.getAsJsonArray("m_pSkillData");
        JsonArray skillIconDataArray = skillObject.getAsJsonArray("m_pSkillIconData");

        for (int powerID = 1; powerID < nanoPowerDataArray.size(); powerID++) {
            JsonObject nanoPowerDataObject = nanoPowerDataArray.get(powerID).getAsJsonObject();
            JsonObject nanoPowerStringObject = nanoPowerStringArray
                    .get(nanoPowerDataObject.get("m_iTuneName").getAsInt())
                    .getAsJsonObject();
            JsonObject skillDataObject = skillDataArray
                    .get(nanoPowerDataObject.get("m_iSkillID").getAsInt())
                    .getAsJsonObject();
            JsonObject skillIconDataObject = skillIconDataArray
                    .get(skillDataObject.get("m_iIcon").getAsInt())
                    .getAsJsonObject();

            NanoPowerInfo nanoPowerInfo = new NanoPowerInfo(
                    powerID,
                    nanoPowerStringObject.get("m_strComment1").getAsString(),
                    nanoPowerStringObject.get("m_strName").getAsString(),
                    nanoPowerStringObject.get("m_strComment").getAsString(),
                    String.format("skillicon_%02d", skillIconDataObject.get("m_iIconNumber").getAsInt())
            );

            nanoPowerInfoMap.put(powerID, nanoPowerInfo);
        }

        for (int i = 1; i < nanoDataArray.size(); i++) {
            JsonObject nanoDataObject = nanoDataArray.get(i).getAsJsonObject();
            JsonObject nanoStringObject = nanoStringArray
                    .get(nanoDataObject.get("m_iNanoName").getAsInt())
                    .getAsJsonObject();
            JsonObject nanoIconObject = nanoIconArray
                    .get(nanoDataObject.get("m_iIcon1").getAsInt())
                    .getAsJsonObject();

            JsonArray nanoPowerListArray = nanoDataObject.get("m_iTune").getAsJsonArray();

            NanoInfo nanoInfo = new NanoInfo(
                    i,
                    nanoStringObject.get("m_strName").getAsString(),
                    nanoStringObject.get("m_strComment1").getAsString(),
                    String.format("nanoicon_%02d", nanoIconObject.get("m_iIconNumber").getAsInt()),
                    IntStream.range(0, 3)
                            .mapToObj(p -> nanoPowerInfoMap.get(nanoPowerListArray.get(p).getAsInt()))
                            .toList()
            );

            nanoInfoMap.put(i, nanoInfo);
        }
    }

    private void readMapRegionData(JsonObject mapXDT, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException {

        List<MapRegionInfo> mapRegionInfoList = staticDataStore.getMapRegionInfoList();
        JsonArray mapRegionsArray = mapXDT.getAsJsonArray("MapRegions");

        for (JsonElement mapRegionElement : mapRegionsArray) {
            JsonObject mapRegionObject = mapRegionElement.getAsJsonObject();

            int x = mapRegionObject.get("X").getAsInt();
            int y = mapRegionObject.get("Y").getAsInt();
            mapRegionInfoList.add(new MapRegionInfo(
                    x,
                    y,
                    mapRegionObject.get("Width").getAsInt(),
                    mapRegionObject.get("Height").getAsInt(),
                    mapRegionObject.get("AreaName").getAsString(),
                    mapRegionObject.get("ZoneName").getAsString(),
                    String.format("minimap_%d_%d", MapRegionInfo.xToTile(x), MapRegionInfo.yToTile(y))
            ));
        }
    }

    public String validateDropDirectory() {
        Path dropPath = Paths.get(preferences.getDropDirectory());

        if (preferences.getDropDirectory().isBlank() || !Files.exists(dropPath))
            return VALIDATION_ERR + " Drops directory does not exist here.";

        if (!Files.isDirectory(dropPath))
            return VALIDATION_ERR + " Drops directory path does not point to a directory.";

        for (String name : PATCH_NAMES) {
            Path patchablePath = dropPath.resolve(name + ".json");

            if (!Files.exists(patchablePath))
                return VALIDATION_ERR + " Directory does not contain \"" + name + ".json\".";

            if (!Files.isRegularFile(patchablePath))
                return VALIDATION_ERR + " Directory contains \"" + name + ".json\", but not as a file.";
        }

        return "";
    }

    public String validatePatchDirectories() {
        for (String patchDirectoryName : preferences.getPatchDirectories()) {
            Path patchPath = Paths.get(patchDirectoryName);

            if (patchDirectoryName.isBlank() || !Files.exists(patchPath))
                return VALIDATION_ERR + " \"" + patchDirectoryName + "\" does not exist.";

            if (!Files.isDirectory(patchPath))
                return VALIDATION_ERR + " \"" + patchDirectoryName + "\" does not point to a directory.";

            if (Arrays.stream(PATCH_NAMES).noneMatch(name -> Files.isRegularFile(patchPath.resolve(name + ".json"))))
                return VALIDATION_ERR + " No files to patch the current files with in \"" + patchDirectoryName + "\".";
        }

        return "";
    }

    public String validateXDTFile() {
        Path xdtPath = Paths.get(preferences.getXDTFile());

        if (preferences.getXDTFile().isBlank() || !Files.exists(xdtPath))
            return VALIDATION_ERR + " XDT file does not exist here.";

        if (!Files.isRegularFile(xdtPath))
            return VALIDATION_ERR + " XDT file path does not point to a file.";

        if (!xdtPath.getFileName().toString().matches("xdt.*\\.json"))
            return VALIDATION_ERR + " The path should point to a file that matches \"xdt*.json\".";

        return "";
    }

    public String validateSaveDirectory() {
        Path savePath = Paths.get(preferences.getSaveDirectory());

        if (preferences.getSaveDirectory().isBlank() || !Files.exists(savePath))
            return VALIDATION_ERR + " Save directory does not exist here.";

        if (!Files.isDirectory(savePath))
            return VALIDATION_ERR + " Save directory path does not point to a directory.";

        int index = preferences.getPatchDirectories().indexOf(preferences.getSaveDirectory());
        if (preferences.isStandaloneSave()) {
            if (index > -1)
                return VALIDATION_WARN + " You will overwrite the data in a patch directory this way.";

            if (preferences.getDropDirectory().equals(preferences.getSaveDirectory()))
                return VALIDATION_WARN + " You will overwrite the data in the drops directory this way.";
        } else {
            if (index > -1 && index < preferences.getPatchDirectories().size() - 1)
                return VALIDATION_ERR + " Save directory can only be the last patch directory or an unrelated directory unless in" +
                        " standalone save mode.";

            if (preferences.getDropDirectory().equals(preferences.getSaveDirectory()))
                return VALIDATION_ERR + " Save directory cannot be the drop directory unless in standalone save mode.";

            if (index > -1 && index == preferences.getPatchDirectories().size() - 1)
                return VALIDATION_WARN + " You will overwrite the data in the last patch directory this way.";
        }

        return "";
    }

    public String validateIconDirectory() {
        Path iconPath = Paths.get(preferences.getIconDirectory());

        if (preferences.getIconDirectory().isBlank() || !Files.exists(iconPath))
            return VALIDATION_WARN + " Icons directory does not exist here.";

        if (!Files.isDirectory(iconPath))
            return VALIDATION_WARN + " Icons directory path does not point to a directory.";

        if (!Files.isRegularFile(iconPath.resolve("cosicon_00.png")))
            return VALIDATION_WARN + " Icons directory does not contain icons (e.g. \"cosicon_00.png\").";

        return "";
    }

    public void throwForInvalid(String errorString) throws IOException {
        if (errorString.startsWith(VALIDATION_ERR))
            throw new IOException(errorString.substring(VALIDATION_ERR.length() + 1));
    }

    public void setPreferences(Preferences preferences) throws NullPointerException {
        this.preferences = Objects.requireNonNull(preferences, "Cannot set a null preference object.");
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public Optional<Preferences> readPreferences() {
        try (FileReader fileReader = new FileReader(PREFERENCE_PATH, StandardCharsets.UTF_8)) {
            return Optional.ofNullable(gson.fromJson(fileReader, Preferences.class));
        } catch (JsonSyntaxException | IOException e) {
            return Optional.empty();
        }
    }

    public void setFromPreferences(Preferences preferences, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException,
            URISyntaxException, IOException {

        throwForInvalid(validateDropDirectory());
        setDropsDirectory(preferences.getDropDirectory());

        throwForInvalid(validatePatchDirectories());
        for (String patchName : preferences.getPatchDirectories())
            addPatchPath(patchName);

        throwForInvalid(validateXDTFile());
        setXDT(preferences.getXDTFile(), staticDataStore);

        throwForInvalid(validateSaveDirectory());
        setConstantDataDirectory(preferences.getSaveDirectory(), staticDataStore);

        fillDerivativeMaps(staticDataStore);

        throwForInvalid(validateIconDirectory());
    }

    public void setDropsDirectory(String dropsDirectory) throws NullPointerException, JsonSyntaxException, IOException {
        for (String name : PATCH_NAMES) {
            try (FileReader fileReader = new FileReader(Paths.get(dropsDirectory, name + ".json").toFile(),
                    StandardCharsets.UTF_8)) {
                JsonObject jsonObject = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class),
                        "Object in file \"" + name + "\" must be a JSON object.");

                prePatchObjects.put(name, jsonObject);
                postPatchObjects.put(name, jsonObject.deepCopy());
            }
        }
    }

    public void addPatchPath(String patchDirectory) throws IOException {
        boolean patchedOnce = false;

        for (String name : PATCH_NAMES) {
            try (FileReader fileReader = new FileReader(Paths.get(patchDirectory, name + ".json").toFile(),
                    StandardCharsets.UTF_8)) {
                JsonObject jsonObject = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class));

                patch(postPatchObjects.get(name), jsonObject);
                patchedOnce = true;
            } catch (NullPointerException | JsonSyntaxException | IOException ignored) {
            }
        }

        if (!patchedOnce)
            throw new IOException("No valid patch files present.");
    }

    public void setXDT(String xdtFile, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException, IOException {

        try (FileReader fileReader = new FileReader(xdtFile, StandardCharsets.UTF_8)) {
            JsonObject xdt = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class),
                    "Invalid XDT file.");

            // order matters
            readItemData(xdt, staticDataStore);
            readNPCMobData(xdt, staticDataStore);
            readEggData(staticDataStore);
            readMissionData(xdt, staticDataStore);
            readInstanceData(xdt, staticDataStore);
            readNanoData(xdt, staticDataStore);
        }
    }

    public void setConstantDataDirectory(String constantDataDirectory, StaticDataStore staticDataStore)
            throws NullPointerException, IllegalStateException, ClassCastException, JsonSyntaxException,
            IOException {

        Map<String, JsonObject> constantDataMap = new HashMap<>();

        for (String constantName : CONSTANT_NAMES) {
            Path constantDataFilePath = Paths.get(constantDataDirectory, constantName + ".json");

            if (!Files.isRegularFile(constantDataFilePath)) {
                Files.copy(Objects.requireNonNull(JSONManager.class.getResourceAsStream(constantName + ".json")),
                        constantDataFilePath);
            }

            try (FileReader fileReader = new FileReader(constantDataFilePath.toFile(), StandardCharsets.UTF_8)) {
                constantDataMap.put(constantName,
                        Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class),
                                "Could not read a file which was supposed to be provided by the program."));
            }
        }

        readMapRegionData(constantDataMap.get(CONSTANT_NAMES[0]), staticDataStore);
    }

    public void fillDerivativeMaps(StaticDataStore staticDataStore) {
        var npcTypeInfoMap = staticDataStore.getNpcTypeInfoMap();
        var missionInfoMap = staticDataStore.getMissionInfoMap();
        var eggInfoMap = staticDataStore.getEggInfoMap();
        var mapRegionInfoList = staticDataStore.getMapRegionInfoList();
        var npcInfoMap = staticDataStore.getNpcInfoMap();
        var mobInfoMap = staticDataStore.getMobInfoMap();
        var instanceInfoMap = staticDataStore.getInstanceInfoMap();

        var vendorItemMap = staticDataStore.getVendorItemMap();
        var rewardMissionMap = staticDataStore.getRewardMissionMap();
        var epInstanceMap = staticDataStore.getEPInstanceMap();
        var eggInstanceRegionGroupedMap = staticDataStore.getEggInstanceRegionGroupedMap();
        var npcInstanceRegionGroupedMap = staticDataStore.getNPCInstanceRegionGroupedMap();
        var mobInstanceRegionGroupedMap = staticDataStore.getMobInstanceRegionGroupedMap();

        npcTypeInfoMap.values().stream()
                .flatMap(npcTypeInfo -> npcTypeInfo.vendorItems().stream())
                .forEach(vii -> {
                    ItemInfo ii = vii.itemInfo();
                    var key = new Pair<>(ii.id(), ii.type().getTypeID());
                    vendorItemMap.putIfAbsent(key, new ArrayList<>());
                    vendorItemMap.get(key).add(vii);
                });

        missionInfoMap.values()
                .forEach(missionInfo -> missionInfo.itemRewards()
                        .forEach(ii -> {
                            var key = new Pair<>(ii.id(), ii.type().getTypeID());
                            rewardMissionMap.putIfAbsent(key, new ArrayList<>());
                            rewardMissionMap.get(key).add(missionInfo);
                        }));

        instanceInfoMap.values()
                .stream()
                .filter(instanceInfo -> instanceInfo.EPID() > 0)
                .forEach(instanceInfo -> epInstanceMap.put(instanceInfo.EPID(), instanceInfo));

        eggInfoMap.values().stream()
                .flatMap(List::stream)
                .forEach(eggInfo -> {
                    int crateID = eggInfo.eggTypeInfo().crateID();

                    eggInstanceRegionGroupedMap.putIfAbsent(crateID, new HashMap<>());
                    var eggGroupedMap = eggInstanceRegionGroupedMap.get(crateID);
                    eggGroupedMap.putIfAbsent(eggInfo.instanceID(), new HashMap<>());
                    var eggRegionMap = eggGroupedMap.get(eggInfo.instanceID());

                    MapRegionInfo mapRegionInfo = eggRegionMap.keySet().stream()
                            .filter(mri -> mri.coordinatesIncluded(eggInfo.x(), eggInfo.y()))
                            .findFirst()
                            .or(() -> mapRegionInfoList.stream()
                                    .filter(mri -> mri.coordinatesIncluded(eggInfo.x(), eggInfo.y()))
                                    .findFirst())
                            .orElse(MapRegionInfo.UNKNOWN);

                    eggRegionMap.putIfAbsent(mapRegionInfo, new ArrayList<>());
                    eggRegionMap.get(mapRegionInfo).add(eggInfo);
                });

        npcInfoMap.forEach((npcType, npcs) -> {
            Map<Long, Map<MapRegionInfo, List<NPCInfo>>> npcGroupedMap = new HashMap<>();

            for (NPCInfo npcInfo : npcs) {
                MapRegionInfo npcRegionInfo = Optional.ofNullable(npcGroupedMap.get(npcInfo.instanceID()))
                        .flatMap(npcgm -> npcgm.keySet().stream()
                                .filter(mri -> mri.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                                .findFirst())
                        .or(() -> mapRegionInfoList.stream()
                                .filter(mri -> mri.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                                .findFirst())
                        .orElse(MapRegionInfo.UNKNOWN);

                npcGroupedMap.putIfAbsent(npcInfo.instanceID(), new HashMap<>());
                var withinInstanceNPCs = npcGroupedMap.get(npcInfo.instanceID());
                withinInstanceNPCs.putIfAbsent(npcRegionInfo, new ArrayList<>());
                withinInstanceNPCs.get(npcRegionInfo).add(npcInfo);
            }

            npcInstanceRegionGroupedMap.put(npcType, npcGroupedMap);
        });

        mobInfoMap.forEach((mobType, mobs) -> {
            Map<Long, Map<MapRegionInfo, List<MobInfo>>> mobGroupedMap = new HashMap<>();

            for (MobInfo mobInfo : mobs) {
                MapRegionInfo mapRegionInfo = Optional.ofNullable(mobGroupedMap.get(mobInfo.instanceID()))
                        .flatMap(mgm -> mgm.keySet().stream()
                                .filter(mri -> mri.coordinatesIncluded(mobInfo.x(), mobInfo.y()))
                                .findFirst())
                        .or(() -> mapRegionInfoList.stream()
                                .filter(mri -> mri.coordinatesIncluded(mobInfo.x(), mobInfo.y()))
                                .findFirst())
                        .orElse(MapRegionInfo.UNKNOWN);

                mobGroupedMap.putIfAbsent(mobInfo.instanceID(), new HashMap<>());
                var withinInstanceMobs = mobGroupedMap.get(mobInfo.instanceID());
                withinInstanceMobs.putIfAbsent(mapRegionInfo, new ArrayList<>());
                withinInstanceMobs.get(mapRegionInfo).add(mobInfo);
            }

            mobInstanceRegionGroupedMap.put(mobType, mobGroupedMap);
        });
    }

    public <T> T getPatchedObject(String name, Class<T> tClass) throws JsonSyntaxException {
        return gson.fromJson(postPatchObjects.get(name), tClass);
    }

    public void save(Drops drops) throws IOException {
        for (String name : PATCH_NAMES) {
            // shortcut
            if (!preferences.isStandaloneSave() && !name.equals("drops"))
                continue;

            JsonObject baseObject = preferences.isStandaloneSave() ?
                    prePatchObjects.get(name) :
                    postPatchObjects.get(name);

            JsonObject changedObject = name.equals("drops") ?
                    gson.toJsonTree(drops).getAsJsonObject() :
                    postPatchObjects.get(name);

            JsonObject objectToSave = getChangedTree(baseObject, changedObject);

            // save drops no matter what, only save other objects if there's a change
            if (name.equals("drops") || objectToSave.size() > 0) {
                try (FileWriter writer = new FileWriter(Paths.get(preferences.getSaveDirectory(), name + ".json")
                        .toFile(), StandardCharsets.UTF_8)) {
                    JsonWriter jsonWriter = gson.newJsonWriter(writer);
                    jsonWriter.setIndent("    ");
                    gson.toJson(objectToSave, jsonWriter);
                }
            }
        }
    }

    public void savePreferences() throws IOException {
        try (FileWriter writer = new FileWriter(PREFERENCE_PATH, StandardCharsets.UTF_8)) {
            JsonWriter jsonWriter = gson.newJsonWriter(writer);
            jsonWriter.setIndent("    ");
            gson.toJson(preferences, Preferences.class, jsonWriter);
        }
    }
}
