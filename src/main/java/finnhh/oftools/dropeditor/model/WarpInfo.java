package finnhh.oftools.dropeditor.model;

import java.util.Optional;

public record WarpInfo(int id, long toInstanceID, int toX, int toY, int warpNPC,
                       Optional<MissionTaskInfo> requiredTask) implements InfoEnum {
}
