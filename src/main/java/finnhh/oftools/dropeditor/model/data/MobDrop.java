package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MobDrop extends Data {
    @Expose
    private final IntegerProperty mobDropID;
    @Expose
    private final IntegerProperty crateDropChanceID;
    @Expose
    private final IntegerProperty crateDropTypeID;
    @Expose
    private final IntegerProperty miscDropChanceID;
    @Expose
    private final IntegerProperty miscDropTypeID;

    public MobDrop() {
        mobDropID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        crateDropChanceID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        crateDropTypeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        miscDropChanceID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        miscDropTypeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
    }

    public MobDrop(MobDrop other) {
        this.mobDropID = new SimpleIntegerProperty(other.mobDropID.get());
        this.crateDropChanceID = new SimpleIntegerProperty(other.crateDropChanceID.get());
        this.crateDropTypeID = new SimpleIntegerProperty(other.crateDropTypeID.get());
        this.miscDropChanceID = new SimpleIntegerProperty(other.miscDropChanceID.get());
        this.miscDropTypeID = new SimpleIntegerProperty(other.miscDropTypeID.get());
    }

    @Override
    public MobDrop getEditableClone() {
        return new MobDrop(this);
    }

    @Override
    public void constructBindings() {
        malformed.bind(mobDropID.lessThan(0)
                .or(crateDropChanceID.lessThan(0))
                .or(crateDropTypeID.lessThan(0))
                .or(miscDropChanceID.lessThan(0))
                .or(miscDropTypeID.lessThan(0)));

        id.set(String.valueOf(mobDropID.get()));
        mobDropID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> mobDropID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerReferences(crateDropChanceID, drops.getCrateDropChances(), drops.getReferenceMap());
        registerIntegerReferences(crateDropTypeID, drops.getCrateDropTypes(), drops.getReferenceMap());
        registerIntegerReferences(miscDropChanceID, drops.getMiscDropChances(), drops.getReferenceMap());
        registerIntegerReferences(miscDropTypeID, drops.getMiscDropTypes(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerReferences(crateDropChanceID, drops.getCrateDropChances(), drops.getReferenceMap());
        unregisterIntegerReferences(crateDropTypeID, drops.getCrateDropTypes(), drops.getReferenceMap());
        unregisterIntegerReferences(miscDropChanceID, drops.getMiscDropChances(), drops.getReferenceMap());
        unregisterIntegerReferences(miscDropTypeID, drops.getMiscDropTypes(), drops.getReferenceMap());
    }

    public int getMobDropID() {
        return mobDropID.get();
    }

    public IntegerProperty mobDropIDProperty() {
        return mobDropID;
    }

    public void setMobDropID(int mobDropID) {
        this.mobDropID.set(mobDropID);
    }

    public int getCrateDropChanceID() {
        return crateDropChanceID.get();
    }

    public IntegerProperty crateDropChanceIDProperty() {
        return crateDropChanceID;
    }

    public void setCrateDropChanceID(int crateDropChanceID) {
        this.crateDropChanceID.set(crateDropChanceID);
    }

    public int getCrateDropTypeID() {
        return crateDropTypeID.get();
    }

    public IntegerProperty crateDropTypeIDProperty() {
        return crateDropTypeID;
    }

    public void setCrateDropTypeID(int crateDropTypeID) {
        this.crateDropTypeID.set(crateDropTypeID);
    }

    public int getMiscDropChanceID() {
        return miscDropChanceID.get();
    }

    public IntegerProperty miscDropChanceIDProperty() {
        return miscDropChanceID;
    }

    public void setMiscDropChanceID(int miscDropChanceID) {
        this.miscDropChanceID.set(miscDropChanceID);
    }

    public int getMiscDropTypeID() {
        return miscDropTypeID.get();
    }

    public IntegerProperty miscDropTypeIDProperty() {
        return miscDropTypeID;
    }

    public void setMiscDropTypeID(int miscDropTypeID) {
        this.miscDropTypeID.set(miscDropTypeID);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobDrop
                && this.mobDropID.equals(((MobDrop) obj).mobDropID)
                && this.crateDropChanceID.equals(((MobDrop) obj).crateDropChanceID)
                && this.crateDropTypeID.equals(((MobDrop) obj).crateDropTypeID)
                && this.miscDropChanceID.equals(((MobDrop) obj).miscDropChanceID)
                && this.miscDropTypeID.equals(((MobDrop) obj).miscDropTypeID);
    }
}
