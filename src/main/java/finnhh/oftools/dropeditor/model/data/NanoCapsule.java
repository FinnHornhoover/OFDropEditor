package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class NanoCapsule extends Data {
    @Expose
    private final IntegerProperty nano;
    @Expose
    private final IntegerProperty crateID;

    public NanoCapsule() {
        nano = new SimpleIntegerProperty(-1);
        crateID = new SimpleIntegerProperty(-1);
    }

    public NanoCapsule(NanoCapsule other) {
        this.nano = new SimpleIntegerProperty(other.nano.get());
        this.crateID = new SimpleIntegerProperty(other.crateID.get());
    }

    @Override
    public NanoCapsule getEditableClone() {
        return new NanoCapsule(this);
    }

    @Override
    public void constructBindings() {
        malformed.bind(nano.lessThan(0)
                .or(crateID.lessThan(0)));

        id.set(String.valueOf(nano.get()));
        nano.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> nano.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerReferences(crateID, drops.getCrates(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerReferences(crateID, drops.getCrates(), drops.getReferenceMap());
    }

    public int getNano() {
        return nano.get();
    }

    public IntegerProperty nanoProperty() {
        return nano;
    }

    public void setNano(int nano) {
        this.nano.set(nano);
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NanoCapsule
                && this.nano.equals(((NanoCapsule) obj).nano)
                && this.crateID.equals(((NanoCapsule) obj).crateID);
    }
}
