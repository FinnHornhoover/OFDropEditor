package finnhh.oftools.dropeditor.model;

import java.util.Objects;
import java.util.Optional;

public record FilterCondition(FilterChoice filterChoice,
                              FilterOperator operator,
                              Object filterValue,
                              boolean allowNulls) {

    public boolean conditionSatisfied(Object object) {
        return filterChoice.valueGetter().apply(Optional.ofNullable(object)).stream()
                .map(objectValue -> operator.getFilterFunction().apply(objectValue, filterValue))
                .reduce(Boolean::logicalOr)
                .orElse(allowNulls);
    }

    public boolean conditionValid() {
        return Objects.nonNull(filterChoice)
                && Objects.nonNull(operator)
                && filterChoice.filterType().valueValid(filterValue);
    }

    public String toShortString() {
        return String.format("%s %s %s %s",
                filterChoice.valueName(),
                operator.getShortString(),
                filterValue,
                allowNulls ? "| null" : "");
    }

    @Override
    public String toString() {
        return String.format("(%s) %s %s %s",
                filterChoice,
                operator,
                filterValue,
                allowNulls ? "or null" : "");
    }
}
