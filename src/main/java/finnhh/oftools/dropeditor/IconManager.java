package finnhh.oftools.dropeditor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

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
    }

    public Map<String, byte[]> getIconMap() {
        return iconMap;
    }
}
