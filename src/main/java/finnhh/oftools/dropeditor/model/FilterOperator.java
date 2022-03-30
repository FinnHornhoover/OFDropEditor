package finnhh.oftools.dropeditor.model;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.StringExpression;

import java.util.List;
import java.util.function.BiFunction;

public enum FilterOperator {
    LESS_THAN("Less Than", "<", (oVal, fVal) -> {
        if (oVal instanceof IntegerExpression oValInt && fVal instanceof Integer fValInt)
            return oValInt.get() < fValInt;
        else if (oVal instanceof Integer oValInt && fVal instanceof Integer fValInt)
            return oValInt < fValInt;
        else if (oVal instanceof DoubleExpression oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.get() < fValDouble;
        else if (oVal instanceof Double oValDouble && fVal instanceof Double fValDouble)
            return oValDouble < fValDouble;
        else
            return false;
    }),
    LESS_THAN_OR_EQUAL("Less Than or Equal To", "<=", (oVal, fVal) -> {
        if (oVal instanceof IntegerExpression oValInt && fVal instanceof Integer fValInt)
            return oValInt.get() <= fValInt;
        else if (oVal instanceof Integer oValInt && fVal instanceof Integer fValInt)
            return oValInt <= fValInt;
        else if (oVal instanceof DoubleExpression oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.get() <= fValDouble;
        else if (oVal instanceof Double oValDouble && fVal instanceof Double fValDouble)
            return oValDouble <= fValDouble;
        else
            return false;
    }),
    EQUAL("Equal To", "=", (oVal, fVal) -> {
        if (oVal instanceof IntegerExpression oValInt && fVal instanceof Integer fValInt)
            return oValInt.get() == fValInt;
        else if (oVal instanceof Integer oValInt && fVal instanceof Integer fValInt)
            return oValInt.doubleValue() == fValInt;
        else if (oVal instanceof DoubleExpression oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.get() == fValDouble;
        else if (oVal instanceof Double oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.doubleValue() == fValDouble;
        else
            return false;
    }),
    GREATER_THAN_OR_EQUAL("Greater Than or Equal To", ">=", (oVal, fVal) -> {
        if (oVal instanceof IntegerExpression oValInt && fVal instanceof Integer fValInt)
            return oValInt.get() >= fValInt;
        else if (oVal instanceof Integer oValInt && fVal instanceof Integer fValInt)
            return oValInt >= fValInt;
        else if (oVal instanceof DoubleExpression oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.get() >= fValDouble;
        else if (oVal instanceof Double oValDouble && fVal instanceof Double fValDouble)
            return oValDouble >= fValDouble;
        else
            return false;
    }),
    GREATER_THAN("Greater Than", ">", (oVal, fVal) -> {
        if (oVal instanceof IntegerExpression oValInt && fVal instanceof Integer fValInt)
            return oValInt.get() > fValInt;
        else if (oVal instanceof Integer oValInt && fVal instanceof Integer fValInt)
            return oValInt > fValInt;
        else if (oVal instanceof DoubleExpression oValDouble && fVal instanceof Double fValDouble)
            return oValDouble.get() > fValDouble;
        else if (oVal instanceof Double oValDouble && fVal instanceof Double fValDouble)
            return oValDouble > fValDouble;
        else
            return false;
    }),
    LIST_CONTAINS("Contains", "<-", (oVal, fVal) -> {
        if (oVal instanceof ListExpression<?> oValList && fVal instanceof List<?> fValList) {
            return oValList.stream()
                    .map(Object::toString)
                    .toList()
                    .containsAll(fValList.stream()
                            .map(Object::toString)
                            .toList());
        } else if (oVal instanceof List<?> oValList && fVal instanceof List<?> fValList) {
            return oValList.stream()
                    .map(Object::toString)
                    .toList()
                    .containsAll(fValList.stream()
                            .map(Object::toString)
                            .toList());
        } else {
            return false;
        }
    }),
    STRING_CONTAINS("Text Contains", "<-", (oVal, fVal) -> {
        if (oVal instanceof StringExpression oValString && fVal instanceof String fValString)
            return oValString.get().contains(fValString);
        else if (oVal instanceof String oValString && fVal instanceof String fValString)
            return oValString.contains(fValString);
        else
            return false;
    });

    private final String operatorString;
    private final String shortString;
    private final BiFunction<Object, Object, Boolean> filterFunction;

    FilterOperator(String operatorString, String shortString, BiFunction<Object, Object, Boolean> filterFunction) {
        this.operatorString = operatorString;
        this.shortString = shortString;
        this.filterFunction = filterFunction;
    }

    public String getOperatorString() {
        return operatorString;
    }

    public String getShortString() {
        return shortString;
    }

    public BiFunction<Object, Object, Boolean> getFilterFunction() {
        return filterFunction;
    }

    @Override
    public String toString() {
        return operatorString;
    }
}
