package finnhh.oftools.dropeditor.model.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class RacingTest extends DataTest<Racing> {
    private final List<Crate> crates;

    RacingTest() {
        super(new Racing());
        mainData.setEPID(15);
        mainData.setTimeLimit(120);

        crates = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Crate crate = new Crate();
            crate.setCrateID(i + 1);
            fullyConfigure(crate);

            crates.add(crate);
            mainData.getRewards().add(i + 1);
            mainData.getRankScores().add((i + 1) * 1000);
        }

        fullyConfigure(mainData);
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        Racing clone = mainData.getEditableClone();

        Racing other = new Racing();
        other.setEPID(30);
        other.setTimeLimit(240);
        other.getRewards().addAll(10, 11, 12, 13, 14);
        other.getRankScores().addAll(10000, 11000, 12000, 13000, 14000);

        clone.setFieldsFromData(other);
        Assertions.assertEquals(clone.getEPID(), other.getEPID());
        Assertions.assertEquals(clone.getTimeLimit(), other.getTimeLimit());
        Assertions.assertEquals(clone.getRewards(), other.getRewards());
        Assertions.assertEquals(clone.getRankScores(), other.getRankScores());

        clone.setFieldsFromData(mainData);
        Assertions.assertEquals(clone.getEPID(), mainData.getEPID());
        Assertions.assertEquals(clone.getTimeLimit(), mainData.getTimeLimit());
        Assertions.assertEquals(clone.getRewards(), mainData.getRewards());
        Assertions.assertEquals(clone.getRankScores(), mainData.getRankScores());
    }

    @Test
    @Override
    void testConstructBindings() {
        Racing other = new Racing();
        other.setEPID(17);

        Assertions.assertEquals(CrateDropType.UNSET_ID, other.getId());

        other.constructBindings();

        int epid = other.getEPID();
        Assertions.assertTrue(other.malformedProperty().isBound());
        Assertions.assertEquals(String.valueOf(epid), other.getId());

        other.setEPID(31);
        Assertions.assertNotEquals(epid, other.getEPID());
        Assertions.assertEquals(String.valueOf(other.getEPID()), other.getId());
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
