package finnhh.oftools.dropeditor.model.data;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public abstract class Data implements BindingConstructor {
    public static final int INT_PLACEHOLDER_ID = -1;
    public final static String UNSET_ID = "<Data>";
    public final static String PLACEHOLDER_ID = String.valueOf(INT_PLACEHOLDER_ID);

    protected final BooleanProperty malformed;
    protected final StringProperty id;

    protected Data() {
        malformed = new SimpleBooleanProperty(false);
        id = new SimpleStringProperty(UNSET_ID);
    }

    public abstract Data getEditableClone();

    public abstract void setFieldsFromData(Data data);

    public void setChildData(Data data) {
    }

    public void registerReferences(Drops drops) {
    }

    public void unregisterReferences(Drops drops) {
    }

    protected final void registerReferenced(ObservableMap<Data, Set<Data>> referenceMap, Data referenced) {
        referenceMap.putIfAbsent(referenced, new HashSet<>());
        referenceMap.get(referenced).add(this);
    }

    protected final void unregisterReferenced(ObservableMap<Data, Set<Data>> referenceMap, Data referenced) {
        var referenceSet = referenceMap.get(referenced);

        if (Objects.nonNull(referenceSet)) {
            referenceSet.remove(this);
            if (referenceSet.isEmpty())
                referenceMap.remove(referenced);
        }
    }

    protected final void registerIntegerReferences(IntegerProperty integerProperty,
                                                   ObservableMap<Integer, ? extends Data> observableMap,
                                                   ObservableMap<Data, Set<Data>> referenceMap) {

        Optional.ofNullable(observableMap.get(integerProperty.get()))
                .ifPresent(ip -> registerReferenced(referenceMap, ip));

        integerProperty.addListener((o, oldVal, newVal) -> {
            Optional.ofNullable(observableMap.get(oldVal.intValue()))
                    .ifPresent(ip -> unregisterReferenced(referenceMap, ip));

            Optional.ofNullable(observableMap.get(newVal.intValue()))
                    .ifPresent(ip -> registerReferenced(referenceMap, ip));
        });
    }

    protected final void registerIntegerListReferences(ListProperty<Integer> integerListProperty,
                                                       ObservableMap<Integer, ? extends Data> observableMap,
                                                       ObservableMap<Data, Set<Data>> referenceMap) {

        integerListProperty.stream()
                .map(observableMap::get)
                .filter(Objects::nonNull)
                .forEach(ip -> registerReferenced(referenceMap, ip));

        integerListProperty.addListener((ListChangeListener<Integer>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().stream()
                            .map(observableMap::get)
                            .filter(Objects::nonNull)
                            .forEach(ip -> unregisterReferenced(referenceMap, ip));
                }

                if (change.wasAdded()) {
                    change.getAddedSubList().stream()
                            .map(observableMap::get)
                            .filter(Objects::nonNull)
                            .forEach(ip -> registerReferenced(referenceMap, ip));
                }
            }
        });
    }

    protected final void unregisterIntegerReferences(IntegerProperty integerProperty,
                                                     ObservableMap<Integer, ? extends Data> observableMap,
                                                     ObservableMap<Data, Set<Data>> referenceMap) {

        Optional.ofNullable(observableMap.get(integerProperty.get()))
                .ifPresent(ip -> unregisterReferenced(referenceMap, ip));
    }

    protected final void unregisterIntegerListReferences(ListProperty<Integer> integerListProperty,
                                                         ObservableMap<Integer, ? extends Data> observableMap,
                                                         ObservableMap<Data, Set<Data>> referenceMap) {

        integerListProperty.stream()
                .map(observableMap::get)
                .filter(Objects::nonNull)
                .forEach(ip -> unregisterReferenced(referenceMap, ip));
    }

    public boolean isMalformed() {
        return malformed.get();
    }

    public BooleanProperty malformedProperty() {
        return malformed;
    }

    public void setMalformed(boolean malformed) {
        this.malformed.set(malformed);
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringExpression getIdBinding() {
        return Bindings.concat(getClass().getSimpleName(), ": ", idProperty());
    }

    public boolean idEquals(Object obj) {
        return obj instanceof Data
                && this.getIdBinding().getValueSafe().equals(((Data) obj).getIdBinding().getValueSafe());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getIdBinding().getValueSafe());
    }

    @Override
    public String toString() {
        return getIdBinding().getValueSafe();
    }
}
