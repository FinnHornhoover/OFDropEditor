package finnhh.oftools.dropeditor.model.data;

import finnhh.oftools.dropeditor.model.Gender;
import finnhh.oftools.dropeditor.model.Rarity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemDropTest extends DataTest<ItemDrop> {

    ItemDropTest() {
        super(new ItemDrop());
        mainData.setItemReferenceID(5);
        mainData.setItemID(90);
        mainData.setType(4);
        mainData.setRarity(Rarity.ULTRA_RARE);
        mainData.setGender(Gender.GIRL);
        mainData.setWeight(4);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        ItemDrop clone = mainData.getEditableClone();

        ItemDrop other = new ItemDrop();
        other.setItemID(91);
        other.setType(3);
        other.setRarity(Rarity.COMMON);
        other.setGender(Gender.BOY);
        other.setWeight(0);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getItemID(), other.getItemID());
        Assertions.assertEquals(clone.getType(), other.getType());
        Assertions.assertSame(clone.getRarity(), other.getRarity());
        Assertions.assertSame(clone.getGender(), other.getGender());
        Assertions.assertEquals(clone.getWeight(), other.getWeight());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getItemID(), mainData.getItemID());
        Assertions.assertEquals(clone.getType(), mainData.getType());
        Assertions.assertSame(clone.getRarity(), mainData.getRarity());
        Assertions.assertSame(clone.getGender(), mainData.getGender());
        Assertions.assertEquals(clone.getWeight(), mainData.getWeight());
    }

    @Test
    @Override
    void testConstructBindings() {
        ItemDrop clone = mainData.getEditableClone();

        clone.constructBindings();
        clone.setWeight(-1);
        Assertions.assertTrue(clone.isMalformed());
        clone.setWeight(4);
        Assertions.assertFalse(clone.isMalformed());
    }
}
