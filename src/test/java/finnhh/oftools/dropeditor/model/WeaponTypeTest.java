package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WeaponTypeTest {
    @Test
    void testToString() {
        for (WeaponType weaponType : WeaponType.values()) {
            String typeString = weaponType.toString();
            Assertions.assertTrue(typeString.contains(weaponType.getName()));
            Assertions.assertTrue(typeString.contains(String.valueOf(ItemType.WEAPON.getTypeID())));
            Assertions.assertTrue(typeString.contains(String.valueOf(weaponType.getTypeID())));
        }
    }

    @Test
    void testForKnownTypeID() {
        for (WeaponType weaponType : WeaponType.values())
            Assertions.assertSame(weaponType, WeaponType.forType(weaponType.getTypeID()));
    }

    @Test
    void testForUnknownTypeID() {
        Assertions.assertSame(WeaponType.NONE, WeaponType.forType(-1));
        Assertions.assertSame(WeaponType.NONE, WeaponType.forType(20));
    }
}
