package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CrateDropChanceTest extends DataTest<CrateDropChance> {

    CrateDropChanceTest() {
        super(new CrateDropChance());
        mainData.setDropChance(1);
        mainData.setDropChanceTotal(3);
        mainData.getCrateTypeDropWeights().addAll(10, 20, 30, 40);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        CrateDropChance clone = mainData.getEditableClone();

        CrateDropChance other = new CrateDropChance();
        other.setDropChance(9);
        other.setDropChanceTotal(10);
        other.getCrateTypeDropWeights().addAll(40, 30, 20, 10);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getDropChance(), other.getDropChance());
        Assertions.assertEquals(clone.getDropChanceTotal(), other.getDropChanceTotal());
        Assertions.assertEquals(clone.getCrateTypeDropWeights(), other.getCrateTypeDropWeights());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getDropChance(), mainData.getDropChance());
        Assertions.assertEquals(clone.getDropChanceTotal(), mainData.getDropChanceTotal());
        Assertions.assertEquals(clone.getCrateTypeDropWeights(), mainData.getCrateTypeDropWeights());
    }

    @Test
    @Override
    void testConstructBindings() {
        CrateDropChance other = new CrateDropChance();
        other.setCrateDropChanceID(2);

        Assertions.assertEquals(CrateDropChance.UNSET_ID, other.getId());

        other.constructBindings();

        int crateDropChanceID = other.getCrateDropChanceID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(crateDropChanceID), other.getId());

        other.setCrateDropChanceID(3);
        Assertions.assertNotEquals(crateDropChanceID, other.getCrateDropChanceID());
        Assertions.assertEquals(String.valueOf(other.getCrateDropChanceID()), other.getId());
    }
}
