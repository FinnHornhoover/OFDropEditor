package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.EventType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

class IconManagerTest {
    private final IconManager iconManager;
    private final Set<String> requiredKeys;

    IconManagerTest() {
        iconManager = new IconManager();
        requiredKeys = new HashSet<>(List.of("unknown", "taro", "fm", "boosts", "potions", "down"));

        for (int i = 1; i < 34; i++) {
            if (i == 6) continue;

            for (String type : List.of("small", "big"))
                requiredKeys.add(String.format("ep_%s_%02d", type, i));
        }

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++)
                requiredKeys.add(String.format("minimap_%d_%d", i, j));
        }

        for (EventType eventType : EventType.values()) {
            if (eventType == EventType.NO_EVENT) continue;

            requiredKeys.add(eventType.iconName());
        }
    }

    static Path resourcePath(String name) throws URISyntaxException {
        return Path.of(Objects.requireNonNull(IconManager.class.getClassLoader().getResource(name)).toURI());
    }

    @Test
    void testValidLoadImage() throws URISyntaxException, IOException {
        iconManager.loadIcons(resourcePath("tdata/icons").toFile().getAbsolutePath());
        var iconMap = iconManager.getIconMap();

        for (String name : requiredKeys)
            Assertions.assertTrue(iconMap.containsKey(name));

        Assertions.assertTrue(iconMap.containsKey("cosicon_00"));
    }

    @Test
    void testNonDirectoryLoadImage() {
        Assertions.assertThrows(IOException.class, () ->
                iconManager.loadIcons(resourcePath("tdata/icons/cosicon_00.png").toFile().getAbsolutePath()));
    }

    @Test
    void testInvalidContentLoadImage() {
        Assertions.assertThrows(IOException.class, () ->
                iconManager.loadIcons(resourcePath("tdata").toFile().getAbsolutePath()));
    }

    @Test
    void testNonExistentDirectoryLoadImage() {
        Assertions.assertThrows(IOException.class, () ->
                iconManager.loadIcons("abcdef"));
    }
}
