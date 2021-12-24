package finnhh.oftools.dropeditor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    private DataStore dataStore;
    private JSONManager jsonManager;
    private MainApplication application;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    public void setDataStore(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void setJSONManager(JSONManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public void setApplication(MainApplication application) {
        this.application = application;
    }
}