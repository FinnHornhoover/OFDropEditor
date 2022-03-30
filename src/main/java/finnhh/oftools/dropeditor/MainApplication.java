package finnhh.oftools.dropeditor;

import com.google.gson.JsonSyntaxException;
import finnhh.oftools.dropeditor.model.FilterCondition;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Preferences;
import finnhh.oftools.dropeditor.model.exception.EditorInitializationException;
import finnhh.oftools.dropeditor.view.component.FilterSelectionBox;
import finnhh.oftools.dropeditor.view.component.ReferenceListBox;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class MainApplication extends Application {
    private StaticDataStore staticDataStore;
    private Drops drops;
    private JSONManager jsonManager;
    private IconManager iconManager;

    private Optional<ButtonType> showAlert(Alert.AlertType alertType, String title, String body) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(body);
        return alert.showAndWait();
    }

    private Optional<File> chooseFile(Stage stage, File initialDirectory, String title) {
        FileChooser chooser = new FileChooser();
        chooser.setInitialDirectory(initialDirectory);
        chooser.setTitle(title);
        return Optional.ofNullable(chooser.showOpenDialog(stage));
    }

    private Optional<File> chooseDirectory(Stage stage, File initialDirectory, String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setInitialDirectory(initialDirectory);
        chooser.setTitle(title);
        return Optional.ofNullable(chooser.showDialog(stage));
    }

    private void userSetup(Stage stage) throws EditorInitializationException {
        // step 0: prompt for previous program preferences
        Optional<Preferences> preferences = jsonManager.readPreferences();

        if (preferences.isPresent()) {
            Optional<ButtonType> result = showAlert(Alert.AlertType.CONFIRMATION,
                    "Existing Setup",
                    "Use previously set preferences (drops directory, patches, save directory etc.)?");

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String iconDirectory = preferences.get().iconDirectory;

                    if (iconDirectory != null)
                        iconManager.setIconDirectory(new File(iconDirectory));

                    jsonManager.setFromPreferences(preferences.get(), staticDataStore);
                    drops = Objects.requireNonNull(jsonManager.getPatchedObject("drops", Drops.class));
                    drops.manageMaps();

                } catch (NullPointerException
                        | IllegalStateException
                        | ClassCastException
                        | JsonSyntaxException
                        | NumberFormatException
                        | IOException e) {
                    throw new EditorInitializationException(e,
                            "Corrupted Preference File",
                            "The program preferences from your last session appears to be corrupted. " +
                                    "Please relaunch the editor, and opt to not load the preference file.");
                }
                return;
            }
        }

        // fresh start
        showAlert(Alert.AlertType.INFORMATION,
                "Welcome to Open Fusion Drop Editor!",
                "Welcome! We have a few steps ahead to set you up, but don't worry, we will save these " +
                        "settings so that you can use the editor immediately the next time you open the editor.");

        // step 1: select directory of drops.json
        Optional<File> dropsDirectory = chooseDirectory(stage,
                new File("."),
                "Choose Drops Directory");

        try {
            jsonManager.setDropsDirectory(dropsDirectory.get());
        } catch (NullPointerException | NoSuchElementException | JsonSyntaxException | IOException e) {
            throw new EditorInitializationException(e,
                    "Invalid Drops Directory",
                    "Please relaunch the editor and select the directory in your server with \"drops.json\" inside.");
        }

        // step 2: patch selection loop
        Optional<ButtonType> patchSelection = showAlert(Alert.AlertType.CONFIRMATION,
                "Select Patch ?",
                "Would you like to specify a patch directory? " +
                        "This is required for builds that use the 104 (OG FF 2010) build as a base (Academy 1013 " +
                        "build etc.).");

        while (patchSelection.isPresent() && patchSelection.get() == ButtonType.OK) {
            Optional<File> patchDirectory = chooseDirectory(stage,
                    dropsDirectory.get(),
                    "Choose Patch Directory");

            try {
                jsonManager.addPatchPath(patchDirectory.get());
            } catch (NoSuchElementException | IOException e) {
                showAlert(Alert.AlertType.WARNING,
                        "Invalid Patch Folder",
                        "There exists no JSON files to use for the patch in this folder (i.e. \"drops.json\"). " +
                                "Skipping...");
            }

            patchSelection = showAlert(Alert.AlertType.CONFIRMATION,
                    "Select Another Patch ?",
                    "Would you like to specify another patch directory?");
        }

        try {
            drops = Objects.requireNonNull(jsonManager.getPatchedObject("drops", Drops.class));
            drops.manageMaps();
        } catch (NullPointerException | NumberFormatException | JsonSyntaxException e) {
            throw new EditorInitializationException(e,
                    "Invalid Drops Directory or Patch Directory",
                    "Please relaunch the editor and select the main and patch directories in your server with " +
                            "\"drops.json\" inside.");
        }

        // step 3: select xdt file
        Optional<File> xdtFile = chooseFile(stage,
                dropsDirectory.get(),
                "Choose XDT File for Your Build");

        try {
            jsonManager.setXDT(xdtFile.get(), staticDataStore);
        } catch (NullPointerException
                | NoSuchElementException
                | IllegalStateException
                | ClassCastException
                | JsonSyntaxException
                | IOException e) {
            throw new EditorInitializationException(e,
                    "Invalid XDT File",
                    "Please relaunch the editor select the XDT file for your build.");
        }

        // step 4: decide on saving preferences
        // step 4.1: decide on save mode (standalone / cumulative)
        boolean standaloneSave = true;

        if (jsonManager.getPatchDirectories().size() > 0) {
            Optional<ButtonType> saveTypeSelection = showAlert(Alert.AlertType.CONFIRMATION,
                    "Save as Standalone Patch ?",
                    "Would you like to save your edits as a standalone patch over the base build? " +
                            "If not, the saved files will build on top of the patches you specified, and you will " +
                            "have to specify the loading order of the patches in the server configuration file.");

            standaloneSave = saveTypeSelection.isPresent() && saveTypeSelection.get() == ButtonType.OK;
        }

        // step 4.2: select save directory
        Optional<File> saveDirectory = chooseDirectory(stage,
                dropsDirectory.get(),
                "Choose Save Directory");

        try {
            jsonManager.setSavePreferences(saveDirectory.get(), standaloneSave);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            throw new EditorInitializationException(e,
                    "Invalid Save Directory",
                    "Save directory is invalid. Keep in mind that you cannot save your progress in a patch file " +
                            "you loaded, unless it is the last one loaded.");
        }

        // step 5: prompt for custom icon directory
        Optional<ButtonType> iconDirectorySelection = showAlert(Alert.AlertType.CONFIRMATION,
                "Select Custom Icon Directory ?",
                "Would you like to select a directory containing in-game icons?");

        if (iconDirectorySelection.isPresent() && iconDirectorySelection.get() == ButtonType.OK) {
            Optional<File> iconDirectory = chooseDirectory(stage,
                    dropsDirectory.get(),
                    "Choose Icon Directory");

            try {
                iconManager.setIconDirectory(iconDirectory.get());
                jsonManager.setIconDirectory(iconDirectory.get());
            } catch (NoSuchElementException | IOException e) {
                throw new EditorInitializationException(e,
                        "Icons Not Loaded",
                        "The icon directory does not contain any icons or does not conform to the UnityPackFF " +
                                "extraction format. Please reload and select a valid icon directory.");
            }
        }

        // step 6: save preferences for future use
        try {
            jsonManager.savePreferences();
        } catch (IOException e) {
            throw new EditorInitializationException(e,
                    "Preferences Not Saved",
                    "Your preferences could not be saved. Please delete or move the preference file and relaunch.");
        }
    }

    public Optional<Data> showSelectionAlert(String title,
                                             String body,
                                             TableView<ReferenceListBox> tableView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(body);
        alert.setGraphic(null);
        alert.setResizable(true);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setContent(tableView);
        dialogPane.getScene().getStylesheets().add(MainApplication.class.getResource("application.css").toExternalForm());
        dialogPane.setMinWidth(450.0);
        Platform.runLater(tableView::requestFocus);

        return alert.showAndWait()
                .filter(bt -> bt == ButtonType.OK)
                .map(bt -> tableView.getSelectionModel().getSelectedItem())
                .map(ReferenceListBox::getOriginData);
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

        MainController controller = fxmlLoader.getController();
        controller.setJSONManager(jsonManager);
        controller.setIconManager(iconManager);
        controller.setDrops(drops);
        controller.setStaticDataStore(staticDataStore);
        controller.setApplication(this);
        controller.setup();

        stage.show();
    }

    @Override
    public void stop() throws Exception {
        jsonManager.save(drops);  // TODO save prompt
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}