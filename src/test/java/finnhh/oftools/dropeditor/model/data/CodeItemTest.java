package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CodeItemTest extends DataTest<CodeItem> {
    private final List<ItemReference> itemReferences;

    CodeItemTest() {
        super(new CodeItem());
        mainData.setCode("abc");

        itemReferences = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemReference itemReference = new ItemReference();
            itemReference.setItemID(i);
            fullyConfigure(itemReference);

            itemReferences.add(itemReference);
            mainData.getItemReferenceIDs().add(i);
        }

        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        CodeItem clone = mainData.getEditableClone();

        CodeItem other = new CodeItem();
        other.setCode("def");
        other.getItemReferenceIDs().addAll(4, 5, 6);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getCode(), other.getCode());
        Assertions.assertEquals(clone.getItemReferenceIDs(), other.getItemReferenceIDs());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getCode(), mainData.getCode());
        Assertions.assertEquals(clone.getItemReferenceIDs(), mainData.getItemReferenceIDs());
    }

    @Test
    @Override
    void testConstructBindings() {
        CodeItem other = new CodeItem();
        other.setCode("def");
        other.getItemReferenceIDs().addAll(4, 5, 6);

        Assertions.assertEquals(CodeItem.INT_PLACEHOLDER_ID, other.getCodeID());
        Assertions.assertEquals(CodeItem.UNSET_ID, other.getId());

        other.constructBindings();

        int codeID = other.getCodeID();
        Assertions.assertNotEquals(CodeItem.INT_PLACEHOLDER_ID, codeID);
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(codeID), other.getId());

        other.setCode("ghi");
        Assertions.assertNotEquals(codeID, other.getCodeID());
        Assertions.assertEquals(String.valueOf(other.getCodeID()), other.getId());
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
