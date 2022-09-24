package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.ByteArrayInputStream;

public class CrateInfoComponent extends VBox implements ObservableComponent<ItemInfo> {
    private final ObjectProperty<ItemInfo> crateInfo;

    private final MainController controller;

    private final CrateInfoTooltipComponent crateInfoTooltipComponent;
    private final Label nameLabel;
    private final Label commentLabel;
    private final ImageView iconView;

    public CrateInfoComponent(double width, MainController controller) {
        crateInfo = new SimpleObjectProperty<>();

        this.controller = controller;

        crateInfoTooltipComponent = new CrateInfoTooltipComponent(controller);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setGraphic(crateInfoTooltipComponent);

        nameLabel = new Label();
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setTooltip(tooltip);

        commentLabel = new Label();
        commentLabel.setWrapText(true);
        commentLabel.setTextAlignment(TextAlignment.CENTER);
        commentLabel.setTooltip(tooltip);

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);
        Tooltip.install(iconView, tooltip);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(nameLabel, commentLabel, iconView);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public Class<ItemInfo> getObservableClass() {
        return ItemInfo.class;
    }

    @Override
    public ReadOnlyObjectProperty<ItemInfo> getObservable() {
        return crateInfo;
    }

    @Override
    public void setObservable(ItemInfo data) {
        var iconMap = controller.getIconManager().getIconMap();

        crateInfo.set(data);
        crateInfoTooltipComponent.setObservable(data);

        String name = crateInfo.isNull().get() ?
                "UNKNOWN" :
                crateInfo.get().name();
        String comment = crateInfo.isNull().get() ?
                "Unknown Crate" :
                crateInfo.get().comment();
        byte[] defaultIcon = iconMap.get("unknown");
        byte[] icon = crateInfo.isNull().get() ?
                defaultIcon :
                iconMap.getOrDefault(crateInfo.get().iconName(), defaultIcon);

        nameLabel.setText(name);
        commentLabel.setText(comment);
        iconView.setImage(new Image(new ByteArrayInputStream(icon)));
    }

    public ItemInfo getCrateInfo() {
        return crateInfo.get();
    }

    public ReadOnlyObjectProperty<ItemInfo> crateInfoProperty() {
        return crateInfo;
    }

    public CrateInfoTooltipComponent getCrateInfoTooltipComponent() {
        return crateInfoTooltipComponent;
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

    public static class CrateInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<ItemInfo> {
        private final ObjectProperty<ItemInfo> crateInfo;

        public CrateInfoTooltipComponent(MainController controller) {
            super(controller);
            crateInfo = new SimpleObjectProperty<>();
        }

        @Override
        public Class<ItemInfo> getObservableClass() {
            return ItemInfo.class;
        }

        @Override
        public ReadOnlyObjectProperty<ItemInfo> getObservable() {
            return crateInfo;
        }

        @Override
        public void setObservable(ItemInfo data) {
            crateInfo.set(data);

            clearMaps();

            if (crateInfo.isNotNull().get()) {
                ItemInfo ci = crateInfo.get();
                arrangeMaps(ci.id(), ci.type());
            }
        }
    }
}
