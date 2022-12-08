package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class NanoCapsuleTest extends DataTest<NanoCapsule> {
    private final Crate crate;

    NanoCapsuleTest() {
        super(new NanoCapsule());
        mainData.setNano(37);

        crate = new Crate();
        crate.setCrateID(123);
        fullyConfigure(crate);

        mainData.setCrateID(crate.getCrateID());
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        NanoCapsule clone = mainData.getEditableClone();

        NanoCapsule other = new NanoCapsule();
        other.setCrateID(454);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getCrateID(), other.getCrateID());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getCrateID(), mainData.getCrateID());
    }

    @Test
    @Override
    void testConstructBindings() {
        NanoCapsule other = new NanoCapsule();
        other.setNano(40);

        Assertions.assertEquals(NanoCapsule.UNSET_ID, other.getId());

        other.constructBindings();

        int nano = other.getNano();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(nano), other.getId());

        other.setNano(49);
        Assertions.assertNotEquals(nano, other.getNano());
        Assertions.assertEquals(String.valueOf(other.getNano()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        Assertions.assertTrue(referenceMap.get(crate).contains(mainData));

        mainData.unregisterReferences(drops);

        Assertions.assertFalse(referenceMap.containsKey(crate));

        mainData.registerReferences(drops);

        Assertions.assertTrue(referenceMap.get(crate).contains(mainData));
    }

    @Test
    @Override
    void testSetChildData() {
        NanoCapsule other = new NanoCapsule();

        other.setChildData(new Event());
        Assertions.assertEquals(Crate.INT_CRATE_PLACEHOLDER_ID, other.getCrateID());

        other.setChildData(crate);
        Assertions.assertEquals(crate.getCrateID(), other.getCrateID());
    }
}
