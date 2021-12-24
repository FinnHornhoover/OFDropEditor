package finnhh.oftools.dropeditor.model;

public record MobInfo(int id, int level, byte[] icon) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof MobInfo && this.id == ((MobInfo) obj).id;
    }
}
