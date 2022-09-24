package finnhh.oftools.dropeditor.model;

import java.util.Optional;
import java.util.Set;

public record InstanceInfo(long id, int zoneX, int zoneY, int EPID, String name,
                           Set<WarpInfo> entryWarps, Optional<MissionTaskInfo> entryTask) implements InfoEnum {
    public static final long OVERWORLD_INSTANCE_ID = 0;
}
