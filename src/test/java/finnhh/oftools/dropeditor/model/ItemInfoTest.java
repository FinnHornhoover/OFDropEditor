package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemInfoTest {
    static ItemInfo makeItemInfo(ItemType itemType, WeaponType weaponType) {
        return new ItemInfo(
                1,
                itemType,
                weaponType,
                true,
                true,
                0,
                0,
                1,
                Rarity.RARE,
                Gender.ANY,
                12,
                12,
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                90,
                3,
                0,
                0,
                "abc",
                "abcabc",
                "abc"
        );
    }

    @Test
    void testWeaponTypeString() {
        for (WeaponType weaponType : WeaponType.values()) {
            Assertions.assertEquals(weaponType.toString(),
                    makeItemInfo(ItemType.WEAPON, weaponType).getTypeString());
        }
    }

    @Test
    void testOtherTypeString() {
        for (ItemType itemType : ItemType.values()) {
            if (itemType == ItemType.WEAPON)
                continue;

            Assertions.assertEquals(itemType.toString(),
                    makeItemInfo(itemType, WeaponType.NONE).getTypeString());
        }
    }
}
