package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GenderTest {
    @Test
    void testForKnownTypeID() {
        Assertions.assertSame(Gender.ANY, Gender.forType(0));
        Assertions.assertSame(Gender.BOY, Gender.forType(1));
        Assertions.assertSame(Gender.GIRL, Gender.forType(2));
    }

    @Test
    void testForUnknownTypeID() {
        Assertions.assertSame(Gender.ANY, Gender.forType(-1));
        Assertions.assertSame(Gender.ANY, Gender.forType(5));
    }

    @Test
    void testAnyGenderMatch() {
        Assertions.assertTrue(Gender.ANY.match(Gender.ANY));
        Assertions.assertTrue(Gender.ANY.match(Gender.BOY));
        Assertions.assertTrue(Gender.ANY.match(Gender.GIRL));
    }

    @Test
    void testBoyGenderMatch() {
        Assertions.assertTrue(Gender.BOY.match(Gender.ANY));
        Assertions.assertTrue(Gender.BOY.match(Gender.BOY));
        Assertions.assertFalse(Gender.BOY.match(Gender.GIRL));
    }

    @Test
    void testGirlGenderMatch() {
        Assertions.assertTrue(Gender.GIRL.match(Gender.ANY));
        Assertions.assertFalse(Gender.GIRL.match(Gender.BOY));
        Assertions.assertTrue(Gender.GIRL.match(Gender.GIRL));
    }
}
