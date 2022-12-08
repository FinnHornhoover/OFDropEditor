package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MiscDropTypeTest extends DataTest<MiscDropType> {

    MiscDropTypeTest() {
        super(new MiscDropType());
        mainData.setBoostAmount(10);
        mainData.setPotionAmount(20);
        mainData.setTaroAmount(30);
        mainData.setFMAmount(40);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        MiscDropType clone = mainData.getEditableClone();

        MiscDropType other = new MiscDropType();
        other.setBoostAmount(100);
        other.setPotionAmount(200);
        other.setTaroAmount(300);
        other.setFMAmount(400);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getBoostAmount(), other.getBoostAmount());
        Assertions.assertEquals(clone.getPotionAmount(), other.getPotionAmount());
        Assertions.assertEquals(clone.getTaroAmount(), other.getTaroAmount());
        Assertions.assertEquals(clone.getFMAmount(), other.getFMAmount());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getBoostAmount(), mainData.getBoostAmount());
        Assertions.assertEquals(clone.getPotionAmount(), mainData.getPotionAmount());
        Assertions.assertEquals(clone.getTaroAmount(), mainData.getTaroAmount());
        Assertions.assertEquals(clone.getFMAmount(), mainData.getFMAmount());
    }

    @Test
    @Override
    void testConstructBindings() {
        MiscDropType other = new MiscDropType();
        other.setMiscDropTypeID(7);

        Assertions.assertEquals(MiscDropType.UNSET_ID, other.getId());

        other.constructBindings();

        int miscDropTypeID = other.getMiscDropTypeID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(miscDropTypeID), other.getId());

        other.setMiscDropTypeID(9);
        Assertions.assertNotEquals(miscDropTypeID, other.getMiscDropTypeID());
        Assertions.assertEquals(String.valueOf(other.getMiscDropTypeID()), other.getId());
    }
}
