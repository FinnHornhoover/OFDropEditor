package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.EditMode;
import finnhh.oftools.dropeditor.model.ViewMode;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.view.component.*;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {
    @FXML
    protected ChoiceBox<ViewMode> viewModeChoiceBox;
    @FXML
    protected TextField searchTextField;
    @FXML
    protected ChoiceBox<EditMode> editModeChoiceBox;
    @FXML
    protected ListView<Data> mainListView;
    @FXML
    protected Label infoLabel;

    protected Drops drops;
    protected JSONManager jsonManager;
    protected IconManager iconManager;
    protected StaticDataStore staticDataStore;
    protected MainApplication application;

    @FXML
    protected void onViewModeChanged(Event event) {
        // TODO
    }

    @FXML
    protected void onSearchText(Event event) {
        // TODO
    }

    @FXML
    protected void onEditModeChanged(Event event) {
        // TODO
    }

    @FXML
    protected void onNewButtonPressed(Event event) {
        // TODO
    }

    @FXML
    protected void onDeleteButtonPressed(Event event) {
        // TODO
    }

    @FXML
    protected void onSettingsButtonPressed(Event event) {
        // TODO
    }

    @FXML
    protected void onSaveButtonPressed(Event event) {
        // TODO
    }

    public void setup() {
        viewModeChoiceBox.getItems().addAll(ViewMode.values());
        viewModeChoiceBox.setValue(ViewMode.MONSTER);
        
        editModeChoiceBox.getItems().addAll(EditMode.values());
        editModeChoiceBox.setValue(EditMode.ASK);

        final MainController controller = this;
        mainListView.setCellFactory(cfData -> new ListCell<>() {
            private final MobComponent mobComponent;
            private final CrateComponent crateComponent;

            {
                mobComponent = new MobComponent(controller, mainListView);
                crateComponent = new CrateComponent(controller, mainListView);
            }

            @Override
            protected void updateItem(Data data, boolean empty) {
                super.updateItem(data, empty);

                Node graphic = null;

                if (!empty) {
                    switch (viewModeChoiceBox.getValue()) {
                        case MONSTER -> {
                            mobComponent.setObservable(data);
                            graphic = mobComponent;
                        }
                        case CRATE -> {
                            crateComponent.setObservable(data);
                            graphic = crateComponent;
                        }
                    }
                }

                setGraphic(graphic);
            }
        });

        mainListView.setEditable(false);
        mainListView.setFocusTraversable(false);
        mainListView.setSelectionModel(new NoSelectionModel<>());

        viewModeChoiceBox.valueProperty().addListener((o, oldVal, newVal) -> {
            mainListView.getItems().clear();
            mainListView.getItems().addAll(newVal.getDataGetter().apply(drops));
            Platform.runLater(mainListView::refresh);
        });

        mainListView.getItems().addAll(viewModeChoiceBox.getValue().getDataGetter().apply(drops));
        Platform.runLater(mainListView::refresh);
    }

    public TableView<ReferenceListComponent> getReferenceGraphic(Class<? extends Data> dataClass,
                                                                 Predicate<Data> filterCondition) {
        TableView<ReferenceListComponent> tableView = new TableView<>();

        TableColumn<ReferenceListComponent, ImageSummaryComponent> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                new ImageSummaryComponent(64.0, this, cellData.getValue().getOriginData())));

        TableColumn<ReferenceListComponent, ReferenceListComponent> referenceColumn = new TableColumn<>("References");
        referenceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        referenceColumn.setCellFactory(cfData -> new TableCell<>() {
            @Override
            protected void updateItem(ReferenceListComponent referenceListComponent, boolean empty) {
                super.updateItem(referenceListComponent, empty);

                Optional.ofNullable(getItem()).ifPresent(ReferenceListComponent::destroyView);

                if (!empty && Objects.nonNull(referenceListComponent)) {
                    referenceListComponent.constructView();
                    setGraphic(referenceListComponent);
                } else {
                    setGraphic(null);
                }
            }
        });

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(referenceColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        drops.getDataMap(dataClass).ifPresent(dataMap -> tableView.getItems().addAll(dataMap.values().stream()
                .filter(filterCondition)
                .map(d -> new ReferenceListComponent(10.0, 64.0, this, d))
                .toList()));

        return tableView;
    }

    public Optional<Data> showSelectionMenuForResult(Class<? extends Data> dataClass,
                                                     Predicate<Data> filterCondition) {
        return application.showSelectionAlert(dataClass.getSimpleName() + " Selection",
                "Please select one:",
                getReferenceGraphic(dataClass, filterCondition));
    }

    public Optional<Data> showSelectionMenuForResult(Class<? extends Data> dataClass) {
        return showSelectionMenuForResult(dataClass, d -> true);
    }

    public void setJSONManager(JSONManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public JSONManager getJsonManager() {
        return jsonManager;
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public void setDrops(Drops drops) {
        this.drops = drops;
    }

    public Drops getDrops() {
        return drops;
    }

    public void setStaticDataStore(StaticDataStore staticDataStore) {
        this.staticDataStore = staticDataStore;
    }

    public StaticDataStore getStaticDataStore() {
        return staticDataStore;
    }

    public void setApplication(MainApplication application) {
        this.application = application;
    }

    public MainApplication getApplication() {
        return application;
    }
}