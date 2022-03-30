package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CrateComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Crate> crate;

    private final MainController controller;

    private final CrateInfoComponent crateInfoComponent;
    private final RarityWeightsComponent rarityWeightsComponent;
    private final ItemSetComponent itemSetComponent;
    private final Label idLabel;
    private final BorderPane crateBorderPane;

    public CrateComponent(MainController controller, ListView<Data> listView) {
        crate = new SimpleObjectProperty<>();

        this.controller = controller;

        crateInfoComponent = new CrateInfoComponent(120.0, controller);
        rarityWeightsComponent = new RarityWeightsComponent(20.0, 120.0, controller, this);
        itemSetComponent = new ItemSetComponent(120.0, 2.0, controller, this);

        itemSetComponent.prefWidthProperty().bind(listView.widthProperty()
                .subtract(crateInfoComponent.widthProperty())
                .subtract(rarityWeightsComponent.widthProperty())
                .subtract(28));
        itemSetComponent.setMaxWidth(USE_PREF_SIZE);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        crateBorderPane = new BorderPane();
        crateBorderPane.setTop(idLabel);
        crateBorderPane.setCenter(crateInfoComponent);

        getChildren().addAll(crateBorderPane, rarityWeightsComponent, itemSetComponent);
        setHgrow(itemSetComponent, Priority.ALWAYS);

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        crateInfoComponent.setDisable(true);
        rarityWeightsComponent.setDisable(true);
        itemSetComponent.setDisable(true);
        setIdDisable(true);

        crate.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                crateInfoComponent.setDisable(true);
                rarityWeightsComponent.setDisable(true);
                itemSetComponent.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                crateInfoComponent.setDisable(false);
                rarityWeightsComponent.setDisable(false);
                itemSetComponent.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public Class<Crate> getObservableClass() {
        return Crate.class;
    }

    @Override
    public ReadOnlyObjectProperty<Crate> getObservable() {
        return crate;
    }

    @Override
    public void setObservable(Data data) {
        crate.set((Crate) data);

        if (crate.isNull().get()) {
            crateInfoComponent.setObservable(null);
            rarityWeightsComponent.setObservable(null);
            itemSetComponent.setObservable(null);
        } else {
            crateInfoComponent.setObservable(controller.getStaticDataStore().getItemInfoMap().get(
                    new Pair<>(crate.get().getCrateID(), 9)));
            rarityWeightsComponent.setObservable(controller.getDrops().getRarityWeights().get(
                    crate.get().getRarityWeightID()));
            itemSetComponent.setObservable(controller.getDrops().getItemSets().get(crate.get().getItemSetID()));
        }
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();
        var rarityWeightsMap = controller.getDrops().getRarityWeights();
        var itemSetMap = controller.getDrops().getItemSets();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.removeIf(fc -> !fc.valueName().equals("crateID"));

        allValues.addAll(getNestedSearchableValues(
                crateInfoComponent.getSearchableValues(),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), 9)))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                rarityWeightsComponent.getSearchableValues(),
                op -> op.map(o -> (Crate) o)
                        .map(c -> rarityWeightsMap.get(c.getRarityWeightID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                itemSetComponent.getSearchableValues(),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemSetMap.get(c.getItemSetID()))
                        .stream().toList()
        ));

        return allValues;
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        int newRarityWeightsID = rarityWeightsComponent.getRarityWeights().getRarityWeightID();
        int newItemSetID = itemSetComponent.getItemSet().getItemSetID();

        if (newRarityWeightsID != crate.get().getRarityWeightID())
            crate.get().setRarityWeightID(newRarityWeightsID);
        if (newItemSetID != crate.get().getItemSetID())
            crate.get().setItemSetID(newItemSetID);
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    public Crate getCrate() {
        return crate.get();
    }

    public ObjectProperty<Crate> crateProperty() {
        return crate;
    }

    public CrateInfoComponent getCrateInfoComponent() {
        return crateInfoComponent;
    }

    public RarityWeightsComponent getRarityWeightsComponent() {
        return rarityWeightsComponent;
    }

    public ItemSetComponent getItemSetComponent() {
        return itemSetComponent;
    }

    public BorderPane getCrateBorderPane() {
        return crateBorderPane;
    }
}
