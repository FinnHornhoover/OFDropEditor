package finnhh.oftools.dropeditor.model;

public enum EditMode {
    ASK("Ask"),
    IN_PLACE("In-Place"),
    COPY("Copy");

    private final String mode;

    EditMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "Edit Mode: " + mode;
    }
}
