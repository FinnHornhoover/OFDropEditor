package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import finnhh.oftools.dropeditor.model.data.Mob;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class ImageSummaryComponent extends StackPane {
    private final MainController controller;
    private final double imageWidth;
    private final ImageView iconView;
    private final Label centerLabel;
    private final Label idLabel;


    public ImageSummaryComponent(double imageWidth, MainController controller, Data data) {
        this.controller = controller;
        this.imageWidth = imageWidth;

        iconView = new ImageView();
        iconView.setFitWidth(imageWidth);
        iconView.setFitHeight(imageWidth);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        String classInitials = data.toString()
                .split(":", 2)[0]
                .replaceAll("[a-z]", "");
        String id = data.getId();

        centerLabel = new Label(classInitials);
        centerLabel.setMinWidth(imageWidth);
        centerLabel.setMinHeight(imageWidth);
        centerLabel.setAlignment(Pos.CENTER);

        idLabel = new Label(id);

        getChildren().addAll(centerLabel, iconView, idLabel);
        StackPane.setAlignment(centerLabel, Pos.CENTER);
        StackPane.setAlignment(iconView, Pos.CENTER);
        StackPane.setAlignment(idLabel, Pos.TOP_LEFT);
        setMaxWidth(imageWidth);
        setMaxHeight(imageWidth);
        setIcon(data);
    }

    private void setIcon(Data data) {
        var iconMap = controller.getIconManager().getIconMap();

        if (data instanceof Mob m) {
            MobTypeInfo mobTypeInfo = controller.getStaticDataStore().getMobTypeInfoMap().get(m.getMobID());

            if (Objects.nonNull(mobTypeInfo) && iconMap.containsKey(mobTypeInfo.iconName())) {
                iconView.setImage(new Image(new ByteArrayInputStream(iconMap.get(mobTypeInfo.iconName()))));
                centerLabel.setText("");
            }
        } else if (data instanceof ItemReference ir) {
            ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                    ir.getItemID(), ir.getType()));

            if (Objects.nonNull(itemInfo) && iconMap.containsKey(itemInfo.iconName())) {
                iconView.setImage(new Image(new ByteArrayInputStream(iconMap.get(itemInfo.iconName()))));
                centerLabel.setText("");
            }
        } else if (data instanceof Crate cr) {
            ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(cr.getCrateID(), 9));

            if (Objects.nonNull(itemInfo) && iconMap.containsKey(itemInfo.iconName())) {
                iconView.setImage(new Image(new ByteArrayInputStream(iconMap.get(itemInfo.iconName()))));
                centerLabel.setText("");
            }
        }
    }

    public double getImageWidth() {
        return imageWidth;
    }

    public Label getIdLabel() {
        return idLabel;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public Label getCenterLabel() {
        return centerLabel;
    }
}
