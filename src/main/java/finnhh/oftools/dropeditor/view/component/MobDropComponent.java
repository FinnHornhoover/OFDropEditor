package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.MobDrop;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class MobDropComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MobDrop> mobDrop;

    private final CrateDropChanceComponent crateDropChanceComponent;
    private final CrateDropTypeComponent crateDropTypeComponent;
    private final MiscDropChanceComponent miscDropChanceComponent;
    private final MiscDropTypeComponent miscDropTypeComponent;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final VBox contentVBox;
    private final Group idGroup;
    private final Label idLabel;

    private final EventHandler<MouseEvent> idClickHandler;

    public MobDropComponent(double boxSpacing,
                            double boxWidth,
                            MainController controller,
                            DataComponent parent) {

        mobDrop = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;

        crateDropChanceComponent = new CrateDropChanceComponent(boxSpacing, boxWidth, controller, this);
        crateDropTypeComponent = new CrateDropTypeComponent(boxSpacing, boxWidth, controller, this);
        miscDropChanceComponent = new MiscDropChanceComponent(boxSpacing, boxWidth, controller, this);
        miscDropTypeComponent = new MiscDropTypeComponent(boxSpacing, boxWidth, controller, this);

        contentVBox = new VBox(miscDropChanceComponent,
                miscDropTypeComponent,
                crateDropChanceComponent,
                crateDropTypeComponent);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().addAll("id-label", "vertical");

        idGroup = new Group(idLabel);

        setLeft(idGroup);
        setCenter(contentVBox);

        idLabel.setText(MobDrop.class.getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(MobDrop.class)
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        mobDrop.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(MobDrop.class.getSimpleName() + ": null");
                contentVBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentVBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public void setObservable(Data data) {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        mobDrop.set((MobDrop) data);

        if (mobDrop.isNull().get()) {
            crateDropChanceComponent.setObservable(null);
            crateDropTypeComponent.setObservable(null);
            miscDropChanceComponent.setObservable(null);
            miscDropTypeComponent.setObservable(null);
        } else {
            var drops = controller.getDrops();

            crateDropChanceComponent.setObservable(
                    drops.getCrateDropChances().get(mobDrop.get().getCrateDropChanceID()));
            crateDropTypeComponent.setObservable(
                    drops.getCrateDropTypes().get(mobDrop.get().getCrateDropTypeID()));
            miscDropChanceComponent.setObservable(
                    drops.getMiscDropChances().get(mobDrop.get().getMiscDropChanceID()));
            miscDropTypeComponent.setObservable(
                    drops.getMiscDropTypes().get(mobDrop.get().getMiscDropTypeID()));
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void refreshObservable(Drops drops) {
        int newCrateDropChanceID = crateDropChanceComponent.getCrateDropChance().getCrateDropChanceID();
        int newCrateDropTypeID = crateDropTypeComponent.getCrateDropType().getCrateDropTypeID();
        int newMiscDropChanceID = miscDropChanceComponent.getMiscDropChance().getMiscDropChanceID();
        int newMiscDropTypeID = miscDropTypeComponent.getMiscDropType().getMiscDropTypeID();

        if (newCrateDropChanceID != mobDrop.get().getCrateDropChanceID())
            mobDrop.get().setCrateDropChanceID(newCrateDropChanceID);

        if (newCrateDropTypeID != mobDrop.get().getCrateDropTypeID())
            mobDrop.get().setCrateDropTypeID(newCrateDropTypeID);

        if (newMiscDropChanceID != mobDrop.get().getMiscDropChanceID())
            mobDrop.get().setMiscDropChanceID(newMiscDropChanceID);

        if (newMiscDropTypeID != mobDrop.get().getMiscDropTypeID())
            mobDrop.get().setMiscDropTypeID(newMiscDropTypeID);
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public MobDrop getMobDrop() {
        return mobDrop.get();
    }

    public ObjectProperty<MobDrop> mobDropProperty() {
        return mobDrop;
    }

    public CrateDropChanceComponent getCrateDropChanceComponent() {
        return crateDropChanceComponent;
    }

    public CrateDropTypeComponent getCrateDropTypeComponent() {
        return crateDropTypeComponent;
    }

    public MiscDropChanceComponent getMiscDropChanceComponent() {
        return miscDropChanceComponent;
    }

    public MiscDropTypeComponent getMiscDropTypeComponent() {
        return miscDropTypeComponent;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    public Group getIdGroup() {
        return idGroup;
    }

    @Override
    public ReadOnlyObjectProperty<MobDrop> getObservable() {
        return mobDrop;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }
}
