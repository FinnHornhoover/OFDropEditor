package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class MobInfoComponent extends VBox implements ObservableComponent<MobTypeInfo> {
    private final ObjectProperty<MobTypeInfo> mobTypeInfo;

    private final MainController controller;

    private final TooltipBoxContainer mobInfoTooltipBoxContainer;
    private final Label mobNameLabel;
    private final StandardImageView iconView;

    public MobInfoComponent(double width, MainController controller) {
        mobTypeInfo = new SimpleObjectProperty<>();

        this.controller = controller;

        mobInfoTooltipBoxContainer = new TooltipBoxContainer(9, 36, controller);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setGraphic(mobInfoTooltipBoxContainer);

        mobNameLabel = new Label();
        mobNameLabel.setWrapText(true);
        mobNameLabel.setTextAlignment(TextAlignment.CENTER);
        mobNameLabel.setTooltip(tooltip);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);
        Tooltip.install(iconView, tooltip);

        setSpacing(2);
        setAlignment(Pos.CENTER);
        setMinWidth(width);
        setMaxWidth(width);
        getChildren().addAll(mobNameLabel, iconView);
        getStyleClass().add("bordered-pane");
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public Class<MobTypeInfo> getObservableClass() {
        return MobTypeInfo.class;
    }

    @Override
    public ReadOnlyObjectProperty<MobTypeInfo> getObservable() {
        return mobTypeInfo;
    }

    @Override
    public void setObservable(MobTypeInfo data) {
        mobTypeInfo.set(data);
    }

    @Override
    public void cleanUIState() {
        mobInfoTooltipBoxContainer.clearGraphics();
        mobNameLabel.setText("Unknown Mob");
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        mobInfoTooltipBoxContainer.arrangeMobLocationMaps(mobTypeInfo.get().type());
        mobNameLabel.setText(mobTypeInfo.get().name());
        iconView.setImage(mobTypeInfo.get().iconName());
    }

    public MobTypeInfo getMobTypeInfo() {
        return mobTypeInfo.get();
    }

    public ReadOnlyObjectProperty<MobTypeInfo> mobTypeInfoProperty() {
        return mobTypeInfo;
    }

    public TooltipBoxContainer getMobInfoTooltipBoxContainer() {
        return mobInfoTooltipBoxContainer;
    }

    public Label getMobNameLabel() {
        return mobNameLabel;
    }

    public StandardImageView getIconView() {
        return iconView;
    }
}
