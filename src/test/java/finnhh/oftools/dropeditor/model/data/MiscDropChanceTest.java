package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MiscDropChanceTest extends DataTest<MiscDropChance> {

    MiscDropChanceTest() {
        super(new MiscDropChance());
        mainData.setBoostDropChance(1);
        mainData.setBoostDropChanceTotal(2);
        mainData.setPotionDropChance(2);
        mainData.setPotionDropChanceTotal(3);
        mainData.setTaroDropChance(3);
        mainData.setTaroDropChanceTotal(4);
        mainData.setFMDropChance(4);
        mainData.setFMDropChanceTotal(5);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        MiscDropChance clone = mainData.getEditableClone();

        MiscDropChance other = new MiscDropChance();
        other.setBoostDropChance(6);
        other.setBoostDropChanceTotal(7);
        other.setPotionDropChance(7);
        other.setPotionDropChanceTotal(8);
        other.setTaroDropChance(8);
        other.setTaroDropChanceTotal(9);
        other.setFMDropChance(9);
        other.setFMDropChanceTotal(10);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getBoostDropChance(), other.getBoostDropChance());
        Assertions.assertEquals(clone.getBoostDropChanceTotal(), other.getBoostDropChanceTotal());
        Assertions.assertEquals(clone.getPotionDropChance(), other.getPotionDropChance());
        Assertions.assertEquals(clone.getPotionDropChanceTotal(), other.getPotionDropChanceTotal());
        Assertions.assertEquals(clone.getTaroDropChance(), other.getTaroDropChance());
        Assertions.assertEquals(clone.getTaroDropChanceTotal(), other.getTaroDropChanceTotal());
        Assertions.assertEquals(clone.getFMDropChance(), other.getFMDropChance());
        Assertions.assertEquals(clone.getFMDropChanceTotal(), other.getFMDropChanceTotal());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getBoostDropChance(), mainData.getBoostDropChance());
        Assertions.assertEquals(clone.getBoostDropChanceTotal(), mainData.getBoostDropChanceTotal());
        Assertions.assertEquals(clone.getPotionDropChance(), mainData.getPotionDropChance());
        Assertions.assertEquals(clone.getPotionDropChanceTotal(), mainData.getPotionDropChanceTotal());
        Assertions.assertEquals(clone.getTaroDropChance(), mainData.getTaroDropChance());
        Assertions.assertEquals(clone.getTaroDropChanceTotal(), mainData.getTaroDropChanceTotal());
        Assertions.assertEquals(clone.getFMDropChance(), mainData.getFMDropChance());
        Assertions.assertEquals(clone.getFMDropChanceTotal(), mainData.getFMDropChanceTotal());
    }

    @Test
    @Override
    void testConstructBindings() {
        MiscDropChance other = new MiscDropChance();
        other.setMiscDropChanceID(2);

        Assertions.assertEquals(MiscDropChance.UNSET_ID, other.getId());

        other.constructBindings();

        int miscDropChanceID = other.getMiscDropChanceID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(miscDropChanceID), other.getId());

        other.setMiscDropChanceID(5);
        Assertions.assertNotEquals(miscDropChanceID, other.getMiscDropChanceID());
        Assertions.assertEquals(String.valueOf(other.getMiscDropChanceID()), other.getId());
    }
}
