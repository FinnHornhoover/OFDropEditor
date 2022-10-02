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

public class ItemReferenceInfoComponent extends VBox implements ObservableComponent<ItemInfo> {
    private final ObjectProperty<ItemInfo> itemInfo;

    private final MainController controller;

    private final ItemReferenceInfoTooltipComponent itemReferenceInfoTooltipComponent;
    private final Label nameLabel;
    private final Label typeLabel;
    private final StandardImageView iconView;

    public ItemReferenceInfoComponent(double width, MainController controller) {
        itemInfo = new SimpleObjectProperty<>();

        this.controller = controller;

        itemReferenceInfoTooltipComponent = new ItemReferenceInfoTooltipComponent(controller);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setGraphic(itemReferenceInfoTooltipComponent);

        nameLabel = new Label();
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setTooltip(tooltip);

        typeLabel = new Label();
        typeLabel.setWrapText(true);
        typeLabel.setTextAlignment(TextAlignment.CENTER);
        typeLabel.setTooltip(tooltip);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);
        Tooltip.install(iconView, tooltip);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(nameLabel, typeLabel, iconView);
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
        return itemInfo;
    }

    @Override
    public void setObservable(ItemInfo data) {
        itemInfo.set(data);
    }

    @Override
    public void cleanUIState() {
        itemReferenceInfoTooltipComponent.setObservableAndState(null);
        nameLabel.setText("UNKNOWN");
        typeLabel.setText("None");
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        itemReferenceInfoTooltipComponent.setObservableAndState(itemInfo.get());
        nameLabel.setText(itemInfo.get().name());
        typeLabel.setText(itemInfo.get().getTypeString());
        iconView.setImage(itemInfo.get().iconName());
    }

    public ItemInfo getItemInfo() {
        return itemInfo.get();
    }

    public ReadOnlyObjectProperty<ItemInfo> itemInfoProperty() {
        return itemInfo;
    }

    public ItemReferenceInfoTooltipComponent getItemReferenceInfoTooltipComponent() {
        return itemReferenceInfoTooltipComponent;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getTypeLabel() {
        return typeLabel;
    }

    public StandardImageView getIconView() {
        return iconView;
    }

    public static class ItemReferenceInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<ItemInfo> {
        private final ObjectProperty<ItemInfo> itemInfo;

        public ItemReferenceInfoTooltipComponent(MainController controller) {
            super(controller);
            itemInfo = new SimpleObjectProperty<>();
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
            return itemInfo;
        }

        @Override
        public void setObservable(ItemInfo data) {
            itemInfo.set(data);
        }

        @Override
        public void cleanUIState() {
            clearMaps();
        }

        @Override
        public void fillUIState() {
            ItemInfo ci = itemInfo.get();
            arrangeItemMaps(ci.id(), ci.type().getTypeID());
        }
    }
}
