package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ItemReference extends Data {
    @Expose
    private final IntegerProperty itemReferenceID;
    @Expose
    private final IntegerProperty itemID;
    @Expose
    private final IntegerProperty type;

    public ItemReference() {
        itemReferenceID = new SimpleIntegerProperty(-1);
        itemID = new SimpleIntegerProperty(-1);
        type = new SimpleIntegerProperty(-1);
    }

    public ItemReference(ItemReference other) {
        this.itemReferenceID = new SimpleIntegerProperty(other.itemReferenceID.get());
        this.itemID = new SimpleIntegerProperty(other.itemID.get());
        this.type = new SimpleIntegerProperty(other.type.get());
    }

    @Override
    public ItemReference getEditableClone() {
        return new ItemReference(this);
    }

    @Override
    public void constructBindings() {
        malformed.bind(itemReferenceID.lessThan(0)
                .or(itemID.lessThan(0))
                .or(type.lessThan(0)));

        id.set(String.valueOf(itemReferenceID.get()));
        itemReferenceID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> itemReferenceID.set(Integer.parseInt(newVal)));
    }

    public int getItemReferenceID() {
        return itemReferenceID.get();
    }

    public IntegerProperty itemReferenceIDProperty() {
        return itemReferenceID;
    }

    public void setItemReferenceID(int itemReferenceID) {
        this.itemReferenceID.set(itemReferenceID);
    }

    public int getItemID() {
        return itemID.get();
    }

    public IntegerProperty itemIDProperty() {
        return itemID;
    }

    public void setItemID(int itemID) {
        this.itemID.set(itemID);
    }

    public int getType() {
        return type.get();
    }

    public IntegerProperty typeProperty() {
        return type;
    }

    public void setType(int type) {
        this.type.set(type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemReference
                && this.itemReferenceID.equals(((ItemReference) obj).itemReferenceID)
                && this.itemID.equals(((ItemReference) obj).itemID)
                && this.type.equals(((ItemReference) obj).type);
    }
}
