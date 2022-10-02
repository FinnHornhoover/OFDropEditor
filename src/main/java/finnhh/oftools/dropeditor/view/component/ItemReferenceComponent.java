package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.IntStream;

public class ItemReferenceComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<ItemReference> itemReference;

    private final MainController controller;

    private final ItemReferenceInfoComponent itemReferenceInfoComponent;
    private final GridPane infoGridPane;
    private final HBox contentHBox;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> removeClickHandler;

    public ItemReferenceComponent(MainController controller) {
        itemReference = new SimpleObjectProperty<>();

        this.controller = controller;

        itemReferenceInfoComponent = new ItemReferenceInfoComponent(160.0, controller);

        infoGridPane = new GridPane();
        IntStream.range(0, 4).forEach(i -> {
            ColumnConstraints nameColumn = new ColumnConstraints();
            nameColumn.setPercentWidth(15);
            nameColumn.setFillWidth(true);

            ColumnConstraints dataColumn = new ColumnConstraints();
            dataColumn.setPercentWidth(10);
            dataColumn.setFillWidth(true);

            infoGridPane.getColumnConstraints().addAll(nameColumn, dataColumn);
        });
        infoGridPane.setAlignment(Pos.CENTER);
        HBox.setHgrow(infoGridPane, Priority.ALWAYS);

        contentHBox = new HBox(2, itemReferenceInfoComponent, infoGridPane);
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

    private Label fillGrid(String name, String data, String cssData, int column, int row) {
        String nameTitle = name.substring(0, 1).toUpperCase(Locale.ENGLISH) +
                name.substring(1).replaceAll("([A-Z])", " $1") + ":";
        Label nameLabel = new Label(nameTitle);
        nameLabel.getStyleClass().add("grid-label");
        nameLabel.setAlignment(Pos.CENTER_RIGHT);
        nameLabel.setPadding(new Insets(0, 4, 0, 4));
        GridPane.setConstraints(nameLabel, column, row);

        Label dataLabel = new Label(data);
        dataLabel.getStyleClass().add(cssData.isEmpty() ? "data-grid-label" : cssData);
        dataLabel.getStyleClass().add("grid-label");
        dataLabel.setPadding(new Insets(0, 4, 0, 4));
        dataLabel.setAlignment(Pos.CENTER);
        GridPane.setConstraints(dataLabel, column + 1, row);

        infoGridPane.getChildren().addAll(nameLabel, dataLabel);
        return dataLabel;
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

        infoGridPane.getChildren().clear();
        itemReferenceInfoComponent.setObservableAndState(null);
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                itemReference.get().getItemID(), itemReference.get().getType()));

        if (Objects.nonNull(itemInfo)) {
            Set<String> unwantedNames = Set.of("id", "type", "weaponType", "name", "comment", "iconName");
            List<Field> fields = Arrays.stream(ItemInfo.class.getDeclaredFields())
                    .filter(f -> !unwantedNames.contains(f.getName()))
                    .toList();

            try {
                for (int i = 0; i < fields.size(); i++) {
                    Field f = fields.get(i);

                    f.setAccessible(true);
                    Object obj = f.get(itemInfo);

                    fillGrid(f.getName(),
                            (obj instanceof Boolean b) ?
                                    (b ? "Yes" : "No") :
                                    obj.toString(),
                            (obj instanceof Boolean b) ?
                                    b + "-grid-label" :
                                    "",
                            2 * (i / 5), i % 5);
                }
            } catch (IllegalAccessException | IllegalArgumentException ignored) {
            }

            Label commentLabel = fillGrid("Comment", itemInfo.comment(), "", 0, 5);
            GridPane.setColumnSpan(commentLabel, 7);
            commentLabel.setAlignment(Pos.CENTER_LEFT);

            itemReferenceInfoComponent.setObservableAndState(itemInfo);
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

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(Rarity.class),
                op -> op.map(o -> (ItemReference) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                        .map(ItemInfo::rarity)
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(Gender.class),
                op -> op.map(o -> (ItemReference) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                        .map(ItemInfo::gender)
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

    public ItemReferenceInfoComponent getItemReferenceInfoComponent() {
        return itemReferenceInfoComponent;
    }

    public GridPane getInfoGridPane() {
        return infoGridPane;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public HBox getIdHBox() {
        return idHBox;
    }
}
