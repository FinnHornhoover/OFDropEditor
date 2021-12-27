package finnhh.oftools.dropeditor.model;

import java.util.Objects;

public record MobTypeInfo(int type, int level, String name, String iconName) {
    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobTypeInfo && this.type == ((MobTypeInfo) obj).type;
    }
}
