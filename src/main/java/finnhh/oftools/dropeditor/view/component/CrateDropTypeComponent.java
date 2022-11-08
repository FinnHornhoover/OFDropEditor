package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.CrateDropType;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.*;
import java.util.stream.IntStream;

public class CrateDropTypeComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<CrateDropType> crateDropType;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final MobDropComponent parent;

    private final HBox listHBox;
    private final ScrollPane contentScrollPane;
    private final Label idLabel;
    private final Button addButton;
    private final HBox idHBox;

    private final List<CrateTypeBoxComponent> typeVBoxCache;

    private final EventHandler<MouseEvent> addClickHandler;
    private final EventHandler<MouseEvent> idClickHandler;
    private final List<ChangeListener<Crate>> valueListeners;
    private final List<EventHandler<MouseEvent>> removeClickHandlers;
    private final List<EventHandler<MouseEvent>> dragDetectedHandlers;
    private final List<EventHandler<MouseDragEvent>> dragEnteredHandlers;
    private final List<EventHandler<MouseDragEvent>> dragExitedHandlers;
    private final List<EventHandler<MouseDragEvent>> dragReleasedHandlers;

    public CrateDropTypeComponent(double boxSpacing,
                                  double boxWidth,
                                  MainController controller,
                                  MobDropComponent parent) {

        crateDropType = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        listHBox = new HBox(boxSpacing);
        listHBox.setAlignment(Pos.CENTER);
        listHBox.getStyleClass().add("bordered-pane");

        contentScrollPane = new ScrollPane(listHBox);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        addButton = new Button("+");
        addButton.getStyleClass().addAll("add-button", "slim-button");
        addButton.disableProperty().bind(controller.getDrops().cloneObjectsBeforeEditingProperty().not());

        idHBox = new HBox(2, idLabel, addButton);

        setTop(idHBox);
        setCenter(contentScrollPane);
        setAlignment(idLabel, Pos.TOP_LEFT);

        typeVBoxCache = new ArrayList<>();

        addClickHandler = event -> this.controller.showSelectionMenuForResult(Crate.class)
                .ifPresent(d -> crateDropAdded(((Crate) d).getCrateID()));
        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass(),
                        d -> ((CrateDropType) d).getCrateIDs().size() == Optional.ofNullable(this.parent)
                                .map(MobDropComponent::getCrateDropChanceComponent)
                                .map(CrateDropChanceComponent::getCrateDropChance)
                                .map(cdc -> cdc.getCrateTypeDropWeights().size())
                                .orElse(0))
                .ifPresent(this::makeReplacement);

        valueListeners = new ArrayList<>();
        removeClickHandlers = new ArrayList<>();
        dragDetectedHandlers = new ArrayList<>();
        dragEnteredHandlers = new ArrayList<>();
        dragExitedHandlers = new ArrayList<>();
        dragReleasedHandlers = new ArrayList<>();
    }

    public void crateDropAdded(int newCrateID) {
        if (!controller.getDrops().isCloneObjectsBeforeEditing())
            return;

        long key = controller.getDrops().generateActionKey();
        makeEdit(key, data -> ((CrateDropType) data).getCrateIDs().add(newCrateID));
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(cdcc -> cdcc.crateDropAdded(key));
    }

    public void crateDropRemoved(int index) {
        if (!controller.getDrops().isCloneObjectsBeforeEditing())
            return;

        long key = controller.getDrops().generateActionKey();
        makeEdit(key, data -> ((CrateDropType) data).getCrateIDs().remove(index));
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(cdcc -> cdcc.crateDropRemoved(key, index));
    }

    public void crateDropPermuted(List<Integer> indexList) {
        if (!controller.getDrops().isCloneObjectsBeforeEditing())
            return;

        long key = controller.getDrops().generateActionKey();
        makeEdit(key, data -> {
            var crateIDs = ((CrateDropType) data).getCrateIDs();
            var newCrateIDs = IntStream.range(0, crateIDs.size())
                    .mapToObj(i -> crateIDs.get(indexList.get(i)))
                    .toList();

            crateIDs.clear();
            crateIDs.addAll(newCrateIDs);
        });
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(cdcc -> cdcc.crateDropPermuted(key, indexList));
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
        return List.of(listHBox);
    }

    @Override
    public Class<CrateDropType> getObservableClass() {
        return CrateDropType.class;
    }

    @Override
    public ReadOnlyObjectProperty<CrateDropType> getObservable() {
        return crateDropType;
    }

    @Override
    public void setObservable(Data data) {
        crateDropType.set((CrateDropType) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        listHBox.getChildren().clear();
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        var crateIDs = crateDropType.get().getCrateIDs();

        if (typeVBoxCache.size() < crateIDs.size()) {
            IntStream.range(0, crateIDs.size() - typeVBoxCache.size())
                    .mapToObj(i -> {
                        CrateTypeBoxComponent ctb = new CrateTypeBoxComponent(boxWidth, controller, this);
                        ctb.getRemoveButton().disableProperty().bind(
                                controller.getDrops().cloneObjectsBeforeEditingProperty().not());
                        return ctb;
                    })
                    .forEach(typeVBoxCache::add);
        }

        IntStream.range(0, crateIDs.size())
                .mapToObj(i -> {
                    CrateTypeBoxComponent ctb = typeVBoxCache.get(i);
                    ctb.setObservableAndState(controller.getDrops().getCrates().get(crateIDs.get(i)));
                    return ctb;
                })
                .forEach(listHBox.getChildren()::add);
    }

    @Override
    public void bindVariablesNonNull() {
        addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);

        var children = listHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        IntStream.range(0, children.size())
                .forEach(index -> {
                    CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) children.get(index);

                    valueListeners.add((o, oldVal, newVal) -> makeEdit(data ->
                            ((CrateDropType) data).getCrateIDs().set(index, newVal.getCrateID())));
                    removeClickHandlers.add(event -> crateDropRemoved(index));
                    dragDetectedHandlers.add(event -> {
                        if (controller.getDrops().isCloneObjectsBeforeEditing())
                            ctb.startFullDrag();
                    });
                    dragEnteredHandlers.add(event -> {
                        ctb.getStyleClass().removeAll("drag-exited", "drag-released");
                        ctb.getStyleClass().add("drag-entered");
                    });
                    dragExitedHandlers.add(event -> {
                        ctb.getStyleClass().removeAll("drag-entered", "drag-released");
                        ctb.getStyleClass().add("drag-exited");
                    });
                    dragReleasedHandlers.add(event -> {
                        ctb.getStyleClass().removeAll("drag-entered", "drag-exited");
                        ctb.getStyleClass().add("drag-released");

                        int swappedIndex = listHBox.getChildren().indexOf((Node) event.getGestureSource());

                        if (swappedIndex > -1 && swappedIndex != index) {
                            List<Integer> order = new ArrayList<>(IntStream.range(0, listHBox.getChildren().size())
                                    .boxed().toList());

                            order.remove(swappedIndex);
                            order.add(index, swappedIndex);

                            crateDropPermuted(order);
                        }
                    });

                    ctb.crateProperty().addListener(valueListeners.get(index));
                    ctb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
                    ctb.addEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
                    ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
                    ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
                    ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
                });
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        var children = listHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        IntStream.range(0, children.size())
                .forEach(index -> {
                    CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) children.get(index);

                    ctb.crateProperty().removeListener(valueListeners.get(index));
                    ctb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
                    ctb.removeEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
                    ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
                    ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
                    ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
                });

        valueListeners.clear();
        removeClickHandlers.clear();
        dragDetectedHandlers.clear();
        dragEnteredHandlers.clear();
        dragExitedHandlers.clear();
        dragReleasedHandlers.clear();
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var crateMap = controller.getDrops().getCrates();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        CrateTypeBoxComponent prototype = typeVBoxCache.isEmpty() ?
                new CrateTypeBoxComponent(boxWidth, controller, this) :
                typeVBoxCache.get(0);
        allValues.addAll(getNestedSearchableValues(
                prototype.getSearchableValues(),
                op -> op.map(o -> (CrateDropType) o)
                        .map(CrateDropType::getCrateIDs)
                        .orElse(FXCollections.emptyObservableList())
                        .stream()
                        .filter(crateMap::containsKey)
                        .map(crateMap::get)
                        .toList()
        ));

        return allValues;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public CrateDropType getCrateDropType() {
        return crateDropType.get();
    }

    public ReadOnlyObjectProperty<CrateDropType> crateDropTypeProperty() {
        return crateDropType;
    }

    public HBox getListHBox() {
        return listHBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }

    public Button getAddButton() {
        return addButton;
    }

    public HBox getIdHBox() {
        return idHBox;
    }
}
