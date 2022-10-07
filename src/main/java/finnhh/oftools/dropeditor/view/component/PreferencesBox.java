package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.JSONManager;
import finnhh.oftools.dropeditor.model.data.Preferences;
import javafx.beans.property.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.util.Optional;
import java.util.function.Supplier;

public class PreferencesBox extends VBox {
    private final Preferences preferences;

    private final StringBox dropDirectoryBox;
    private final StringListBox patchDirectoriesBox;
    private final StringBox xdtFileBox;
    private final StringBox saveDirectoryBox;
    private final BooleanBox standaloneSaveBox;
    private final StringBox iconDirectoryBox;

    private final BooleanProperty malformed;

    public PreferencesBox(Stage stage, JSONManager jsonManager) {
        preferences = jsonManager.getPreferences();

        dropDirectoryBox = new StringBox(this,
                "Drops (\"drops.json\") Directory",
                "Usually the \"tdata\" directory in the server files.",
                () -> chooseDirectory(stage, ".", "Choose Drops Directory")
                        .filter(File::isDirectory),
                jsonManager::validateDropDirectory,
                preferences.dropDirectoryProperty());

        patchDirectoriesBox = new StringListBox(this,
                "Patch Directories (In Order of Application)",
                "Usually some directories inside \"tdata/patches\". You need to specify the 1013 patch for Academy.",
                () -> chooseDirectory(stage, preferences.getDropDirectory(), "Choose Patch Directory")
                        .filter(File::isDirectory),
                jsonManager::validatePatchDirectories,
                preferences.patchDirectoriesProperty());

        xdtFileBox = new StringBox(this,
                "XDT (\"xdt*.json\") File",
                "Usually inside \"tdata\". You need xdt1013.json for Academy.",
                () -> chooseFile(stage, preferences.getDropDirectory(), "Choose XDT File")
                        .filter(File::isFile),
                jsonManager::validateXDTFile,
                preferences.xdtFileProperty());

        saveDirectoryBox = new StringBox(this,
                "Save Directory (Recommended: Empty Directory)",
                "Save directory for the program. Resulting JSONs (and patched JSONs if standalone) will be saved" +
                        " here.",
                () -> chooseDirectory(stage, preferences.getDropDirectory(), "Choose Save Directory")
                        .filter(File::isDirectory),
                jsonManager::validateSaveDirectory,
                preferences.saveDirectoryProperty());

        standaloneSaveBox = new BooleanBox(this,
                "Standalone Save?",
                "If selected, all JSONs will be saved into the save directory as a whole. Edit \"tdatadir\" in" +
                        " the server config to use.\nIf not selected, you will need to specify the patches you used" +
                        " in the server config \"enabledpatches\" in order (plus the save folder if it is separate).",
                preferences.standaloneSaveProperty());

        iconDirectoryBox = new StringBox(this,
                "Icons Directory (Highly Recommended)",
                "Icons extracted from the game files should be here.",
                () -> chooseDirectory(stage, preferences.getDropDirectory(), "Choose Icons Directory")
                        .filter(File::isDirectory),
                jsonManager::validateIconDirectory,
                preferences.iconDirectoryProperty());

        malformed = new SimpleBooleanProperty();
        malformed.bind(dropDirectoryBox.malformedProperty()
                .or(patchDirectoriesBox.malformedProperty())
                .or(xdtFileBox.malformedProperty())
                .or(saveDirectoryBox.malformedProperty())
                .or(iconDirectoryBox.malformedProperty()));

        setSpacing(10);
        getChildren().addAll(
                dropDirectoryBox,
                patchDirectoriesBox,
                xdtFileBox,
                saveDirectoryBox,
                standaloneSaveBox,
                iconDirectoryBox);
        setVgrow(patchDirectoriesBox, Priority.ALWAYS);
    }

    private static Optional<File> chooseFile(Stage stage, String initialDirectory, String title) {
        FileChooser chooser = new FileChooser();
        File root = new File(initialDirectory);
        chooser.setInitialDirectory(root.isDirectory() ? root : new File("."));
        chooser.setTitle(title);
        return Optional.ofNullable(chooser.showOpenDialog(stage));
    }

    private static Optional<File> chooseDirectory(Stage stage, String initialDirectory, String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        File root = new File(initialDirectory);
        chooser.setInitialDirectory(root.isDirectory() ? root : new File("."));
        chooser.setTitle(title);
        return Optional.ofNullable(chooser.showDialog(stage));
    }

    private static void addValidationEffects(Supplier<String> validator,
                                             Node node,
                                             BooleanProperty malformed,
                                             Tooltip tooltip) {

        node.getStyleClass().removeIf("malformed-field"::equals);
        node.getStyleClass().removeIf("warning-field"::equals);

        String errorMessage = validator.get();
        tooltip.setText(errorMessage);
        malformed.set(errorMessage.startsWith("[ERR]"));

        if (errorMessage.isBlank()) {
            Tooltip.uninstall(node, tooltip);
        } else {
            node.getStyleClass().add(errorMessage.startsWith("[ERR]") ? "malformed-field" : "warning-field");
            Tooltip.install(node, tooltip);
        }
    }

    public void refreshValidationEffects() {
        dropDirectoryBox.refreshValidationEffects();
        patchDirectoriesBox.refreshValidationEffects();
        xdtFileBox.refreshValidationEffects();
        saveDirectoryBox.refreshValidationEffects();
        iconDirectoryBox.refreshValidationEffects();
    }

    public Preferences getPreferences() {
        return preferences;
    }

    public StringBox getDropDirectoryBox() {
        return dropDirectoryBox;
    }

    public StringListBox getPatchDirectoriesBox() {
        return patchDirectoriesBox;
    }

    public StringBox getXDTFileBox() {
        return xdtFileBox;
    }

    public StringBox getSaveDirectoryBox() {
        return saveDirectoryBox;
    }

    public BooleanBox getStandaloneSaveBox() {
        return standaloneSaveBox;
    }

    public StringBox getIconDirectoryBox() {
        return iconDirectoryBox;
    }

    public boolean isMalformed() {
        return malformed.get();
    }

    public ReadOnlyBooleanProperty malformedProperty() {
        return malformed;
    }

    public static class StringBox extends VBox {
        private final BooleanProperty malformed;
        private final Label label;
        private final TextField textField;
        private final Tooltip tooltip;
        private final Supplier<String> validator;

        public StringBox(PreferencesBox parent,
                         String title,
                         String hint,
                         Supplier<Optional<File>> selector,
                         Supplier<String> validator,
                         StringProperty property) {

            malformed = new SimpleBooleanProperty();

            label = new Label(title);
            Tooltip hintTooltip = new Tooltip();
            hintTooltip.setShowDelay(Duration.ZERO);
            hintTooltip.setText(hint);
            label.setTooltip(hintTooltip);

            textField = new TextField("");
            textField.setEditable(false);

            tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.ZERO);
            textField.setTooltip(tooltip);

            this.validator = validator;

            property.bind(textField.textProperty());
            textField.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> selector.get()
                    .ifPresent(f -> {
                        textField.setText(f.getAbsolutePath());
                        parent.refreshValidationEffects();
                    }));
            refreshValidationEffects();

            setSpacing(5);
            getStyleClass().add("bordered-pane");
            setPadding(new Insets(5));
            getChildren().addAll(label, textField);
        }

        public void refreshValidationEffects() {
            addValidationEffects(validator, textField, malformed, tooltip);
        }

        public Label getLabel() {
            return label;
        }

        public TextField getTextField() {
            return textField;
        }

        public boolean isMalformed() {
            return malformed.get();
        }

        public ReadOnlyBooleanProperty malformedProperty() {
            return malformed;
        }
    }

    public static class BooleanBox extends HBox {
        private final Label label;
        private final CheckBox checkBox;

        public BooleanBox(PreferencesBox parent, String title, String hint, BooleanProperty property) {
            label = new Label(title);

            checkBox = new CheckBox();
            Tooltip tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.ZERO);
            checkBox.setTooltip(tooltip);

            property.bind(checkBox.selectedProperty());
            checkBox.selectedProperty().addListener((o, oldVal, newVal) -> parent.refreshValidationEffects());

            Tooltip hintTooltip = new Tooltip();
            hintTooltip.setShowDelay(Duration.ZERO);
            hintTooltip.setText(hint);
            Tooltip.install(this, hintTooltip);
            setSpacing(5);
            getStyleClass().add("bordered-pane");
            setPadding(new Insets(5));
            getChildren().addAll(label, checkBox);
        }

        public Label getLabel() {
            return label;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }
    }

    public static class StringListBox extends VBox {
        private final BooleanProperty malformed;
        private final Label label;
        private final Button addButton;
        private final HBox hBox;
        private final ListView<String> listView;
        private final Tooltip tooltip;
        private final Supplier<String> validator;

        public StringListBox(PreferencesBox parent,
                             String title,
                             String hint,
                             Supplier<Optional<File>> selector,
                             Supplier<String> validator,
                             ListProperty<String> property) {

            malformed = new SimpleBooleanProperty();

            label = new Label(title);

            addButton = new Button("+");
            addButton.getStyleClass().addAll("add-button", "slim-button");

            hBox = new HBox(5, label, addButton);
            Tooltip hintTooltip = new Tooltip();
            hintTooltip.setShowDelay(Duration.ZERO);
            hintTooltip.setText(hint);
            Tooltip.install(hBox, hintTooltip);

            listView = new ListView<>();

            tooltip = new Tooltip();
            tooltip.setShowDelay(Duration.ZERO);
            listView.setTooltip(tooltip);

            this.validator = validator;

            listView.setCellFactory(cfData -> new ListCell<>() {
                private final Button removeButton;
                private EventHandler<MouseEvent> removeClickHandler;

                {
                    removeButton = new Button("-");
                    removeButton.getStyleClass().addAll("remove-button", "slim-button");
                    removeClickHandler = event -> { };
                }

                @Override
                protected void updateItem(String s, boolean empty) {
                    super.updateItem(s, empty);

                    removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                    removeClickHandler = event -> { };

                    setText(empty ? "" : s);
                    setGraphic(empty ? null : removeButton);

                    if (!empty) {
                        removeClickHandler = event -> {
                            getListView().getItems().remove(getItem());
                            parent.refreshValidationEffects();
                        };
                        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
                    }
                }
            });
            setVgrow(listView, Priority.ALWAYS);
            property.bind(listView.itemsProperty());
            addButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> selector.get()
                    .ifPresent(f -> {
                        listView.getItems().add(f.getAbsolutePath());
                        parent.refreshValidationEffects();
                    }));
            refreshValidationEffects();

            setSpacing(5);
            getStyleClass().add("bordered-pane");
            setPadding(new Insets(5));
            getChildren().addAll(hBox, listView);
        }

        public void refreshValidationEffects() {
            addValidationEffects(validator, listView, malformed, tooltip);
        }

        public Label getLabel() {
            return label;
        }

        public Button getAddButton() {
            return addButton;
        }

        public HBox getHBox() {
            return hBox;
        }

        public ListView<String> getListView() {
            return listView;
        }

        public boolean isMalformed() {
            return malformed.get();
        }

        public ReadOnlyBooleanProperty malformedProperty() {
            return malformed;
        }
    }
}
