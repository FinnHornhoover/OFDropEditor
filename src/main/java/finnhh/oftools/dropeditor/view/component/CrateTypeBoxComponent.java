package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CrateTypeBoxComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<Crate> crate;

    private final MainController controller;
    private final DataComponent parent;

    private final TooltipBoxContainer crateTypeBoxTooltipBoxContainer;
    private final Label nameLabel;
    private final Label commentLabel;
    private final StandardImageView iconView;
    private final VBox contentVBox;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> idClickHandler;

    public CrateTypeBoxComponent(double width,
                    MainController controller,
                    DataComponent parent) {

        crate = new SimpleObjectProperty<>();

        this.controller = controller;
        this.parent = parent;

        crateTypeBoxTooltipBoxContainer = new TooltipBoxContainer(25, 100, controller);
        Tooltip tooltip = new Tooltip();
        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setGraphic(crateTypeBoxTooltipBoxContainer);

        nameLabel = new Label();
        nameLabel.setTooltip(tooltip);

        commentLabel = new Label();
        commentLabel.setTooltip(tooltip);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);
        Tooltip.install(iconView, tooltip);

        contentVBox = new VBox(2, nameLabel, commentLabel, iconView);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setMinWidth(width);
        contentVBox.setMaxWidth(width);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(2, idLabel, removeButton);

        setTop(idHBox);
        setCenter(contentVBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass())
                .ifPresent(this::setObservableAndState);
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
        return List.of(contentVBox);
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
        DataComponent.super.cleanUIState();

        crateTypeBoxTooltipBoxContainer.clearGraphics();
        nameLabel.setText("<INVALID>");
        commentLabel.setText("<INVALID>");
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        ItemInfo itemInfo = controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(
                crate.get().getCrateID(), ItemType.CRATE.getTypeID()));

        if (Objects.nonNull(itemInfo)) {
            crateTypeBoxTooltipBoxContainer.arrangeItemContentMaps(itemInfo.id());
            nameLabel.setText(itemInfo.name());
            commentLabel.setText(itemInfo.comment());
            iconView.setImage(itemInfo.iconName());
        }
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(ItemInfo.class),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), ItemType.CRATE.getTypeID())))
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(ItemType.class),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), ItemType.CRATE.getTypeID())))
                        .map(ItemInfo::type)
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(WeaponType.class),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), ItemType.CRATE.getTypeID())))
                        .map(ItemInfo::weaponType)
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(Rarity.class),
                op -> op.map(o -> (Crate) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getCrateID(), ItemType.CRATE.getTypeID())))
                        .map(ItemInfo::rarity)
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(Gender.class),
                op -> op.map(o -> (Crate) o)
                        .map(ir -> itemInfoMap.get(new Pair<>(ir.getCrateID(), ItemType.CRATE.getTypeID())))
                        .map(ItemInfo::gender)
                        .stream().toList()
        ));

        return allValues;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public Crate getCrate() {
        return crate.get();
    }

    public ReadOnlyObjectProperty<Crate> crateProperty() {
        return crate;
    }

    public TooltipBoxContainer getCrateTypeBoxTooltipBoxContainer() {
        return crateTypeBoxTooltipBoxContainer;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public Label getCommentLabel() {
        return commentLabel;
    }

    public StandardImageView getIconView() {
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
}
