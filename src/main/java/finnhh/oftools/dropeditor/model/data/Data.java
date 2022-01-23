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
    protected final BooleanProperty malformed;
    protected final StringProperty id;

    protected Data() {
        malformed = new SimpleBooleanProperty(false);
        id = new SimpleStringProperty("<Data>");
    }

    public abstract Data getEditableClone();

    public void registerReferences(Drops drops) {
    }

    public void unregisterReferences(Drops drops) {
    }

    public void registerReferenced(ObservableMap<Data, Set<Data>> referenceMap, Data referenced) {
        referenceMap.putIfAbsent(referenced, new HashSet<>());
        referenceMap.get(referenced).add(this);
    }

    public void unregisterReferenced(ObservableMap<Data, Set<Data>> referenceMap, Data referenced) {
        var referenceSet = referenceMap.get(referenced);

        if (!Objects.isNull(referenceSet)) {
            referenceSet.remove(this);
            if (referenceSet.isEmpty())
                referenceMap.remove(referenced);
        }
    }

    public final void registerIntegerReferences(IntegerProperty integerProperty,
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

    public final void registerIntegerListReferences(ListProperty<Integer> integerListProperty,
                                                    ObservableMap<Integer, ? extends Data> observableMap,
                                                    ObservableMap<Data, Set<Data>> referenceMap) {

        integerListProperty.stream()
                .map(observableMap::get)
                .filter(ip -> !Objects.isNull(ip))
                .forEach(ip -> registerReferenced(referenceMap, ip));

        integerListProperty.addListener((ListChangeListener<Integer>) change -> {
            while (change.next()) {
                if (change.wasRemoved()) {
                    change.getRemoved().stream()
                            .map(observableMap::get)
                            .filter(ip -> !Objects.isNull(ip))
                            .forEach(ip -> unregisterReferenced(referenceMap, ip));
                }

                if (change.wasAdded()) {
                    change.getAddedSubList().stream()
                            .map(observableMap::get)
                            .filter(ip -> !Objects.isNull(ip))
                            .forEach(ip -> registerReferenced(referenceMap, ip));
                }
            }
        });
    }

    public final void unregisterIntegerReferences(IntegerProperty integerProperty,
                                                  ObservableMap<Integer, ? extends Data> observableMap,
                                                  ObservableMap<Data, Set<Data>> referenceMap) {

        Optional.ofNullable(observableMap.get(integerProperty.get()))
                .ifPresent(ip -> unregisterReferenced(referenceMap, ip));
    }

    public final void unregisterIntegerListReferences(ListProperty<Integer> integerListProperty,
                                                      ObservableMap<Integer, ? extends Data> observableMap,
                                                      ObservableMap<Data, Set<Data>> referenceMap) {

        integerListProperty.stream()
                .map(observableMap::get)
                .filter(ip -> !Objects.isNull(ip))
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
