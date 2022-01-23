package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.MobTypeInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.Objects;

public class MobInfoComponent extends VBox implements ObservableComponent<MobTypeInfo> {
    private final ObjectProperty<MobTypeInfo> mobTypeInfo;
    private final ObjectProperty<byte[]> icon;

    private final Map<String, byte[]> iconMap;

    private final Label mobNameLabel;
    private final ImageView iconView;

    public MobInfoComponent(Map<String, byte[]> iconMap) {
        this(160.0, iconMap);
    }

    public MobInfoComponent(double width, Map<String, byte[]> iconMap) {
        mobTypeInfo = new SimpleObjectProperty<>();
        icon = new SimpleObjectProperty<>();

        this.iconMap = iconMap;

        mobNameLabel = new Label();
        mobNameLabel.setWrapText(true);
        mobNameLabel.setTextAlignment(TextAlignment.CENTER);

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(mobNameLabel, iconView);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public ReadOnlyObjectProperty<MobTypeInfo> getObservable() {
        return mobTypeInfo;
    }

    @Override
    public void setObservable(MobTypeInfo data) {
        mobTypeInfo.set(data);

        if (mobTypeInfo.isNull().get()) {
            mobNameLabel.setText("<INVALID>");
            iconView.setImage(null);
        } else {
            String name = Objects.isNull(mobTypeInfo.get()) ?
                    "Unknown Mob" :
                    mobTypeInfo.get().name();
            byte[] defaultIcon = iconMap.get("unknown");
            byte[] icon = Objects.isNull(mobTypeInfo.get()) ?
                    defaultIcon :
                    iconMap.getOrDefault(mobTypeInfo.get().iconName(), defaultIcon);

            this.icon.set(icon);

            mobNameLabel.setText(name);
            iconView.setImage(new Image(new ByteArrayInputStream(this.icon.get())));
        }
    }

    public MobTypeInfo getMobTypeInfo() {
        return mobTypeInfo.get();
    }

    public ReadOnlyObjectProperty<MobTypeInfo> mobTypeInfoProperty() {
        return mobTypeInfo;
    }

    public byte[] getIcon() {
        return icon.get();
    }

    public ReadOnlyObjectProperty<byte[]> iconProperty() {
        return icon;
    }

    public Label getMobNameLabel() {
        return mobNameLabel;
    }

    public ImageView getIconView() {
        return iconView;
    }
}
