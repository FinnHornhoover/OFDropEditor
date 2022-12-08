package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MobDropTest extends DataTest<MobDrop> {
    private final CrateDropChance crateDropChance;
    private final CrateDropType crateDropType;
    private final MiscDropChance miscDropChance;
    private final MiscDropType miscDropType;

    MobDropTest() {
        super(new MobDrop());
        mainData.setMobDropID(4);

        crateDropChance = new CrateDropChance();
        fullyConfigure(crateDropChance);

        crateDropType = new CrateDropType();
        fullyConfigure(crateDropType);

        miscDropChance = new MiscDropChance();
        fullyConfigure(miscDropChance);

        miscDropType = new MiscDropType();
        fullyConfigure(miscDropType);

        mainData.setCrateDropChanceID(crateDropChance.getCrateDropChanceID());
        mainData.setCrateDropTypeID(crateDropType.getCrateDropTypeID());
        mainData.setMiscDropChanceID(miscDropChance.getMiscDropChanceID());
        mainData.setMiscDropTypeID(miscDropType.getMiscDropTypeID());
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        MobDrop clone = mainData.getEditableClone();

        MobDrop other = new MobDrop();
        other.setCrateDropChanceID(3);
        other.setCrateDropTypeID(4);
        other.setMiscDropChanceID(5);
        other.setMiscDropTypeID(6);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getCrateDropChanceID(), other.getCrateDropChanceID());
        Assertions.assertEquals(clone.getCrateDropTypeID(), other.getCrateDropTypeID());
        Assertions.assertEquals(clone.getMiscDropChanceID(), other.getMiscDropChanceID());
        Assertions.assertEquals(clone.getMiscDropTypeID(), other.getMiscDropTypeID());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getCrateDropChanceID(), mainData.getCrateDropChanceID());
        Assertions.assertEquals(clone.getCrateDropTypeID(), mainData.getCrateDropTypeID());
        Assertions.assertEquals(clone.getMiscDropChanceID(), mainData.getMiscDropChanceID());
        Assertions.assertEquals(clone.getMiscDropTypeID(), mainData.getMiscDropTypeID());
    }

    @Test
    @Override
    void testConstructBindings() {
        MobDrop other = new MobDrop();
        other.setMobDropID(44);

        Assertions.assertEquals(MobDrop.UNSET_ID, other.getId());

        other.constructBindings();

        int mobDropID = other.getMobDropID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(mobDropID), other.getId());

        other.setMobDropID(33);
        Assertions.assertNotEquals(mobDropID, other.getMobDropID());
        Assertions.assertEquals(String.valueOf(other.getMobDropID()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        Assertions.assertTrue(referenceMap.get(crateDropChance).contains(mainData));
        Assertions.assertTrue(referenceMap.get(crateDropType).contains(mainData));
        Assertions.assertTrue(referenceMap.get(miscDropChance).contains(mainData));
        Assertions.assertTrue(referenceMap.get(miscDropType).contains(mainData));

        mainData.unregisterReferences(drops);

        Assertions.assertFalse(referenceMap.containsKey(crateDropChance));
        Assertions.assertFalse(referenceMap.containsKey(crateDropType));
        Assertions.assertFalse(referenceMap.containsKey(miscDropChance));
        Assertions.assertFalse(referenceMap.containsKey(miscDropType));

        mainData.registerReferences(drops);

        Assertions.assertTrue(referenceMap.get(crateDropChance).contains(mainData));
        Assertions.assertTrue(referenceMap.get(crateDropType).contains(mainData));
        Assertions.assertTrue(referenceMap.get(miscDropChance).contains(mainData));
        Assertions.assertTrue(referenceMap.get(miscDropType).contains(mainData));
    }

    @Test
    @Override
    void testSetChildData() {
        MobDrop other = new MobDrop();

        other.setChildData(new Event());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getCrateDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getCrateDropTypeID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropTypeID());

        other.setChildData(crateDropChance);
        Assertions.assertEquals(crateDropChance.getCrateDropChanceID(), other.getCrateDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getCrateDropTypeID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropTypeID());

        other.setChildData(crateDropType);
        Assertions.assertEquals(crateDropChance.getCrateDropChanceID(), other.getCrateDropChanceID());
        Assertions.assertEquals(crateDropType.getCrateDropTypeID(), other.getCrateDropTypeID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropTypeID());

        other.setChildData(miscDropChance);
        Assertions.assertEquals(crateDropChance.getCrateDropChanceID(), other.getCrateDropChanceID());
        Assertions.assertEquals(crateDropType.getCrateDropTypeID(), other.getCrateDropTypeID());
        Assertions.assertEquals(miscDropChance.getMiscDropChanceID(), other.getMiscDropChanceID());
        Assertions.assertEquals(MobDrop.INT_PLACEHOLDER_ID, other.getMiscDropTypeID());

        other.setChildData(miscDropType);
        Assertions.assertEquals(crateDropChance.getCrateDropChanceID(), other.getCrateDropChanceID());
        Assertions.assertEquals(crateDropType.getCrateDropTypeID(), other.getCrateDropTypeID());
        Assertions.assertEquals(miscDropChance.getMiscDropChanceID(), other.getMiscDropChanceID());
        Assertions.assertEquals(miscDropType.getMiscDropTypeID(), other.getMiscDropTypeID());
    }
}
