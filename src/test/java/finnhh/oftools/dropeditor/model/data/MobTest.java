package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MobTest extends DataTest<Mob> {
    private final MobDrop mobDrop;

    MobTest() {
        super(new Mob());
        mainData.setMobID(232);

        mobDrop = new MobDrop();
        mobDrop.setMobDropID(2);
        fullyConfigure(mobDrop);

        mainData.setMobDropID(mobDrop.getMobDropID());
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        Mob clone = mainData.getEditableClone();

        Mob other = new Mob();
        other.setMobDropID(33);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getMobDropID(), other.getMobDropID());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getMobDropID(), mainData.getMobDropID());
    }

    @Test
    @Override
    void testConstructBindings() {
        Mob other = new Mob();
        other.setMobID(6);

        Assertions.assertEquals(Mob.UNSET_ID, other.getId());

        other.constructBindings();

        int mobID = other.getMobID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(mobID), other.getId());

        other.setMobID(9);
        Assertions.assertNotEquals(mobID, other.getMobID());
        Assertions.assertEquals(String.valueOf(other.getMobID()), other.getId());
    }

    @Test
    @Override
    void testRegisterUnregisterReferences() {
        var referenceMap = drops.getReferenceMap();

        Assertions.assertTrue(referenceMap.get(mobDrop).contains(mainData));

        mainData.unregisterReferences(drops);

        Assertions.assertFalse(referenceMap.containsKey(mobDrop));

        mainData.registerReferences(drops);

        Assertions.assertTrue(referenceMap.get(mobDrop).contains(mainData));
    }

    @Test
    @Override
    void testSetChildData() {
        Mob other = new Mob();

        other.setChildData(new Event());
        Assertions.assertEquals(Mob.INT_PLACEHOLDER_ID, other.getMobDropID());

        other.setChildData(mobDrop);
        Assertions.assertEquals(mobDrop.getMobDropID(), other.getMobDropID());
    }
}
