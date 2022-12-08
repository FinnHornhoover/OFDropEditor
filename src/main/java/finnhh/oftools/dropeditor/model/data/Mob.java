package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

@IdMeaningful
public class Mob extends Data {
    @Expose
    private final IntegerProperty mobID;
    @Expose
    private final IntegerProperty mobDropID;

    public Mob() {
        mobID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        mobDropID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
    }

    public Mob(Mob other) {
        this.mobID = new SimpleIntegerProperty(other.mobID.get());
        this.mobDropID = new SimpleIntegerProperty(other.mobDropID.get());
    }

    @Override
    public Mob getEditableClone() {
        return new Mob(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        Mob other = (Mob) data;
        this.mobID.set(other.mobID.get());
        this.mobDropID.set(other.mobDropID.get());
    }

    @Override
    public void setChildData(Data data) {
        if (data instanceof MobDrop mobDrop)
            mobDropID.set(mobDrop.getMobDropID());
    }

    @Override
    public void constructBindings() {
        malformed.bind(mobID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(mobDropID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)));

        id.set(String.valueOf(mobID.get()));
        mobID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> mobID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerReferences(mobDropID, drops.getMobDrops(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerReferences(mobDropID, drops.getMobDrops(), drops.getReferenceMap());
    }

    public int getMobID() {
        return mobID.get();
    }

    public IntegerProperty mobIDProperty() {
        return mobID;
    }

    public void setMobID(int mobID) {
        this.mobID.set(mobID);
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Mob
                && this.mobID.get() == ((Mob) obj).mobID.get()
                && this.mobDropID.get() == ((Mob) obj).mobDropID.get();
    }
}
