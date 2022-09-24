package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CrateTypeBoxComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<Crate> crate;
    private final ObjectProperty<ItemInfo> itemInfo;

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

    public CrateTypeBoxComponent(double width,
                    MainController controller,
                    DataComponent parent) {

        crate = new SimpleObjectProperty<>();
        itemInfo = new SimpleObjectProperty<>();

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
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(2, idLabel, removeButton);

        setTop(idHBox);
        setCenter(contentVBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        // observable is listened, it is okay to just set the observable
        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass())
                .ifPresent(this::setObservable);

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        crate.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
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
    public Class<Crate> getObservableClass() {
        return Crate.class;
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

            nameLabel.setText(name);
            commentLabel.setText(comment);
            iconView.setImage(new Image(new ByteArrayInputStream(icon)));

            contentVBox.setDisable(Objects.isNull(itemInfo));
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(ItemInfo.class),
                op -> op.map(o -> (Crate) o)
                        .map(c -> itemInfoMap.get(new Pair<>(c.getCrateID(), 9)))
                        .stream().toList()
        ));

        return allValues;
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
