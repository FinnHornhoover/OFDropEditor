package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class PreferencesTest {
    private final Preferences preferences;

    PreferencesTest() {
        preferences = new Preferences();
        preferences.setDropDirectory("a/b/c");
        preferences.getPatchDirectories().addAll("d/e", "f/g", "h/i");
        preferences.setSaveDirectory("a/b/c/d");
    }

    @Test
    void testIsOverwritingDropDirectory() {
        preferences.setSaveDirectory("a/b/c");
        Assertions.assertTrue(preferences.isOverwritingDropDirectory());

        preferences.setSaveDirectory("a/b/c/d");
        Assertions.assertFalse(preferences.isOverwritingDropDirectory());
    }

    @Test
    void testIsOverwritingAnyPatchDirectory() {
        for (String path : List.of("d/e", "f/g", "h/i")) {
            preferences.setSaveDirectory(path);
            Assertions.assertTrue(preferences.isOverwritingAnyPatchDirectory());
        }

        preferences.setSaveDirectory("a/b/c/d");
        Assertions.assertFalse(preferences.isOverwritingAnyPatchDirectory());
    }

    @Test
    void testIsOverwritingLastPatchDirectory() {
        preferences.setSaveDirectory("h/i");
        Assertions.assertTrue(preferences.isOverwritingLastPatchDirectory());

        preferences.setSaveDirectory("a/b/c/d");
        Assertions.assertFalse(preferences.isOverwritingLastPatchDirectory());
    }
}
