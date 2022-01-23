package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.EditMode;
import finnhh.oftools.dropeditor.model.ViewMode;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.view.component.MobComponent;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;

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
    private StaticDataStore staticDataStore;
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

        mainListView.setCellFactory(cfData -> new ListCell<>() {
            private final MobComponent mobComponent;

            {
                mobComponent = new MobComponent(drops,
                        staticDataStore.getMobTypeInfoMap(),
                        staticDataStore.getItemInfoMap(),
                        iconManager.getIconMap());
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

    public void setJSONManager(JSONManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public void setDrops(Drops drops) {
        this.drops = drops;
    }

    public void setStaticDataStore(StaticDataStore staticDataStore) {
        this.staticDataStore = staticDataStore;
    }

    public void setApplication(MainApplication application) {
        this.application = application;
    }
}