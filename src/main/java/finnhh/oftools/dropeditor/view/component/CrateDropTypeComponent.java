package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.CrateDropType;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    private final List<TypeVBox> typeVBoxCache;

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
        addButton.getStyleClass().add("add-button");

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

        idLabel.setText(CrateDropType.class.getSimpleName() + ": null");
        listHBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(CrateDropType.class,
                        d -> ((CrateDropType) d).getCrateIDs().size() == Optional.ofNullable(this.parent)
                                .map(MobDropComponent::getCrateDropChanceComponent)
                                .map(CrateDropChanceComponent::getCrateDropChance)
                                .map(cdc -> cdc.getCrateTypeDropWeights().size())
                                .orElse(0))
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        // both makeEditable and setObservable sets the observable, just use a listener here
        crateDropType.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(CrateDropType.class.getSimpleName() + ": null");
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
        var children = listHBox.getChildren().filtered(c -> c instanceof TypeVBox);

        for (int index = 0; index < children.size(); index++) {
            TypeVBox tvb = (TypeVBox) children.get(index);

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                makeEditable(controller.getDrops());
                crateDropType.get().getCrateIDs().set(finalIndex, newVal.getCrateID());

                TypeVBox current = (TypeVBox) listHBox.getChildren()
                        .filtered(c -> c instanceof TypeVBox)
                        .get(finalIndex);
                current.setObservable(newVal);
            });
            removeClickHandlers.add(event -> crateDropRemoved(finalIndex));
            dragDetectedHandlers.add(event -> tvb.startFullDrag());
            dragEnteredHandlers.add(event -> {
                tvb.getStyleClass().removeAll("drag-exited", "drag-released");
                tvb.getStyleClass().add("drag-entered");
            });
            dragExitedHandlers.add(event -> {
                tvb.getStyleClass().removeAll("drag-entered", "drag-released");
                tvb.getStyleClass().add("drag-exited");
            });
            dragReleasedHandlers.add(event -> {
                tvb.getStyleClass().removeAll("drag-entered", "drag-exited");
                tvb.getStyleClass().add("drag-released");

                int swappedIndex = listHBox.getChildren().indexOf((Node) event.getGestureSource());

                if (swappedIndex > -1 && swappedIndex != finalIndex) {
                    List<Integer> order = new ArrayList<>(IntStream.range(0, listHBox.getChildren().size())
                            .boxed().toList());

                    order.remove(swappedIndex);
                    order.add(finalIndex, swappedIndex);

                    crateDropPermuted(order);
                }
            });

            tvb.crateProperty().addListener(valueListeners.get(index));
            tvb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            tvb.addEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
            tvb.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
            tvb.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
            tvb.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
        }
    }

    private void unbindListVariables() {
        var children = listHBox.getChildren().filtered(c -> c instanceof TypeVBox);

        for (int index = 0; index < children.size(); index++) {
            TypeVBox tvb = (TypeVBox) children.get(index);
            tvb.crateProperty().removeListener(valueListeners.get(index));
            tvb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
            tvb.removeEventHandler(MouseDragEvent.DRAG_DETECTED, dragDetectedHandlers.get(index));
            tvb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, dragEnteredHandlers.get(index));
            tvb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, dragExitedHandlers.get(index));
            tvb.removeEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, dragReleasedHandlers.get(index));
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
                    .mapToObj(i -> new TypeVBox(boxWidth, controller, this))
                    .forEach(typeVBoxCache::add);
        }

        IntStream.range(0, crateIDs.size())
                .mapToObj(i -> {
                    TypeVBox tvb = typeVBoxCache.get(i);
                    tvb.setObservable(controller.getDrops().getCrates().get(crateIDs.get(i)));
                    return tvb;
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

        var children = listHBox.getChildren().filtered(c -> c instanceof TypeVBox);

        for (int index = 0; index < children.size(); index++) {
            int crateID = ((TypeVBox) children.get(index)).getCrate().getCrateID();

            if (crateID != crateDropType.get().getCrateIDs().get(index))
                crateDropType.get().getCrateIDs().set(index, crateID);
        }
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
    public ReadOnlyObjectProperty<CrateDropType> getObservable() {
        return crateDropType;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class TypeVBox extends BorderPane implements DataComponent {
        private final ObjectProperty<Crate> crate;
        private final ObjectProperty<ItemInfo> itemInfo;
        private final ObjectProperty<byte[]> icon;

        private final MainController controller;
        private final DataComponent parent;

        private final Label nameLabel;
        private final Label commentLabel;
        private final ImageView iconView;
        private final VBox contentVBox;
        private final Label idLabel;
        private final Button removeButton;
        private final HBox idHBox;

        private final EventHandler<MouseEvent> idClickHandler;

        public TypeVBox(double width,
                        MainController controller,
                        DataComponent parent) {

            crate = new SimpleObjectProperty<>();
            itemInfo = new SimpleObjectProperty<>();
            icon = new SimpleObjectProperty<>();

            this.controller = controller;
            this.parent = parent;
            nameLabel = new Label();
            commentLabel = new Label();

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            contentVBox = new VBox(2, nameLabel, commentLabel, iconView);
            contentVBox.setAlignment(Pos.CENTER);
            contentVBox.setMinWidth(width);
            contentVBox.setMaxWidth(width);
            contentVBox.getStyleClass().add("bordered-pane");

            idLabel = new Label();
            idLabel.getStyleClass().add("id-label");

            removeButton = new Button("-");
            removeButton.setMinWidth(USE_COMPUTED_SIZE);
            removeButton.getStyleClass().add("remove-button");

            idHBox = new HBox(2, idLabel, removeButton);

            setTop(idHBox);
            setCenter(contentVBox);
            setAlignment(idLabel, Pos.TOP_LEFT);

            idLabel.setText(Crate.class.getSimpleName() + ": null");
            contentVBox.setDisable(true);
            setIdDisable(true);

            // observable is listened, it is okay to just set the observable
            idClickHandler = event -> this.controller.showSelectionMenuForResult(Crate.class)
                    .ifPresent(this::setObservable);

            // both makeEditable and setObservable sets the observable, just use a listener here
            crate.addListener((o, oldVal, newVal) -> {
                if (Objects.isNull(newVal)) {
                    idLabel.setText(Crate.class.getSimpleName() + ": null");
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
        public ReadOnlyObjectProperty<Crate> getObservable() {
            return crate;
        }

        @Override
        public void setObservable(Data data) {
            idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

            crate.set((Crate) data);

            var iconMap = controller.getIconManager().getIconMap();
            var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();
            byte[] defaultIcon = iconMap.get("unknown");

            if (crate.isNull().get()) {
                nameLabel.setText("<INVALID>");
                commentLabel.setText("<INVALID>");
                iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));

                contentVBox.setDisable(true);
            } else {
                ItemInfo itemInfo = itemInfoMap.get(new Pair<>(crate.get().getCrateID(), 9));

                String name = Objects.isNull(itemInfo) ? "<INVALID>" : itemInfo.name();
                String comment = Objects.isNull(itemInfo) ? "<INVALID>" :  itemInfo.comment();
                byte[] icon = Objects.isNull(itemInfo) ?
                        defaultIcon :
                        iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);

                this.itemInfo.set(itemInfo);
                this.icon.set(icon);

                nameLabel.setText(name);
                commentLabel.setText(comment);
                iconView.setImage(new Image(new ByteArrayInputStream(icon)));

                contentVBox.setDisable(Objects.isNull(itemInfo));
            }

            idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
        }

        public Crate getCrate() {
            return crate.get();
        }

        public ReadOnlyObjectProperty<Crate> crateProperty() {
            return crate;
        }

        public ItemInfo getItemInfo() {
            return itemInfo.get();
        }

        public ReadOnlyObjectProperty<ItemInfo> itemInfoProperty() {
            return itemInfo;
        }

        public byte[] getIcon() {
            return icon.get();
        }

        public ReadOnlyObjectProperty<byte[]> iconProperty() {
            return icon;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public Label getCommentLabel() {
            return commentLabel;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public VBox getContentVBox() {
            return contentVBox;
        }

        public Button getRemoveButton() {
            return removeButton;
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
}
