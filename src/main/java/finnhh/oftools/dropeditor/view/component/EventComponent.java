package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.EventType;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Event;
import finnhh.oftools.dropeditor.model.data.MobDrop;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EventComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Event> event;

    private final MainController controller;

    private final Label nameLabel;
    private final ImageView iconView;
    private final VBox eventVBox;

    private final MobDropComponent mobDropComponent;

    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;
    private final BorderPane eventBorderPane;

    private final EventHandler<MouseEvent> removeClickHandler;

    public EventComponent(MainController controller, ListView<Data> listView) {
        event = new SimpleObjectProperty<>();

        this.controller = controller;

        nameLabel = new Label();
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setWrapText(true);

        iconView = new ImageView();
        iconView.setFitWidth(64);
        iconView.setFitHeight(64);
        iconView.setPreserveRatio(true);
        iconView.setCache(true);

        eventVBox = new VBox(2, nameLabel, iconView);
        eventVBox.setAlignment(Pos.CENTER);
        eventVBox.getStyleClass().add("bordered-pane");
        eventVBox.setMinWidth(160.0);
        eventVBox.setMaxWidth(160.0);

        mobDropComponent = new MobDropComponent(60.0, 160.0, controller, this);

        mobDropComponent.prefWidthProperty().bind(listView.widthProperty()
                .subtract(eventVBox.widthProperty())
                .subtract(28));
        mobDropComponent.setMaxWidth(USE_PREF_SIZE);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        eventBorderPane = new BorderPane();
        eventBorderPane.setTop(idHBox);
        eventBorderPane.setCenter(eventVBox);

        getChildren().addAll(eventBorderPane, mobDropComponent);
        setHgrow(mobDropComponent, Priority.ALWAYS);

        removeClickHandler = event -> {
            this.controller.getDrops().remove(this.event.get());
            this.controller.getDrops().getReferenceMap().values().forEach(set -> set.remove(this.event.get()));
            listView.getItems().remove(this.event.get());
        };

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        eventVBox.setDisable(true);
        mobDropComponent.setDisable(true);
        setIdDisable(true);

        event.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                eventVBox.setDisable(true);
                mobDropComponent.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                eventVBox.setDisable(false);
                mobDropComponent.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public Class<Event> getObservableClass() {
        return Event.class;
    }

    @Override
    public ReadOnlyObjectProperty<Event> getObservable() {
        return event;
    }

    @Override
    public void setObservable(Data data) {
        var iconMap = controller.getIconManager().getIconMap();
        byte[] defaultIcon = iconMap.get("unknown");

        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);

        event.set((Event) data);

        if (event.isNull().get()) {
            nameLabel.setText("UNKNOWN");
            iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));
            mobDropComponent.setObservable(null);
        } else {
            EventType eventType = EventType.forType(event.get().getEventID());

            String name = Objects.isNull(eventType) ?
                    "UNKNOWN" :
                    eventType.getName();
            byte[] icon = Objects.isNull(eventType) ?
                    defaultIcon :
                    iconMap.getOrDefault(eventType.iconName(), defaultIcon);

            nameLabel.setText(name);
            iconView.setImage(new Image(new ByteArrayInputStream(icon)));
            mobDropComponent.setObservable(controller.getDrops().getMobDrops().get(event.get().getMobDropID()));
        }

        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
    }

    @Override
    public void refreshObservable(Drops drops) {
        makeEditable(drops);

        MobDrop newMobDrop = mobDropComponent.getMobDrop();

        if (Objects.nonNull(newMobDrop) && newMobDrop.getMobDropID() != event.get().getMobDropID())
            event.get().setMobDropID(newMobDrop.getMobDropID());
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var mobDropMap = controller.getDrops().getMobDrops();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.removeIf(fc -> !fc.valueName().equals("eventID"));

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(EventType.class),
                op -> op.map(o -> (Event) o)
                        .map(e -> EventType.forType(e.getEventID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                mobDropComponent.getSearchableValues(),
                op -> op.map(o -> (Event) o)
                        .map(e -> mobDropMap.get(e.getMobDropID()))
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

    public Event getEvent() {
        return event.get();
    }

    public ReadOnlyObjectProperty<Event> eventProperty() {
        return event;
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public ImageView getIconView() {
        return iconView;
    }

    public VBox getEventVBox() {
        return eventVBox;
    }

    public MobDropComponent getMobDropComponent() {
        return mobDropComponent;
    }

    public HBox getIdHBox() {
        return idHBox;
    }

    public BorderPane getEventBorderPane() {
        return eventBorderPane;
    }
}
