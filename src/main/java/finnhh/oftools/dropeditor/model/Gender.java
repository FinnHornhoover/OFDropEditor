package finnhh.oftools.dropeditor.model;

public enum Gender {
    ANY(0, "Any"),
    BOY(1, "Boy"),
    GIRL(2, "Girl");

    private final int typeID;
    private final String name;

    Gender(int typeID, String name) {
        this.typeID = typeID;
        this.name = name;
    }

    public int getTypeID() {
        return typeID;
    }

    public String getName() {
        return name;
    }

    public boolean match(Gender other) {
        return this == ANY || other == ANY || this == other;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Gender forType(int typeID) {
        Gender[] genders = values();
        return (typeID > -1 && typeID < genders.length) ? genders[typeID] : ANY;
    }
}
