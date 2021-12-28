package finnhh.oftools.dropeditor.model;

public enum ViewMode {
    MONSTER("Monsters");

    private final String mode;

    ViewMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "View Mode: " + mode;
    }
}
