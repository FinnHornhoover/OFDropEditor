package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.Gender;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.Rarity;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import finnhh.oftools.dropeditor.model.data.ItemSet;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.stream.IntStream;

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
    private final Spinner<Integer> defaultWeightSpinner;
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

    private final HBox listHBox;
    private final ScrollPane listScrollPane;
    private final VBox contentVBox;
    private final Label idLabel;

    private final List<ItemDropVBox> itemDropVBoxCache;
    private final ObservableList<Node> originalOrderList;

    private final EventHandler<ActionEvent> defaultWeightRecalculateClickHandler;
    private final ChangeListener<Integer> defaultWeightListener;
    private final ChangeListener<Boolean> ignoreRarityListener;
    private final ChangeListener<Boolean> ignoreGenderListener;
    private final ChangeListener<Rarity> rarityViewSettingsListener;
    private final ChangeListener<Gender> genderViewSettingsListener;
    private final EventHandler<MouseEvent> addClickHandler;
    private final List<ChangeListener<Integer>> valueListeners;
    private final List<EventHandler<MouseEvent>> removeClickHandlers;
    private final List<ChangeListener<Rarity>> rarityListeners;
    private final List<ChangeListener<Gender>> genderListeners;
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
        defaultWeightSpinner = new Spinner<>(0, Integer.MAX_VALUE, 1);
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

        listHBox = new HBox(listBoxSpacing);
        listHBox.setAlignment(Pos.CENTER);

        listScrollPane = new ScrollPane(listHBox);
        listScrollPane.setFitToHeight(true);
        listScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        contentVBox = new VBox(settingsHBox, listScrollPane);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentVBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        itemDropVBoxCache = new ArrayList<>();
        originalOrderList = FXCollections.observableArrayList();

        defaultWeightRecalculateClickHandler = event -> {
            unbindVariables();
            handleDefaultWeightChange();
            bindVariables();
        };
        defaultWeightListener = (o, oldVal, newVal) -> {
            Rarity rarity = rarityViewSettingsChoiceBox.getValue();
            Gender gender = genderViewSettingsChoiceBox.getValue();
            double hValue = listScrollPane.getHvalue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setDefaultItemWeight(newVal);
            handleDefaultWeightChange();
            rarityViewSettingsChoiceBox.setValue(rarity);
            genderViewSettingsChoiceBox.setValue(gender);
            listScrollPane.setHvalue(hValue);

            bindVariables();
        };
        ignoreRarityListener = (o, oldVal, newVal) -> {
            Gender gender = genderViewSettingsChoiceBox.getValue();
            double hValue = listScrollPane.getHvalue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setIgnoreRarity(newVal);
            ignoreRarityButton.setSelected(newVal);
            rarityViewSettingsChoiceBox.setValue(Rarity.ANY);
            rarityViewSettingsChoiceBox.setDisable(newVal);
            genderViewSettingsChoiceBox.setValue(gender);
            listScrollPane.setHvalue(hValue);

            bindVariables();
        };
        ignoreGenderListener = (o, oldVal, newVal) -> {
            Rarity rarity = rarityViewSettingsChoiceBox.getValue();
            double hValue = listScrollPane.getHvalue();

            makeEditable(this.controller.getDrops());

            unbindVariables();

            itemSet.get().setIgnoreGender(newVal);
            ignoreGenderButton.setSelected(newVal);
            rarityViewSettingsChoiceBox.setValue(rarity);
            genderViewSettingsChoiceBox.setValue(Gender.ANY);
            genderViewSettingsChoiceBox.setDisable(newVal);
            listScrollPane.setHvalue(hValue);

            bindVariables();
        };
        rarityViewSettingsListener = (o, oldVal, newVal) -> {
            unbindVariables();
            bindVariables();
        };
        genderViewSettingsListener = (o, oldVal, newVal) -> {
            unbindVariables();
            bindVariables();
        };
        addClickHandler = event -> itemDropAdded();
        valueListeners = new ArrayList<>();
        removeClickHandlers = new ArrayList<>();
        rarityListeners = new ArrayList<>();
        genderListeners = new ArrayList<>();

        idLabel.setText(ItemSet.class.getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        // TODO: does not save
        idClickHandler = event -> this.controller.showSelectionMenuForResult(ItemSet.class)
                .ifPresent(this::setObservable);

        // both makeEditable and setObservable sets the observable, just use a listener here
        itemSet.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(ItemSet.class.getSimpleName() + ": null");
                contentVBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentVBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private boolean boxIncluded(ItemDropVBox idvb) {
        ItemReference ir = idvb.getItemReference();

        if (Objects.isNull(ir))
            return false;

        ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                new Pair<>(ir.getItemID(), ir.getType()));

        if (Objects.isNull(itemInfo))
            return false;

        Rarity filterRarity = rarityViewSettingsChoiceBox.getValue();
        Gender filterGender = genderViewSettingsChoiceBox.getValue();
        int rarityType = itemSet.get().getAlterRarityMap().getOrDefault(
                ir.getItemReferenceID(), itemInfo.rarity());
        int genderType = itemSet.get().getAlterGenderMap().getOrDefault(
                ir.getItemReferenceID(), itemInfo.gender());

        return (itemSet.get().getIgnoreRarity() || Rarity.forType(rarityType).match(filterRarity))
                && (itemSet.get().getIgnoreGender() || Gender.forType(genderType).match(filterGender));
    }

    private void bindListVariables() {
        DoubleExpression totalExpression = originalOrderList.stream()
                .filter(c -> c instanceof ItemDropVBox)
                .map(c -> (ItemDropVBox) c)
                .filter(this::boxIncluded)
                .map(idvb -> DoubleBinding.doubleExpression(idvb.getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var childrenCopy = new ArrayList<>(originalOrderList);
        childrenCopy.sort(Comparator.comparingDouble(c -> {
            if (c instanceof ItemDropVBox idvb) {
                if (Objects.isNull(idvb.getItemReference()))
                    return Double.MAX_VALUE;

                if (boxIncluded(idvb))
                    return idvb.getSpinner().getValue();

                return 0.0;
            }
            return -1.0;
        }).reversed());
        listHBox.getChildren().addAll(childrenCopy);

        var children = originalOrderList.filtered(c -> c instanceof ItemDropVBox);

        for (int index = 0; index < children.size(); index++) {
            ItemDropVBox idvb = (ItemDropVBox) children.get(index);

            DoubleExpression percentExpression = boxIncluded(idvb) ?
                    DoubleBinding.doubleExpression(idvb.getSpinner().valueProperty())
                            .multiply(100.0)
                            .divide(Bindings.max(1.0, totalExpression)) :
                    Bindings.createDoubleBinding(() -> 0.0);

            idvb.getPercentageSlider().valueProperty().bind(percentExpression);
            idvb.getPercentageLabel().textProperty().bind(percentExpression.asString(Locale.US, "%.5f%%"));

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                Rarity rarity = rarityViewSettingsChoiceBox.getValue();
                Gender gender = genderViewSettingsChoiceBox.getValue();
                double hValue = listScrollPane.getHvalue();

                makeEditable(controller.getDrops());

                unbindVariables();

                ItemDropVBox current = (ItemDropVBox) originalOrderList
                        .filtered(c -> c instanceof ItemDropVBox)
                        .get(finalIndex);

                int itemReferenceID = current.getItemReference().getItemReferenceID();
                int defaultWeight = itemSet.get().getDefaultItemWeight();
                var alterWeightMap = itemSet.get().getAlterItemWeightMap();

                alterWeightMap.remove(itemReferenceID);
                if (newVal != defaultWeight)
                    alterWeightMap.put(itemReferenceID, newVal);

                current.getSpinner().getValueFactory().setValue(newVal);

                rarityViewSettingsChoiceBox.setValue(rarity);
                genderViewSettingsChoiceBox.setValue(gender);
                listScrollPane.setHvalue(hValue);

                bindVariables();
            });
            removeClickHandlers.add(event -> itemDropRemoved(finalIndex));
            rarityListeners.add((o, oldVal, newVal) -> {
                Rarity rarity = rarityViewSettingsChoiceBox.getValue();
                Gender gender = genderViewSettingsChoiceBox.getValue();
                double hValue = listScrollPane.getHvalue();

                makeEditable(controller.getDrops());

                unbindVariables();

                ItemDropVBox current = (ItemDropVBox) originalOrderList
                        .filtered(c -> c instanceof ItemDropVBox)
                        .get(finalIndex);

                ItemReference itemReference = current.getItemReference();
                ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                        new Pair<>(itemReference.getItemID(), itemReference.getType()));

                var alterRarityMap = itemSet.get().getAlterRarityMap();

                alterRarityMap.remove(itemReference.getItemReferenceID());
                if (Objects.isNull(itemInfo) || newVal != Rarity.forType(itemInfo.rarity()))
                    alterRarityMap.put(itemReference.getItemReferenceID(), newVal.getType());

                current.getRarityChoiceBox().setValue(newVal);

                rarityViewSettingsChoiceBox.setValue(rarity);
                genderViewSettingsChoiceBox.setValue(gender);
                listScrollPane.setHvalue(hValue);

                bindVariables();
            });
            genderListeners.add((o, oldVal, newVal) -> {
                Rarity rarity = rarityViewSettingsChoiceBox.getValue();
                Gender gender = genderViewSettingsChoiceBox.getValue();
                double hValue = listScrollPane.getHvalue();

                makeEditable(controller.getDrops());

                unbindVariables();

                ItemDropVBox current = (ItemDropVBox) originalOrderList
                        .filtered(c -> c instanceof ItemDropVBox)
                        .get(finalIndex);

                ItemReference itemReference = current.getItemReference();
                ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(
                        new Pair<>(itemReference.getItemID(), itemReference.getType()));

                var alterGenderMap = itemSet.get().getAlterGenderMap();

                alterGenderMap.remove(itemReference.getItemReferenceID());
                if (Objects.isNull(itemInfo) || newVal != Gender.forType(itemInfo.rarity()))
                    alterGenderMap.put(itemReference.getItemReferenceID(), newVal.getType());

                current.getGenderChoiceBox().setValue(newVal);

                rarityViewSettingsChoiceBox.setValue(rarity);
                genderViewSettingsChoiceBox.setValue(gender);
                listScrollPane.setHvalue(hValue);

                bindVariables();
            });

            idvb.getSpinner().valueProperty().addListener(valueListeners.get(index));
            idvb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            idvb.getRarityChoiceBox().valueProperty().addListener(rarityListeners.get(index));
            idvb.getGenderChoiceBox().valueProperty().addListener(genderListeners.get(index));
        }
    }

    private void unbindListVariables() {
        var children = originalOrderList.filtered(c -> c instanceof ItemDropVBox);

        for (int index = 0; index < children.size(); index++) {
            ItemDropVBox idvb = (ItemDropVBox) children.get(index);

            idvb.getPercentageSlider().valueProperty().unbind();
            idvb.getPercentageLabel().textProperty().unbind();

            idvb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
            idvb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            idvb.getRarityChoiceBox().valueProperty().removeListener(rarityListeners.get(index));
            idvb.getGenderChoiceBox().valueProperty().removeListener(genderListeners.get(index));
        }

        valueListeners.clear();
        removeClickHandlers.clear();
        rarityListeners.clear();
        genderListeners.clear();
        listHBox.getChildren().clear();
    }

    private void bindVariables() {
        defaultWeightRecalculateButton.addEventHandler(ActionEvent.ACTION, defaultWeightRecalculateClickHandler);
        defaultWeightSpinner.valueProperty().addListener(defaultWeightListener);
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        genderViewSettingsChoiceBox.valueProperty().addListener(genderViewSettingsListener);
        rarityViewSettingsChoiceBox.valueProperty().addListener(rarityViewSettingsListener);
        ignoreGenderButton.selectedProperty().addListener(ignoreGenderListener);
        ignoreRarityButton.selectedProperty().addListener(ignoreRarityListener);
        bindListVariables();
    }

    private void unbindVariables() {
        unbindListVariables();
        ignoreRarityButton.selectedProperty().removeListener(ignoreRarityListener);
        ignoreGenderButton.selectedProperty().removeListener(ignoreGenderListener);
        rarityViewSettingsChoiceBox.valueProperty().removeListener(rarityViewSettingsListener);
        genderViewSettingsChoiceBox.valueProperty().removeListener(genderViewSettingsListener);
        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        defaultWeightSpinner.valueProperty().removeListener(defaultWeightListener);
        defaultWeightRecalculateButton.removeEventHandler(ActionEvent.ACTION, defaultWeightRecalculateClickHandler);
    }

    private void populateListBox() {
        var itemReferenceIDs = itemSet.get().getItemReferenceIDs();

        if (itemDropVBoxCache.size() < itemReferenceIDs.size()) {
            IntStream.range(0, itemReferenceIDs.size() - itemDropVBoxCache.size())
                    .mapToObj(i -> new ItemDropVBox(listBoxWidth, listBoxSpacing, controller, this))
                    .forEach(itemDropVBoxCache::add);
        }

        IntStream.range(0, itemReferenceIDs.size())
                .mapToObj(i -> {
                    ItemDropVBox idvb = itemDropVBoxCache.get(i);
                    idvb.setObservable(controller.getDrops().getItemReferences().get(itemReferenceIDs.get(i)));
                    return idvb;
                })
                .forEach(originalOrderList::add);
    }

    private void handleDefaultWeightChange() {
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Integer> allKeysMap = new LinkedHashMap<>();

        int mode = itemSet.get().getDefaultItemWeight();
        int maxOccurrence = 0;
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        for (Node c : originalOrderList.filtered(c -> c instanceof ItemDropVBox)) {
            ItemDropVBox idvb = (ItemDropVBox) c;
            ItemReference uncheckedIR = idvb.getItemReference();

            if (Objects.isNull(uncheckedIR))
                continue;

            ItemReference ir = controller.getDrops().getItemReferences().get(uncheckedIR.getItemReferenceID());

            if (Objects.isNull(ir) || !itemInfoMap.containsKey(new Pair<>(ir.getItemID(), ir.getType())))
                continue;

            int weight = itemSet.get().getAlterItemWeightMap().getOrDefault(
                    ir.getItemReferenceID(),
                    itemSet.get().getDefaultItemWeight());

            allKeysMap.put(ir.getItemReferenceID(), weight);
            idvb.getSpinner().getValueFactory().setValue(weight);

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

    public void itemDropAdded() {
        Rarity rarity = rarityViewSettingsChoiceBox.getValue();
        Gender gender = genderViewSettingsChoiceBox.getValue();
        double hValue = listScrollPane.getHvalue();

        makeEditable(controller.getDrops());

        unbindVariables();
        originalOrderList.clear();

        rarityViewSettingsChoiceBox.setValue(rarity);
        genderViewSettingsChoiceBox.setValue(gender);
        listScrollPane.setHvalue(hValue);

        itemSet.get().getItemReferenceIDs().add(-1);
        itemSet.get().getItemReferenceIDs().sort(Comparator.naturalOrder());

        populateListBox();
        bindVariables();
    }

    public void itemDropRemoved(int index) {
        Rarity rarity = rarityViewSettingsChoiceBox.getValue();
        Gender gender = genderViewSettingsChoiceBox.getValue();
        double hValue = listScrollPane.getHvalue();

        makeEditable(controller.getDrops());

        unbindVariables();
        originalOrderList.clear();

        rarityViewSettingsChoiceBox.setValue(rarity);
        genderViewSettingsChoiceBox.setValue(gender);
        listScrollPane.setHvalue(hValue);

        itemSet.get().getItemReferenceIDs().remove(index);

        populateListBox();
        bindVariables();
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

        originalOrderList.clear();
        ignoreRarityButton.setSelected(false);
        ignoreGenderButton.setSelected(false);
        defaultWeightSpinner.getValueFactory().setValue(1);
        rarityViewSettingsChoiceBox.setValue(Rarity.ANY);
        genderViewSettingsChoiceBox.setValue(Gender.ANY);
        listScrollPane.setHvalue(0);

        if (itemSet.isNotNull().get()) {
            populateListBox();

            ignoreRarityButton.setSelected(itemSet.get().getIgnoreRarity());
            ignoreGenderButton.setSelected(itemSet.get().getIgnoreGender());
            defaultWeightSpinner.getValueFactory().setValue(itemSet.get().getDefaultItemWeight());

            bindVariables();
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
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

    public Spinner<Integer> getDefaultWeightSpinner() {
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

    public HBox getListHBox() {
        return listHBox;
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
        private final ObjectProperty<ItemReference> itemReference;
        private final ObjectProperty<ItemInfo> itemInfo;
        private final ObjectProperty<byte[]> icon;

        private final MainController controller;
        private final ItemSetComponent parent;

        private final ImageView iconView;
        private final Label nameLabel;
        private final Spinner<Integer> spinner;
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

            itemReference = new SimpleObjectProperty<>();
            itemInfo = new SimpleObjectProperty<>();
            icon = new SimpleObjectProperty<>();

            this.controller = controller;
            this.parent = parent;

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            nameLabel = new Label();
            spinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
            spinner.setEditable(true);
            spinner.getEditor().setOnAction(event -> {
                try {
                    Integer.parseInt(spinner.getEditor().getText());
                    spinner.commitValue();
                } catch (NumberFormatException e) {
                    spinner.cancelEdit();
                }
            });

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
            removeButton.getStyleClass().add("remove-button");

            idHBox = new HBox(2, idLabel, removeButton);

            setSpacing(boxSpacing);
            getChildren().addAll(contentVBox, idHBox);

            idLabel.setText(ItemReference.class.getSimpleName() + ": null");
            contentVBox.setDisable(true);
            setIdDisable(true);

            // TODO: runs out of heap space
            idClickHandler = event -> this.controller.showSelectionMenuForResult(ItemReference.class)
                    .ifPresent(this::setObservable);

            // both makeEditable and setObservable sets the observable, just use a listener here
            itemReference.addListener((o, oldVal, newVal) -> {
                if (Objects.isNull(newVal)) {
                    idLabel.setText(ItemReference.class.getSimpleName() + ": null");
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
                    ObservableMap<Integer, Integer> rarityAlterations = parent.getItemSet().getAlterRarityMap();
                    ObservableMap<Integer, Integer> genderAlterations = parent.getItemSet().getAlterGenderMap();
                    ObservableMap<Integer, Integer> weightAlterations = parent.getItemSet().getAlterItemWeightMap();

                    String name = itemInfo.name();
                    byte[] icon = iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);

                    this.itemInfo.set(itemInfo);
                    this.icon.set(icon);

                    nameLabel.setText(name);
                    iconView.setImage(new Image(new ByteArrayInputStream(icon)));
                    spinner.getValueFactory().setValue(weightAlterations.getOrDefault(
                            itemReference.get().getItemReferenceID(),
                            parent.getItemSet().getDefaultItemWeight()));
                    rarityChoiceBox.setValue(Rarity.forType(rarityAlterations.getOrDefault(
                            itemReference.get().getItemReferenceID(),
                            itemInfo.rarity())));
                    genderChoiceBox.setValue(Gender.forType(genderAlterations.getOrDefault(
                            itemReference.get().getItemReferenceID(),
                            itemInfo.gender())));
                    contentVBox.setDisable(false);
                }
            }

            idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
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

        public byte[] getIcon() {
            return icon.get();
        }

        public ReadOnlyObjectProperty<byte[]> iconProperty() {
            return icon;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public Spinner<Integer> getSpinner() {
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
