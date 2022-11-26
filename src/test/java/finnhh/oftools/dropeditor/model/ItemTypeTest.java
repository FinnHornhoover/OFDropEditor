package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemTypeTest {
    @Test
    void testToString() {
        for (ItemType itemType : ItemType.values()) {
            String typeString = itemType.toString();
            Assertions.assertTrue(typeString.contains(itemType.getName()));
            Assertions.assertTrue(typeString.contains(String.valueOf(itemType.getTypeID())));
        }
    }

    @Test
    void testForKnownTypeID() {
        for (ItemType itemType : ItemType.values())
            Assertions.assertSame(itemType, ItemType.forType(itemType.getTypeID()));
    }

    @Test
    void testForUnknownTypeID() {
        Assertions.assertSame(ItemType.NONE, ItemType.forType(-1));
        Assertions.assertSame(ItemType.NONE, ItemType.forType(20));
    }
}
