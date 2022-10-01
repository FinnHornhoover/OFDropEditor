package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CrateComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Crate> crate;

    private final MainController controller;

    private final CrateInfoComponent crateInfoComponent;
    private final RarityWeightsComponent rarityWeightsComponent;
    private final ItemSetComponent itemSetComponent;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;
    private final BorderPane crateBorderPane;

    private final EventHandler<MouseEvent> removeClickHandler;

    public CrateComponent(MainController controller) {
        crate = new SimpleObjectProperty<>();

        this.controller = controller;

        crateInfoComponent = new CrateInfoComponent(120.0, controller);
        rarityWeightsComponent = new RarityWeightsComponent(20.0, 120.0, controller, this);
        itemSetComponent = new ItemSetComponent(120.0, 2.0, controller, this);

        itemSetComponent.prefWidthProperty().bind(this.controller.getMainListView().widthProperty()
                .subtract(crateInfoComponent.widthProperty())
                .subtract(rarityWeightsComponent.widthProperty())
                .subtract(28));
        itemSetComponent.setMaxWidth(USE_PREF_SIZE);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        crateBorderPane = new BorderPane();
        crateBorderPane.setTop(idHBox);
        crateBorderPane.setCenter(crateInfoComponent);

        getChildren().addAll(crateBorderPane, rarityWeightsComponent, itemSetComponent);
        setHgrow(itemSetComponent, Priority.ALWAYS);

        removeClickHandler = event -> onRemoveClick();
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public List<Pane> getContentPanes() {
        return List.of(crateInfoComponent, rarityWeightsComponent, itemSetComponent);
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
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        crateInfoComponent.setObservableAndState(null);
        rarityWeightsComponent.setObservableAndState(null);
        itemSetComponent.setObservableAndState(null);
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        crateInfoComponent.setObservableAndState(controller.getStaticDataStore().getItemInfoMap().get(
                new Pair<>(crate.get().getCrateID(), Crate.TYPE)));
        rarityWeightsComponent.setObservableAndState(controller.getDrops().getRarityWeights().get(
                crate.get().getRarityWeightID()));
        itemSetComponent.setObservableAndState(controller.getDrops().getItemSets().get(crate.get().getItemSetID()));
    }

    @Override
    public void bindVariablesNullable() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public void unbindVariables() {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
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
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), Crate.TYPE)))
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
    public void updateObservableFromUI(Drops drops) {
        Optional.ofNullable(rarityWeightsComponent.getRarityWeights())
                .filter(rw -> rw.getRarityWeightID() != crate.get().getRarityWeightID())
                .ifPresent(rw -> crate.get().setRarityWeightID(rw.getRarityWeightID()));

        Optional.ofNullable(itemSetComponent.getItemSet())
                .filter(is -> is.getItemSetID() != crate.get().getItemSetID())
                .ifPresent(is -> crate.get().setItemSetID(is.getItemSetID()));
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
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

    public HBox getIdHBox() {
        return idHBox;
    }

    public BorderPane getCrateBorderPane() {
        return crateBorderPane;
    }
}
