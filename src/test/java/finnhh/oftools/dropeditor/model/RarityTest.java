package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RarityTest {
    @Test
    void testForKnownTypeID() {
        for (Rarity rarity : Rarity.values())
            Assertions.assertSame(rarity, Rarity.forType(rarity.getTypeID()));
    }

    @Test
    void testForUnknownTypeID() {
        Assertions.assertSame(Rarity.ANY, Rarity.forType(-1));
        Assertions.assertSame(Rarity.ANY, Rarity.forType(6));
    }

    @Test
    void testAnyRarityMatch() {
        for (Rarity rarity : Rarity.values())
            Assertions.assertTrue(Rarity.ANY.match(rarity));
    }

    @Test
    void testOtherRarityMatch() {
        Assertions.assertTrue(Rarity.RARE.match(Rarity.ANY));
        Assertions.assertTrue(Rarity.RARE.match(Rarity.RARE));
        Assertions.assertFalse(Rarity.RARE.match(Rarity.UNCOMMON));
    }
}
