package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class ItemSet extends Data {
    @Expose
    private final IntegerProperty itemSetID;
    @Expose
    private final BooleanProperty ignoreRarity;
    @Expose
    private final BooleanProperty ignoreGender;
    @Expose
    private final IntegerProperty defaultItemWeight;
    @Expose
    private final MapProperty<Integer, Integer> alterRarityMap;
    @Expose
    private final MapProperty<Integer, Integer> alterGenderMap;
    @Expose
    private final MapProperty<Integer, Integer> alterItemWeightMap;
    @Expose
    private final ListProperty<Integer> itemReferenceIDs;

    public ItemSet() {
        itemSetID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        ignoreRarity = new SimpleBooleanProperty(false);
        ignoreGender = new SimpleBooleanProperty(false);
        defaultItemWeight = new SimpleIntegerProperty(1);
        alterRarityMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
        alterGenderMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
        alterItemWeightMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
        itemReferenceIDs = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public ItemSet(ItemSet other) {
        this.itemSetID = new SimpleIntegerProperty(other.itemSetID.get());
        this.ignoreRarity = new SimpleBooleanProperty(other.ignoreRarity.get());
        this.ignoreGender = new SimpleBooleanProperty(other.ignoreGender.get());
        this.defaultItemWeight = new SimpleIntegerProperty(other.defaultItemWeight.get());
        this.alterRarityMap = new SimpleMapProperty<>(
                FXCollections.observableMap(new LinkedHashMap<>(other.alterRarityMap.get())));
        this.alterGenderMap = new SimpleMapProperty<>(
                FXCollections.observableMap(new LinkedHashMap<>(other.alterGenderMap.get())));
        this.alterItemWeightMap = new SimpleMapProperty<>(
                FXCollections.observableMap(new LinkedHashMap<>(other.alterItemWeightMap.get())));
        this.itemReferenceIDs = new SimpleListProperty<>(
                FXCollections.observableArrayList(other.itemReferenceIDs.get()));
    }

    @Override
    public ItemSet getEditableClone() {
        return new ItemSet(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        ItemSet other = (ItemSet) data;
        this.itemSetID.set(other.itemSetID.get());
        this.ignoreRarity.set(other.ignoreRarity.get());
        this.ignoreGender.set(other.ignoreGender.get());
        this.defaultItemWeight.set(other.defaultItemWeight.get());
        this.alterRarityMap.set(FXCollections.observableMap(new LinkedHashMap<>(other.alterRarityMap.get())));
        this.alterGenderMap.set(FXCollections.observableMap(new LinkedHashMap<>(other.alterGenderMap.get())));
        this.alterItemWeightMap.set(FXCollections.observableMap(new LinkedHashMap<>(other.alterItemWeightMap.get())));
        this.itemReferenceIDs.set(FXCollections.observableArrayList(other.itemReferenceIDs.get()));
    }

    @Override
    public void constructBindings() {
        malformed.bind(itemSetID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(defaultItemWeight.lessThan(0))
                .or(alterRarityMap.isNull())
                .or(alterGenderMap.isNull())
                .or(alterItemWeightMap.isNull())
                .or(itemReferenceIDs.isNull())
                .or(itemReferenceIDs.emptyProperty()));

        id.set(String.valueOf(itemSetID.get()));
        itemSetID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> itemSetID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerListReferences(itemReferenceIDs, drops.getItemReferences(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerListReferences(itemReferenceIDs, drops.getItemReferences(), drops.getReferenceMap());
    }

    public int getItemSetID() {
        return itemSetID.get();
    }

    public IntegerProperty itemSetIDProperty() {
        return itemSetID;
    }

    public void setItemSetID(int itemSetID) {
        this.itemSetID.set(itemSetID);
    }

    public boolean getIgnoreRarity() {
        return ignoreRarity.get();
    }

    public BooleanProperty ignoreRarityProperty() {
        return ignoreRarity;
    }

    public void setIgnoreRarity(boolean ignoreRarity) {
        this.ignoreRarity.set(ignoreRarity);
    }

    public boolean getIgnoreGender() {
        return ignoreGender.get();
    }

    public BooleanProperty ignoreGenderProperty() {
        return ignoreGender;
    }

    public void setIgnoreGender(boolean ignoreGender) {
        this.ignoreGender.set(ignoreGender);
    }

    public int getDefaultItemWeight() {
        return defaultItemWeight.get();
    }

    public IntegerProperty defaultItemWeightProperty() {
        return defaultItemWeight;
    }

    public void setDefaultItemWeight(int defaultItemWeight) {
        this.defaultItemWeight.set(defaultItemWeight);
    }

    public void recalculateDefaultItemWeight() {
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Integer, Integer> allKeysMap = new LinkedHashMap<>();

        int mode = defaultItemWeight.get();
        int maxOccurrence = 0;

        for (int itemReferenceID : itemReferenceIDs) {
            int weight = alterItemWeightMap.get().getOrDefault(itemReferenceID, defaultItemWeight.get());

            allKeysMap.put(itemReferenceID, weight);
            counts.put(weight, counts.getOrDefault(weight, 0) + 1);

            if (counts.get(weight) > maxOccurrence) {
                maxOccurrence = counts.get(weight);
                mode = weight;
            }
        }

        defaultItemWeight.set(mode);
        alterItemWeightMap.clear();
        allKeysMap.forEach((key, value) -> {
            if (value != defaultItemWeight.get())
                alterItemWeightMap.put(key, value);
        });
    }

    public ObservableMap<Integer, Integer> getAlterRarityMap() {
        return alterRarityMap.get();
    }

    public MapProperty<Integer, Integer> alterRarityMapProperty() {
        return alterRarityMap;
    }

    public void setAlterRarityMap(ObservableMap<Integer, Integer> alterRarityMap) {
        this.alterRarityMap.set(alterRarityMap);
    }

    public ObservableMap<Integer, Integer> getAlterGenderMap() {
        return alterGenderMap.get();
    }

    public MapProperty<Integer, Integer> alterGenderMapProperty() {
        return alterGenderMap;
    }

    public void setAlterGenderMap(ObservableMap<Integer, Integer> alterGenderMap) {
        this.alterGenderMap.set(alterGenderMap);
    }

    public ObservableMap<Integer, Integer> getAlterItemWeightMap() {
        return alterItemWeightMap.get();
    }

    public MapProperty<Integer, Integer> alterItemWeightMapProperty() {
        return alterItemWeightMap;
    }

    public void setAlterItemWeightMap(ObservableMap<Integer, Integer> alterItemWeightMap) {
        this.alterItemWeightMap.set(alterItemWeightMap);
    }

    public ObservableList<Integer> getItemReferenceIDs() {
        return itemReferenceIDs.get();
    }

    public ListProperty<Integer> itemReferenceIDsProperty() {
        return itemReferenceIDs;
    }

    public void setItemReferenceIDs(ObservableList<Integer> itemReferenceIDs) {
        this.itemReferenceIDs.set(itemReferenceIDs);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemSet
                && this.itemSetID.get() == ((ItemSet) obj).itemSetID.get()
                && this.ignoreRarity.get() == ((ItemSet) obj).ignoreRarity.get()
                && this.ignoreGender.get() == ((ItemSet) obj).ignoreGender.get()
                && this.defaultItemWeight.get() == ((ItemSet) obj).defaultItemWeight.get()
                && this.alterRarityMap.equals(((ItemSet) obj).alterRarityMap)
                && this.alterGenderMap.equals(((ItemSet) obj).alterGenderMap)
                && this.alterItemWeightMap.equals(((ItemSet) obj).alterItemWeightMap)
                && this.itemReferenceIDs.equals(((ItemSet) obj).itemReferenceIDs);
    }
}
