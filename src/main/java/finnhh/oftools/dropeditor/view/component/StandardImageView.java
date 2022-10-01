package finnhh.oftools.dropeditor.view.component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.ByteArrayInputStream;
import java.util.Map;

public class StandardImageView extends ImageView {
    public static final String DEFAULT_ICON_NAME = "unknown";

    private final Map<String, byte[]> iconMap;

    public StandardImageView(Map<String, byte[]> iconMap, double size) {
        this.iconMap = iconMap;

        setFitWidth(size);
        setFitHeight(size);
        setPreserveRatio(true);
        setCache(true);
        cleanImage();
    }

    public void setImage(String iconName) {
        setImage(new Image(new ByteArrayInputStream(iconMap.getOrDefault(iconName, iconMap.get(DEFAULT_ICON_NAME)))));
    }

    public void cleanImage() {
        setImage(new Image(new ByteArrayInputStream(iconMap.get(DEFAULT_ICON_NAME))));
    }
}
