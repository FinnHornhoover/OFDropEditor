package finnhh.oftools.dropeditor.model;

public enum ItemType {
    WEAPON(0, "Weapon"),
    SHIRT(1, "Shirt"),
    PANTS(2, "Pants"),
    SHOES(3, "Shoes"),
    HAT(4, "Hat"),
    GLASSES(5, "Glasses"),
    BACKPACK(6, "Backpack"),
    GENERAL_ITEM(7, "General Item"),
    NONE(8, "None"),
    CRATE(9, "Crate"),
    VEHICLE(10, "Vehicle");

    private final int typeID;
    private final String name;

    ItemType(int typeID, String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static ItemType forType(int typeID) {
        ItemType[] itemTypes = values();
        return (typeID > -1 && typeID < itemTypes.length) ? itemTypes[typeID] : NONE;
    }
}
