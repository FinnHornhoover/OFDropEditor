package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
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

public class RacingInfoComponent extends VBox implements ObservableComponent<InstanceInfo> {
    private final ObjectProperty<InstanceInfo> instanceInfo;

    private final MainController controller;

    private final Label izNameLabel;
    private final ImageView iconView;

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

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(izNameLabel, iconView);
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

        instanceInfo.set(data);

        String name = instanceInfo.isNull().get() ?
                "Unknown" :
                instanceInfo.get().name();
        byte[] defaultIcon = iconMap.get("unknown");
        byte[] icon = instanceInfo.isNull().get() ?
                defaultIcon :
                iconMap.getOrDefault(String.format("ep_small_%02d", instanceInfo.get().EPID()), defaultIcon);

        izNameLabel.setText(name);
        iconView.setImage(new Image(new ByteArrayInputStream(icon)));
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
}
