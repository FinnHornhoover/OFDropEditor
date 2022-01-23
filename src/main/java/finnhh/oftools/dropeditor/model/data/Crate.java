package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Crate extends Data {
    @Expose
    private final IntegerProperty crateID;
    @Expose
    private final IntegerProperty itemSetID;
    @Expose
    private final IntegerProperty rarityWeightID;

    public Crate() {
        crateID = new SimpleIntegerProperty(-1);
        itemSetID = new SimpleIntegerProperty(-1);
        rarityWeightID = new SimpleIntegerProperty(-1);
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
    public void constructBindings() {
        malformed.bind(crateID.lessThan(0)
                .or(itemSetID.lessThan(0))
                .or(rarityWeightID.lessThan(0)));

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
                && this.crateID.equals(((Crate) obj).crateID)
                && this.itemSetID.equals(((Crate) obj).itemSetID)
                && this.rarityWeightID.equals(((Crate) obj).rarityWeightID);
    }
}
