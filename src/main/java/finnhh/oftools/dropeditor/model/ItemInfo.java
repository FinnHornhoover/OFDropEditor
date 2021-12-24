package finnhh.oftools.dropeditor.model;

public record ItemInfo(int id, int type, int gender, byte[] icon) {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof ItemInfo && this.id == ((ItemInfo) obj).id && this.type == ((ItemInfo) obj).type;
    }
}
