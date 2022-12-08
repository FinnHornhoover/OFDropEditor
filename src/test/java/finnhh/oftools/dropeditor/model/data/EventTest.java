package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class EventTest extends DataTest<Event> {
    private final MobDrop mobDrop;

    EventTest() {
        super(new Event());
        mainData.setEventID(4);

        mobDrop = new MobDrop();
        mobDrop.setMobDropID(2);
        fullyConfigure(mobDrop);

        mainData.setMobDropID(mobDrop.getMobDropID());
        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        Event clone = mainData.getEditableClone();

        Event other = new Event();
        other.setMobDropID(33);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getMobDropID(), other.getMobDropID());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getMobDropID(), mainData.getMobDropID());
    }

    @Test
    @Override
    void testConstructBindings() {
        Event other = new Event();
        other.setEventID(6);

        Assertions.assertEquals(Event.UNSET_ID, other.getId());

        other.constructBindings();

        int eventID = other.getEventID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(eventID), other.getId());

        other.setEventID(9);
        Assertions.assertNotEquals(eventID, other.getEventID());
        Assertions.assertEquals(String.valueOf(other.getEventID()), other.getId());
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
        Event other = new Event();

        other.setChildData(new Event());
        Assertions.assertEquals(Event.INT_PLACEHOLDER_ID, other.getMobDropID());

        other.setChildData(mobDrop);
        Assertions.assertEquals(mobDrop.getMobDropID(), other.getMobDropID());
    }
}
