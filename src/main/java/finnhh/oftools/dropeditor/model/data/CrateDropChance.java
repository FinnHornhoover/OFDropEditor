package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CrateDropChance extends Data {
    @Expose
    private final IntegerProperty crateDropChanceID;
    @Expose
    private final IntegerProperty dropChance;
    @Expose
    private final IntegerProperty dropChanceTotal;
    @Expose
    private final ListProperty<Integer> crateTypeDropWeights;

    public CrateDropChance() {
        crateDropChanceID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        dropChance = new SimpleIntegerProperty(0);
        dropChanceTotal = new SimpleIntegerProperty(1);
        crateTypeDropWeights = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public CrateDropChance(CrateDropChance other) {
        this.crateDropChanceID = new SimpleIntegerProperty(other.crateDropChanceID.get());
        this.dropChance = new SimpleIntegerProperty(other.dropChance.get());
        this.dropChanceTotal = new SimpleIntegerProperty(other.dropChanceTotal.get());
        this.crateTypeDropWeights = new SimpleListProperty<>(
                FXCollections.observableArrayList(other.crateTypeDropWeights.get()));
    }

    @Override
    public CrateDropChance getEditableClone() {
        return new CrateDropChance(this);
    }

    @Override
    public void constructBindings() {
        dropChanceTotal.addListener((o, oldVal, newVal) ->
                dropChanceTotal.set(Math.max(1, newVal.intValue())));
        dropChance.addListener((o, oldVal, newVal) ->
                dropChance.set(Math.min(Math.max(0, newVal.intValue()), dropChanceTotal.get())));

        malformed.bind(crateDropChanceID.lessThan(0)
                .or(dropChance.lessThan(0))
                .or(dropChanceTotal.lessThan(1))
                .or(dropChanceTotal.lessThan(dropChance))
                .or(crateTypeDropWeights.isNull())
                .or(crateTypeDropWeights.emptyProperty()));

        id.set(String.valueOf(crateDropChanceID.get()));
        crateDropChanceID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> crateDropChanceID.set(Integer.parseInt(newVal)));
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

    public int getDropChance() {
        return dropChance.get();
    }

    public IntegerProperty dropChanceProperty() {
        return dropChance;
    }

    public void setDropChance(int dropChance) {
        this.dropChance.set(dropChance);
    }

    public int getDropChanceTotal() {
        return dropChanceTotal.get();
    }

    public IntegerProperty dropChanceTotalProperty() {
        return dropChanceTotal;
    }

    public void setDropChanceTotal(int dropChanceTotal) {
        this.dropChanceTotal.set(dropChanceTotal);
    }

    public ObservableList<Integer> getCrateTypeDropWeights() {
        return crateTypeDropWeights.get();
    }

    public ListProperty<Integer> crateTypeDropWeightsProperty() {
        return crateTypeDropWeights;
    }

    public void setCrateTypeDropWeights(ObservableList<Integer> crateTypeDropWeights) {
        this.crateTypeDropWeights.set(crateTypeDropWeights);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CrateDropChance
                && this.crateDropChanceID.equals(((CrateDropChance) obj).crateDropChanceID)
                && this.dropChance.equals(((CrateDropChance) obj).dropChance)
                && this.dropChanceTotal.equals(((CrateDropChance) obj).dropChanceTotal)
                && this.crateTypeDropWeights.equals(((CrateDropChance) obj).crateTypeDropWeights);
    }
}
