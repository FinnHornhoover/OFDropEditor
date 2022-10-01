package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class CrateInfoComponent extends VBox implements ObservableComponent<ItemInfo> {
    private final ObjectProperty<ItemInfo> crateInfo;

    private final MainController controller;

    private final CrateInfoTooltipComponent crateInfoTooltipComponent;
    private final Label nameLabel;
    private final Label commentLabel;
    private final StandardImageView iconView;

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

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);
        Tooltip.install(iconView, tooltip);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(nameLabel, commentLabel, iconView);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public MainController getController() {
        return controller;
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
    }

    @Override
    public void cleanUIState() {
        crateInfoTooltipComponent.setObservableAndState(null);
        nameLabel.setText("UNKNOWN");
        commentLabel.setText("Unknown Crate");
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        crateInfoTooltipComponent.setObservableAndState(crateInfo.get());
        nameLabel.setText(crateInfo.get().name());
        commentLabel.setText(crateInfo.get().comment());
        iconView.setImage(crateInfo.get().iconName());
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

    public StandardImageView getIconView() {
        return iconView;
    }

    public static class CrateInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<ItemInfo> {
        private final ObjectProperty<ItemInfo> crateInfo;

        public CrateInfoTooltipComponent(MainController controller) {
            super(controller);
            crateInfo = new SimpleObjectProperty<>();
        }

        @Override
        public MainController getController() {
            return controller;
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
        }

        @Override
        public void cleanUIState() {
            clearMaps();
        }

        @Override
        public void fillUIState() {
            ItemInfo ci = crateInfo.get();
            arrangeMaps(ci.id(), ci.type());
        }
    }
}
