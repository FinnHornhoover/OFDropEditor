package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.MobDrop;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.Map;
import java.util.Objects;

public class MobDropComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MobDrop> mobDrop;

    private final Drops drops;

    private final CrateDropChanceComponent crateDropChanceComponent;
    private final CrateDropTypeComponent crateDropTypeComponent;
    private final MiscDropChanceComponent miscDropChanceComponent;
    private final MiscDropTypeComponent miscDropTypeComponent;

    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final VBox contentVBox;
    private final Group idGroup;
    private final Label idLabel;

    public MobDropComponent(Drops drops,
                            Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                            Map<String, byte[]> iconMap,
                            DataComponent parent) {
        this(60.0, 160.0, drops, itemInfoMap, iconMap, parent);
    }

    public MobDropComponent(double boxSpacing,
                            double boxWidth,
                            Drops drops,
                            Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                            Map<String, byte[]> iconMap,
                            DataComponent parent) {

        mobDrop = new SimpleObjectProperty<>();

        this.drops = drops;

        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        crateDropChanceComponent = new CrateDropChanceComponent(boxSpacing, boxWidth, drops, this);
        crateDropTypeComponent = new CrateDropTypeComponent(boxSpacing, boxWidth, drops, itemInfoMap, iconMap, this);
        miscDropChanceComponent = new MiscDropChanceComponent(boxSpacing, boxWidth, drops, this);
        miscDropTypeComponent = new MiscDropTypeComponent(boxSpacing, boxWidth, drops, iconMap, this);

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
        mobDrop.set((MobDrop) data);

        if (mobDrop.isNull().get()) {
            crateDropChanceComponent.setObservable(null);
            crateDropTypeComponent.setObservable(null);
            miscDropChanceComponent.setObservable(null);
            miscDropTypeComponent.setObservable(null);
        } else {
            crateDropChanceComponent.setObservable(
                    drops.getCrateDropChances().get(mobDrop.get().getCrateDropChanceID()));
            crateDropTypeComponent.setObservable(
                    drops.getCrateDropTypes().get(mobDrop.get().getCrateDropTypeID()));
            miscDropChanceComponent.setObservable(
                    drops.getMiscDropChances().get(mobDrop.get().getMiscDropChanceID()));
            miscDropTypeComponent.setObservable(
                    drops.getMiscDropTypes().get(mobDrop.get().getMiscDropTypeID()));
        }
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
