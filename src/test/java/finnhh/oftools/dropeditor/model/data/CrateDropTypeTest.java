package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class CrateDropTypeTest extends DataTest<CrateDropType> {
    private final List<Crate> crates;

    CrateDropTypeTest() {
        super(new CrateDropType());

        crates = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Crate crate = new Crate();
            crate.setCrateID(i + 1);
            fullyConfigure(crate);

            crates.add(crate);
            mainData.getCrateIDs().add(i + 1);
        }

        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        CrateDropType clone = mainData.getEditableClone();

        CrateDropType other = new CrateDropType();
        other.getCrateIDs().addAll(10, 11, 12, 13, 14);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getCrateIDs(), other.getCrateIDs());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getCrateIDs(), mainData.getCrateIDs());
    }

    @Test
    @Override
    void testConstructBindings() {
        CrateDropType other = new CrateDropType();
        other.setCrateDropTypeID(2);

        Assertions.assertEquals(CrateDropType.UNSET_ID, other.getId());

        other.constructBindings();

        int crateDropTypeID = other.getCrateDropTypeID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(crateDropTypeID), other.getId());

        other.setCrateDropTypeID(3);
        Assertions.assertNotEquals(crateDropTypeID, other.getCrateDropTypeID());
        Assertions.assertEquals(String.valueOf(other.getCrateDropTypeID()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        for (Crate crate : crates)
            Assertions.assertTrue(referenceMap.get(crate).contains(mainData));

        mainData.unregisterReferences(drops);

        for (Crate crate : crates)
            Assertions.assertFalse(referenceMap.containsKey(crate));

        mainData.registerReferences(drops);

        for (Crate crate : crates)
            Assertions.assertTrue(referenceMap.get(crate).contains(mainData));
    }
}
