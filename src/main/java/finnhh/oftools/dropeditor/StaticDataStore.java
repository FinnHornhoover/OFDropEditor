package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.*;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticDataStore {
    private final Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap;
    private final Map<Integer, NPCTypeInfo> npcTypeInfoMap;
    private final Map<Integer, List<NPCInfo>> npcInfoMap;
    private final Map<Integer, MobTypeInfo> mobTypeInfoMap;
    private final Map<Integer, List<MobInfo>> mobInfoMap;
    private final Map<Integer, EggTypeInfo> eggTypeInfoMap;
    private final Map<Integer, List<EggInfo>> eggInfoMap;
    private final Map<Integer, MissionTaskInfo> missionTaskInfoMap;
    private final Map<Integer, MissionInfo> missionInfoMap;
    private final Map<Integer, WarpInfo> warpInfoMap;
    private final Map<Long, InstanceInfo> instanceInfoMap;
    private final List<MapRegionInfo> mapRegionInfoList;

    private final Map<Pair<Integer, Integer>, List<VendorItemInfo>> vendorItemMap;
    private final Map<Pair<Integer, Integer>, List<MissionInfo>> rewardMissionMap;
    private final Map<Integer, InstanceInfo> epInstanceMap;
    private final InstanceRegionGroupedMap<Integer, EggInfo> eggInstanceRegionGroupedMap;
    private final InstanceRegionGroupedMap<Integer, NPCInfo> npcInstanceRegionGroupedMap;
    private final InstanceRegionGroupedMap<Integer, MobInfo> mobInstanceRegionGroupedMap;

    public StaticDataStore() {
        itemInfoMap = new HashMap<>();
        npcTypeInfoMap = new HashMap<>();
        npcInfoMap = new HashMap<>();
        mobTypeInfoMap = new HashMap<>();
        mobInfoMap = new HashMap<>();
        eggTypeInfoMap = new HashMap<>();
        eggInfoMap = new HashMap<>();
        missionTaskInfoMap = new HashMap<>();
        missionInfoMap = new HashMap<>();
        warpInfoMap = new HashMap<>();
        instanceInfoMap = new HashMap<>();
        mapRegionInfoList = new ArrayList<>();

        vendorItemMap = new HashMap<>();
        rewardMissionMap = new HashMap<>();
        epInstanceMap = new HashMap<>();
        eggInstanceRegionGroupedMap = new InstanceRegionGroupedMap<>();
        npcInstanceRegionGroupedMap = new InstanceRegionGroupedMap<>();
        mobInstanceRegionGroupedMap = new InstanceRegionGroupedMap<>();
    }

    public Map<Pair<Integer, Integer>, ItemInfo> getItemInfoMap() {
        return itemInfoMap;
    }

    public Map<Integer, NPCTypeInfo> getNpcTypeInfoMap() {
        return npcTypeInfoMap;
    }

    public Map<Integer, List<NPCInfo>> getNpcInfoMap() {
        return npcInfoMap;
    }

    public Map<Integer, MobTypeInfo> getMobTypeInfoMap() {
        return mobTypeInfoMap;
    }

    public Map<Integer, List<MobInfo>> getMobInfoMap() {
        return mobInfoMap;
    }

    public Map<Integer, EggTypeInfo> getEggTypeInfoMap() {
        return eggTypeInfoMap;
    }

    public Map<Integer, List<EggInfo>> getEggInfoMap() {
        return eggInfoMap;
    }

    public Map<Integer, MissionTaskInfo> getMissionTaskInfoMap() {
        return missionTaskInfoMap;
    }

    public Map<Integer, MissionInfo> getMissionInfoMap() {
        return missionInfoMap;
    }

    public Map<Integer, WarpInfo> getWarpInfoMap() {
        return warpInfoMap;
    }

    public Map<Long, InstanceInfo> getInstanceInfoMap() {
        return instanceInfoMap;
    }

    public List<MapRegionInfo> getMapRegionInfoList() {
        return mapRegionInfoList;
    }

    public Map<Pair<Integer, Integer>, List<VendorItemInfo>> getVendorItemMap() {
        return vendorItemMap;
    }

    public Map<Pair<Integer, Integer>, List<MissionInfo>> getRewardMissionMap() {
        return rewardMissionMap;
    }

    public InstanceRegionGroupedMap<Integer, EggInfo> getEggInstanceRegionGroupedMap() {
        return eggInstanceRegionGroupedMap;
    }

    public InstanceRegionGroupedMap<Integer, NPCInfo> getNPCInstanceRegionGroupedMap() {
        return npcInstanceRegionGroupedMap;
    }

    public InstanceRegionGroupedMap<Integer, MobInfo> getMobInstanceRegionGroupedMap() {
        return mobInstanceRegionGroupedMap;
    }

    public Map<Integer, InstanceInfo> getEPInstanceMap() {
        return epInstanceMap;
    }

    public static class InstanceRegionGroupedMap<K, V extends InfoEnum>
            extends HashMap<K, Map<Long, Map<MapRegionInfo, List<V>>>> {
    }
}
