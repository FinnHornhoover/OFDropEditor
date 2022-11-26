package finnhh.oftools.dropeditor.model;

public enum ReferenceMode {
    NONE,
    UNIQUE,
    MULTIPLE;

    public static ReferenceMode forSize(int size) {
        if (size <= 0) return NONE;
        if (size == 1) return UNIQUE;
        return MULTIPLE;
    }
}
