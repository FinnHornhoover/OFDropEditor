package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.EditMode;
import finnhh.oftools.dropeditor.model.ViewMode;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainController {
    @FXML
    protected ChoiceBox<ViewMode> viewModeChoiceBox;
    @FXML
    protected TextField searchTextField;
    @FXML
    protected ChoiceBox<EditMode> editModeChoiceBox;
    @FXML
    protected ListView<String> mainListView;  // TODO replace type
    @FXML
    protected Label infoLabel;

    protected Drops drops;
    protected JSONManager jsonManager;
    protected IconManager iconManager;
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

    public void setApplication(MainApplication application) {
        this.application = application;
    }
}