package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class FilterChoiceTest {
    private final Map<String, List<Map<String, String>>> dataSource = Map.ofEntries(
            Map.entry("a", List.of(
                    Map.ofEntries(
                            Map.entry("aa", "x"),
                            Map.entry("ab", "y"),
                            Map.entry("ac", "z")
                    ),
                    Map.ofEntries(
                            Map.entry("ad", "xx"),
                            Map.entry("ae", "yy"),
                            Map.entry("af", "zz")
                    )
            )),
            Map.entry("b", List.of(
                    Map.ofEntries(
                            Map.entry("ba", "xxx"),
                            Map.entry("bb", "yyy"),
                            Map.entry("bc", "zzz")
                    ),
                    Map.ofEntries(
                            Map.entry("bd", "xxxx"),
                            Map.entry("be", "yyyy"),
                            Map.entry("bf", "zzzz")
                    )
            ))
    );

    @Test
    void testNesting() {
        FilterChoice filterChoice = new FilterChoice(
                FilterType.STRING,
                opt -> opt.map(o -> (Map<?,?>) o)
                        .map(m -> m.get("ba"))
                        .stream().toList(),
                List.of("k")
        );

        FilterChoice nestedFilterChoice = filterChoice.nest("l",
                opt -> opt.map(o -> (String) o)
                        .map(dataSource::get)
                        .stream()
                        .flatMap(List::stream)
                        .toList());

        FilterCondition filterCondition = new FilterCondition(
                nestedFilterChoice,
                FilterOperator.STRING_CONTAINS,
                "x",
                false
        );

        Assertions.assertEquals("k", nestedFilterChoice.valueNameTrail().get(0));
        Assertions.assertEquals("l", nestedFilterChoice.valueNameTrail().get(1));

        Assertions.assertFalse(filterCondition.conditionSatisfied("a"));
        Assertions.assertTrue(filterCondition.conditionSatisfied("b"));
    }

    @Test
    void testValueNameEmpty() {
        FilterChoice filterChoice = new FilterChoice(
                FilterType.STRING,
                opt -> opt.stream().toList(),
                List.of()
        );

        Assertions.assertTrue(filterChoice.valueName().isEmpty());
    }

    @Test
    void testValueNameFilled() {
        FilterChoice filterChoice = new FilterChoice(
                FilterType.STRING,
                opt -> opt.stream().toList(),
                List.of("a", "b", "c")
        );

        Assertions.assertEquals("a", filterChoice.valueName());
    }

    @Test
    void testToString() {
        FilterChoice filterChoice = new FilterChoice(
                FilterType.STRING,
                opt -> opt.stream().toList(),
                List.of("a", "b", "c")
        );

        Assertions.assertTrue(filterChoice.toString().matches(".*(a.*b.*c|c.*b.*a).*"));
    }
}
