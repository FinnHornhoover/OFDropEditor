package finnhh.oftools.dropeditor.model;

import java.util.Objects;

public record ItemInfo(int id, int type, boolean tradeable, boolean sellable, int buyPrice, int sellPrice,
                       int stackSize, int rarity, int requiredLevel, int contentLevel, int pointDamage, int groupDamage,
                       int fireRate, int defense, int gender, int weaponType, String name, String comment,
                       String iconName) implements InfoEnum {
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemInfo &&
                this.id == ((ItemInfo) obj).id &&
                this.type == ((ItemInfo) obj).type;
    }
}
