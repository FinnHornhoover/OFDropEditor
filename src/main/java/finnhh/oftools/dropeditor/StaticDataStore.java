package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.*;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticDataStore {
    private final Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap;
    private final Map<Integer, MobTypeInfo> mobTypeInfoMap;
    private final Map<Integer, List<MobInfo>> mobInfoMap;
    private final Map<Integer, EggTypeInfo> eggTypeInfoMap;
    private final Map<Integer, EggInfo> eggInfoMap;

    public StaticDataStore() {
        itemInfoMap = new HashMap<>();
        mobTypeInfoMap = new HashMap<>();
        mobInfoMap = new HashMap<>();
        eggTypeInfoMap = new HashMap<>();
        eggInfoMap = new HashMap<>();
    }

    public Map<Pair<Integer, Integer>, ItemInfo> getItemInfoMap() {
        return itemInfoMap;
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

    public Map<Integer, EggInfo> getEggInfoMap() {
        return eggInfoMap;
    }
}
