package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Mob extends Data {
    @Expose
    private final IntegerProperty mobID;
    @Expose
    private final IntegerProperty mobDropID;

    public Mob() {
        mobID = new SimpleIntegerProperty(-1);
        mobDropID = new SimpleIntegerProperty(-1);
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
    public void constructBindings() {
        malformed.bind(mobID.lessThan(0)
                .or(mobDropID.lessThan(0)));

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
                && this.mobID.equals(((Mob) obj).mobID)
                && this.mobDropID.equals(((Mob) obj).mobDropID);
    }
}
