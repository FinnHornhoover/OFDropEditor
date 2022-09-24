package finnhh.oftools.dropeditor.model;

import java.util.Set;

public record MissionInfo(int id, int type, int npc, String name, String typeName, Set<MissionTaskInfo> tasks,
                          int taroReward, int fmReward, Set<ItemInfo> itemRewards) implements InfoEnum {
}
