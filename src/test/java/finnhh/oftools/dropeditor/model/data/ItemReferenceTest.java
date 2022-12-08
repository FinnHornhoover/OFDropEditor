package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ItemReferenceTest extends DataTest<ItemReference> {

    ItemReferenceTest() {
        super(new ItemReference());
        mainData.setItemID(90);
        mainData.setType(4);
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        ItemReference clone = mainData.getEditableClone();

        ItemReference other = new ItemReference();
        other.setItemID(91);
        other.setType(3);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getItemID(), other.getItemID());
        Assertions.assertEquals(clone.getType(), other.getType());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getItemID(), mainData.getItemID());
        Assertions.assertEquals(clone.getType(), mainData.getType());
    }

    @Test
    @Override
    void testConstructBindings() {
        ItemReference other = new ItemReference();
        other.setItemReferenceID(5);

        Assertions.assertEquals(Crate.UNSET_ID, other.getId());

        other.constructBindings();

        int itemReferenceID = other.getItemReferenceID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(itemReferenceID), other.getId());

        other.setItemReferenceID(6);
        Assertions.assertNotEquals(itemReferenceID, other.getItemReferenceID());
        Assertions.assertEquals(String.valueOf(other.getItemReferenceID()), other.getId());
    }
}
