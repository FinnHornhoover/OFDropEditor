package finnhh.oftools.dropeditor.model;

import java.util.List;
import java.util.Objects;

public record NPCTypeInfo(int type, String name, String iconName,
                          List<VendorItemInfo> vendorItems) implements InfoEnum {
    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NPCTypeInfo && this.type == ((NPCTypeInfo) obj).type;
    }
}
