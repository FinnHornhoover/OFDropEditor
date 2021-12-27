package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    private Drops drops;
    private JSONManager jsonManager;
    private IconManager iconManager;
    private MainApplication application;

    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
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