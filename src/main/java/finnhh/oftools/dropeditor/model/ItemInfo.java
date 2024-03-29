package finnhh.oftools.dropeditor.model;

import java.util.Objects;

public record ItemInfo(int id, ItemType type, WeaponType weaponType, boolean tradeable, boolean sellable, int buyPrice,
                       int sellPrice, int stackSize, Rarity rarity, Gender gender, int requiredLevel, int contentLevel,
                       int pointDamage, int groupDamage, int fireInitialTime, int fireDeliverTime, int fireDelayTime,
                       int fireDurationTime, int fireRange, int attackAngle, int numberOfTargets, int defense,
                       int vehicleClass, String name, String comment, String iconName) implements InfoEnum {

    public String getTypeString() {
        return type == ItemType.WEAPON ? weaponType.toString() : type.toString();
    }

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
