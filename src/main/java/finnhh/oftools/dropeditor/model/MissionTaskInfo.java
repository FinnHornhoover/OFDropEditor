package finnhh.oftools.dropeditor.model;

import java.util.Set;

public record MissionTaskInfo(int id, int type, int missionID, int missionType, int npc, String missionName,
                              String missionTypeName, int taroReward, int fmReward,
                              Set<ItemInfo> itemRewards) implements InfoEnum {
}
