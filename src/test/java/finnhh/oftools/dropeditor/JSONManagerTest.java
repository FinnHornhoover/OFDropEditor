package finnhh.oftools.dropeditor;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Preferences;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class JSONManagerTest {
    private final JSONManager jsonManager = new JSONManager();

    static Path resourcePath(String name) throws URISyntaxException {
        return Path.of(Objects.requireNonNull(JSONManagerTest.class.getClassLoader().getResource(name)).toURI());
    }

    static void assertXDTFilled(StaticDataStore staticDataStore) {
        Assertions.assertNotEquals(staticDataStore.getItemInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getNpcTypeInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getNpcInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getMobTypeInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getMobInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getEggTypeInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getEggInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getMissionTaskInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getMissionInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getWarpInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getWarpInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getInstanceInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getNanoPowerInfoMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getNanoInfoMap().size(), 0);
    }

    static void assertConstantDataFilled(StaticDataStore staticDataStore) {
        Assertions.assertNotEquals(staticDataStore.getMapRegionInfoList().size(), 0);
    }

    static void assertDerivativeDataFilled(StaticDataStore staticDataStore) {
        Assertions.assertNotEquals(staticDataStore.getVendorItemMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getRewardMissionMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getEPInstanceMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getEggInstanceRegionGroupedMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getNPCInstanceRegionGroupedMap().size(), 0);
        Assertions.assertNotEquals(staticDataStore.getMobInstanceRegionGroupedMap().size(), 0);
    }

    @Nested
    class TestReadPreferences {
        @BeforeEach
        void setup() throws IOException {
            Files.move(Path.of(JSONManager.PREFERENCE_PATH), Path.of("temp_" + JSONManager.PREFERENCE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        @AfterEach
        void cleanup() throws IOException {
            Files.move(Path.of("temp_" + JSONManager.PREFERENCE_PATH), Path.of(JSONManager.PREFERENCE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        @Test
        void validPreferenceFileExists() throws IOException, URISyntaxException {
            Files.copy(resourcePath("preferences_test.json"), Path.of(JSONManager.PREFERENCE_PATH));

            jsonManager.readPreferences().ifPresentOrElse(p -> {
                Assertions.assertFalse(p.getDropDirectory().isBlank());
                Assertions.assertFalse(p.getPatchDirectories().isEmpty());
                Assertions.assertFalse(p.getXDTFile().isBlank());
                Assertions.assertFalse(p.getSaveDirectory().isBlank());
                Assertions.assertFalse(p.isStandaloneSave());
                Assertions.assertFalse(p.getIconDirectory().isBlank());
            }, () -> Assertions.fail("Preferences could not be read."));
        }

        @Test
        void emptyObjectPreferenceFileExists() throws IOException, URISyntaxException {
            Files.copy(resourcePath("preferences_empty_test.json"), Path.of(JSONManager.PREFERENCE_PATH));

            jsonManager.readPreferences().ifPresentOrElse(p -> {
                Assertions.assertTrue(p.getDropDirectory().isBlank());
                Assertions.assertTrue(p.getPatchDirectories().isEmpty());
                Assertions.assertTrue(p.getXDTFile().isBlank());
                Assertions.assertTrue(p.getSaveDirectory().isBlank());
                Assertions.assertFalse(p.isStandaloneSave());
                Assertions.assertTrue(p.getIconDirectory().isBlank());
            }, () -> Assertions.fail("Preferences could not be read."));
        }

        @Test
        void invalidPreferenceFileExists() throws IOException, URISyntaxException {
            Files.copy(resourcePath("preferences_invalid_test.json"), Path.of(JSONManager.PREFERENCE_PATH));
            Assertions.assertTrue(jsonManager.readPreferences().isEmpty());
        }

        @Test
        void preferenceFileAbsent() {
            Assertions.assertTrue(jsonManager.readPreferences().isEmpty());
        }
    }

    @Nested
    class TestSavePreferences {
        @BeforeEach
        void setup() throws IOException {
            Files.move(Path.of(JSONManager.PREFERENCE_PATH), Path.of("temp_" + JSONManager.PREFERENCE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);

            jsonManager.setPreferences(new Preferences());
        }

        @AfterEach
        void cleanup() throws IOException {
            Files.move(Path.of("temp_" + JSONManager.PREFERENCE_PATH), Path.of(JSONManager.PREFERENCE_PATH),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        @Test
        void validSave() throws IOException {
            jsonManager.savePreferences();
            Assertions.assertTrue(Files.isRegularFile(Path.of(JSONManager.PREFERENCE_PATH)));
        }

        @Test
        void validExistingSave() throws IOException {
            Path prefPath = Path.of(JSONManager.PREFERENCE_PATH);
            Files.copy(Path.of("temp_" + JSONManager.PREFERENCE_PATH), prefPath,
                    StandardCopyOption.REPLACE_EXISTING);

            jsonManager.savePreferences();
            Assertions.assertTrue(Files.isRegularFile(prefPath));
        }

        @Test
        void invalidExistingSaveAsDirectory() throws IOException {
            Path prefPath = Path.of(JSONManager.PREFERENCE_PATH);
            Files.createDirectory(prefPath);

            Assertions.assertThrows(IOException.class, jsonManager::savePreferences);
        }
    }

    @Nested
    class TestValidateDropDirectory {
        void setup(String dirName) {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(dirName);
            jsonManager.setPreferences(preferences);
        }

        @Test
        void validDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateDropDirectory().isBlank());
        }

        @Test
        void nonExistentDropDirectory() {
            setup("dirName");
            Assertions.assertTrue(jsonManager.validateDropDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void nonDirectoryDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateDropDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void missingContentInDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateDropDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void invalidContentInDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateDropDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }
    }

    @Nested
    class TestValidatePatchDirectories {
        @Test
        void validPatchDirectories() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);
            Assertions.assertTrue(jsonManager.validatePatchDirectories().isBlank());
        }

        @Test
        void emptyPatchDirectories() {
            jsonManager.setPreferences(new Preferences());
            Assertions.assertTrue(jsonManager.validatePatchDirectories().isBlank());
        }

        @Test
        void nonExistentPatchDirectory() {
            Preferences preferences = new Preferences();
            preferences.getPatchDirectories().add("dirName");
            jsonManager.setPreferences(preferences);
            Assertions.assertTrue(jsonManager.validatePatchDirectories().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void nonDirectoryPatchDirectory() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.getPatchDirectories().add(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);
            Assertions.assertTrue(jsonManager.validatePatchDirectories().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void emptyContentInPatchDirectory() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);
            Assertions.assertTrue(jsonManager.validatePatchDirectories().startsWith(JSONManager.VALIDATION_ERR));
        }
    }

    @Nested
    class TestValidateXDTFile {
        void setup(String dirName) {
            Preferences preferences = new Preferences();
            preferences.setXDTFile(dirName);
            jsonManager.setPreferences(preferences);
        }

        @Test
        void validXDTFile() throws URISyntaxException {
            setup(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateXDTFile().isBlank());
        }

        @Test
        void nonExistentXDTFile() {
            setup("fileName.json");
            Assertions.assertTrue(jsonManager.validateXDTFile().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void nonFileXDTFile() throws URISyntaxException {
            setup(resourcePath("tdata").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateXDTFile().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void invalidFileNameXDTFile() throws URISyntaxException {
            setup(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateXDTFile().startsWith(JSONManager.VALIDATION_ERR));
        }
    }

    @Nested
    class TestValidateSaveDirectory {
        void setup(String dirName, boolean standalone) throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setSaveDirectory(dirName);
            preferences.setStandaloneSave(standalone);
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);
        }

        @Test
        void validStandaloneSaveDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), true);
            Assertions.assertTrue(jsonManager.validateSaveDirectory().isBlank());
        }

        @Test
        void validNonStandaloneSaveDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), false);
            Assertions.assertTrue(jsonManager.validateSaveDirectory().isBlank());
        }

        @Test
        void nonExistentSaveDirectory() throws URISyntaxException {
            setup("dirName", false);
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void nonDirectorySaveDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/drops.json").toFile().getAbsolutePath(), false);
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void standaloneSaveDirectoryIsPatchDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), true);
            Preferences preferences = jsonManager.getPreferences();
            preferences.getPatchDirectories().add(preferences.getSaveDirectory());
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }

        @Test
        void standaloneSaveDirectoryIsDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), true);
            Preferences preferences = jsonManager.getPreferences();
            preferences.setDropDirectory(preferences.getSaveDirectory());
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }

        @Test
        void nonStandaloneSaveDirectoryIsMiddlePatchDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), false);
            Preferences preferences = jsonManager.getPreferences();
            preferences.getPatchDirectories().add(preferences.getSaveDirectory());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }

        @Test
        void nonStandaloneSaveDirectoryIsLastPatchDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), false);
            Preferences preferences = jsonManager.getPreferences();
            preferences.getPatchDirectories().add(preferences.getSaveDirectory());
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }

        @Test
        void nonStandaloneSaveDirectoryIsDropDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), false);
            Preferences preferences = jsonManager.getPreferences();
            preferences.setDropDirectory(preferences.getSaveDirectory());
            Assertions.assertTrue(jsonManager.validateSaveDirectory().startsWith(JSONManager.VALIDATION_ERR));
        }
    }

    @Nested
    class TestValidateIconDirectory {
        void setup(String dirName) {
            Preferences preferences = new Preferences();
            preferences.setIconDirectory(dirName);
            jsonManager.setPreferences(preferences);
        }

        @Test
        void validIconDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/icons").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateIconDirectory().isBlank());
        }

        @Test
        void nonExistentIconDirectory() {
            setup("dirName");
            Assertions.assertTrue(jsonManager.validateIconDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }

        @Test
        void nonDirectoryIconDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateIconDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }

        @Test
        void emptyContentInIconDirectory() throws URISyntaxException {
            setup(resourcePath("tdata/patch").toFile().getAbsolutePath());
            Assertions.assertTrue(jsonManager.validateIconDirectory().startsWith(JSONManager.VALIDATION_WARN));
        }
    }

    @Nested
    class TestReadDropDirectory {
        void setup(String dirName) {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(dirName);
            jsonManager.setPreferences(preferences);
        }

        @Test
        void validDropDirectoryFiles() throws URISyntaxException, IOException {
            setup(resourcePath("tdata").toFile().getAbsolutePath());
            jsonManager.readDropDirectory();
            Assertions.assertTrue(jsonManager.getPrePatchObjects().keySet()
                    .containsAll(Arrays.asList(JSONManager.PATCH_NAMES)));
            Assertions.assertTrue(jsonManager.getOnePatchBeforeObjects().keySet()
                    .containsAll(Arrays.asList(JSONManager.PATCH_NAMES)));
            Assertions.assertTrue(jsonManager.getPostPatchObjects().keySet()
                    .containsAll(Arrays.asList(JSONManager.PATCH_NAMES)));
        }

        @Test
        void emptyDropDirectoryFiles() throws URISyntaxException {
            setup(resourcePath("tdata/patch/emptybadpatch").toFile().getAbsolutePath());
            Assertions.assertThrows(NullPointerException.class, jsonManager::readDropDirectory);
        }

        @Test
        void invalidDropDirectoryFiles() throws URISyntaxException {
            setup(resourcePath("tdata/patch/badpatch").toFile().getAbsolutePath());
            Assertions.assertThrows(JsonSyntaxException.class, jsonManager::readDropDirectory);
        }

        @Test
        void missingDropDirectoryFiles() throws URISyntaxException {
            setup(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            Assertions.assertThrows(IOException.class, jsonManager::readDropDirectory);
        }

        @Test
        void nonFileDropDirectoryFiles() throws URISyntaxException {
            setup(resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath());
            Assertions.assertThrows(IOException.class, jsonManager::readDropDirectory);
        }
    }

    @Nested
    class TestReadPatch {
        static Map<String, JsonObject> cloneMap(Map<String, JsonObject> map) {
            Map<String, JsonObject> clone = new HashMap<>();
            for (String name : map.keySet())
                clone.put(name, map.get(name).deepCopy());
            return clone;
        }

        @BeforeEach
        void setup() throws URISyntaxException, IOException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);
            jsonManager.readDropDirectory();
        }

        @Test
        void validPatchFiles() throws URISyntaxException, IOException {
            var postPatchClone = cloneMap(jsonManager.getPostPatchObjects());
            jsonManager.readPatch(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());

            Assertions.assertFalse(Arrays.stream(JSONManager.PATCH_NAMES).allMatch(name ->
                    jsonManager.getPrePatchObjects().get(name).equals(jsonManager.getPostPatchObjects().get(name))));
            Assertions.assertTrue(Arrays.stream(JSONManager.PATCH_NAMES).allMatch(name ->
                    jsonManager.getOnePatchBeforeObjects().get(name).equals(postPatchClone.get(name))));
        }

        @Test
        void emptyAllPatchFiles() {
            Assertions.assertThrows(IOException.class, () ->
                    jsonManager.readPatch(resourcePath("tdata/patch/emptybadpatch").toFile().getAbsolutePath()));
        }

        @Test
        void invalidAllPatchFiles() {
            Assertions.assertThrows(IOException.class, () ->
                    jsonManager.readPatch(resourcePath("tdata/patch/badpatch").toFile().getAbsolutePath()));
        }

        @Test
        void missingAllPatchFiles() {
            Assertions.assertThrows(IOException.class, () ->
                    jsonManager.readPatch(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath()));
        }

        @Test
        void nonFileAllPatchFiles() {
            Assertions.assertThrows(IOException.class, () ->
                    jsonManager.readPatch(resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath()));
        }
    }

    @Nested
    class TestReadXDT {
        void setup(String fileName) throws URISyntaxException, IOException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.setXDTFile(fileName);
            jsonManager.setPreferences(preferences);
            jsonManager.readDropDirectory();
        }

        @Test
        void validXDTFile() throws URISyntaxException, IOException {
            setup(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            StaticDataStore staticDataStore = new StaticDataStore();
            jsonManager.readXDT(staticDataStore);
            assertXDTFilled(staticDataStore);
        }

        @Test
        void emptyXDTFile() throws URISyntaxException, IOException {
            setup(resourcePath("tdata/patch/emptybadpatch/drops.json").toFile().getAbsolutePath());
            Assertions.assertThrows(NullPointerException.class, () -> jsonManager.readXDT(new StaticDataStore()));
        }

        @Test
        void invalidXDTFile() throws URISyntaxException, IOException {
            setup(resourcePath("tdata/patch/badpatch/drops.json").toFile().getAbsolutePath());
            Assertions.assertThrows(JsonSyntaxException.class, () -> jsonManager.readXDT(new StaticDataStore()));
        }

        @Test
        void nonFileAllPatchFiles() throws URISyntaxException, IOException {
            setup(resourcePath("tdata/patch/testpatch/drops.json").toFile().getAbsolutePath());
            Assertions.assertThrows(IOException.class, () -> jsonManager.readXDT(new StaticDataStore()));
        }
    }

    @Nested
    class TestSetAndReadConstantDataDirectory {
        @Test
        void validEmptyConstantDataDirectory(@TempDir Path path) throws IOException {
            StaticDataStore staticDataStore = new StaticDataStore();
            jsonManager.setAndReadConstantDataDirectory(path.toFile().getAbsolutePath(), staticDataStore);
            assertConstantDataFilled(staticDataStore);
        }

        @Test
        void validConstantDataDirectory() throws URISyntaxException, IOException {
            StaticDataStore staticDataStore = new StaticDataStore();
            jsonManager.setAndReadConstantDataDirectory(
                    resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath(), staticDataStore);
            assertConstantDataFilled(staticDataStore);
        }

        @Test
        void nonDirectoryConstantDataDirectory() {
            StaticDataStore staticDataStore = new StaticDataStore();
            Assertions.assertThrows(IOException.class, () -> jsonManager.setAndReadConstantDataDirectory(
                    resourcePath("tdata/drops.json").toFile().getAbsolutePath(), staticDataStore));
        }

        @Test
        void unsafeConstantDataDirectory() {
            StaticDataStore staticDataStore = new StaticDataStore();
            Assertions.assertThrows(IOException.class, () -> jsonManager.setAndReadConstantDataDirectory(
                    resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath(), staticDataStore));
        }
    }

    @Nested
    class TestReadAllData {
        @Test
        void validData() throws URISyntaxException, IOException {
            StaticDataStore staticDataStore = new StaticDataStore();
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            preferences.setSaveDirectory(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath());
            preferences.setIconDirectory(resourcePath("tdata/icons").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            jsonManager.readAllData(staticDataStore);

            assertXDTFilled(staticDataStore);
            assertConstantDataFilled(staticDataStore);
            assertDerivativeDataFilled(staticDataStore);
        }

        @Test
        void validDataInvalidIconDirectory() throws URISyntaxException, IOException {
            StaticDataStore staticDataStore = new StaticDataStore();
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            preferences.setSaveDirectory(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath());
            preferences.setIconDirectory(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            jsonManager.readAllData(staticDataStore);

            // program should work without a valid icon directory
            assertXDTFilled(staticDataStore);
            assertConstantDataFilled(staticDataStore);
            assertDerivativeDataFilled(staticDataStore);
        }

        @Test
        void invalidDropDirectoryPath() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(IOException.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidDropDirectoryContent() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata/patch/badpatch").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(Exception.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidPatchDirectoryPath() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(IOException.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidPatchDirectoryContent() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/badpatch").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(Exception.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidXDTFilePath() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(IOException.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidXDTFileContent() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata/patch/badpatch/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(Exception.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidSaveDirectoryPath() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            preferences.setSaveDirectory(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(IOException.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }

        @Test
        void invalidSaveDirectoryContent() throws URISyntaxException {
            Preferences preferences = new Preferences();
            preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
            preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
            preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
            preferences.setSaveDirectory(resourcePath("tdata/patch/testpatch").toFile().getAbsolutePath());
            jsonManager.setPreferences(preferences);

            Assertions.assertThrows(Exception.class, () -> jsonManager.readAllData(new StaticDataStore()));
        }
    }

    @Nested
    class TestSaveAllData {
        void copyDropDirectory(Path tempPath) throws URISyntaxException, IOException {
            for (String name : JSONManager.PATCH_NAMES) {
                Files.copy(resourcePath("tdata").resolve(name + ".json"),
                        tempPath.resolve(name + ".json"));
            }
        }

        Path copyAndGetXDTFile(Path tempPath) throws URISyntaxException, IOException {
            Path xdtPath = tempPath.resolve("xdt1013.json");
            Files.copy(resourcePath("tdata/xdt1013.json"), xdtPath);
            return xdtPath;
        }

        Path copyAndGetPatch1013(Path tempPath) throws URISyntaxException, IOException {
            Path validPatchPath = tempPath.resolve("patch").resolve("1013");
            Files.createDirectories(validPatchPath);
            for (String name : JSONManager.PATCH_NAMES) {
                try {
                    Files.copy(resourcePath("tdata/patch/1013").resolve(name + ".json"),
                            validPatchPath.resolve(name + ".json"));
                } catch (IOException ignored) {
                }
            }
            return validPatchPath;
        }

        Path makeAndGetFirstEditPatch(Path tempPath) throws IOException {
            Path editPatchPath = tempPath.resolve("patch").resolve("editpatch");
            Files.createDirectories(editPatchPath);
            for (String name : JSONManager.PATCH_NAMES) {
                if (name.equals("drops"))
                    continue;

                Files.writeString(editPatchPath.resolve(name + ".json"),
                        "{\"edited\": 1}\n", StandardCharsets.UTF_8);
            }
            return editPatchPath;
        }

        Path makeAndGetSecondEditPatch(Path tempPath) throws IOException {
            Path editPatchPath = tempPath.resolve("patch").resolve("editpatch2");
            Files.createDirectories(editPatchPath);
            Files.writeString(editPatchPath.resolve("mobs.json"),
                    "{\"!edited\": 2}\n", StandardCharsets.UTF_8);
            return editPatchPath;
        }

        Preferences setupAndMakePreference(Path tempPath, Path savePath, boolean standalone)
                throws URISyntaxException, IOException {

            copyDropDirectory(tempPath);
            Path xdtPath = copyAndGetXDTFile(tempPath);
            Path validPatchPath = copyAndGetPatch1013(tempPath);
            Path firstEditPatchPath = makeAndGetFirstEditPatch(tempPath);
            Path secondEditPatchPath = makeAndGetSecondEditPatch(tempPath);

            Preferences preferences = new Preferences();
            preferences.setDropDirectory(tempPath.toFile().getAbsolutePath());
            preferences.getPatchDirectories().addAll(
                    validPatchPath.toFile().getAbsolutePath(),
                    firstEditPatchPath.toFile().getAbsolutePath(),
                    secondEditPatchPath.toFile().getAbsolutePath());
            preferences.setXDTFile(xdtPath.toFile().getAbsolutePath());
            preferences.setStandaloneSave(standalone);
            preferences.setSaveDirectory(savePath.toFile().getAbsolutePath());

            return preferences;
        }

        Drops makeDrops(Preferences preferences) throws URISyntaxException, IOException {
            jsonManager.setPreferences(preferences);
            jsonManager.readAllData(new StaticDataStore());

            Drops drops = jsonManager.generatePatchedDrops();
            drops.manageMaps();
            return drops;
        }

        @Test
        void validDropDirectoryStandaloneSave(@TempDir Path path) throws URISyntaxException, IOException {
            Preferences preferences = setupAndMakePreference(path, path, true);
            Drops drops = makeDrops(preferences);

            jsonManager.saveAllData(drops);

            for (String name : JSONManager.PATCH_NAMES) {
                if (name.equals("drops"))
                    continue;

                Assertions.assertTrue(Files.readString(path.resolve(name + ".json"), StandardCharsets.UTF_8)
                        .contains(String.format("edited\": %d", name.equals("mobs") ? 2 : 1)));
            }
        }

        @Test
        void validPatchStandaloneSave(@TempDir Path path) throws URISyntaxException, IOException {
            Path savePatchPath = path.resolve("patch").resolve("1013");

            Preferences preferences = setupAndMakePreference(path, savePatchPath, true);
            Drops drops = makeDrops(preferences);

            jsonManager.saveAllData(drops);

            for (String name : JSONManager.PATCH_NAMES) {
                if (name.equals("drops"))
                    continue;

                Assertions.assertTrue(Files.readString(savePatchPath.resolve(name + ".json"),
                        StandardCharsets.UTF_8).contains(String.format("edited\": %d", name.equals("mobs") ? 2 : 1)));
            }
        }

        @Test
        void validRegularNonStandaloneSave(@TempDir Path path) throws URISyntaxException, IOException {
            Path savePatchPath = path.resolve("patch").resolve("savepatch");
            Files.createDirectories(savePatchPath);

            Preferences preferences = setupAndMakePreference(path, savePatchPath, false);
            Drops drops = makeDrops(preferences);

            jsonManager.saveAllData(drops);

            Assertions.assertEquals("{}", Files.readString(savePatchPath.resolve("drops.json")));
        }

        @Test
        void validLastPatchNonStandaloneSave(@TempDir Path path) throws URISyntaxException, IOException {
            Path savePatchPath = path.resolve("patch").resolve("editpatch2");

            Preferences preferences = setupAndMakePreference(path, savePatchPath, false);
            Drops drops = makeDrops(preferences);

            jsonManager.saveAllData(drops);

            Assertions.assertTrue(Files.readString(savePatchPath.resolve("mobs.json"), StandardCharsets.UTF_8)
                    .contains("edited\": 2"));
        }
    }

    @Test
    void testThrowForInvalid() {
        Assertions.assertThrows(IOException.class,
                () -> JSONManager.throwForInvalid(JSONManager.VALIDATION_ERR + "abc"));
    }

    @Test
    void testFillDerivativeMaps() throws URISyntaxException, IOException {
        StaticDataStore staticDataStore = new StaticDataStore();
        Preferences preferences = new Preferences();
        preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
        preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
        jsonManager.setPreferences(preferences);
        jsonManager.readDropDirectory();
        jsonManager.readXDT(staticDataStore);

        jsonManager.fillDerivativeMaps(staticDataStore);

        assertDerivativeDataFilled(staticDataStore);
    }

    @Test
    void testGeneratePatchedDrops() throws URISyntaxException, IOException {
        StaticDataStore staticDataStore = new StaticDataStore();
        Preferences preferences = new Preferences();
        preferences.setDropDirectory(resourcePath("tdata").toFile().getAbsolutePath());
        preferences.getPatchDirectories().add(resourcePath("tdata/patch/1013").toFile().getAbsolutePath());
        preferences.setXDTFile(resourcePath("tdata/xdt1013.json").toFile().getAbsolutePath());
        preferences.setSaveDirectory(resourcePath("tdata/patch/savepatch").toFile().getAbsolutePath());
        preferences.setIconDirectory(resourcePath("tdata/drops.json").toFile().getAbsolutePath());
        jsonManager.setPreferences(preferences);

        jsonManager.readAllData(staticDataStore);
        Drops drops = jsonManager.generatePatchedDrops();
        drops.manageMaps();

        Assertions.assertEquals(drops.getItemReferences().size(), 2222);
    }
}