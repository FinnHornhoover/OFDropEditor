package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

@IdMeaningful
public class Crate extends Data {
    public static final int INT_CRATE_PLACEHOLDER_ID = 0;

    @Expose
    private final IntegerProperty crateID;
    @Expose
    private final IntegerProperty itemSetID;
    @Expose
    private final IntegerProperty rarityWeightID;

    public Crate() {
        crateID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        itemSetID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        rarityWeightID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
    }

    public Crate(Crate other) {
        this.crateID = new SimpleIntegerProperty(other.crateID.get());
        this.itemSetID = new SimpleIntegerProperty(other.itemSetID.get());
        this.rarityWeightID = new SimpleIntegerProperty(other.rarityWeightID.get());
    }

    @Override
    public Crate getEditableClone() {
        return new Crate(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        Crate other = (Crate) data;
        this.crateID.set(other.crateID.get());
        this.itemSetID.set(other.itemSetID.get());
        this.rarityWeightID.set(other.rarityWeightID.get());
    }

    @Override
    public void setChildData(Data data) {
        if (data instanceof ItemSet itemSet)
            itemSetID.set(itemSet.getItemSetID());
        else if (data instanceof RarityWeights rarityWeights)
            rarityWeightID.set(rarityWeights.getRarityWeightID());
    }

    @Override
    public void constructBindings() {
        malformed.bind(crateID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(itemSetID.lessThanOrEqualTo(INT_PLACEHOLDER_ID))
                .or(rarityWeightID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)));

        id.set(String.valueOf(crateID.get()));
        crateID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> crateID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerReferences(itemSetID, drops.getItemSets(), drops.getReferenceMap());
        registerIntegerReferences(rarityWeightID, drops.getRarityWeights(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerReferences(itemSetID, drops.getItemSets(), drops.getReferenceMap());
        unregisterIntegerReferences(rarityWeightID, drops.getRarityWeights(), drops.getReferenceMap());
    }

    public int getCrateID() {
        return crateID.get();
    }

    public IntegerProperty crateIDProperty() {
        return crateID;
    }

    public void setCrateID(int crateID) {
        this.crateID.set(crateID);
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

    public int getRarityWeightID() {
        return rarityWeightID.get();
    }

    public IntegerProperty rarityWeightIDProperty() {
        return rarityWeightID;
    }

    public void setRarityWeightID(int rarityWeightID) {
        this.rarityWeightID.set(rarityWeightID);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Crate
                && this.crateID.get() == ((Crate) obj).crateID.get()
                && this.itemSetID.get() == ((Crate) obj).itemSetID.get()
                && this.rarityWeightID.get() == ((Crate) obj).rarityWeightID.get();
    }
}
