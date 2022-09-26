package finnhh.oftools.dropeditor.model;

import java.util.*;

public record InstanceInfo(long id, int zoneX, int zoneY, int EPID, String name,
                           Set<WarpInfo> entryWarps, Optional<MissionTaskInfo> entryTask) implements InfoEnum {
    public static final long OVERWORLD_INSTANCE_ID = 0;

    public List<NPCInfo> getEntryWarpNPCs(Map<Integer, List<NPCInfo>> npcInfoMap) {
        return entryWarps.stream()
                .map(wi -> npcInfoMap.get(wi.warpNPC()))
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .toList();
    }

    public static List<MapRegionInfo> getOverworldNPCLocations(List<MapRegionInfo> mapRegionList,
                                                               List<NPCInfo> npcInfoList) {
        return npcInfoList.stream()
                .filter(npcInfo -> npcInfo.instanceID() == OVERWORLD_INSTANCE_ID)
                .flatMap(npcInfo -> mapRegionList.stream()
                        .filter(mri -> mri.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                        .findFirst()
                        .stream())
                .distinct()
                .toList();
    }
}
