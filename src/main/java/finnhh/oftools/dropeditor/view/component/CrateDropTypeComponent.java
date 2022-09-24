package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.CrateDropType;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
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
    private final List<ChangeListener<Crate>> valueListeners;
    private final List<EventHandler<MouseEvent>> removeClickHandlers;
    private final List<EventHandler<MouseEvent>> dragDetectedHandlers;
    private final List<EventHandler<MouseDragEvent>> dragEnteredHandlers;
    private final List<EventHandler<MouseDragEvent>> dragExitedHandlers;
    private final List<EventHandler<MouseDragEvent>> dragReleasedHandlers;
    private final EventHandler<MouseEvent> idClickHandler;

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

        idHBox = new HBox(2, idLabel, addButton);

        setTop(idHBox);
        setCenter(contentScrollPane);
        setAlignment(idLabel, Pos.TOP_LEFT);

        typeVBoxCache = new ArrayList<>();

        addClickHandler = event -> this.controller.showSelectionMenuForResult(Crate.class)
                .ifPresent(d -> crateDropAdded(((Crate) d).getCrateID()));
        valueListeners = new ArrayList<>();
        removeClickHandlers = new ArrayList<>();
        dragDetectedHandlers = new ArrayList<>();
        dragEnteredHandlers = new ArrayList<>();
        dragExitedHandlers = new ArrayList<>();
        dragReleasedHandlers = new ArrayList<>();

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass(),
                        d -> ((CrateDropType) d).getCrateIDs().size() == Optional.ofNullable(this.parent)
                                .map(MobDropComponent::getCrateDropChanceComponent)
                                .map(CrateDropChanceComponent::getCrateDropChance)
                                .map(cdc -> cdc.getCrateTypeDropWeights().size())
                                .orElse(0))
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        listHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        crateDropType.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                listHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                listHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindListVariables() {
        var children = listHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        for (int index = 0; index < children.size(); index++) {
            CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) children.get(index);

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                makeEditable(controller.getDrops());
                crateDropType.get().getCrateIDs().set(finalIndex, newVal.getCrateID());

                CrateTypeBoxComponent current = (CrateTypeBoxComponent) listHBox.getChildren()
                        .filtered(c -> c instanceof CrateTypeBoxComponent)
                        .get(finalIndex);
                current.setObservable(newVal);
            });
            removeClickHandlers.add(event -> crateDropRemoved(finalIndex));
            dragDetectedHandlers.add(event -> ctb.startFullDrag());
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

                if (swappedIndex > -1 && swappedIndex != finalIndex) {
                    List<Integer> order = new ArrayList<>(IntStream.range(0, listHBox.getChildren().size())
                            .boxed().toList());

                    order.remove(swappedIndex);
                    order.add(finalIndex, swappedIndex);

                    crateDropPermuted(order);
                }
            });

            ctb.crateProperty().addListener(valueListeners.get(index));
            ctb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            ctb.addEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
            ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
            ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
            ctb.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
        }
    }

    private void unbindListVariables() {
        var children = listHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        for (int index = 0; index < children.size(); index++) {
            CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) children.get(index);
            ctb.crateProperty().removeListener(valueListeners.get(index));
            ctb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            ctb.removeEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
            ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
            ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
            ctb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
        }

        valueListeners.clear();
        removeClickHandlers.clear();
        dragDetectedHandlers.clear();
        dragEnteredHandlers.clear();
        dragExitedHandlers.clear();
        dragReleasedHandlers.clear();
    }

    private void populateListBox() {
        var crateIDs = crateDropType.get().getCrateIDs();

        if (typeVBoxCache.size() < crateIDs.size()) {
            IntStream.range(0, crateIDs.size() - typeVBoxCache.size())
                    .mapToObj(i -> new CrateTypeBoxComponent(boxWidth, controller, this))
                    .forEach(typeVBoxCache::add);
        }

        IntStream.range(0, crateIDs.size())
                .mapToObj(i -> {
                    CrateTypeBoxComponent ctb = typeVBoxCache.get(i);
                    ctb.setObservable(controller.getDrops().getCrates().get(crateIDs.get(i)));
                    return ctb;
                })
                .forEach(listHBox.getChildren()::add);
    }

    public void crateDropAdded(int newCrateID) {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        crateDropType.get().getCrateIDs().add(newCrateID);

        populateListBox();
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(CrateDropChanceComponent::crateDropAdded);
        bindListVariables();
    }

    public void crateDropRemoved(int index) {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        crateDropType.get().getCrateIDs().remove(index);

        populateListBox();
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(cdcc -> cdcc.crateDropRemoved(index));
        bindListVariables();
    }

    public void crateDropPermuted(List<Integer> indexList) {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        var crateIDs = crateDropType.get().getCrateIDs();
        var newCrateIDs = IntStream.range(0, crateIDs.size())
                .mapToObj(i -> crateIDs.get(indexList.get(i)))
                .toList();

        crateIDs.clear();
        crateIDs.addAll(newCrateIDs);

        populateListBox();
        Optional.ofNullable(parent)
                .map(MobDropComponent::getCrateDropChanceComponent)
                .ifPresent(cdcc -> cdcc.crateDropPermuted(indexList));
        bindListVariables();
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
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        crateDropType.set((CrateDropType) data);

        addButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        unbindListVariables();

        listHBox.getChildren().clear();

        if (crateDropType.isNotNull().get()) {
            populateListBox();
            bindListVariables();
            addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, addClickHandler);
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        var children = listHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        for (int index = 0; index < children.size(); index++) {
            int crateID = ((CrateTypeBoxComponent) children.get(index)).getCrate().getCrateID();

            if (crateID != crateDropType.get().getCrateIDs().get(index))
                crateDropType.get().getCrateIDs().set(index, crateID);
        }
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

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }
}
