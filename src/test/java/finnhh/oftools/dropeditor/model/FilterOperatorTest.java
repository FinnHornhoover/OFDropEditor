package finnhh.oftools.dropeditor.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

class FilterOperatorTest {
    @Nested
    class TestLessThan {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.LESS_THAN.getFilterFunction();

        @Test
        void operatedValueIntegerExpression() {
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(1000000), 5000000));
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(5000000), 5000000));
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(6000000), 5000000));
        }

        @Test
        void operatedValueInteger() {
            Assertions.assertTrue(filterFunction.apply(1000000, 5000000));
            Assertions.assertFalse(filterFunction.apply(5000000, 5000000));
            Assertions.assertFalse(filterFunction.apply(6000000, 5000000));
        }

        @Test
        void operatedValueDoubleExpression() {
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(1000000.0), 5000000.0));
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(5000000.0), 5000000.0));
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(6000000.0), 5000000.0));
        }

        @Test
        void operatedValueDouble() {
            Assertions.assertTrue(filterFunction.apply(1000000.0, 5000000.0));
            Assertions.assertFalse(filterFunction.apply(5000000.0, 5000000.0));
            Assertions.assertFalse(filterFunction.apply(6000000.0, 5000000.0));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeMixing() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000.0));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestLessThanOrEqual {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.LESS_THAN_OR_EQUAL.getFilterFunction();

        @Test
        void operatedValueIntegerExpression() {
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(1000000), 5000000));
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(5000000), 5000000));
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(6000000), 5000000));
        }

        @Test
        void operatedValueInteger() {
            Assertions.assertTrue(filterFunction.apply(1000000, 5000000));
            Assertions.assertTrue(filterFunction.apply(5000000, 5000000));
            Assertions.assertFalse(filterFunction.apply(6000000, 5000000));
        }

        @Test
        void operatedValueDoubleExpression() {
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(1000000.0), 5000000.0));
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(5000000.0), 5000000.0));
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(6000000.0), 5000000.0));
        }

        @Test
        void operatedValueDouble() {
            Assertions.assertTrue(filterFunction.apply(1000000.0, 5000000.0));
            Assertions.assertTrue(filterFunction.apply(5000000.0, 5000000.0));
            Assertions.assertFalse(filterFunction.apply(6000000.0, 5000000.0));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeMixing() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000.0));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestEqual {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.EQUAL.getFilterFunction();

        @Test
        void operatedValueIntegerExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(1000000), 5000000));
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(5000000), 5000000));
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(6000000), 5000000));
        }

        @Test
        void operatedValueInteger() {
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000));
            Assertions.assertTrue(filterFunction.apply(5000000, 5000000));
            Assertions.assertFalse(filterFunction.apply(6000000, 5000000));
        }

        @Test
        void operatedValueDoubleExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(1000000.0), 5000000.0));
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(5000000.0), 5000000.0));
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(6000000.0), 5000000.0));
        }

        @Test
        void operatedValueDouble() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000.0));
            Assertions.assertTrue(filterFunction.apply(5000000.0, 5000000.0));
            Assertions.assertFalse(filterFunction.apply(6000000.0, 5000000.0));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeMixing() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000.0));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestGreaterThan {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.GREATER_THAN.getFilterFunction();

        @Test
        void operatedValueIntegerExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(1000000), 5000000));
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(5000000), 5000000));
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(6000000), 5000000));
        }

        @Test
        void operatedValueInteger() {
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000));
            Assertions.assertFalse(filterFunction.apply(5000000, 5000000));
            Assertions.assertTrue(filterFunction.apply(6000000, 5000000));
        }

        @Test
        void operatedValueDoubleExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(1000000.0), 5000000.0));
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(5000000.0), 5000000.0));
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(6000000.0), 5000000.0));
        }

        @Test
        void operatedValueDouble() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000.0));
            Assertions.assertFalse(filterFunction.apply(5000000.0, 5000000.0));
            Assertions.assertTrue(filterFunction.apply(6000000.0, 5000000.0));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeMixing() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000.0));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestGreaterThanOrEqual {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.GREATER_THAN_OR_EQUAL.getFilterFunction();

        @Test
        void operatedValueIntegerExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleIntegerProperty(1000000), 5000000));
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(5000000), 5000000));
            Assertions.assertTrue(filterFunction.apply(new SimpleIntegerProperty(6000000), 5000000));
        }

        @Test
        void operatedValueInteger() {
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000));
            Assertions.assertTrue(filterFunction.apply(5000000, 5000000));
            Assertions.assertTrue(filterFunction.apply(6000000, 5000000));
        }

        @Test
        void operatedValueDoubleExpression() {
            Assertions.assertFalse(filterFunction.apply(new SimpleDoubleProperty(1000000.0), 5000000.0));
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(5000000.0), 5000000.0));
            Assertions.assertTrue(filterFunction.apply(new SimpleDoubleProperty(6000000.0), 5000000.0));
        }

        @Test
        void operatedValueDouble() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000.0));
            Assertions.assertTrue(filterFunction.apply(5000000.0, 5000000.0));
            Assertions.assertTrue(filterFunction.apply(6000000.0, 5000000.0));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeMixing() {
            Assertions.assertFalse(filterFunction.apply(1000000.0, 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000.0));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestListContains {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.LIST_CONTAINS.getFilterFunction();

        @Test
        void operatedValueListExpression() {
            Assertions.assertTrue(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList(1.1, 3.3, 5.5, 7.7, 9.9)),
                    List.of()
            ));
            Assertions.assertTrue(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList(1.1, 3.3, 5.5, 7.7, 9.9)),
                    List.of(1.1, 3.3, 5.5, 7.7)
            ));
            Assertions.assertTrue(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList(1.1, 3.3, 5.5, 7.7, 9.9)),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertFalse(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList(1.1, 3.3, 5.5, 7.7)),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertFalse(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList()),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertTrue(filterFunction.apply(
                    new SimpleListProperty<>(FXCollections.observableArrayList()),
                    List.of()
            ));
        }

        @Test
        void operatedValueList() {
            Assertions.assertTrue(filterFunction.apply(
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9),
                    List.of()
            ));
            Assertions.assertTrue(filterFunction.apply(
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9),
                    List.of(1.1, 3.3, 5.5, 7.7)
            ));
            Assertions.assertTrue(filterFunction.apply(
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertFalse(filterFunction.apply(
                    List.of(1.1, 3.3, 5.5, 7.7),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertFalse(filterFunction.apply(
                    List.of(),
                    List.of(1.1, 3.3, 5.5, 7.7, 9.9)
            ));
            Assertions.assertTrue(filterFunction.apply(
                    List.of(),
                    List.of()
            ));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, List.of(1.1, 3.3, 5.5, 7.7, 9.9)));
            Assertions.assertFalse(filterFunction.apply(List.of(1.1, 3.3, 5.5, 7.7), null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply("1000000", List.of(1.1, 3.3, 5.5, 7.7, 9.9)));
            Assertions.assertFalse(filterFunction.apply(List.of(1.1, 3.3, 5.5, 7.7), "5000000"));
            Assertions.assertFalse(filterFunction.apply("1000000", "5000000"));
        }
    }

    @Nested
    class TestStringContains {
        private final BiFunction<Object, Object, Boolean> filterFunction = FilterOperator.STRING_CONTAINS.getFilterFunction();

        @Test
        void operatedValueStringExpression() {
            Assertions.assertTrue(filterFunction.apply(new SimpleStringProperty("a bb ccc dddd"), ""));
            Assertions.assertTrue(filterFunction.apply(new SimpleStringProperty("a bb ccc dddd"), "a bb ccc d"));
            Assertions.assertTrue(filterFunction.apply(new SimpleStringProperty("a bb ccc dddd"), "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply(new SimpleStringProperty("a bb ccc d"), "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply(new SimpleStringProperty(""), "a bb ccc dddd"));
            Assertions.assertTrue(filterFunction.apply(new SimpleStringProperty(""), ""));
        }

        @Test
        void operatedValueString() {
            Assertions.assertTrue(filterFunction.apply("a bb ccc dddd", ""));
            Assertions.assertTrue(filterFunction.apply("a bb ccc dddd", "a bb ccc d"));
            Assertions.assertTrue(filterFunction.apply("a bb ccc dddd", "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply("a bb ccc d", "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply("", "a bb ccc dddd"));
            Assertions.assertTrue(filterFunction.apply("", ""));
        }

        @Test
        void valueNull() {
            Assertions.assertFalse(filterFunction.apply(null, "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply("a bb ccc d", null));
            Assertions.assertFalse(filterFunction.apply(null, null));
        }

        @Test
        void valueTypeNotRecognized() {
            Assertions.assertFalse(filterFunction.apply(1000000, "a bb ccc dddd"));
            Assertions.assertFalse(filterFunction.apply("a bb ccc d", 5000000));
            Assertions.assertFalse(filterFunction.apply(1000000, 5000000));
        }
    }
}
