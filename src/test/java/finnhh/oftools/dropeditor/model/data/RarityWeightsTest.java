package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RarityWeightsTest extends DataTest<RarityWeights> {

    RarityWeightsTest() {
        super(new RarityWeights());
        mainData.getWeights().addAll(10, 20, 30, 40);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        RarityWeights clone = mainData.getEditableClone();

        RarityWeights other = new RarityWeights();
        other.getWeights().addAll(23, 27, 24, 26);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getWeights(), other.getWeights());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getWeights(), mainData.getWeights());
    }

    @Test
    @Override
    void testConstructBindings() {
        RarityWeights other = new RarityWeights();
        other.setRarityWeightID(8);

        Assertions.assertEquals(CrateDropType.UNSET_ID, other.getId());

        other.constructBindings();

        int rarityWeightID = other.getRarityWeightID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(rarityWeightID), other.getId());

        other.setRarityWeightID(9);
        Assertions.assertNotEquals(rarityWeightID, other.getRarityWeightID());
        Assertions.assertEquals(String.valueOf(other.getRarityWeightID()), other.getId());
    }
}
