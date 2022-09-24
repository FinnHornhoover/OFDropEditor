package finnhh.oftools.dropeditor.model;

public record MapRegionInfo(int x, int y, int width, int height,
                            String areaName, String zoneName, String iconName) implements InfoEnum {
    public static final int TILE_COUNT = 16;
    public static final int TILE_SIZE = 51200;
    public static final MapRegionInfo UNKNOWN = new MapRegionInfo(
            0,
            0,
            TILE_COUNT * TILE_SIZE,
            TILE_COUNT * TILE_SIZE,
            "Unknown",
            "Unknown",
            "minimap_1_7"
    );

    public boolean coordinatesIncluded(int x, int y) {
        return this.x <= x && x < this.x + width && this.y <= y && y < this.y + height;
    }

    public static int xToTile(int x) {
        return x / TILE_SIZE;
    }

    public static int yToTile(int y) {
        return TILE_COUNT - 1 - y / TILE_SIZE;
    }

    public static int xToPixel(int x, int pixelTileSize) {
        return (x % TILE_SIZE) * pixelTileSize / TILE_SIZE;
    }

    public static int yToPixel(int y, int pixelTileSize) {
        return pixelTileSize - (y % TILE_SIZE) * pixelTileSize / TILE_SIZE;
    }
}
