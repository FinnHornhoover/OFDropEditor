package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.ItemType;
import finnhh.oftools.dropeditor.model.WeaponType;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.util.*;

public class ItemReferenceComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<ItemReference> itemReference;

    private final MainController controller;

    private final StandardImageView iconView;
    private final FlowPane infoFlowPane;
    private final HBox contentHBox;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> removeClickHandler;


    public ItemReferenceComponent(MainController controller) {
        itemReference = new SimpleObjectProperty<>();

        this.controller = controller;

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);

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

        removeClickHandler = event -> onRemoveClick();
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public List<Pane> getContentPanes() {
        return List.of(contentHBox);
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
        itemReference.set((ItemReference) data);
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        infoFlowPane.getChildren().clear();
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                itemReference.get().getItemID(), itemReference.get().getType()));

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
            iconView.setImage(itemInfo.iconName());
        }
    }

    @Override
    public void bindVariablesNullable() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public void unbindVariables() {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
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

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(ItemType.class),
                op -> op.map(o -> (ItemReference) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                        .map(ItemInfo::type)
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(WeaponType.class),
                op -> op.map(o -> (ItemReference) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                        .map(ItemInfo::weaponType)
                        .stream().toList()
        ));

        return allValues;
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

    public StandardImageView getIconView() {
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
