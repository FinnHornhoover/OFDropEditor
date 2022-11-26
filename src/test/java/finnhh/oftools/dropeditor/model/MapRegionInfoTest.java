package finnhh.oftools.dropeditor.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MapRegionInfoTest {
    private final MapRegionInfo mapRegionInfo = new MapRegionInfo(
            4000,
            95000,
            30000,
            10000,
            "abc",
            "ABC",
            "abc"
    );

    @Test
    void testCoordinatesIncluded() {
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x(),
                mapRegionInfo.y()
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x(),
                mapRegionInfo.y() + mapRegionInfo.height() / 2
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width() / 2,
                mapRegionInfo.y()
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width() / 2,
                mapRegionInfo.y() + mapRegionInfo.height() / 2
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width() / 2,
                mapRegionInfo.y() + mapRegionInfo.height() - 1
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width() - 1,
                mapRegionInfo.y() + mapRegionInfo.height() / 2
        ));
        Assertions.assertTrue(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width() - 1,
                mapRegionInfo.y() + mapRegionInfo.height() - 1
        ));
    }

    @Test
    void testCoordinatesNotIncluded() {
        Assertions.assertFalse(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x(),
                mapRegionInfo.y() + mapRegionInfo.height()
        ));
        Assertions.assertFalse(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width(),
                mapRegionInfo.y()
        ));
        Assertions.assertFalse(mapRegionInfo.coordinatesIncluded(
                mapRegionInfo.x() + mapRegionInfo.width(),
                mapRegionInfo.y() + mapRegionInfo.height()
        ));
    }

    @Test
    void testXToTile() {
        int lastTile = -1;
        for (int i = 0; i < MapRegionInfo.TILE_SIZE * MapRegionInfo.TILE_COUNT; i += MapRegionInfo.TILE_SIZE) {
            int currentTile = MapRegionInfo.xToTile(i);
            Assertions.assertTrue(currentTile >= 0);
            Assertions.assertTrue(currentTile < MapRegionInfo.TILE_COUNT);

            // x tiles increase with coordinates
            Assertions.assertTrue(currentTile > lastTile);
            lastTile = currentTile;
        }
    }

    @Test
    void testYToTile() {
        int lastTile = MapRegionInfo.TILE_COUNT;
        for (int i = 0; i < MapRegionInfo.TILE_SIZE * MapRegionInfo.TILE_COUNT; i += MapRegionInfo.TILE_SIZE) {
            int currentTile = MapRegionInfo.yToTile(i);
            Assertions.assertTrue(currentTile >= 0);
            Assertions.assertTrue(currentTile < MapRegionInfo.TILE_COUNT);

            // y tiles decrease with coordinates
            Assertions.assertTrue(currentTile < lastTile);
            lastTile = currentTile;
        }
    }

    @Test
    void testXToPixel() {
        int pixelTileSize = MapRegionInfo.TILE_SIZE / 100;
        int lastPixel = -1;
        for (int i = 0; i < MapRegionInfo.TILE_SIZE; i += 100) {
            int currentPixel = MapRegionInfo.xToPixel(i, pixelTileSize);
            Assertions.assertTrue(currentPixel >= 0);
            Assertions.assertTrue(currentPixel < pixelTileSize);

            // x pixels increase with coordinates
            Assertions.assertTrue(currentPixel > lastPixel);
            lastPixel = currentPixel;
        }
    }

    @Test
    void testYToPixel() {
        int pixelTileSize = MapRegionInfo.TILE_SIZE / 100;
        int lastPixel = pixelTileSize;
        for (int i = 0; i < MapRegionInfo.TILE_SIZE; i += 100) {
            int currentPixel = MapRegionInfo.yToPixel(i, pixelTileSize);
            Assertions.assertTrue(currentPixel >= 0);
            Assertions.assertTrue(currentPixel < pixelTileSize);

            // y pixels decrease with coordinates
            Assertions.assertTrue(currentPixel < lastPixel);
            lastPixel = currentPixel;
        }
    }
}
