package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.EventType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class IconManager {
    private final Map<String, byte[]> iconMap;

    public IconManager() {
        iconMap = new HashMap<>();
    }

    private byte[] loadSingleEmbeddedImage(String directory, String name) throws IOException {
        return Files.readAllBytes(Paths.get(new File(Objects.requireNonNull(
                IconManager.class.getResource(directory + "/" + name + ".png")).getFile()).getPath()));
    }

    public void setIconDirectory(File iconDirectory) throws IOException {
        iconMap.clear();

        try (Stream<Path> pathStream = Files.list(iconDirectory.toPath())) {
            for (Path path : pathStream.toList()) {
                String name = path.getFileName().toString().split("\\.")[0];
                iconMap.put(name, Files.readAllBytes(path));
            }
        }

        for (String name : List.of("unknown", "taro", "fm", "boosts", "potions", "down")) {
            iconMap.put(name, loadSingleEmbeddedImage(".", name));
        }

        // race IZ icons
        for (int i = 1; i < 34; i++) {
            if (i == 6) continue;

            String name = String.format("ep_small_%02d", i);
            iconMap.put(name, loadSingleEmbeddedImage("ep", name));
        }

        // minimap icons
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                String name = String.format("minimap_%d_%d", i, j);
                iconMap.put(name, loadSingleEmbeddedImage("minimap", name));
            }
        }

        // event icons
        for (EventType eventType : EventType.values()) {
            if (eventType == EventType.NO_EVENT) continue;

            String name = eventType.iconName();
            iconMap.put(name, loadSingleEmbeddedImage("event", name));
        }
    }

    public Map<String, byte[]> getIconMap() {
        return iconMap;
    }
}
