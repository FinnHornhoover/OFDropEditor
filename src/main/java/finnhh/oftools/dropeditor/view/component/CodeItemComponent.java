package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.CodeItem;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import finnhh.oftools.dropeditor.model.data.ItemSet;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.*;

public class CodeItemComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<CodeItem> codeItem;

    private final MainController controller;
    private final double listBoxWidth;
    private final double listBoxSpacing;

    private final TextField codeTextField;
    private final Button addButton;
    private final HBox topHBox;
    private final ListView<ItemReference> itemListView;
    private final VBox contentVBox;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> addClickHandler;
    private final EventHandler<ActionEvent> textFieldActionHandler;
    private final EventHandler<MouseEvent> removeClickHandler;

    public CodeItemComponent(MainController controller, ListView<Data> listView) {
        codeItem = new SimpleObjectProperty<>();

        this.controller = controller;
        listBoxWidth = 180.0;
        listBoxSpacing = 2.0;

        codeTextField = new TextField();
        codeTextField.getStyleClass().add("code-text-field");

        addButton = new Button("Add Item");
        addButton.setMinWidth(USE_COMPUTED_SIZE);
        addButton.getStyleClass().add("add-button");

        topHBox = new HBox(10, codeTextField, addButton);
        HBox.setHgrow(codeTextField, Priority.ALWAYS);

        itemListView = new ListView<>();
        itemListView.setOrientation(Orientation.HORIZONTAL);
        itemListView.setEditable(false);
        itemListView.setFocusTraversable(false);
        itemListView.setSelectionModel(new NoSelectionModel<>());
        itemListView.setPrefHeight(150);
        final CodeItemComponent codeItemComponent = this;
        itemListView.setCellFactory(cfData -> new ListCell<>() {
            private final ItemReferenceVBox itemReferenceVBox;
            private EventHandler<MouseEvent> removeClickHandler;

            {
                itemReferenceVBox = new ItemReferenceVBox(listBoxWidth, listBoxSpacing, controller, codeItemComponent);
                removeClickHandler = event -> { };
            }

            @Override
            protected void updateItem(ItemReference itemReference, boolean empty) {
                super.updateItem(itemReference, empty);

                itemReferenceVBox.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                removeClickHandler = event -> { };

                if (!empty) {
                    itemReferenceVBox.setObservable(itemReference);
                    removeClickHandler = event -> {
                        if (!event.isConsumed()) {
                            event.consume();
                            Optional.ofNullable(itemReference)
                                    .ifPresent(ir -> itemDropRemoved(ir.getItemReferenceID()));
                        }
                    };
                    itemReferenceVBox.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                    setGraphic(itemReferenceVBox);
                } else {
                    setGraphic(null);
                }
            }
        });

        contentVBox = new VBox(2, topHBox, itemListView);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(idHBox, contentVBox);

        addClickHandler = event -> this.controller.showSelectionMenuForResult(ItemReference.class)
                .ifPresent(d -> itemDropAdded(((ItemReference) d).getItemReferenceID()));
        textFieldActionHandler = event -> {
            unbindVariables();

            makeEditable(this.controller.getDrops());
            codeItem.get().setCode(codeTextField.getText());
            idLabel.setText(codeItem.get().getIdBinding().getValueSafe());

            bindVariables();
        };
        removeClickHandler = event -> {
            this.controller.getDrops().remove(codeItem.get());
            this.controller.getDrops().getReferenceMap().values().forEach(set -> set.remove(codeItem.get()));
            listView.getItems().remove(codeItem.get());
        };

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        codeItem.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                contentVBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentVBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindVariables() {
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        codeTextField.addEventHandler(ActionEvent.ACTION, textFieldActionHandler);
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    private void unbindVariables() {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
        codeTextField.removeEventHandler(ActionEvent.ACTION, textFieldActionHandler);
        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
    }

    private void clearItemDrops() {
        itemListView.getItems().clear();
    }

    private void populateItemDrops() {
        itemListView.getItems().addAll(codeItem.get().getItemReferenceIDs().stream()
                .map(irID -> controller.getDrops().getItemReferences().get(irID))
                .toList());
    }

    public void itemDropAdded(Integer newItemReferenceID) {
        if (!codeItem.get().getItemReferenceIDs().contains(newItemReferenceID)) {
            makeEditable(controller.getDrops());

            unbindVariables();
            clearItemDrops();

            codeItem.get().getItemReferenceIDs().add(newItemReferenceID);
            codeItem.get().getItemReferenceIDs().sort(Comparator.naturalOrder());

            populateItemDrops();
            bindVariables();
        }
    }

    public void itemDropRemoved(Integer itemReferenceID) {
        makeEditable(controller.getDrops());

        unbindVariables();
        clearItemDrops();

        codeItem.get().getItemReferenceIDs().remove(itemReferenceID);

        populateItemDrops();
        bindVariables();
    }

    public void itemDropChanged(Integer oldItemReferenceID, Integer newItemReferenceID) {
        makeEditable(controller.getDrops());

        unbindVariables();
        clearItemDrops();

        int index = codeItem.get().getItemReferenceIDs().indexOf(oldItemReferenceID);
        codeItem.get().getItemReferenceIDs().set(index, newItemReferenceID);
        codeItem.get().getItemReferenceIDs().sort(Comparator.naturalOrder());

        populateItemDrops();
        bindVariables();
    }

    @Override
    public Class<CodeItem> getObservableClass() {
        return CodeItem.class;
    }

    @Override
    public ReadOnlyObjectProperty<CodeItem> getObservable() {
        return codeItem;
    }

    @Override
    public void setObservable(Data data) {
        codeItem.set((CodeItem) data);

        unbindVariables();
        clearItemDrops();
        codeTextField.setText("");

        if (codeItem.isNotNull().get()) {
            codeTextField.setText(codeItem.get().getCode());
            populateItemDrops();
            bindVariables();
        }
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemReferenceMap = controller.getDrops().getItemReferences();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        ItemReferenceVBox prototype = new ItemReferenceVBox(listBoxWidth, listBoxSpacing, controller, this);

        allValues.addAll(getNestedSearchableValues(
                prototype.getSearchableValues(),
                op -> op.map(o -> (ItemSet) o)
                        .map(is -> is.getItemReferenceIDs().stream()
                                .filter(itemReferenceMap::containsKey)
                                .map(itemReferenceMap::get)
                                .toList())
                        .orElse(List.of())
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

    public CodeItem getCodeItem() {
        return codeItem.get();
    }

    public ObjectProperty<CodeItem> codeItemProperty() {
        return codeItem;
    }

    public TextField getCodeTextField() {
        return codeTextField;
    }

    public Button getAddButton() {
        return addButton;
    }

    public HBox getTopHBox() {
        return topHBox;
    }

    public ListView<ItemReference> getItemListView() {
        return itemListView;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    public HBox getIdHBox() {
        return idHBox;
    }

    public static class ItemReferenceVBox extends VBox implements DataComponent {
        private final ObjectProperty<ItemReference> itemReference;
        private final ObjectProperty<ItemInfo> itemInfo;

        private final MainController controller;
        private final CodeItemComponent parent;

        private final ImageView iconView;
        private final Label nameLabel;
        private final VBox contentVBox;
        private final Label idLabel;
        private final Button removeButton;
        private final HBox idHBox;

        private final EventHandler<MouseEvent> idClickHandler;

        public ItemReferenceVBox(double boxWidth,
                                 double boxSpacing,
                                 MainController controller,
                                 CodeItemComponent parent) {

            itemReference = new SimpleObjectProperty<>();
            itemInfo = new SimpleObjectProperty<>();

            this.controller = controller;
            this.parent = parent;

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            nameLabel = new Label();
            nameLabel.setWrapText(true);
            nameLabel.setTextAlignment(TextAlignment.CENTER);

            contentVBox = new VBox(boxSpacing, iconView, nameLabel);
            contentVBox.setMinWidth(boxWidth);
            contentVBox.setMaxWidth(boxWidth);
            contentVBox.setAlignment(Pos.CENTER);

            idLabel = new Label();
            idLabel.getStyleClass().add("id-label");

            removeButton = new Button("-");
            removeButton.setMinWidth(USE_COMPUTED_SIZE);
            removeButton.getStyleClass().addAll("remove-button", "slim-button");

            idHBox = new HBox(2, idLabel, removeButton);

            setSpacing(boxSpacing);
            getChildren().addAll(contentVBox, idHBox);

            idLabel.setText(getObservableClass().getSimpleName() + ": null");
            contentVBox.setDisable(true);
            setIdDisable(true);

            // it is okay to just set the observable
            idClickHandler = event -> this.controller.showSelectionMenuForResult(ItemReference.class)
                    .map(d -> (ItemReference) d)
                    .ifPresent(idr -> {
                        int oldID = getObservable().get().getItemReferenceID();
                        setObservable(idr);
                        this.parent.itemDropChanged(oldID, idr.getItemReferenceID());
                    });

            // both makeEditable and setObservable sets the observable, just use a listener here
            itemReference.addListener((o, oldVal, newVal) -> {
                if (Objects.isNull(newVal)) {
                    idLabel.setText(getObservableClass().getSimpleName() + ": null");
                    contentVBox.setDisable(true);
                    setIdDisable(true);
                } else {
                    idLabel.setText(newVal.getIdBinding().getValueSafe());
                    contentVBox.setDisable(false);
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
            idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

            itemReference.set((ItemReference) data);

            var iconMap = controller.getIconManager().getIconMap();
            byte[] defaultIcon = iconMap.get("unknown");

            nameLabel.setText("<INVALID>");
            iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));
            contentVBox.setDisable(true);

            if (itemReference.isNotNull().get()) {
                ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                        itemReference.get().getItemID(),
                        itemReference.get().getType()
                ));

                if (Objects.nonNull(itemInfo)) {
                    String name = itemInfo.name();
                    byte[] icon = iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);

                    this.itemInfo.set(itemInfo);

                    nameLabel.setText(name);
                    iconView.setImage(new Image(new ByteArrayInputStream(icon)));
                    contentVBox.setDisable(false);
                }
            }

            idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
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
        public DataComponent getParentComponent() {
            return parent;
        }

        @Override
        public Label getIdLabel() {
            return idLabel;
        }

        public ItemReference getItemReference() {
            return itemReference.get();
        }

        public ReadOnlyObjectProperty<ItemReference> itemReferenceProperty() {
            return itemReference;
        }

        public ItemInfo getItemInfo() {
            return itemInfo.get();
        }

        public ReadOnlyObjectProperty<ItemInfo> itemInfoProperty() {
            return itemInfo;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public VBox getContentVBox() {
            return contentVBox;
        }

        public Button getRemoveButton() {
            return removeButton;
        }

        public HBox getIdHBox() {
            return idHBox;
        }
    }
}
