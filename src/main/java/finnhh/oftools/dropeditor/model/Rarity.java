package finnhh.oftools.dropeditor.model;

public enum Rarity {
    ANY(0, "Any"),
    COMMON(1, "Common"),
    UNCOMMON(2, "Uncommon"),
    RARE(3, "Rare"),
    ULTRA_RARE(4, "Ultra Rare");

    private final int typeID;
    private final String name;

    Rarity(int typeID, String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    public boolean match(Rarity other) {
        return this == ANY || other == ANY || this == other;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Rarity forType(int typeID) {
        Rarity[] rarities = values();
        return (typeID > -1 && typeID < rarities.length) ? rarities[typeID] : ANY;
    }
}
