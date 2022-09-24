package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.Gender;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.Rarity;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemDrop;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import finnhh.oftools.dropeditor.model.data.ItemSet;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.controlsfx.control.ToggleSwitch;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemSetComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<ItemSet> itemSet;

    private final MainController controller;
    private final double listBoxWidth;
    private final double listBoxSpacing;
    private final DataComponent parent;

    private final ToggleSwitch ignoreRarityButton;
    private final ToggleSwitch ignoreGenderButton;
    private final Button addButton;
    private final VBox globalIgnoreSettingsVBox;

    private final Label defaultWeightLabel;
    private final StandardSpinner defaultWeightSpinner;
    private final Button defaultWeightRecalculateButton;
    private final VBox globalWeightSettingsVBox;

    private final Label rarityViewSettingsLabel;
    private final ChoiceBox<Rarity> rarityViewSettingsChoiceBox;
    private final HBox rarityViewSettingsHBox;

    private final Label genderViewSettingsLabel;
    private final ChoiceBox<Gender> genderViewSettingsChoiceBox;
    private final HBox genderViewSettingsHBox;

    private final VBox viewSettingsVBox;
    private final HBox settingsHBox;

    private final ObservableList<ItemDrop> originalItemList;
    private final ListView<ItemDrop> itemListView;

    private final VBox contentVBox;
    private final Label idLabel;

    private final EventHandler<ActionEvent> defaultWeightRecalculateClickHandler;
    private final ChangeListener<Integer> defaultWeightListener;
    private final ChangeListener<Boolean> ignoreRarityListener;
    private final ChangeListener<Boolean> ignoreGenderListener;
    private final ChangeListener<Rarity> rarityViewSettingsListener;
    private final ChangeListener<Gender> genderViewSettingsListener;
    private final EventHandler<MouseEvent> addClickHandler;
    private final EventHandler<MouseEvent> idClickHandler;

    public ItemSetComponent(double listBoxWidth,
                            double listBoxSpacing,
                            MainController controller,
                            DataComponent parent) {

        itemSet = new SimpleObjectProperty<>();

        this.controller = controller;

        this.listBoxWidth = listBoxWidth;
        this.listBoxSpacing = listBoxSpacing;
        this.parent = parent;

        ignoreRarityButton = new ToggleSwitch("ItemSet Ignores Rarity");
        ignoreGenderButton = new ToggleSwitch("ItemSet Ignores Gender");
        addButton = new Button("Add Item");
        addButton.setMinWidth(USE_COMPUTED_SIZE);
        addButton.getStyleClass().add("add-button");
        globalIgnoreSettingsVBox = new VBox(2, ignoreRarityButton, ignoreGenderButton, addButton);
        globalIgnoreSettingsVBox.setAlignment(Pos.CENTER_RIGHT);
        globalIgnoreSettingsVBox.getStyleClass().add("bordered-pane");
        globalIgnoreSettingsVBox.setPadding(new Insets(2));

        defaultWeightLabel = new Label("Default Item Weight");
        defaultWeightSpinner = new StandardSpinner(0, Integer.MAX_VALUE, 1);
        defaultWeightSpinner.setMinWidth(listBoxWidth);
        defaultWeightSpinner.setMaxWidth(listBoxWidth);
        defaultWeightRecalculateButton = new Button("Recalculate");
        globalWeightSettingsVBox = new VBox(2,
                defaultWeightLabel, defaultWeightSpinner, defaultWeightRecalculateButton);
        globalWeightSettingsVBox.setAlignment(Pos.CENTER);
        globalWeightSettingsVBox.getStyleClass().add("bordered-pane");
        globalWeightSettingsVBox.setPadding(new Insets(2));

        rarityViewSettingsLabel = new Label("Filter Rarity Rolled");
        rarityViewSettingsChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Rarity.values()));
        rarityViewSettingsChoiceBox.setMinWidth(listBoxWidth);
        rarityViewSettingsChoiceBox.setMaxWidth(listBoxWidth);
        rarityViewSettingsChoiceBox.setValue(Rarity.ANY);
        rarityViewSettingsHBox = new HBox(2, rarityViewSettingsLabel, rarityViewSettingsChoiceBox);
        rarityViewSettingsHBox.setAlignment(Pos.CENTER_RIGHT);

        genderViewSettingsLabel = new Label("Filter Player Gender");
        genderViewSettingsChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Gender.values()));
        genderViewSettingsChoiceBox.setMinWidth(listBoxWidth);
        genderViewSettingsChoiceBox.setMaxWidth(listBoxWidth);
        genderViewSettingsChoiceBox.setValue(Gender.ANY);
        genderViewSettingsHBox = new HBox(2, genderViewSettingsLabel, genderViewSettingsChoiceBox);
        genderViewSettingsHBox.setAlignment(Pos.CENTER_RIGHT);

        viewSettingsVBox = new VBox(2, rarityViewSettingsHBox, genderViewSettingsHBox);
        viewSettingsVBox.setAlignment(Pos.CENTER_RIGHT);
        viewSettingsVBox.getStyleClass().add("bordered-pane");
        viewSettingsVBox.setPadding(new Insets(2));

        settingsHBox = new HBox(10, globalIgnoreSettingsVBox, globalWeightSettingsVBox, viewSettingsVBox);
        settingsHBox.setAlignment(Pos.CENTER_LEFT);

        originalItemList = FXCollections.observableArrayList();

        itemListView = new ListView<>();
        itemListView.setOrientation(Orientation.HORIZONTAL);
        itemListView.setEditable(false);
        itemListView.setFocusTraversable(false);
        itemListView.setSelectionModel(new NoSelectionModel<>());
        final ItemSetComponent itemSetComponent = this;
        itemListView.setCellFactory(cfData -> new ListCell<>() {
            private final ItemDropVBox itemDropVBox;

            private EventHandler<Event> weightHandler;
            private EventHandler<MouseEvent> removeClickHandler;
            private EventHandler<ActionEvent> rarityHandler;
            private EventHandler<ActionEvent> genderHandler;

            {
                itemDropVBox = new ItemDropVBox(listBoxWidth, listBoxSpacing, controller, itemSetComponent);

                weightHandler = event -> {};
                removeClickHandler = event -> {};
                rarityHandler = event -> {};
                genderHandler = event -> {};
            }

            private <T> void handleEvent(Event event,
                                         Supplier<T> valSupplier,
                                         Function<ItemDrop, T> valGetter,
                                         BiConsumer<ItemDrop, T> handler) {
                Rarity rarity = rarityViewSettingsChoiceBox.getValue();
                Gender gender = genderViewSettingsChoiceBox.getValue();
                ItemDrop itemDrop = itemDropVBox.getItemDrop();
                T newVal = valSupplier.get();

                if (!event.isConsumed() && !valGetter.apply(itemDrop).equals(newVal)) {
                    event.consume();
                    makeEditable(controller.getDrops());

                    unbindVariables();

                    handler.accept(itemDrop, newVal);

                    rarityViewSettingsChoiceBox.setValue(rarity);
                    genderViewSettingsChoiceBox.setValue(gender);

                    itemSetComponent.requestFocus();
                    refreshItemDrops();
                    bindVariables();
                }
            }

            private void bindFor(ItemDrop itemDrop) {
                if (Objects.isNull(itemDrop))
                    return;

                weightHandler = event -> handleEvent(
                        event,
                        itemDropVBox.getSpinner()::getValue,
                        ItemDrop::getWeight,
                        (idr, newVal) -> {
                            int itemReferenceID = idr.getItemReferenceID();
                            int defaultWeight = itemSet.get().getDefaultItemWeight();
                            var alterWeightMap = itemSet.get().getAlterItemWeightMap();

                            alterWeightMap.remove(itemReferenceID);
                            if (newVal != defaultWeight)
                                alterWeightMap.put(itemReferenceID, newVal);
                        }
                );
                removeClickHandler = event -> {
                    if (!event.isConsumed()) {
                        event.consume();
                        itemDropRemoved(itemDropVBox.getItemDrop().getItemReferenceID());
                    }
                };
                rarityHandler = event -> handleEvent(
                        event,
                        itemDropVBox.getRarityChoiceBox()::getValue,
                        ItemDrop::getRarity,
                        (idr, newVal) -> {
                            ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                                    new Pair<>(idr.getItemID(), idr.getType()));

                            var alterRarityMap = itemSet.get().getAlterRarityMap();

                            alterRarityMap.remove(idr.getItemReferenceID());
                            if (Objects.isNull(itemInfo) || newVal != Rarity.forType(itemInfo.rarity()))
                                alterRarityMap.put(idr.getItemReferenceID(), newVal.getType());
                        }
                );
                genderHandler = event -> handleEvent(
                        event,
                        itemDropVBox.getGenderChoiceBox()::getValue,
                        ItemDrop::getGender,
                        (idr, newVal) -> {
                            ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                                    new Pair<>(idr.getItemID(), idr.getType()));

                            var alterGenderMap = itemSet.get().getAlterGenderMap();

                            alterGenderMap.remove(idr.getItemReferenceID());
                            if (Objects.isNull(itemInfo) || newVal != Gender.forType(itemInfo.gender()))
                                alterGenderMap.put(idr.getItemReferenceID(), newVal.getType());
                        }
                );

                DoubleExpression percentExpression = itemDrop.weightProperty()
                        .multiply(100.0)
                        .divide(Bindings.max(1.0, itemListView.getItems().stream()
                                .map(idr -> idr.weightProperty().add(0.0))
                                .reduce(DoubleExpression::add)
                                .orElse(Bindings.createDoubleBinding(() -> 0.0))));

                itemDropVBox.getPercentageSlider().valueProperty().bind(percentExpression);
                itemDropVBox.getPercentageLabel().textProperty().bind(percentExpression.asString(Locale.US, "%.5f%%"));

                itemDropVBox.getSpinner().addEventHandler(MouseEvent.MOUSE_CLICKED, weightHandler);
                itemDropVBox.getSpinner().addEventHandler(ActionEvent.ACTION, weightHandler);
                itemDropVBox.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                itemDropVBox.getRarityChoiceBox().addEventHandler(ActionEvent.ACTION, rarityHandler);
                itemDropVBox.getGenderChoiceBox().addEventHandler(ActionEvent.ACTION, genderHandler);
            }

            private void unbindFor(ItemDrop oldItemDrop) {
                if (Objects.isNull(oldItemDrop))
                    return;

                itemDropVBox.getPercentageLabel().textProperty().unbind();
                itemDropVBox.getPercentageSlider().valueProperty().unbind();

                itemDropVBox.getSpinner().removeEventHandler(MouseEvent.MOUSE_CLICKED, weightHandler);
                itemDropVBox.getSpinner().removeEventHandler(ActionEvent.ACTION, weightHandler);
                itemDropVBox.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                itemDropVBox.getRarityChoiceBox().removeEventHandler(ActionEvent.ACTION, rarityHandler);
                itemDropVBox.getGenderChoiceBox().removeEventHandler(ActionEvent.ACTION, genderHandler);
            }

            @Override
            protected void updateItem(ItemDrop itemDrop, boolean empty) {
                super.updateItem(itemDrop, empty);

                unbindFor(getItem());

                if (!empty) {
                    itemDropVBox.setObservable(itemDrop);
                    bindFor(itemDrop);
                    setGraphic(itemDropVBox);
                } else {
                    setGraphic(null);
                }
            }
        });

        contentVBox = new VBox(settingsHBox, itemListView);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentVBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        defaultWeightRecalculateClickHandler = event -> {
            unbindVariables();
            handleDefaultWeightChange();
            refreshItemDrops();
            bindVariables();
        };
        defaultWeightListener = (o, oldVal, newVal) -> {
            Rarity rarity = rarityViewSettingsChoiceBox.getValue();
            Gender gender = genderViewSettingsChoiceBox.getValue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setDefaultItemWeight(newVal);
            handleDefaultWeightChange();
            rarityViewSettingsChoiceBox.setValue(rarity);
            genderViewSettingsChoiceBox.setValue(gender);

            refreshItemDrops();
            bindVariables();
        };
        ignoreRarityListener = (o, oldVal, newVal) -> {
            Gender gender = genderViewSettingsChoiceBox.getValue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setIgnoreRarity(newVal);
            ignoreRarityButton.setSelected(newVal);
            rarityViewSettingsChoiceBox.setValue(Rarity.ANY);
            rarityViewSettingsChoiceBox.setDisable(newVal);
            genderViewSettingsChoiceBox.setValue(gender);

            refreshListView();
            bindVariables();
        };
        ignoreGenderListener = (o, oldVal, newVal) -> {
            Rarity rarity = rarityViewSettingsChoiceBox.getValue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setIgnoreGender(newVal);
            ignoreGenderButton.setSelected(newVal);
            rarityViewSettingsChoiceBox.setValue(rarity);
            genderViewSettingsChoiceBox.setValue(Gender.ANY);
            genderViewSettingsChoiceBox.setDisable(newVal);

            refreshListView();
            bindVariables();
        };
        rarityViewSettingsListener = (o, oldVal, newVal) -> refreshListView();
        genderViewSettingsListener = (o, oldVal, newVal) -> refreshListView();
        addClickHandler = event -> this.controller.showSelectionMenuForResult(ItemReference.class)
                .ifPresent(d -> itemDropAdded(((ItemReference) d).getItemReferenceID()));

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass())
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        // both makeEditable and setObservable sets the observable, just use a listener here
        itemSet.addListener((o, oldVal, newVal) -> {
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

    private boolean itemIncluded(ItemDrop idr) {
        return Objects.nonNull(idr) &&
                controller.getStaticDataStore().getItemInfoMap().containsKey(new Pair<>(idr.getItemID(), idr.getType())) &&
                (itemSet.get().getIgnoreRarity() || idr.getRarity().match(rarityViewSettingsChoiceBox.getValue())) &&
                (itemSet.get().getIgnoreGender() || idr.getGender().match(genderViewSettingsChoiceBox.getValue()));
    }

    private void bindVariables() {
        defaultWeightRecalculateButton.addEventHandler(ActionEvent.ACTION, defaultWeightRecalculateClickHandler);
        defaultWeightSpinner.valueProperty().addListener(defaultWeightListener);
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        genderViewSettingsChoiceBox.valueProperty().addListener(genderViewSettingsListener);
        rarityViewSettingsChoiceBox.valueProperty().addListener(rarityViewSettingsListener);
        ignoreGenderButton.selectedProperty().addListener(ignoreGenderListener);
        ignoreRarityButton.selectedProperty().addListener(ignoreRarityListener);
    }

    private void unbindVariables() {
        ignoreRarityButton.selectedProperty().removeListener(ignoreRarityListener);
        ignoreGenderButton.selectedProperty().removeListener(ignoreGenderListener);
        rarityViewSettingsChoiceBox.valueProperty().removeListener(rarityViewSettingsListener);
        genderViewSettingsChoiceBox.valueProperty().removeListener(genderViewSettingsListener);
        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        defaultWeightSpinner.valueProperty().removeListener(defaultWeightListener);
        defaultWeightRecalculateButton.removeEventHandler(ActionEvent.ACTION, defaultWeightRecalculateClickHandler);
    }

    private ItemDrop makeItemDrop(ItemReference ir, ItemSet itemSet) {
        ObservableMap<Integer, Integer> rarityAlterations = itemSet.getAlterRarityMap();
        ObservableMap<Integer, Integer> genderAlterations = itemSet.getAlterGenderMap();
        ObservableMap<Integer, Integer> weightAlterations = itemSet.getAlterItemWeightMap();

        var idr = Objects.isNull(ir) ? new ItemDrop() : new ItemDrop(ir);
        idr.constructBindings();

        ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                new Pair<>(idr.getItemID(), idr.getType()));

        if (Objects.nonNull(itemInfo)) {
            idr.setWeight(weightAlterations.getOrDefault(
                    idr.getItemReferenceID(),
                    itemSet.getDefaultItemWeight()));
            idr.setRarity(Rarity.forType(rarityAlterations.getOrDefault(
                    idr.getItemReferenceID(),
                    itemInfo.rarity())));
            idr.setGender(Gender.forType(genderAlterations.getOrDefault(
                    idr.getItemReferenceID(),
                    itemInfo.gender())));
        }

        return idr;
    }

    private ItemDrop makeItemDrop(ItemReference ir) {
        return makeItemDrop(ir, itemSet.get());
    }

    private void clearItemDrops() {
        originalItemList.clear();
        itemListView.getItems().clear();
    }

    private void populateItemDrops() {
        itemSet.get().getItemReferenceIDs().stream()
                .map(irID -> makeItemDrop(controller.getDrops().getItemReferences().get(irID)))
                .forEach(originalItemList::add);

        itemListView.getItems().addAll(originalItemList.stream()
                .filter(this::itemIncluded)
                .sorted(Comparator.comparingInt(ItemDrop::getWeight).reversed())
                .toList());
    }

    private void refreshListView() {
        itemListView.getItems().clear();
        itemListView.getItems().addAll(originalItemList.stream()
                .filter(this::itemIncluded)
                .sorted(Comparator.comparingInt(ItemDrop::getWeight).reversed())
                .toList());
    }

    private void refreshItemDrops() {
        clearItemDrops();
        populateItemDrops();
    }

    private void handleDefaultWeightChange() {
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Integer> allKeysMap = new LinkedHashMap<>();

        int mode = itemSet.get().getDefaultItemWeight();
        int maxOccurrence = 0;
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();
        var itemReferences = controller.getDrops().getItemReferences();

        for (ItemDrop idr : originalItemList) {
            if (!itemReferences.containsKey(idr.getItemReferenceID()) ||
                    !itemInfoMap.containsKey(new Pair<>(idr.getItemID(), idr.getType())))
                continue;

            int weight = itemSet.get().getAlterItemWeightMap().getOrDefault(
                    idr.getItemReferenceID(),
                    itemSet.get().getDefaultItemWeight());

            allKeysMap.put(idr.getItemReferenceID(), weight);

            if (counts.containsKey(weight))
                counts.put(weight, counts.get(weight) + 1);
            else
                counts.put(weight, 1);

            if (counts.get(weight) > maxOccurrence) {
                maxOccurrence = counts.get(weight);
                mode = weight;
            }
        }

        itemSet.get().setDefaultItemWeight(mode);
        defaultWeightSpinner.getValueFactory().setValue(mode);
        itemSet.get().getAlterItemWeightMap().clear();

        allKeysMap.forEach((key, value) -> {
            if (value != itemSet.get().getDefaultItemWeight())
                itemSet.get().getAlterItemWeightMap().put(key, value);
        });
    }

    public void itemDropAdded(Integer newItemReferenceID) {
        Rarity rarity = rarityViewSettingsChoiceBox.getValue();
        Gender gender = genderViewSettingsChoiceBox.getValue();

        if (!itemSet.get().getItemReferenceIDs().contains(newItemReferenceID)) {
            makeEditable(controller.getDrops());

            unbindVariables();
            clearItemDrops();

            rarityViewSettingsChoiceBox.setValue(rarity);
            genderViewSettingsChoiceBox.setValue(gender);

            itemSet.get().getItemReferenceIDs().add(newItemReferenceID);
            itemSet.get().getItemReferenceIDs().sort(Comparator.naturalOrder());

            populateItemDrops();
            bindVariables();
        }
    }

    public void itemDropRemoved(Integer itemReferenceID) {
        Rarity rarity = rarityViewSettingsChoiceBox.getValue();
        Gender gender = genderViewSettingsChoiceBox.getValue();

        makeEditable(controller.getDrops());

        unbindVariables();
        clearItemDrops();

        rarityViewSettingsChoiceBox.setValue(rarity);
        genderViewSettingsChoiceBox.setValue(gender);

        itemSet.get().getItemReferenceIDs().remove(itemReferenceID);

        populateItemDrops();
        bindVariables();
    }

    public void itemDropChanged(Integer oldItemReferenceID, Integer newItemReferenceID) {
        Rarity rarity = rarityViewSettingsChoiceBox.getValue();
        Gender gender = genderViewSettingsChoiceBox.getValue();

        makeEditable(controller.getDrops());

        unbindVariables();
        clearItemDrops();

        rarityViewSettingsChoiceBox.setValue(rarity);
        genderViewSettingsChoiceBox.setValue(gender);

        int index = itemSet.get().getItemReferenceIDs().indexOf(oldItemReferenceID);
        itemSet.get().getItemReferenceIDs().set(index, newItemReferenceID);
        itemSet.get().getItemReferenceIDs().sort(Comparator.naturalOrder());

        populateItemDrops();
        bindVariables();
    }

    @Override
    public Class<ItemSet> getObservableClass() {
        return ItemSet.class;
    }

    @Override
    public ReadOnlyObjectProperty<ItemSet> getObservable() {
        return itemSet;
    }

    @Override
    public void setObservable(Data data) {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        itemSet.set((ItemSet) data);

        unbindVariables();

        clearItemDrops();
        ignoreRarityButton.setSelected(false);
        ignoreGenderButton.setSelected(false);
        defaultWeightSpinner.getValueFactory().setValue(1);
        rarityViewSettingsChoiceBox.setValue(Rarity.ANY);
        genderViewSettingsChoiceBox.setValue(Gender.ANY);

        if (itemSet.isNotNull().get()) {
            populateItemDrops();

            ignoreRarityButton.setSelected(itemSet.get().getIgnoreRarity());
            ignoreGenderButton.setSelected(itemSet.get().getIgnoreGender());
            defaultWeightSpinner.getValueFactory().setValue(itemSet.get().getDefaultItemWeight());

            bindVariables();
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemReferenceMap = controller.getDrops().getItemReferences();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        ItemDropVBox prototype = new ItemDropVBox(listBoxWidth, listBoxSpacing, controller, this);

        allValues.addAll(getNestedSearchableValues(
                prototype.getSearchableValues(),
                op -> op.map(o -> (ItemSet) o)
                        .map(is -> is.getItemReferenceIDs().stream()
                                .filter(itemReferenceMap::containsKey)
                                .map(itemReferenceMap::get)
                                .map(ir -> makeItemDrop(ir, is))
                                .toList())
                        .orElse(List.of())
        ));

        return allValues;
    }

    public ItemSet getItemSet() {
        return itemSet.get();
    }

    public ReadOnlyObjectProperty<ItemSet> itemSetProperty() {
        return itemSet;
    }

    public double getListBoxWidth() {
        return listBoxWidth;
    }

    public double getListBoxSpacing() {
        return listBoxSpacing;
    }

    public ToggleSwitch getIgnoreRarityButton() {
        return ignoreRarityButton;
    }

    public ToggleSwitch getIgnoreGenderButton() {
        return ignoreGenderButton;
    }

    public VBox getGlobalIgnoreSettingsVBox() {
        return globalIgnoreSettingsVBox;
    }

    public Label getDefaultWeightLabel() {
        return defaultWeightLabel;
    }

    public StandardSpinner getDefaultWeightSpinner() {
        return defaultWeightSpinner;
    }

    public Button getDefaultWeightRecalculateButton() {
        return defaultWeightRecalculateButton;
    }

    public VBox getGlobalWeightSettingsVBox() {
        return globalWeightSettingsVBox;
    }

    public HBox getRarityViewSettingsHBox() {
        return rarityViewSettingsHBox;
    }

    public HBox getGenderViewSettingsHBox() {
        return genderViewSettingsHBox;
    }

    public HBox getSettingsHBox() {
        return settingsHBox;
    }

    public Label getRarityViewSettingsLabel() {
        return rarityViewSettingsLabel;
    }

    public ChoiceBox<Rarity> getRarityViewSettingsChoiceBox() {
        return rarityViewSettingsChoiceBox;
    }

    public Label getGenderViewSettingsLabel() {
        return genderViewSettingsLabel;
    }

    public ChoiceBox<Gender> getGenderViewSettingsChoiceBox() {
        return genderViewSettingsChoiceBox;
    }

    public VBox getViewSettingsVBox() {
        return viewSettingsVBox;
    }

    public ListView<ItemDrop> getItemListView() {
        return itemListView;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    public static class ItemDropVBox extends VBox implements DataComponent {
        private final ObjectProperty<ItemDrop> itemDrop;
        private final ObjectProperty<ItemInfo> itemInfo;

        private final MainController controller;
        private final ItemSetComponent parent;

        private final ImageView iconView;
        private final Label nameLabel;
        private final StandardSpinner spinner;
        private final Label percentageLabel;
        private final Slider percentageSlider;
        private final ChoiceBox<Rarity> rarityChoiceBox;
        private final ChoiceBox<Gender> genderChoiceBox;
        private final VBox contentVBox;
        private final Label idLabel;
        private final Button removeButton;
        private final HBox idHBox;

        private final EventHandler<MouseEvent> idClickHandler;

        public ItemDropVBox(double boxWidth,
                            double boxSpacing,
                            MainController controller,
                            ItemSetComponent parent) {

            itemDrop = new SimpleObjectProperty<>();
            itemInfo = new SimpleObjectProperty<>();

            this.controller = controller;
            this.parent = parent;

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            nameLabel = new Label();
            spinner = new StandardSpinner(0, Integer.MAX_VALUE, 0);

            percentageLabel = new Label();
            percentageSlider = new Slider();
            percentageSlider.setShowTickLabels(false);
            percentageSlider.setMouseTransparent(true);
            percentageSlider.setOrientation(Orientation.VERTICAL);

            rarityChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Rarity.values()));
            rarityChoiceBox.setMinWidth(boxWidth);
            rarityChoiceBox.setMaxWidth(boxWidth);
            genderChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(Gender.values()));
            genderChoiceBox.setMinWidth(boxWidth);
            genderChoiceBox.setMaxWidth(boxWidth);
            contentVBox = new VBox(boxSpacing, percentageSlider, percentageLabel, spinner, iconView, nameLabel,
                    rarityChoiceBox, genderChoiceBox);
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
                    .map(d -> this.parent.makeItemDrop((ItemReference) d))
                    .ifPresent(idr -> {
                        int oldID = getObservable().get().getItemReferenceID();
                        setObservable(idr);
                        this.parent.itemDropChanged(oldID, idr.getItemReferenceID());
                    });

            // both makeEditable and setObservable sets the observable, just use a listener here
            itemDrop.addListener((o, oldVal, newVal) -> {
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
        public Class<ItemDrop> getObservableClass() {
            return ItemDrop.class;
        }

        @Override
        public ReadOnlyObjectProperty<ItemDrop> getObservable() {
            return itemDrop;
        }

        @Override
        public void setObservable(Data data) {
            idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

            itemDrop.set((ItemDrop) data);

            var iconMap = controller.getIconManager().getIconMap();
            byte[] defaultIcon = iconMap.get("unknown");

            nameLabel.setText("<INVALID>");
            iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));
            contentVBox.setDisable(true);

            if (itemDrop.isNotNull().get()) {
                ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                        itemDrop.get().getItemID(),
                        itemDrop.get().getType()
                ));

                if (Objects.nonNull(itemInfo)) {
                    String name = itemInfo.name();
                    byte[] icon = iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);

                    this.itemInfo.set(itemInfo);

                    nameLabel.setText(name);
                    iconView.setImage(new Image(new ByteArrayInputStream(icon)));
                    spinner.getValueFactory().setValue(itemDrop.get().getWeight());
                    rarityChoiceBox.setValue(itemDrop.get().getRarity());
                    genderChoiceBox.setValue(itemDrop.get().getGender());
                    contentVBox.setDisable(false);
                }
            }

            idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
        }

        @Override
        public Set<FilterChoice> getSearchableValues() {
            var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

            Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

            allValues.addAll(ObservableComponent.getSearchableValuesFor(ItemReference.class));
            allValues.addAll(getNestedSearchableValues(
                    ObservableComponent.getSearchableValuesFor(ItemInfo.class),
                    op -> op.map(o -> (ItemReference) o)
                            .map(ir -> itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType())))
                            .stream().toList()
            ));

            return allValues;
        }

        public ItemDrop getItemDrop() {
            return itemDrop.get();
        }

        public ReadOnlyObjectProperty<ItemDrop> itemDropProperty() {
            return itemDrop;
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

        public StandardSpinner getSpinner() {
            return spinner;
        }

        public Label getPercentageLabel() {
            return percentageLabel;
        }

        public Slider getPercentageSlider() {
            return percentageSlider;
        }

        public ChoiceBox<Rarity> getRarityChoiceBox() {
            return rarityChoiceBox;
        }

        public ChoiceBox<Gender> getGenderChoiceBox() {
            return genderChoiceBox;
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

        @Override
        public DataComponent getParentComponent() {
            return parent;
        }

        @Override
        public Label getIdLabel() {
            return idLabel;
        }
    }
}
