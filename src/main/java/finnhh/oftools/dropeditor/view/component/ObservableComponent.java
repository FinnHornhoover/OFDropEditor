package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.FilterType;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface ObservableComponent<T> {
    Class<? extends T> getObservableClass();

    ReadOnlyObjectProperty<? extends T> getObservable();

    void setObservable(T data);

    static Set<FilterChoice> getSearchableValuesFor(Class<?> valueClass) {
        return Arrays.stream(valueClass.getDeclaredFields())
                .filter(f -> FilterType.getFilterTypeFor(f.getType()) != FilterType.NONE)
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
