package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.ItemType;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import finnhh.oftools.dropeditor.model.data.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.Objects;

public class ImageSummaryBox extends StackPane {
    private final MainController controller;
    private final double imageWidth;
    private final Tooltip tooltip;
    private final StandardImageView iconView;
    private final Label centerLabel;
    private final Label idLabel;
    private final Label extraLabel;

    public ImageSummaryBox(double imageWidth, MainController controller, Data data) {
        this.controller = controller;
        this.imageWidth = imageWidth;

        tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        Tooltip.install(this, tooltip);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), imageWidth);

        String classInitials = data.toString()
                .split(":", 2)[0]
                .replaceAll("[a-z]", "");
        String id = data.getId();

        centerLabel = new Label(classInitials);
        centerLabel.setMinWidth(imageWidth);
        centerLabel.setMinHeight(imageWidth);
        centerLabel.setAlignment(Pos.CENTER);

        idLabel = new Label(id);
        extraLabel = new Label();

        getChildren().addAll(centerLabel, iconView, idLabel);
        StackPane.setAlignment(centerLabel, Pos.CENTER);
        StackPane.setAlignment(iconView, Pos.CENTER);
        StackPane.setAlignment(idLabel, Pos.TOP_LEFT);
        setMaxWidth(imageWidth);
        setMaxHeight(imageWidth);
        setIconAndExtra(data);
    }

    private void setIconAndExtra(Data data) {
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        if (data instanceof Mob m) {
            MobTypeInfo mobTypeInfo = controller.getStaticDataStore().getMobTypeInfoMap().get(m.getMobID());

            if (Objects.nonNull(mobTypeInfo)) {
                tooltip.setText(String.format("%s (%d)", mobTypeInfo.name(), mobTypeInfo.type()));
                iconView.setImage(mobTypeInfo.iconName());
                centerLabel.setText("");
                setExtra(mobTypeInfo.level() + "Lv");
            }
        } else if (data instanceof ItemReference ir) {
            ItemInfo itemInfo = itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType()));

            if (Objects.nonNull(itemInfo)) {
                tooltip.setText(String.format("%s (%d) - %s", itemInfo.name(), itemInfo.id(), itemInfo.getTypeString()));
                iconView.setImage(itemInfo.iconName());
                centerLabel.setText("");
                setExtra(itemInfo.requiredLevel() + "Lv");
            }
        } else if (data instanceof Crate cr) {
            ItemInfo itemInfo = itemInfoMap.get(new Pair<>(cr.getCrateID(), ItemType.CRATE.getTypeID()));

            if (Objects.nonNull(itemInfo)) {
                tooltip.setText(String.format("%s - %s (%d)", itemInfo.name(), itemInfo.comment(), itemInfo.id()));
                iconView.setImage(itemInfo.iconName());
                centerLabel.setText("");
                setExtra(itemInfo.contentLevel() + "Lv");
            }
        } else if (data instanceof Racing r) {
            InstanceInfo instanceInfo = controller.getStaticDataStore().getEPInstanceMap().get(r.getEPID());

            if (Objects.nonNull(instanceInfo)) {
                tooltip.setText(String.format("%s (%d)", instanceInfo.name(), instanceInfo.EPID()));
                iconView.setImage(String.format("ep_small_%02d", instanceInfo.EPID()));
                centerLabel.setText("");
            }
        } else if (data instanceof CodeItem ci) {
            tooltip.setText(ci.getCode());
            idLabel.setText(ci.getCode());
        } else {
            tooltip.setText(data.toString());
        }
    }

    public void setExtra(String extraString) {
        extraLabel.setText(extraString);
        getChildren().add(extraLabel);
        StackPane.setAlignment(extraLabel, Pos.BOTTOM_RIGHT);
    }

    public double getImageWidth() {
        return imageWidth;
    }

    public Label getIdLabel() {
        return idLabel;
    }

    public StandardImageView getIconView() {
        return iconView;
    }

    public Label getCenterLabel() {
        return centerLabel;
    }
}
