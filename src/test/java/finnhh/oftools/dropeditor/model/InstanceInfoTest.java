package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class InstanceInfoTest {
    private final NPCTypeInfo npcTypeOne = new NPCTypeInfo(45, "abc", "abc", List.of());
    private final NPCTypeInfo npcTypeTwo = new NPCTypeInfo(54, "def", "def", List.of());
    private final List<NPCInfo> npcInfoListOne = List.of(
            new NPCInfo(npcTypeOne, 40000, 40000, 0),
            new NPCInfo(npcTypeTwo, 90000, 90000, 1)
    );
    private final List<NPCInfo> npcInfoListTwo = List.of(
            new NPCInfo(npcTypeOne, 90000, 90000, 0),
            new NPCInfo(npcTypeTwo, 40000, 40000, 1)
    );
    private final Map<Integer, List<NPCInfo>> npcInfoMap = Map.ofEntries(
            Map.entry(32, npcInfoListOne),
            Map.entry(322, npcInfoListTwo)
    );
    private final List<MapRegionInfo> mapRegionList = List.of(
            new MapRegionInfo(35000, 35000, 10000, 10000, "area1", "zone1", "area1"),
            new MapRegionInfo(85000, 85000, 10000, 10000, "area2", "zone2", "area2")
    );

    @Test
    void testGetEntryWarpNPCs() {
        InstanceInfo zeroInstanceInfo = new InstanceInfo(
                0,
                0,
                0,
                0,
                "Overworld",
                Set.of(new WarpInfo(400, 0, 36000, 36000, 32, Optional.empty())),
                Optional.empty()
        );
        InstanceInfo oneInstanceInfo = new InstanceInfo(
                1,
                1,
                1,
                0,
                "Instance 1",
                Set.of(new WarpInfo(40, 0, 36000, 36000, 322, Optional.empty())),
                Optional.empty()
        );

        Assertions.assertEquals(npcInfoListOne, zeroInstanceInfo.getEntryWarpNPCs(npcInfoMap));
        Assertions.assertEquals(npcInfoListTwo, oneInstanceInfo.getEntryWarpNPCs(npcInfoMap));
    }

    @Test
    void testGetOverworldNPCLocations() {
        List<MapRegionInfo> overworldListOne = InstanceInfo.getOverworldNPCLocations(mapRegionList, npcInfoListOne);
        Assertions.assertEquals(1, overworldListOne.size());
        Assertions.assertEquals(mapRegionList.get(0), overworldListOne.get(0));

        List<MapRegionInfo> overworldListTwo = InstanceInfo.getOverworldNPCLocations(mapRegionList, npcInfoListTwo);
        Assertions.assertEquals(1, overworldListTwo.size());
        Assertions.assertEquals(mapRegionList.get(1), overworldListTwo.get(0));
    }
}
