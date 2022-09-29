package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.*;

public class ItemReferenceComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<ItemReference> itemReference;

    private final MainController controller;

    private final ImageView iconView;
    private final FlowPane infoFlowPane;
    private final HBox contentHBox;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> removeClickHandler;


    public ItemReferenceComponent(MainController controller, ListView<Data> listView) {
        itemReference = new SimpleObjectProperty<>();

        this.controller = controller;

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        infoFlowPane = new FlowPane(iconView);
        infoFlowPane.setHgap(2);
        infoFlowPane.setVgap(2);
        HBox.setHgrow(infoFlowPane, Priority.ALWAYS);

        contentHBox = new HBox(2, iconView, infoFlowPane);
        contentHBox.setAlignment(Pos.CENTER);
        contentHBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(idHBox, contentHBox);

        removeClickHandler = event -> {
            this.controller.getDrops().remove(itemReference.get());
            this.controller.getDrops().getReferenceMap().values().forEach(set -> set.remove(itemReference.get()));
            listView.getItems().remove(itemReference.get());
        };

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        itemReference.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                contentHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public Class<ItemReference> getObservableClass() {
        return ItemReference.class;
    }

    @Override
    public ReadOnlyObjectProperty<ItemReference> getObservable() {
        return itemReference;
    }

    @Override
    public void setObservable(Data data) {
        var iconMap = controller.getIconManager().getIconMap();
        byte[] defaultIcon = iconMap.get("unknown");

        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);

        itemReference.set((ItemReference) data);

        infoFlowPane.getChildren().clear();

        if (itemReference.isNull().get()) {
            iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));
        } else {
            var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

            ItemInfo itemInfo = itemInfoMap.get(new Pair<>(
                    itemReference.get().getItemID(), itemReference.get().getType()));

            byte[] icon = Objects.isNull(itemInfo) ?
                    defaultIcon :
                    iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);
            iconView.setImage(new Image(new ByteArrayInputStream(icon)));

            if (Objects.nonNull(itemInfo)) {
                Arrays.stream(ItemInfo.class.getDeclaredFields())
                        .map(f -> {
                            String objectString = "<INVALID>";
                            try {
                                f.setAccessible(true);
                                objectString = f.get(itemInfo).toString();
                            } catch (IllegalAccessException | IllegalArgumentException ignored) {
                            }

                            return new Label(String.format("%s%s: %s",
                                    f.getName().substring(0, 1).toUpperCase(Locale.ENGLISH),
                                    f.getName().substring(1).replaceAll("([A-Z])", " $1"),
                                    objectString));
                        })
                        .forEach(infoFlowPane.getChildren()::add);
            }
        }

        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(ItemInfo.class),
                op -> op.map(o -> (ItemReference) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                        .stream().toList()
        ));

        return allValues;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    }

    public ItemReference getItemReference() {
        return itemReference.get();
    }

    public ReadOnlyObjectProperty<ItemReference> itemReferenceProperty() {
        return itemReference;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public FlowPane getInfoFlowPane() {
        return infoFlowPane;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public HBox getIdHBox() {
        return idHBox;
    }
}
