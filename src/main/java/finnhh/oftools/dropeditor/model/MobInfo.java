package finnhh.oftools.dropeditor.model;

import java.util.Optional;

public record MobInfo(MobTypeInfo mobTypeInfo, Optional<MobTypeInfo> parentTypeInfo, int x, int y, long instanceID) {
}
