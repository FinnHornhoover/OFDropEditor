package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
import finnhh.oftools.dropeditor.model.MapRegionInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Optional;

public class RacingInfoComponent extends VBox implements ObservableComponent<InstanceInfo> {
    private final ObjectProperty<InstanceInfo> instanceInfo;

    private final MainController controller;

    private final RacingInfoTooltipComponent racingInfoTooltipComponent;
    private final Label izNameLabel;
    private final StandardImageView iconView;
    private final Label entryLabel;

    public RacingInfoComponent(double width, MainController controller) {
        instanceInfo = new SimpleObjectProperty<>();

        this.controller = controller;

        racingInfoTooltipComponent = new RacingInfoTooltipComponent(controller);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setGraphic(racingInfoTooltipComponent);

        izNameLabel = new Label();
        izNameLabel.setWrapText(true);
        izNameLabel.setTextAlignment(TextAlignment.CENTER);
        izNameLabel.setTooltip(tooltip);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap());
        Tooltip.install(iconView, tooltip);

        entryLabel = new Label();
        entryLabel.setWrapText(true);
        entryLabel.setTextAlignment(TextAlignment.CENTER);
        entryLabel.setTooltip(tooltip);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(izNameLabel, iconView, entryLabel);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public MainController getController() {
        return controller;
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
        instanceInfo.set(data);
    }

    @Override
    public void cleanUIState() {
        racingInfoTooltipComponent.setObservableAndState(null);
        izNameLabel.setText("Unknown");
        entryLabel.setText(MapRegionInfo.UNKNOWN.areaName() + " - " + MapRegionInfo.UNKNOWN.zoneName());
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        MapRegionInfo entryRegion = Optional.ofNullable(instanceInfo.get())
                .flatMap(ii -> InstanceInfo.getOverworldNPCLocations(
                                controller.getStaticDataStore().getMapRegionInfoList(),
                                ii.getEntryWarpNPCs(controller.getStaticDataStore().getNpcInfoMap())
                        ).stream().findFirst())
                .orElse(MapRegionInfo.UNKNOWN);

        racingInfoTooltipComponent.setObservableAndState(instanceInfo.get());
        izNameLabel.setText(instanceInfo.get().name());
        entryLabel.setText(entryRegion.areaName() + " - " + entryRegion.zoneName());
        iconView.setImage(String.format("ep_big_%02d", instanceInfo.get().EPID()));
    }

    public RacingInfoTooltipComponent getRacingInfoTooltipComponent() {
        return racingInfoTooltipComponent;
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo.get();
    }

    public ReadOnlyObjectProperty<InstanceInfo> instanceInfoProperty() {
        return instanceInfo;
    }

    public Label getIZNameLabel() {
        return izNameLabel;
    }

    public StandardImageView getIconView() {
        return iconView;
    }

    public Label getEntryLabel() {
        return entryLabel;
    }

    public static class RacingInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<InstanceInfo> {
        private final ObjectProperty<InstanceInfo> instanceInfo;

        public RacingInfoTooltipComponent(MainController controller) {
            super(controller);
            instanceInfo = new SimpleObjectProperty<>();
        }

        @Override
        public MainController getController() {
            return controller;
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
            instanceInfo.set(data);
        }

        @Override
        public void cleanUIState() {
            clearMaps();
        }

        @Override
        public void fillUIState() {
            arrangeRacingLocationMaps(instanceInfo.get().EPID());
        }
    }
}
