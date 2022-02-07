package finnhh.oftools.dropeditor.model;

public enum Rarity {
    ANY(0, "Any"),
    COMMON(1, "Common"),
    UNCOMMON(2, "Uncommon"),
    RARE(3, "Rare"),
    ULTRA_RARE(4, "Ultra Rare");

    private final int type;
    private final String name;

    Rarity(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
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

    public static Rarity forType(int type) {
        Rarity[] rarities = values();
        return (type > -1 && type < rarities.length) ? rarities[type] : ANY;
    }
}
