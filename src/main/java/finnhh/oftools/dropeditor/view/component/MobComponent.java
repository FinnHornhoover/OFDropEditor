package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Mob;
import finnhh.oftools.dropeditor.model.data.MobDrop;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class MobComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Mob> mob;

    private final MainController controller;

    private final MobInfoComponent mobInfoComponent;
    private final MobDropComponent mobDropComponent;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;
    private final BorderPane mobBorderPane;

    private final EventHandler<MouseEvent> removeClickHandler;

    public MobComponent(MainController controller, ListView<Data> listView) {
        mob = new SimpleObjectProperty<>();

        this.controller = controller;

        mobInfoComponent = new MobInfoComponent(160.0, controller);
        mobDropComponent = new MobDropComponent(60.0, 160.0, controller, this);

        mobDropComponent.prefWidthProperty().bind(listView.widthProperty()
                .subtract(mobInfoComponent.widthProperty())
                .subtract(28));
        mobDropComponent.setMaxWidth(USE_PREF_SIZE);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        mobBorderPane = new BorderPane();
        mobBorderPane.setTop(idHBox);
        mobBorderPane.setCenter(mobInfoComponent);

        getChildren().addAll(mobBorderPane, mobDropComponent);
        setHgrow(mobDropComponent, Priority.ALWAYS);

        removeClickHandler = event -> {
            this.controller.getDrops().remove(mob.get());
            this.controller.getDrops().getReferenceMap().values().forEach(set -> set.remove(mob.get()));
            listView.getItems().remove(mob.get());
        };

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        mobInfoComponent.setDisable(true);
        mobDropComponent.setDisable(true);
        setIdDisable(true);

        mob.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
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
    public Class<Mob> getObservableClass() {
        return Mob.class;
    }

    @Override
    public ReadOnlyObjectProperty<Mob> getObservable() {
        return mob;
    }

    @Override
    public void setObservable(Data data) {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);

        mob.set((Mob) data);

        if (mob.isNull().get()) {
            mobInfoComponent.setObservable(null);
            mobDropComponent.setObservable(null);
        } else {
            mobInfoComponent.setObservable(controller.getStaticDataStore().getMobTypeInfoMap().get(mob.get().getMobID()));
            mobDropComponent.setObservable(controller.getDrops().getMobDrops().get(mob.get().getMobDropID()));
        }

        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        MobDrop newMobDrop = mobDropComponent.getMobDrop();

        if (Objects.nonNull(newMobDrop) && newMobDrop.getMobDropID() != mob.get().getMobDropID())
            mob.get().setMobDropID(newMobDrop.getMobDropID());
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var mobTypeInfoMap = controller.getStaticDataStore().getMobTypeInfoMap();
        var mobDropMap = controller.getDrops().getMobDrops();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.removeIf(fc -> !fc.valueName().equals("mobID"));

        allValues.addAll(getNestedSearchableValues(
                mobInfoComponent.getSearchableValues(),
                op -> op.map(o -> (Mob) o)
                        .map(m -> mobTypeInfoMap.get(m.getMobID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                mobDropComponent.getSearchableValues(),
                op -> op.map(o -> (Mob) o)
                        .map(m -> mobDropMap.get(m.getMobDropID()))
                        .stream().toList()
        ));

        return allValues;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
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

    public HBox getIdHBox() {
        return idHBox;
    }

    public BorderPane getMobBorderPane() {
        return mobBorderPane;
    }
}
