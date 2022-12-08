package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

@IdMeaningful
public class CodeItem extends Data {
    private final IntegerProperty codeID;
    @Expose
    private final StringProperty code;
    @Expose
    private final ListProperty<Integer> itemReferenceIDs;

    public CodeItem() {
        codeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        code = new SimpleStringProperty(null);
        itemReferenceIDs = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public CodeItem(CodeItem other) {
        this.codeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        this.code = new SimpleStringProperty(other.code.get());
        this.itemReferenceIDs = new SimpleListProperty<>(
                FXCollections.observableArrayList(other.itemReferenceIDs.get()));
    }

    @Override
    public CodeItem getEditableClone() {
        return new CodeItem(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        CodeItem other = (CodeItem) data;
        this.codeID.set(other.codeID.get());
        this.code.set(other.code.get());
        this.itemReferenceIDs.set(FXCollections.observableArrayList(other.itemReferenceIDs.get()));
    }

    @Override
    public void constructBindings() {
        codeID.set(Objects.hashCode(code.get()) - 1);
        code.addListener((o, oldVal, newVal) -> codeID.set(Objects.hashCode(newVal) - 1));

        malformed.bind(code.isNull()
                .or(itemReferenceIDs.isNull())
                .or(itemReferenceIDs.emptyProperty()));

        id.set(String.valueOf(codeID.get()));
        codeID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> codeID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerListReferences(itemReferenceIDs, drops.getItemReferences(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerListReferences(itemReferenceIDs, drops.getItemReferences(), drops.getReferenceMap());
    }

    public int getCodeID() {
        return codeID.get();
    }

    public ReadOnlyIntegerProperty codeIDProperty() {
        return codeID;
    }

    public String getCode() {
        return code.get();
    }

    public StringProperty codeProperty() {
        return code;
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public ObservableList<Integer> getItemReferenceIDs() {
        return itemReferenceIDs.get();
    }

    public ListProperty<Integer> itemReferenceIDsProperty() {
        return itemReferenceIDs;
    }

    public void setItemReferenceIDs(ObservableList<Integer> itemReferenceIDs) {
        this.itemReferenceIDs.set(itemReferenceIDs);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CodeItem
                && this.codeID.get() == ((CodeItem) obj).codeID.get()
                && this.code.get().equals(((CodeItem) obj).code.get())
                && this.itemReferenceIDs.equals(((CodeItem) obj).itemReferenceIDs);
    }
}
