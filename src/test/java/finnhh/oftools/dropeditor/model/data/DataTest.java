package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

abstract class DataTest<T extends Data> {
    final Drops drops;
    final T mainData;

    DataTest(T mainData) {
        drops = new Drops();
        drops.manageMaps();

        this.mainData = mainData;
    }

    void fullyConfigure(Data data) {
        data.constructBindings();
        drops.add(data);
        data.registerReferences(drops);
    }

    @Test
    void testGetEditableClone() {
        Data clone = mainData.getEditableClone();
        clone.constructBindings();

        Assertions.assertEquals(mainData, clone);
        Assertions.assertNotSame(mainData, clone);
    }

    @Test
    void testSetFieldsFromInvalidData() {
        Data wrongData = (mainData instanceof Event) ? new Mob() : new Event();
        Assertions.assertThrows(ClassCastException.class, () -> mainData.setFieldsFromData(wrongData));
    }

    abstract void testSetFieldsFromValidData();

    abstract void testConstructBindings();

    void testRegisterUnregisterReferences() {
    }

    void testSetChildData() {
    }
}
