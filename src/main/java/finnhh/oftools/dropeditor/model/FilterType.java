package finnhh.oftools.dropeditor.model;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.StringExpression;

import java.util.Arrays;
import java.util.List;

public enum FilterType {
    NONE(List.of()),
    INTEGER(List.of(
            FilterOperator.LESS_THAN,
            FilterOperator.LESS_THAN_OR_EQUAL,
            FilterOperator.EQUAL,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            FilterOperator.GREATER_THAN)),
    DOUBLE(List.of(
            FilterOperator.LESS_THAN,
            FilterOperator.LESS_THAN_OR_EQUAL,
            FilterOperator.EQUAL,
            FilterOperator.GREATER_THAN_OR_EQUAL,
            FilterOperator.GREATER_THAN)),
    STRING(List.of(FilterOperator.STRING_CONTAINS)),
    LIST(List.of(FilterOperator.LIST_CONTAINS));

    private final List<FilterOperator> allowedOperators;

    FilterType(List<FilterOperator> allowedOperators) {
        this.allowedOperators = allowedOperators;
    }

    public List<FilterOperator> getAllowedOperators() {
        return allowedOperators;
    }

    public Object convertValue(String stringValue) {
        switch (this) {
            case INTEGER -> {
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return Integer.MIN_VALUE;
                }
            }
            case DOUBLE -> {
                try {
                    return Double.parseDouble(stringValue);
                } catch (NumberFormatException e) {
                    return Double.NaN;
                }
            }
            case STRING -> {
                return stringValue;
            }
            case LIST -> {
                return Arrays.stream(stringValue.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            default -> {
                return null;
            }
        }
    }

    public boolean valueValid(Object objectValue) {
        return switch (this) {
            case NONE -> false;
            case INTEGER -> ((Integer) objectValue) != Integer.MIN_VALUE;
            case DOUBLE -> !Double.isNaN((Double) objectValue);
            case STRING -> !((String) objectValue).isEmpty();
            case LIST -> ((List<?>) objectValue).size() > 0;
        };
    }

    public static FilterType getFilterTypeFor(Class<?> valueClass) {
        if (IntegerExpression.class.isAssignableFrom(valueClass) || Integer.class.isAssignableFrom(valueClass) || int.class.isAssignableFrom(valueClass))
            return INTEGER;
        else if (DoubleExpression.class.isAssignableFrom(valueClass) || Double.class.isAssignableFrom(valueClass) || double.class.isAssignableFrom(valueClass))
            return DOUBLE;
        else if (StringExpression.class.isAssignableFrom(valueClass) || String.class.isAssignableFrom(valueClass))
            return STRING;
        else if (ListExpression.class.isAssignableFrom(valueClass) || List.class.isAssignableFrom(valueClass))
            return LIST;
        else
            return NONE;
    }
}
