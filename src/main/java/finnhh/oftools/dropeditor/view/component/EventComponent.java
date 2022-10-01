package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.EventType;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Event;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EventComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Event> event;

    private final MainController controller;

    private final Label nameLabel;
    private final StandardImageView iconView;
    private final VBox eventVBox;

    private final MobDropComponent mobDropComponent;

    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;
    private final BorderPane eventBorderPane;

    private final EventHandler<MouseEvent> removeClickHandler;

    public EventComponent(MainController controller) {
        event = new SimpleObjectProperty<>();

        this.controller = controller;

        nameLabel = new Label();
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setWrapText(true);

        iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);

        eventVBox = new VBox(2, nameLabel, iconView);
        eventVBox.setAlignment(Pos.CENTER);
        eventVBox.getStyleClass().add("bordered-pane");
        eventVBox.setMinWidth(160.0);
        eventVBox.setMaxWidth(160.0);

        mobDropComponent = new MobDropComponent(60.0, 160.0, controller, this);

        mobDropComponent.prefWidthProperty().bind(this.controller.getMainListView().widthProperty()
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
        return List.of(eventVBox, mobDropComponent);
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
        event.set((Event) data);
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        mobDropComponent.setObservableAndState(null);
        nameLabel.setText("UNKNOWN");
        iconView.cleanImage();
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        EventType eventType = EventType.forType(event.get().getEventID());

        if (eventType != EventType.NO_EVENT) {
            mobDropComponent.setObservableAndState(
                    controller.getDrops().getMobDrops().get(event.get().getMobDropID()));
            nameLabel.setText(eventType.getName());
            iconView.setImage(eventType.iconName());
        }
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
    public void updateObservableFromUI(Drops drops) {
        Optional.ofNullable(mobDropComponent.getMobDrop())
                .filter(md -> md.getMobDropID() != event.get().getMobDropID())
                .ifPresent(md -> event.get().setMobDropID(md.getMobDropID()));
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

    public StandardImageView getIconView() {
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
