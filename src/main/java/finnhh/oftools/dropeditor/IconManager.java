package finnhh.oftools.dropeditor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IconManager {
    private final Map<String, byte[]> iconMap;

    public IconManager() {
        iconMap = new HashMap<>();
    }

    public void setIconDirectory(File iconDirectory) throws IOException {
        iconMap.clear();

        for (Path path : Files.list(iconDirectory.toPath()).toList()) {
            String name = path.getFileName().toString().split("\\.")[0];
            iconMap.put(name, Files.readAllBytes(path));
        }

        for (String name : List.of("unknown", "taro", "fm", "boosts", "potions", "down")) {
            Path path = Paths.get(new File(
                    Objects.requireNonNull(IconManager.class.getResource(name + ".png")).getFile()).getPath());
            iconMap.put(name, Files.readAllBytes(path));
        }
    }

    public Map<String, byte[]> getIconMap() {
        return iconMap;
    }
}
