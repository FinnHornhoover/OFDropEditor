package finnhh.oftools.dropeditor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import finnhh.oftools.dropeditor.model.data.Preferences;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class JSONManager {
    public static final String PREFERENCE_PATH = "drop_editor_preferences.json";
    public static final String[] PATCH_NAMES = new String[] {
            "drops",
    };

    private final Gson gson;

    private final Map<String, JsonObject> prePatchObjects;
    private final Map<String, JsonObject> postPatchObjects;
    private JsonObject xdt;  // TODO: do not save, directly turn into static data

    private final List<File> patchDirectories;
    private File saveDirectory;
    private boolean standaloneSave;
    private File iconDirectory; // TODO: do not save, directly turn into static data

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

    public Optional<Preferences> getPreferences() {
        File preferenceFile = new File(PREFERENCE_PATH);

        try (FileReader fileReader = new FileReader(preferenceFile)) {
            return Optional.ofNullable(gson.fromJson(fileReader, Preferences.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public void setFromPreferences(Preferences preferences) throws NullPointerException, IOException {
        setDropsDirectory(new File(preferences.dropDirectory));

        setXDT(new File(preferences.xdtFile));

        for (String patchName : preferences.patchDirectories)
            addPatchPath(new File(patchName));

        setSavePreferences(new File(preferences.saveDirectory), preferences.standaloneSave);

        if (preferences.iconDirectory != null)
            setIconDirectory(new File(preferences.iconDirectory));

        this.preferences = preferences;
    }

    public void setDropsDirectory(File dropsDirectory) throws NullPointerException, IOException {
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

    public void setXDT(File xdtFile) throws NullPointerException, IOException {
        try (FileReader fileReader = new FileReader(xdtFile)) {
            xdt = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class), "Invalid XDT file.");
        }

        preferences.xdtFile = xdtFile.getAbsolutePath();
    }

    public void addPatchPath(File patchDirectory) throws IOException {
        boolean patchedOnce = false;
        Path patchPath = patchDirectory.toPath();

        for (String name : PATCH_NAMES) {
            try (FileReader fileReader = new FileReader(patchPath.resolve(name + ".json").toFile())) {
                JsonObject jsonObject = Objects.requireNonNull(gson.fromJson(fileReader, JsonObject.class));

                patch(postPatchObjects.get(name), jsonObject);
                patchedOnce = true;
            } catch (IOException | NullPointerException ignored) {
            }
        }

        if (patchedOnce) {
            patchDirectories.add(patchDirectory);
            preferences.patchDirectories.add(patchDirectory.getAbsolutePath());
        } else {
            throw new IOException("No valid patch files present.");
        }
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
        this.iconDirectory = iconDirectory;

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

    public void save(Map<String, ?> objectMap) throws IOException {
        Path savePath = saveDirectory.toPath();
        for (String name : PATCH_NAMES) {
            try (FileWriter writer = new FileWriter(savePath.resolve(name).toFile())) {
                JsonObject changedObject = gson.toJsonTree(objectMap.get(name)).getAsJsonObject();
                JsonObject objectToSave = getChangedTree(  // TODO: ideally, keep cumulative patch objects at each patch
                        standaloneSave ? prePatchObjects.get(name) : postPatchObjects.get(name),
                        changedObject);
                gson.toJson(objectToSave, writer);
            }
        }
    }

    public void savePreferences() throws IOException {
        try (FileWriter writer = new FileWriter(PREFERENCE_PATH)) {
            gson.toJson(preferences, writer);
        }
    }
}
