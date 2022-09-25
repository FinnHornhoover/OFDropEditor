package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.FilterCondition;
import finnhh.oftools.dropeditor.model.ViewMode;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.view.component.*;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class MainController {
    @FXML
    protected ChoiceBox<ViewMode> viewModeChoiceBox;
    @FXML
    protected FilterListBox filterListBox;
    @FXML
    protected ListView<Data> mainListView;
    @FXML
    protected Label infoLabel;

    protected Map<ViewMode, ObservableComponent<?>> rootPrototypeMap;
    protected Drops drops;
    protected JSONManager jsonManager;
    protected IconManager iconManager;
    protected StaticDataStore staticDataStore;
    protected MainApplication application;

    @FXML
    protected void onAddFilter() {
        showFilterMenuForResult().ifPresent(filterListBox::addFilter);
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
        rootPrototypeMap = new HashMap<>();
        for (ViewMode viewMode : ViewMode.values())
            rootPrototypeMap.put(viewMode, viewMode.getComponentConstructor().apply(this, mainListView));

        viewModeChoiceBox.getItems().addAll(ViewMode.values());
        viewModeChoiceBox.setValue(ViewMode.MONSTER);

        final MainController controller = this;
        mainListView.setCellFactory(cfData -> new ListCell<>() {
            private final MobComponent mobComponent;
            private final CrateComponent crateComponent;
            private final RacingComponent racingComponent;
            private final CodeItemComponent codeItemComponent;

            {
                mobComponent = new MobComponent(controller, mainListView);
                crateComponent = new CrateComponent(controller, mainListView);
                racingComponent = new RacingComponent(controller, mainListView);
                codeItemComponent = new CodeItemComponent(controller, mainListView);
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
                        case RACING -> {
                            racingComponent.setObservable(data);
                            graphic = racingComponent;
                        }
                        case CODE_ITEM -> {
                            codeItemComponent.setObservable(data);
                            graphic = codeItemComponent;
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
            infoLabel.setText(String.format("Showing %1$d items out of %1$d", mainListView.getItems().size()));
            filterListBox.clearFilters();
            Platform.runLater(mainListView::refresh);
        });

        filterListBox.filterConditionListProperty().addListener((ListChangeListener<FilterCondition>) change -> {
            mainListView.getItems().clear();
            var dataList = viewModeChoiceBox.getValue().getDataGetter().apply(drops);
            mainListView.getItems().addAll(dataList.stream()
                    .filter(d -> change.getList().stream()
                            .allMatch(fc -> fc.conditionSatisfied(d)))
                    .toList());
            infoLabel.setText(String.format("Showing %d items out of %d",
                    mainListView.getItems().size(), dataList.size()));
            Platform.runLater(mainListView::refresh);
        });

        mainListView.getItems().addAll(viewModeChoiceBox.getValue().getDataGetter().apply(drops));
        infoLabel.setText(String.format("Showing %1$d items out of %1$d", mainListView.getItems().size()));
        Platform.runLater(mainListView::refresh);
    }

    public TableView<ReferenceListBox> getReferenceGraphic(Class<? extends Data> dataClass,
                                                           Predicate<Data> filterCondition) {
        TableView<ReferenceListBox> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        TableColumn<ReferenceListBox, ImageSummaryBox> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(
                new ImageSummaryBox(64.0, this, cellData.getValue().getOriginData())));

        TableColumn<ReferenceListBox, ReferenceListBox> referenceColumn = new TableColumn<>("References");
        referenceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()));
        referenceColumn.setCellFactory(cfData -> new TableCell<>() {
            @Override
            protected void updateItem(ReferenceListBox referenceListBox, boolean empty) {
                super.updateItem(referenceListBox, empty);
                setGraphic((!empty && Objects.nonNull(referenceListBox)) ? referenceListBox : null);
            }
        });

        tableView.getColumns().add(idColumn);
        tableView.getColumns().add(referenceColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        idColumn.setMaxWidth(68.0);
        referenceColumn.prefWidthProperty().bind(tableView.widthProperty()
                .subtract(idColumn.widthProperty())
                .subtract(30));

        drops.getDataMap(dataClass).ifPresent(dataMap -> tableView.getItems().addAll(dataMap.values().stream()
                .filter(filterCondition)
                .map(d -> new ReferenceListBox(64.0, this, d))
                .toList()));

        return tableView;
    }

    public Optional<FilterCondition> showFilterMenuForResult() {
        return application.showFilterSelectionAlert("New Filter", "Filter Selection", new FilterSelectionBox(10.0,
                rootPrototypeMap.get(viewModeChoiceBox.getValue()).getSearchableValues()));
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