package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
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
import java.util.Objects;

public class CrateInfoComponent extends VBox implements ObservableComponent<ItemInfo> {
    private final ObjectProperty<ItemInfo> crateInfo;
    private final ObjectProperty<byte[]> icon;

    private final MainController controller;

    private final Label nameLabel;
    private final Label commentLabel;
    private final ImageView iconView;

    public CrateInfoComponent(double width, MainController controller) {
        crateInfo = new SimpleObjectProperty<>();
        icon = new SimpleObjectProperty<>();

        this.controller = controller;

        nameLabel = new Label();
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        commentLabel = new Label();
        commentLabel.setWrapText(true);
        commentLabel.setTextAlignment(TextAlignment.CENTER);

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(nameLabel, commentLabel, iconView);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public ReadOnlyObjectProperty<ItemInfo> getObservable() {
        return crateInfo;
    }

    @Override
    public void setObservable(ItemInfo data) {
        crateInfo.set(data);

        if (crateInfo.isNull().get()) {
            nameLabel.setText("<INVALID>");
            commentLabel.setText("<INVALID>");
            iconView.setImage(null);
        } else {
            var iconMap = controller.getIconManager().getIconMap();
            String name = Objects.isNull(crateInfo.get()) ?
                    "UNKNOWN" :
                    crateInfo.get().name();
            String comment = Objects.isNull(crateInfo.get()) ?
                    "Unknown Crate" :
                    crateInfo.get().comment();
            byte[] defaultIcon = iconMap.get("unknown");
            byte[] icon = Objects.isNull(crateInfo.get()) ?
                    defaultIcon :
                    iconMap.getOrDefault(crateInfo.get().iconName(), defaultIcon);

            this.icon.set(icon);

            nameLabel.setText(name);
            commentLabel.setText(comment);
            iconView.setImage(new Image(new ByteArrayInputStream(this.icon.get())));
        }
    }

    public ItemInfo getCrateInfo() {
        return crateInfo.get();
    }

    public ReadOnlyObjectProperty<ItemInfo> crateInfoProperty() {
        return crateInfo;
    }

    public byte[] getIcon() {
        return icon.get();
    }

    public ReadOnlyObjectProperty<byte[]> iconProperty() {
        return icon;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getCommentLabel() {
        return commentLabel;
    }

    public ImageView getIconView() {
        return iconView;
    }
}
