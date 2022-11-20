package finnhh.oftools.dropeditor;

import com.google.gson.JsonSyntaxException;
import finnhh.oftools.dropeditor.model.FilterCondition;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Preferences;
import finnhh.oftools.dropeditor.model.exception.EditorInitializationException;
import finnhh.oftools.dropeditor.view.component.FilterSelectionBox;
import finnhh.oftools.dropeditor.view.component.PreferencesBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.function.Function;

public class MainApplication extends Application {
    private StaticDataStore staticDataStore;
    private Drops drops;
    private JSONManager jsonManager;
    private IconManager iconManager;
    private MainController controller;

    public Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String body) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(body);
        alert.getDialogPane().setMinWidth(600.0);
        return alert.showAndWait();
    }

    public Optional<Preferences> showPreferencesAlert(Stage stage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Welcome to Open Fusion Drop Editor!");
        alert.setHeaderText("Initial Setup");
        alert.setGraphic(null);
        alert.setResizable(true);

        PreferencesBox preferencesBox = new PreferencesBox(stage, jsonManager);
        StackPane stackPane = new StackPane(preferencesBox);
        StackPane dummyStackPane = new StackPane();

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.lookupButton(ButtonType.OK).disableProperty().bind(preferencesBox.malformedProperty());
        dialogPane.setContent(stackPane);
        dialogPane.getScene().getStylesheets().add(MainApplication.class.getResource("application.css").toExternalForm());
        dialogPane.setMinWidth(600.0);

        Window dialogWindow = dialogPane.getScene().getWindow();
        dialogWindow.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal)
                stackPane.getChildren().remove(dummyStackPane);
            else
                stackPane.getChildren().add(dummyStackPane);
        });
        dialogWindow.setOnCloseRequest(Event::consume);


        return alert.showAndWait()
                .filter(bt -> bt == ButtonType.OK)
                .map(bt -> preferencesBox.getPreferences());
    }

    public <T extends Node> Optional<Data> showSelectionAlert(String title,
                                                              String body,
                                                              T form,
                                                              Function<T, Optional<Data>> dataExtractor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(body);
        alert.setGraphic(null);
        alert.setResizable(true);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(form);
        dialogPane.getScene().getStylesheets().add(MainApplication.class.getResource("application.css").toExternalForm());
        dialogPane.setMinWidth(450.0);
        Platform.runLater(form::requestFocus);

        return alert.showAndWait()
                .filter(bt -> bt == ButtonType.OK)
                .flatMap(bt -> dataExtractor.apply(form));
    }

    public Optional<FilterCondition> showFilterSelectionAlert(String title,
                                                              String body,
                                                              FilterSelectionBox filterSelectionBox) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(body);
        alert.setGraphic(null);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(filterSelectionBox);
        dialogPane.getScene().getStylesheets().add(MainApplication.class.getResource("application.css").toExternalForm());
        dialogPane.setMinWidth(450.0);

        return alert.showAndWait()
                .filter(bt -> bt == ButtonType.OK)
                .map(bt -> filterSelectionBox.getCondition())
                .filter(FilterCondition::conditionValid);
    }

    private void userSetup(Stage stage) throws EditorInitializationException {
        Preferences preferences = jsonManager.readPreferences()
                .filter(p -> showAlert(Alert.AlertType.CONFIRMATION,
                        "Existing Setup",
                        "Use previously set preferences (drops directory, patches, save directory etc.)?")
                        .map(bt -> bt == ButtonType.OK)
                        .orElse(false))
                .or(() -> showPreferencesAlert(stage))
                .orElseThrow(() -> new EditorInitializationException("Preferences Not Present",
                        "No preferences were provided to the program. Please relaunch."));

        try {
            jsonManager.setPreferences(preferences);
            jsonManager.savePreferences();
            iconManager.loadIcons(preferences.getIconDirectory());

            jsonManager.readAllData(staticDataStore);
            drops = jsonManager.generatePatchedDrops();
            drops.manageMaps();

        } catch (NullPointerException
                 | IllegalStateException
                 | ClassCastException
                 | JsonSyntaxException
                 | NumberFormatException
                 | URISyntaxException
                 | IOException e) {
            throw new EditorInitializationException(e,
                    "Files Not Readable or Writable",
                    e.getMessage() + " Please relaunch the program and opt to not load the preference file.");
        }
    }

    @Override
    public void init() throws Exception {
        super.init();
        staticDataStore = new StaticDataStore();
        jsonManager = new JSONManager();
        iconManager = new IconManager();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 700);
        scene.getStylesheets().add(MainApplication.class.getResource("application.css").toExternalForm());
        stage.setTitle("OpenFusion Drop Editor Tool");
        stage.setScene(scene);

        try {
            userSetup(stage);
        } catch (EditorInitializationException e) {
            showAlert(Alert.AlertType.ERROR, e.getTitle(), e.getContent());
            throw e;
        }

        controller = fxmlLoader.getController();
        controller.setJSONManager(jsonManager);
        controller.setIconManager(iconManager);
        controller.setDrops(drops);
        controller.setStaticDataStore(staticDataStore);
        controller.setApplication(this);
        controller.setup(scene);

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        controller.stop();
        jsonManager.saveAllData(drops);
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}