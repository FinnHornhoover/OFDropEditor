package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
import finnhh.oftools.dropeditor.model.MapRegionInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.util.Optional;

public class RacingInfoComponent extends VBox implements ObservableComponent<InstanceInfo> {
    private final ObjectProperty<InstanceInfo> instanceInfo;

    private final MainController controller;

    private final Label izNameLabel;
    private final ImageView iconView;
    private final Label entryLabel;

    public RacingInfoComponent(double width, MainController controller) {
        instanceInfo = new SimpleObjectProperty<>();

        this.controller = controller;

        izNameLabel = new Label();
        izNameLabel.setWrapText(true);
        izNameLabel.setTextAlignment(TextAlignment.CENTER);

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        entryLabel = new Label();
        entryLabel.setWrapText(true);
        entryLabel.setTextAlignment(TextAlignment.CENTER);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(izNameLabel, iconView, entryLabel);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public Class<InstanceInfo> getObservableClass() {
        return InstanceInfo.class;
    }

    @Override
    public ReadOnlyObjectProperty<InstanceInfo> getObservable() {
        return instanceInfo;
    }

    @Override
    public void setObservable(InstanceInfo data) {
        var iconMap = controller.getIconManager().getIconMap();
        var npcInfoMap = controller.getStaticDataStore().getNpcInfoMap();
        var mapRegionList = controller.getStaticDataStore().getMapRegionInfoList();

        instanceInfo.set(data);

        String name = instanceInfo.isNull().get() ?
                "Unknown" :
                instanceInfo.get().name();
        byte[] defaultIcon = iconMap.get("unknown");
        byte[] icon = instanceInfo.isNull().get() ?
                defaultIcon :
                iconMap.getOrDefault(String.format("ep_small_%02d", instanceInfo.get().EPID()), defaultIcon);
        MapRegionInfo entryRegion = Optional.ofNullable(instanceInfo.get())
                .flatMap(ii -> InstanceInfo.getOverworldNPCLocations(mapRegionList, ii.getEntryWarpNPCs(npcInfoMap))
                        .stream().findFirst())
                .orElse(MapRegionInfo.UNKNOWN);
        String entry = entryRegion.areaName() + " - " + entryRegion.zoneName();

        izNameLabel.setText(name);
        iconView.setImage(new Image(new ByteArrayInputStream(icon)));
        entryLabel.setText(entry);
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo.get();
    }

    public ReadOnlyObjectProperty<InstanceInfo> instanceInfoProperty() {
        return instanceInfo;
    }

    public Label getIzNameLabel() {
        return izNameLabel;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public Label getEntryLabel() {
        return entryLabel;
    }
}
