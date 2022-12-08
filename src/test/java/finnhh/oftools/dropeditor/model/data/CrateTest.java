package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CrateTest extends DataTest<Crate> {
    private final RarityWeights rarityWeights;
    private final ItemSet itemSet;

    CrateTest() {
        super(new Crate());
        mainData.setCrateID(1212);

        rarityWeights = new RarityWeights();
        rarityWeights.getWeights().addAll(10, 20, 30, 40);
        fullyConfigure(rarityWeights);

        itemSet = new ItemSet();
        itemSet.setDefaultItemWeight(1);
        fullyConfigure(itemSet);

        mainData.setRarityWeightID(rarityWeights.getRarityWeightID());
        mainData.setItemSetID(itemSet.getItemSetID());

        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        Crate clone = mainData.getEditableClone();

        Crate other = new Crate();
        other.setRarityWeightID(2);
        other.setItemSetID(2);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getRarityWeightID(), other.getRarityWeightID());
        Assertions.assertEquals(clone.getItemSetID(), other.getItemSetID());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getRarityWeightID(), mainData.getRarityWeightID());
        Assertions.assertEquals(clone.getItemSetID(), mainData.getItemSetID());
    }

    @Test
    @Override
    void testConstructBindings() {
        Crate other = new Crate();
        other.setCrateID(2);

        Assertions.assertEquals(Crate.UNSET_ID, other.getId());

        other.constructBindings();

        int crateID = other.getCrateID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(crateID), other.getId());

        other.setCrateID(3);
        Assertions.assertNotEquals(crateID, other.getCrateID());
        Assertions.assertEquals(String.valueOf(other.getCrateID()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        Assertions.assertTrue(referenceMap.get(rarityWeights).contains(mainData));
        Assertions.assertTrue(referenceMap.get(itemSet).contains(mainData));

        mainData.unregisterReferences(drops);

        Assertions.assertFalse(referenceMap.containsKey(rarityWeights));
        Assertions.assertFalse(referenceMap.containsKey(itemSet));

        mainData.registerReferences(drops);

        Assertions.assertTrue(referenceMap.get(rarityWeights).contains(mainData));
        Assertions.assertTrue(referenceMap.get(itemSet).contains(mainData));
    }

    @Test
    @Override
    void testSetChildData() {
        Crate other = new Crate();

        other.setChildData(new Event());
        Assertions.assertEquals(Crate.INT_PLACEHOLDER_ID, other.getRarityWeightID());
        Assertions.assertEquals(Crate.INT_PLACEHOLDER_ID, other.getItemSetID());

        other.setChildData(rarityWeights);
        Assertions.assertEquals(rarityWeights.getRarityWeightID(), other.getRarityWeightID());
        Assertions.assertEquals(Crate.INT_PLACEHOLDER_ID, other.getItemSetID());

        other.setChildData(itemSet);
        Assertions.assertEquals(rarityWeights.getRarityWeightID(), other.getRarityWeightID());
        Assertions.assertEquals(itemSet.getItemSetID(), other.getItemSetID());
    }
}
