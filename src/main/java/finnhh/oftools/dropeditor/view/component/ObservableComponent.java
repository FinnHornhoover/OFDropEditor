package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.FilterType;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ObservableComponent<T> {
    MainController getController();

    Class<? extends T> getObservableClass();

    ReadOnlyObjectProperty<? extends T> getObservable();

    void setObservable(T data);

    void cleanUIState();

    void fillUIState();

    default void bindVariablesNonNull() {
    }

    default void bindVariablesNullable() {
    }

    default void unbindVariables() {
    }

    default void refreshObservableAndState() {
        setObservableAndState(getObservable().get());
    }

    default void setObservableAndState(T data) {
        // 1. unbind listeners, handlers, and bound variables
        unbindVariables();

        // 2. make the UI State "clean" i.e. as if observable is null
        cleanUIState();

        // 3. set the observable
        setObservable(data);

        if (getObservable().isNotNull().get()) {
            // 4?. fill the UI State with the non-null observable
            fillUIState();

            // 5?. bind variables that depend on a non-null observable
            bindVariablesNonNull();
        }

        // 6. bind all variables that are independent of the nullity of the observable
        bindVariablesNullable();
    }

    static Set<FilterChoice> getSearchableValuesFor(Class<?> valueClass) {
        return Arrays.stream(valueClass.getDeclaredFields())
                .filter(f -> FilterType.getFilterTypeFor(f.getType()) != FilterType.NONE
                        && !Modifier.isStatic(f.getModifiers()))
                .map(f -> new FilterChoice(
                        FilterType.getFilterTypeFor(f.getType()),
                        op -> op.map(o -> {
                                    try {
                                        f.setAccessible(true);
                                        return f.get(o);
                                    } catch (IllegalAccessException | IllegalArgumentException e) {
                                        return null;
                                    }
                                })
                                .map(List::of)
                                .orElse(List.of()),
                        List.of(f.getName(), valueClass.getSimpleName())
                ))
                .collect(Collectors.toSet());
    }

    default Set<FilterChoice> getSearchableValuesForObservable() {
        return getSearchableValuesFor(getObservableClass());
    }

    default Set<FilterChoice> getNestedSearchableValues(Set<FilterChoice> innerSet,
                                                        Function<Optional<?>, List<?>> nestedGetter) {
        return innerSet.stream()
                .map(fc -> fc.nest(getObservableClass().getSimpleName(), nestedGetter))
                .collect(Collectors.toSet());
    }

    default Set<FilterChoice> getSearchableValues() {
        return getSearchableValuesForObservable();
    }
}
