package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class ItemSetTest extends DataTest<ItemSet> {
    private final List<ItemReference> itemReferences;

    ItemSetTest() {
        super(new ItemSet());
        mainData.setIgnoreGender(true);
        mainData.setDefaultItemWeight(2);

        itemReferences = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemReference itemReference = new ItemReference();
            itemReference.setItemID(i);
            fullyConfigure(itemReference);

            itemReferences.add(itemReference);
            mainData.getItemReferenceIDs().add(i);
        }

        mainData.getAlterRarityMap().put(0, 1);
        mainData.getAlterItemWeightMap().put(1, 4);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        ItemSet clone = mainData.getEditableClone();

        ItemSet other = new ItemSet();
        other.setIgnoreRarity(true);
        other.setDefaultItemWeight(10);
        other.getAlterGenderMap().put(4, 0);
        other.getAlterItemWeightMap().put(5, 1);
        other.getItemReferenceIDs().addAll(4, 5, 6);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getIgnoreRarity(), other.getIgnoreRarity());
        Assertions.assertEquals(clone.getIgnoreGender(), other.getIgnoreGender());
        Assertions.assertEquals(clone.getDefaultItemWeight(), other.getDefaultItemWeight());
        Assertions.assertEquals(clone.getAlterRarityMap(), other.getAlterRarityMap());
        Assertions.assertEquals(clone.getAlterGenderMap(), other.getAlterGenderMap());
        Assertions.assertEquals(clone.getAlterItemWeightMap(), other.getAlterItemWeightMap());
        Assertions.assertEquals(clone.getItemReferenceIDs(), other.getItemReferenceIDs());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getIgnoreRarity(), mainData.getIgnoreRarity());
        Assertions.assertEquals(clone.getIgnoreGender(), mainData.getIgnoreGender());
        Assertions.assertEquals(clone.getDefaultItemWeight(), mainData.getDefaultItemWeight());
        Assertions.assertEquals(clone.getAlterRarityMap(), mainData.getAlterRarityMap());
        Assertions.assertEquals(clone.getAlterGenderMap(), mainData.getAlterGenderMap());
        Assertions.assertEquals(clone.getAlterItemWeightMap(), mainData.getAlterItemWeightMap());
        Assertions.assertEquals(clone.getItemReferenceIDs(), mainData.getItemReferenceIDs());
    }

    @Test
    @Override
    void testConstructBindings() {
        ItemSet other = new ItemSet();
        other.setItemSetID(3);

        Assertions.assertEquals(ItemSet.UNSET_ID, other.getId());

        other.constructBindings();

        int itemSetID = other.getItemSetID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(itemSetID), other.getId());

        other.setItemSetID(6);
        Assertions.assertNotEquals(itemSetID, other.getItemSetID());
        Assertions.assertEquals(String.valueOf(other.getItemSetID()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        for (ItemReference itemReference : itemReferences)
            Assertions.assertTrue(referenceMap.get(itemReference).contains(mainData));

        mainData.unregisterReferences(drops);

        for (ItemReference itemReference : itemReferences)
            Assertions.assertFalse(referenceMap.containsKey(itemReference));

        mainData.registerReferences(drops);

        for (ItemReference itemReference : itemReferences)
            Assertions.assertTrue(referenceMap.get(itemReference).contains(mainData));
    }
}
