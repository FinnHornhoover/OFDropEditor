package finnhh.oftools.dropeditor.model.data;

import java.util.List;
import java.util.Map;

public class ItemSet {
    public int ItemSetID;
    public boolean IgnoreRarity;
    public boolean IgnoreGender;
    public int DefaultItemWeight;
    public Map<Integer, Integer> AlterRarityMap;
    public Map<Integer, Integer> AlterGenderMap;
    public Map<Integer, Integer> AlterItemWeightMap;
    public List<Integer> ItemReferenceIDs;
}
