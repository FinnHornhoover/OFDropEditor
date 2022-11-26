package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class FilterConditionTest {
    private final FilterChoice filterChoice = new FilterChoice(
            FilterType.STRING,
            opt -> opt.map(o -> (String) o)
                    .map(s -> Arrays.asList(s.split("\\s+")))
                    .stream()
                    .flatMap(List::stream)
                    .toList(),
            List.of("a")
    );
    private final FilterCondition filterCondition = new FilterCondition(
            filterChoice,
            FilterOperator.STRING_CONTAINS,
            "c",
            false
    );

    @Nested
    class TestConditionSatisfied {
        private final String dataSourceTrue = "cccc rrrr aaaa";
        private final String dataSourceFalse = "aaaa ddddd bbbb t";

        @Test
        void validCondition() {
            Assertions.assertTrue(filterCondition.conditionSatisfied(dataSourceTrue));
            Assertions.assertFalse(filterCondition.conditionSatisfied(dataSourceFalse));
        }

        @Test
        void operatorTypeMismatch() {
            FilterCondition filterConditionGT = new FilterCondition(
                    filterChoice,
                    FilterOperator.GREATER_THAN,
                    "c",
                    false
            );

            Assertions.assertFalse(filterConditionGT.conditionSatisfied(dataSourceTrue));
            Assertions.assertFalse(filterConditionGT.conditionSatisfied(dataSourceFalse));
        }

        @Test
        void filterValueTypeMismatch() {
            FilterCondition filterConditionIntValue = new FilterCondition(
                    filterChoice,
                    FilterOperator.STRING_CONTAINS,
                    45,
                    false
            );

            Assertions.assertFalse(filterConditionIntValue.conditionSatisfied(dataSourceTrue));
            Assertions.assertFalse(filterConditionIntValue.conditionSatisfied(dataSourceFalse));
        }

        @Test
        void filterValueNull() {
            FilterCondition filterConditionNullValue = new FilterCondition(
                    filterChoice,
                    FilterOperator.STRING_CONTAINS,
                    null,
                    false
            );

            Assertions.assertFalse(filterConditionNullValue.conditionSatisfied(dataSourceTrue));
            Assertions.assertFalse(filterConditionNullValue.conditionSatisfied(dataSourceFalse));
        }

        @Test
        void allowNulls() {
            FilterCondition filterConditionAllowNulls = new FilterCondition(
                    filterChoice,
                    FilterOperator.STRING_CONTAINS,
                    "c",
                    true
            );

            Assertions.assertFalse(filterCondition.conditionSatisfied(null));
            Assertions.assertTrue(filterConditionAllowNulls.conditionSatisfied(null));
        }
    }

    @Nested
    class TestConditionValid {
        @Test
        void validCondition() {
            Assertions.assertTrue(filterCondition.conditionValid());
        }

        @Test
        void nullFilterChoice() {
            FilterCondition filterConditionNullChoice = new FilterCondition(
                    null,
                    FilterOperator.STRING_CONTAINS,
                    "c",
                    false
            );

            Assertions.assertFalse(filterConditionNullChoice.conditionValid());
        }

        @Test
        void nullOperator() {
            FilterCondition filterConditionNullOperator = new FilterCondition(
                    filterChoice,
                    null,
                    "c",
                    false
            );

            Assertions.assertFalse(filterConditionNullOperator.conditionValid());
        }

        @Test
        void nullFilterValue() {
            FilterCondition filterConditionNullOperator = new FilterCondition(
                    filterChoice,
                    FilterOperator.STRING_CONTAINS,
                    null,
                    false
            );

            Assertions.assertFalse(filterConditionNullOperator.conditionValid());
        }

        @Test
        void valueInvalid() {
            FilterCondition filterConditionNullOperator = new FilterCondition(
                    filterChoice,
                    FilterOperator.STRING_CONTAINS,
                    "",
                    false
            );

            Assertions.assertFalse(filterConditionNullOperator.conditionValid());
        }
    }

    @Test
    void testToString() {
        FilterCondition filterConditionAllowNulls = new FilterCondition(
                filterChoice,
                FilterOperator.STRING_CONTAINS,
                "c",
                true
        );

        Assertions.assertTrue(filterCondition.toString().matches(String.join(".*",
                "",
                filterCondition.filterChoice().toString(),
                filterCondition.operator().toString(),
                filterCondition.filterValue().toString(),
                "")));
        Assertions.assertTrue(filterConditionAllowNulls.toString().matches(String.join(".*",
                "",
                filterConditionAllowNulls.filterChoice().toString(),
                filterConditionAllowNulls.operator().toString(),
                filterConditionAllowNulls.filterValue().toString(),
                "null",
                "")));
    }

    @Test
    void testToShortString() {
        FilterCondition filterConditionAllowNulls = new FilterCondition(
                filterChoice,
                FilterOperator.STRING_CONTAINS,
                "c",
                true
        );

        Assertions.assertTrue(filterCondition.toShortString().matches(String.join(".*",
                "",
                filterCondition.filterChoice().toString(),
                filterCondition.operator().getShortString(),
                filterCondition.filterValue().toString(),
                "")));
        Assertions.assertTrue(filterConditionAllowNulls.toShortString().matches(String.join(".*",
                "",
                filterConditionAllowNulls.filterChoice().toString(),
                filterConditionAllowNulls.operator().getShortString(),
                filterConditionAllowNulls.filterValue().toString(),
                "null",
                "")));
        Assertions.assertTrue(filterCondition.toShortString().length() < filterCondition.toString().length());
        Assertions.assertTrue(filterConditionAllowNulls.toShortString().length() < filterConditionAllowNulls.toString().length());
    }
}
