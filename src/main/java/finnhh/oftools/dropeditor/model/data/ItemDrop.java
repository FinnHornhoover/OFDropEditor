package finnhh.oftools.dropeditor.model.data;

import finnhh.oftools.dropeditor.model.Gender;
import finnhh.oftools.dropeditor.model.Rarity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ItemDrop extends ItemReference {
    private final ObjectProperty<Rarity> rarity;
    private final ObjectProperty<Gender> gender;
    private final IntegerProperty weight;

    public ItemDrop() {
        rarity = new SimpleObjectProperty<>(Rarity.ANY);
        gender = new SimpleObjectProperty<>(Gender.ANY);
        weight = new SimpleIntegerProperty(0);
    }

    public ItemDrop(ItemReference other) {
        super(other);

        if (other instanceof ItemDrop otherWeighted) {
            this.rarity = new SimpleObjectProperty<>(otherWeighted.rarity.get());
            this.gender = new SimpleObjectProperty<>(otherWeighted.gender.get());
            this.weight = new SimpleIntegerProperty(otherWeighted.weight.get());
        } else {
            this.rarity = new SimpleObjectProperty<>(Rarity.ANY);
            this.gender = new SimpleObjectProperty<>(Gender.ANY);
            this.weight = new SimpleIntegerProperty(0);
        }
    }

    @Override
    public ItemDrop getEditableClone() {
        return new ItemDrop(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        super.setFieldsFromData(data);

        ItemDrop other = (ItemDrop) data;
        this.rarity.set(other.rarity.get());
        this.gender.set(other.gender.get());
        this.weight.set(other.weight.get());
    }

    @Override
    public void constructBindings() {
        super.constructBindings();
        malformed.unbind();
        malformed.bind(itemReferenceID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(itemID.lessThan(0))
                .or(type.lessThan(0))
                .or(weight.lessThan(0)));
    }

    public Rarity getRarity() {
        return rarity.get();
    }

    public ObjectProperty<Rarity> rarityProperty() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity.set(rarity);
    }

    public Gender getGender() {
        return gender.get();
    }

    public ObjectProperty<Gender> genderProperty() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender.set(gender);
    }

    public int getWeight() {
        return weight.get();
    }

    public IntegerProperty weightProperty() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight.set(weight);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)
                && obj instanceof ItemDrop
                && this.rarity.get() == ((ItemDrop) obj).rarity.get()
                && this.gender.get() == ((ItemDrop) obj).gender.get()
                && this.weight.get() == ((ItemDrop) obj).weight.get();
    }
}
