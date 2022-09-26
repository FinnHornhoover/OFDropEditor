package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CrateDropType extends Data {
    @Expose
    private final IntegerProperty crateDropTypeID;
    @Expose
    private final ListProperty<Integer> crateIDs;

    public CrateDropType()  {
        crateDropTypeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        crateIDs = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public CrateDropType(CrateDropType other)  {
        this.crateDropTypeID = new SimpleIntegerProperty(other.crateDropTypeID.get());
        this.crateIDs = new SimpleListProperty<>(
                FXCollections.observableArrayList(other.crateIDs.get()));
    }

    @Override
    public CrateDropType getEditableClone() {
        return new CrateDropType(this);
    }

    @Override
    public void constructBindings() {
        malformed.bind(crateDropTypeID.lessThan(0)
                .or(crateIDs.isNull())
                .or(crateIDs.emptyProperty()));

        id.set(String.valueOf(crateDropTypeID.get()));
        crateDropTypeID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> crateDropTypeID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerListReferences(crateIDs, drops.getCrates(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerListReferences(crateIDs, drops.getCrates(), drops.getReferenceMap());
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

    public ObservableList<Integer> getCrateIDs() {
        return crateIDs.get();
    }

    public ListProperty<Integer> crateIDsProperty() {
        return crateIDs;
    }

    public void setCrateIDs(ObservableList<Integer> crateIDs) {
        this.crateIDs.set(crateIDs);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CrateDropType
                && this.crateDropTypeID.equals(((CrateDropType) obj).crateDropTypeID)
                && this.crateIDs.equals(((CrateDropType) obj).crateIDs);
    }
}
