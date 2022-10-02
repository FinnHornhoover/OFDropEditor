package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

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

    public CodeItemComponent(MainController controller) {
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
                    itemReferenceVBox.setObservableAndState(itemReference);
                    removeClickHandler = event -> {
                        if (!event.isConsumed()) {
                            event.consume();
                            Optional.ofNullable(itemReference)
                                    .ifPresent(ir -> makeEdit(() -> codeItem.get().getItemReferenceIDs().remove(
                                            (Integer) ir.getItemReferenceID())));
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
                .ifPresent(d -> makeEdit(() -> {
                    codeItem.get().getItemReferenceIDs().add(((ItemReference) d).getItemReferenceID());
                    codeItem.get().getItemReferenceIDs().sort(Comparator.naturalOrder());
                }));
        textFieldActionHandler = event -> makeEdit(() -> codeItem.get().setCode(codeTextField.getText()));
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
        return List.of(contentVBox);
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
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        codeTextField.setText("");
        itemListView.getItems().clear();
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        codeTextField.setText(codeItem.get().getCode());
        itemListView.getItems().addAll(codeItem.get().getItemReferenceIDs().stream()
                .map(irID -> controller.getDrops().getItemReferences().get(irID))
                .toList());
    }

    @Override
    public void bindVariablesNonNull() {
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        codeTextField.addEventHandler(ActionEvent.ACTION, textFieldActionHandler);
    }

    @Override
    public void bindVariablesNullable() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public void unbindVariables() {
        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        codeTextField.removeEventHandler(ActionEvent.ACTION, textFieldActionHandler);
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
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

        private final MainController controller;
        private final CodeItemComponent parent;

        private final StandardImageView iconView;
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

            this.controller = controller;
            this.parent = parent;

            iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);

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

            // it is okay to just set the observable
            idClickHandler = event -> this.controller.showSelectionMenuForResult(ItemReference.class)
                    .ifPresent(d -> {
                        int oldID = getObservable().get().getItemReferenceID();
                        setObservableAndState(d);
                        this.parent.makeEdit(() -> {
                            int index = this.parent.getCodeItem().getItemReferenceIDs().indexOf(oldID);
                            this.parent.getCodeItem().getItemReferenceIDs().set(index,
                                    ((ItemReference) d).getItemReferenceID());
                            this.parent.getCodeItem().getItemReferenceIDs().sort(Comparator.naturalOrder());
                        });
                    });
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
            return List.of(contentVBox);
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
            DataComponent.super.cleanUIState();

            nameLabel.setText("<INVALID>");
            iconView.cleanImage();
        }

        @Override
        public void fillUIState() {
            DataComponent.super.fillUIState();

            ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                    itemReference.get().getItemID(), itemReference.get().getType()));

            if (Objects.nonNull(itemInfo)) {
                nameLabel.setText(itemInfo.name());
                iconView.setImage(itemInfo.iconName());
            }
        }

        @Override
        public void bindVariablesNullable() {
            idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
        }

        @Override
        public void unbindVariables() {
            idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
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
        public DataComponent getParentComponent() {
            return parent;
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
