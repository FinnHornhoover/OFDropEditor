package finnhh.oftools.dropeditor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public record FilterChoice(FilterType filterType,
                           Function<Optional<?>, List<?>> valueGetter,
                           List<String> valueNameTrail) {

    public FilterChoice nest(String valueClassName, Function<Optional<?>, List<?>> nestedGetter) {
        List<String> newNameTrail = new ArrayList<>(valueNameTrail);
        newNameTrail.add(valueClassName);
        return new FilterChoice(
                filterType,
                op -> nestedGetter.apply(op).stream()
                        .flatMap(obj -> valueGetter.apply(Optional.ofNullable(obj)).stream())
                        .toList(),
                newNameTrail
        );
    }

    public String valueName() {
        return valueNameTrail.isEmpty() ? "" : valueNameTrail.get(0);
    }

    @Override
    public String toString() {
        return String.join(" < ", valueNameTrail);
    }
}
