package finnhh.oftools.dropeditor.model;

import javafx.beans.binding.DoubleExpression;
import javafx.beans.binding.IntegerExpression;
import javafx.beans.binding.ListExpression;
import javafx.beans.binding.StringExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

class FilterTypeTest {
    @Nested
    class TestAllowedOperators {
        @Test
        void noneAllowedOperators() {
            Assertions.assertEquals(0, FilterType.NONE.getAllowedOperators().size());
        }

        @Test
        void integerAllowedOperators() {
            Assertions.assertEquals(FilterType.INTEGER.getAllowedOperators(), List.of(
                    FilterOperator.LESS_THAN,
                    FilterOperator.LESS_THAN_OR_EQUAL,
                    FilterOperator.EQUAL,
                    FilterOperator.GREATER_THAN_OR_EQUAL,
                    FilterOperator.GREATER_THAN));
        }

        @Test
        void doubleAllowedOperators() {
            Assertions.assertEquals(FilterType.DOUBLE.getAllowedOperators(), List.of(
                    FilterOperator.LESS_THAN,
                    FilterOperator.LESS_THAN_OR_EQUAL,
                    FilterOperator.EQUAL,
                    FilterOperator.GREATER_THAN_OR_EQUAL,
                    FilterOperator.GREATER_THAN));
        }

        @Test
        void stringAllowedOperators() {
            Assertions.assertEquals(FilterType.STRING.getAllowedOperators(), List.of(FilterOperator.STRING_CONTAINS));
        }

        @Test
        void listAllowedOperators() {
            Assertions.assertEquals(FilterType.LIST.getAllowedOperators(), List.of(FilterOperator.LIST_CONTAINS));
        }
    }

    @Nested
    class TestConvertValue {
        @Test
        void integerConversion() {
            Assertions.assertEquals(12300, FilterType.INTEGER.convertValue("0012300"));
        }

        @Test
        void nonIntegerConversion() {
            Assertions.assertEquals(Integer.MIN_VALUE, FilterType.INTEGER.convertValue("abc"));
        }

        @Test
        void doubleConversion() {
            Assertions.assertEquals(123.0, FilterType.DOUBLE.convertValue("00123.00"));
        }

        @Test
        void nonDoubleConversion() {
            Assertions.assertEquals(Double.NaN, FilterType.DOUBLE.convertValue("abc"));
        }

        @Test
        void stringConversion() {
            Assertions.assertEquals("abc", FilterType.STRING.convertValue("abc"));
        }

        @Test
        void listConversion() {
            Assertions.assertEquals(List.of("1", "2", "3"), FilterType.LIST.convertValue("  1,2,,  3 "));
        }

        @Test
        void nonListConversion() {
            Assertions.assertEquals(List.of(), FilterType.LIST.convertValue(",,, , , , ,, ,"));
        }
    }

    @Nested
    class TestValueValid {
        @Test
        void noneValid() {
            Assertions.assertFalse(FilterType.NONE.valueValid(null));
        }

        @Test
        void integerValid() {
            Assertions.assertTrue(FilterType.INTEGER.valueValid(123));
            Assertions.assertFalse(FilterType.INTEGER.valueValid(Integer.MIN_VALUE));
        }

        @Test
        void doubleValid() {
            Assertions.assertTrue(FilterType.DOUBLE.valueValid(123.0));
            Assertions.assertFalse(FilterType.DOUBLE.valueValid(Double.NaN));
        }

        @Test
        void stringValid() {
            Assertions.assertTrue(FilterType.STRING.valueValid("abc"));
            Assertions.assertFalse(FilterType.STRING.valueValid(""));
        }

        @Test
        void listValid() {
            Assertions.assertTrue(FilterType.LIST.valueValid(List.of(1, 2, 3)));
            Assertions.assertFalse(FilterType.LIST.valueValid(List.of()));
        }
    }

    @Nested
    class TestGetFilterTypeFor {
        @Test
        void integerLikeClass() {
            Assertions.assertSame(FilterType.INTEGER, FilterType.getFilterTypeFor(IntegerExpression.class));
            Assertions.assertSame(FilterType.INTEGER, FilterType.getFilterTypeFor(Integer.class));
            Assertions.assertSame(FilterType.INTEGER, FilterType.getFilterTypeFor(int.class));
        }

        @Test
        void doubleLikeClass() {
            Assertions.assertSame(FilterType.DOUBLE, FilterType.getFilterTypeFor(DoubleExpression.class));
            Assertions.assertSame(FilterType.DOUBLE, FilterType.getFilterTypeFor(Double.class));
            Assertions.assertSame(FilterType.DOUBLE, FilterType.getFilterTypeFor(double.class));
        }

        @Test
        void stringLikeClass() {
            Assertions.assertSame(FilterType.STRING, FilterType.getFilterTypeFor(StringExpression.class));
            Assertions.assertSame(FilterType.STRING, FilterType.getFilterTypeFor(String.class));
        }

        @Test
        void listLikeClass() {
            Assertions.assertSame(FilterType.LIST, FilterType.getFilterTypeFor(ListExpression.class));
            Assertions.assertSame(FilterType.LIST, FilterType.getFilterTypeFor(List.class));
        }

        @Test
        void unknownClass() {
            Assertions.assertSame(FilterType.NONE, FilterType.getFilterTypeFor(EventTypeTest.class));
        }
    }
}
