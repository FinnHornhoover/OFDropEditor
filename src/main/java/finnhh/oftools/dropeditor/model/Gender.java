package finnhh.oftools.dropeditor.model;

public enum Gender {
    ANY(0, "Any"),
    BOY(1, "Boy"),
    GIRL(2, "Girl");

    private final int type;
    private final String name;

    Gender(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
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

    public static Gender forType(int type) {
        Gender[] genders = values();
        return (type > -1 && type < genders.length) ? genders[type] : ANY;
    }
}
