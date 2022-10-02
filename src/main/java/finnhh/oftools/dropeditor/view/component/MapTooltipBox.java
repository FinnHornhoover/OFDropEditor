package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
import finnhh.oftools.dropeditor.model.MapRegionInfo;
import finnhh.oftools.dropeditor.model.NPCInfo;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapTooltipBox extends VBox {
    public static final int TOOLTIP_TILE_PIXEL_SIZE = 256;
    public static final int TOOLTIP_MARKER_PIXEL_SIZE = 4;

    private final Label tooltipLabel;
    private final Canvas mapCanvas;
    private final Group mapGroup;

    private final MainController controller;

    private int hash;

    public MapTooltipBox(MainController controller) {
        this.controller = controller;

        tooltipLabel = new Label();
        tooltipLabel.setWrapText(true);
        tooltipLabel.setTextAlignment(TextAlignment.CENTER);

        mapCanvas = new Canvas(TOOLTIP_TILE_PIXEL_SIZE, TOOLTIP_TILE_PIXEL_SIZE);
        mapGroup = new Group(mapCanvas);

        hash = -1;

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setFillWidth(false);
        getChildren().addAll(tooltipLabel, mapGroup);
    }

    public void setMapImage(byte[] icon) {
        Image image = new Image(new ByteArrayInputStream(icon));
        mapCanvas.getGraphicsContext2D().drawImage(image, 0, 0, TOOLTIP_TILE_PIXEL_SIZE, TOOLTIP_TILE_PIXEL_SIZE);
        hash = Objects.hash(hash, Arrays.hashCode(icon));
    }

    public void addMarker(int x, int y) {
        GraphicsContext gc = mapCanvas.getGraphicsContext2D();
        gc.setFill(Color.RED);
        gc.fillRect(
                MapRegionInfo.xToPixel(x, TOOLTIP_TILE_PIXEL_SIZE),
                MapRegionInfo.yToPixel(y, TOOLTIP_TILE_PIXEL_SIZE),
                TOOLTIP_MARKER_PIXEL_SIZE,
                TOOLTIP_MARKER_PIXEL_SIZE
        );
        hash = Objects.hash(hash, x, y);
    }

    public void addLabel(String s) {
        Label entryLabel = new Label(s);
        entryLabel.setWrapText(true);
        entryLabel.setTextAlignment(TextAlignment.CENTER);

        getChildren().add(getChildren().size() - 1, entryLabel);
        hash = Objects.hash(hash, s);
    }

    public Label getTooltipLabel() {
        return tooltipLabel;
    }

    public Canvas getMapCanvas() {
        return mapCanvas;
    }

    public Group getMapGroup() {
        return mapGroup;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MapTooltipBox && this.hash == ((MapTooltipBox) o).hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public void addLocations(long instanceID, MapRegionInfo mri) {
        var instanceInfoMap = controller.getStaticDataStore().getInstanceInfoMap();
        var npcInfoMap = controller.getStaticDataStore().getNpcInfoMap();
        var mapRegionList = controller.getStaticDataStore().getMapRegionInfoList();
        var iconMap = controller.getIconManager().getIconMap();

        setMapImage(iconMap.get(mri.iconName()));

        if (instanceID == InstanceInfo.OVERWORLD_INSTANCE_ID) {
            tooltipLabel.setText(mri.areaName() + " - " + mri.zoneName());
        } else if (instanceInfoMap.containsKey(instanceID)) {
            InstanceInfo ii = instanceInfoMap.get(instanceID);

            tooltipLabel.setText(ii.name());
            hash = Objects.hash(hash, ii.name());

            ii.entryTask()
                    .map(mto -> "Required: " + mto.missionTypeName() + " - " + mto.missionName())
                    .ifPresent(this::addLabel);

            List<NPCInfo> entryNPCs = ii.getEntryWarpNPCs(npcInfoMap);
            List<String> worldEntryStrings = InstanceInfo.getOverworldNPCLocations(mapRegionList, entryNPCs)
                    .stream()
                    .map(mapRegionInfo -> "Entry: " + mapRegionInfo.areaName() + " - " + mapRegionInfo.zoneName())
                    .toList();

            worldEntryStrings.forEach(this::addLabel);

            if (worldEntryStrings.isEmpty()) {
                entryNPCs.stream()
                        .filter(npcInfo -> instanceInfoMap.containsKey(npcInfo.instanceID()))
                        .map(npcInfo -> "Entry: " + instanceInfoMap.get(npcInfo.instanceID()).name())
                        .distinct()
                        .forEach(this::addLabel);
            }
        }
    }
}
