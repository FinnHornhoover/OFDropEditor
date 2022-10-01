package finnhh.oftools.dropeditor.model;

public enum WeaponType {
    NONE(0, "None"),
    MELEE(1, "Melee"),
    PISTOL(2, "Pistol"),
    SHATTERGUN(3, "Shattergun"),
    RIFLE(4, "Rifle"),
    ROCKET(5, "Rocket"),
    THROWN(6, "Thrown");

    private final int typeID;
    private final String name;

    WeaponType(int typeID, String name) {
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

    public static WeaponType forType(int typeID) {
        WeaponType[] weaponTypes = values();
        return (typeID > -1 && typeID < weaponTypes.length) ? weaponTypes[typeID] : NONE;
    }
}
