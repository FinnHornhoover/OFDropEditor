package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Mob;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.Map;
import java.util.Objects;

public class MobComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Mob> mob;

    private final Drops drops;
    private final Map<Integer, MobTypeInfo> mobTypeInfoMap;

    private final MobInfoComponent mobInfoComponent;
    private final MobDropComponent mobDropComponent;
    private final Label idLabel;
    private final BorderPane mobBorderPane;

    public MobComponent(Drops drops,
                        Map<Integer, MobTypeInfo> mobTypeInfoMap,
                        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                        Map<String, byte[]> iconMap,
                        ListView<Data> listView) {

        mob = new SimpleObjectProperty<>();

        this.drops = drops;
        this.mobTypeInfoMap = mobTypeInfoMap;

        mobInfoComponent = new MobInfoComponent(iconMap);
        mobDropComponent = new MobDropComponent(drops, itemInfoMap, iconMap, this);

        mobDropComponent.prefWidthProperty().bind(listView.widthProperty()
                .subtract(mobInfoComponent.widthProperty())
                .subtract(28));
        mobDropComponent.setMaxWidth(USE_PREF_SIZE);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        mobBorderPane = new BorderPane();
        mobBorderPane.setTop(idLabel);
        mobBorderPane.setCenter(mobInfoComponent);

        getChildren().addAll(mobBorderPane, mobDropComponent);
        setHgrow(mobDropComponent, Priority.ALWAYS);

        idLabel.setText(Mob.class.getSimpleName() + ": null");
        mobInfoComponent.setDisable(true);
        mobDropComponent.setDisable(true);
        setIdDisable(true);

        mob.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(Mob.class.getSimpleName() + ": null");
                mobInfoComponent.setDisable(true);
                mobDropComponent.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                mobInfoComponent.setDisable(false);
                mobDropComponent.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public ReadOnlyObjectProperty<Mob> getObservable() {
        return mob;
    }

    @Override
    public void setObservable(Data data) {
        mob.set((Mob) data);

        if (mob.isNull().get()) {
            mobInfoComponent.setObservable(null);
            mobDropComponent.setObservable(null);
        } else {
            mobInfoComponent.setObservable(mobTypeInfoMap.get(mob.get().getMobID()));
            mobDropComponent.setObservable(drops.getMobDrops().get(mob.get().getMobDropID()));
        }
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        int newMobDropID = mobDropComponent.getMobDrop().getMobDropID();

        if (newMobDropID != mob.get().getMobDropID())
            mob.get().setMobDropID(newMobDropID);
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    public Mob getMob() {
        return mob.get();
    }

    public ReadOnlyObjectProperty<Mob> mobProperty() {
        return mob;
    }

    public MobInfoComponent getMobComponent() {
        return mobInfoComponent;
    }

    public MobDropComponent getMobDropComponent() {
        return mobDropComponent;
    }

    public BorderPane getMobBorderPane() {
        return mobBorderPane;
    }
}
