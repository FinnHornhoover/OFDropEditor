package finnhh.oftools.dropeditor.model;

public enum ItemType {
    WEAPON(0, "Weapon", "m_pWeaponItemTable", "wpnicon"),
    SHIRT(1, "Shirt", "m_pShirtsItemTable", "cosicon"),
    PANTS(2, "Pants", "m_pPantsItemTable", "cosicon"),
    SHOES(3, "Shoes", "m_pShoesItemTable", "cosicon"),
    HAT(4, "Hat", "m_pHatItemTable", "cosicon"),
    GLASSES(5, "Glasses", "m_pGlassItemTable", "cosicon"),
    BACKPACK(6, "Backpack", "m_pBackItemTable", "cosicon"),
    GENERAL_ITEM(7, "General Item", "m_pGeneralItemTable", "generalitemicon"),
    NONE(8, "None", "", "error"),
    CRATE(9, "Crate", "m_pChestItemTable", "generalitemicon"),
    VEHICLE(10, "Vehicle", "m_pVehicleItemTable", "vehicle");

    private final int typeID;
    private final String name;
    private final String xdtKey;
    private final String iconPrefix;

    ItemType(int typeID, String name, String xdtKey, String iconPrefix) {
        this.typeID = typeID;
        this.name = name;
        this.xdtKey = xdtKey;
        this.iconPrefix = iconPrefix;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    public String getXDTKey() {
        return xdtKey;
    }

    public String getIconPrefix() {
        return iconPrefix;
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
