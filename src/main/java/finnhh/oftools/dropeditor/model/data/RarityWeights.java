package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RarityWeights extends Data {
    @Expose
    private final IntegerProperty rarityWeightID;
    @Expose
    private final ListProperty<Integer> weights;

    public RarityWeights() {
        rarityWeightID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        weights = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public RarityWeights(RarityWeights other) {
        this.rarityWeightID = new SimpleIntegerProperty(other.rarityWeightID.get());
        this.weights = new SimpleListProperty<>(FXCollections.observableArrayList(other.weights.get()));
    }

    @Override
    public RarityWeights getEditableClone() {
        return new RarityWeights(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        RarityWeights other = (RarityWeights) data;
        this.rarityWeightID.set(other.rarityWeightID.get());
        this.weights.set(FXCollections.observableArrayList(other.weights.get()));
    }

    @Override
    public void constructBindings() {
        malformed.bind(rarityWeightID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(weights.isNull())
                .or(weights.emptyProperty())
                .or(weights.sizeProperty().greaterThan(4)));

        id.set(String.valueOf(rarityWeightID.get()));
        rarityWeightID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> rarityWeightID.set(Integer.parseInt(newVal)));
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

    public ObservableList<Integer> getWeights() {
        return weights.get();
    }

    public ListProperty<Integer> weightsProperty() {
        return weights;
    }

    public void setWeights(ObservableList<Integer> weights) {
        this.weights.set(weights);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RarityWeights
                && this.rarityWeightID.get() == ((RarityWeights) obj).rarityWeightID.get()
                && this.weights.equals(((RarityWeights) obj).weights);
    }
}
