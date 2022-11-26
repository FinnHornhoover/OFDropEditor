package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventTypeTest {
    @Test
    void testForNegativeUnknownTypeID() {
        Assertions.assertSame(EventType.forType(-2), EventType.NO_EVENT);
    }

    @Test
    void testForKnownTypeID() {
        Assertions.assertSame(EventType.forType(4), EventType.BIRTHDAY_BASH);
    }

    @Test
    void testForPositiveUnknownTypeID() {
        Assertions.assertSame(EventType.forType(100), EventType.CUSTOM_EVENT);
    }
}
