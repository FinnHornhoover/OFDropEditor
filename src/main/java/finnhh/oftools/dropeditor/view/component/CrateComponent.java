package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.ItemInfo;
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

import java.util.Map;
import java.util.Objects;

public class CrateComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Crate> crate;

    private final Drops drops;
    private final Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap;

    private final CrateInfoComponent crateInfoComponent;
    private final RarityWeightsComponent rarityWeightsComponent;
    private final ItemSetComponent itemSetComponent;
    private final Label idLabel;
    private final BorderPane crateBorderPane;

    public CrateComponent(Drops drops,
                          Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                          Map<String, byte[]> iconMap,
                          ListView<Data> listView) {

        crate = new SimpleObjectProperty<>();

        this.drops = drops;
        this.itemInfoMap = itemInfoMap;

        crateInfoComponent = new CrateInfoComponent(120.0, iconMap);
        rarityWeightsComponent = new RarityWeightsComponent(20.0, 120.0, drops, this);
        itemSetComponent = new ItemSetComponent(120.0, 2.0, drops, itemInfoMap, iconMap, this);

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

        idLabel.setText(Crate.class.getSimpleName() + ": null");
        crateInfoComponent.setDisable(true);
        rarityWeightsComponent.setDisable(true);
        itemSetComponent.setDisable(true);
        setIdDisable(true);

        crate.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(Crate.class.getSimpleName() + ": null");
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
            crateInfoComponent.setObservable(itemInfoMap.get(new Pair<>(crate.get().getCrateID(), 9)));
            rarityWeightsComponent.setObservable(drops.getRarityWeights().get(crate.get().getRarityWeightID()));
            itemSetComponent.setObservable(drops.getItemSets().get(crate.get().getItemSetID()));
        }
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        int newRarityWeightsID = rarityWeightsComponent.getRarityWeights().getRarityWeightID();
        int newItemSetID = itemSetComponent.getItemSet().getItemSetID();

        if (newRarityWeightsID != crate.get().getRarityWeightID())
            crate.get().setCrateID(newRarityWeightsID);
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
