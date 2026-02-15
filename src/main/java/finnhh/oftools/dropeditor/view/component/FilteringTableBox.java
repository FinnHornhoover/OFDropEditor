package finnhh.oftools.dropeditor.view.component;

import javafx.animation.PauseTransition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.controlsfx.control.ToggleSwitch;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class FilteringTableBox<T> extends VBox {
    private final TableView<T> tableView;
    private final TextField textField;
    private final ToggleSwitch shouldCloneToDesiredIDSwitch;
    private final TextField desiredIDTextField;
    private final PauseTransition pause;

    public FilteringTableBox(final Supplier<List<T>> dataGetter) {
        tableView = new TableView<>();

        textField = new TextField();
        textField.getStyleClass().add("add-graphic-text-field");
        textField.setPromptText("Search by name or by e.g. level=8");

        shouldCloneToDesiredIDSwitch = new ToggleSwitch("Try cloning selected object at ID below?");
        Tooltip tooltip = new Tooltip(
                "Turn ON to use a copied version of the selected object " +
                "with the ID specified in the text box.\nWill only work if that ID is not taken and valid.");
        tooltip.setShowDelay(Duration.ZERO);
        shouldCloneToDesiredIDSwitch.setTooltip(tooltip);
        shouldCloneToDesiredIDSwitch.setDisable(true);

        desiredIDTextField = new TextField();
        desiredIDTextField.getStyleClass().add("add-graphic-text-field");
        desiredIDTextField.setDisable(true);

        // Only update after the user stops typing (debounce with PauseTransition)
        pause = new PauseTransition(Duration.millis(350));

        textField.textProperty().addListener((o, oldVal, newVal) -> {
            pause.stop();
            pause.setOnFinished(e -> {
                tableView.getItems().clear();
                tableView.getItems().addAll(dataGetter.get()
                        .stream()
                        .filter(t -> t.toString().toLowerCase(Locale.ENGLISH)
                                .contains(newVal.toLowerCase(Locale.ENGLISH).trim()))
                        .toList());
            });
            pause.playFromStart();
        });

        setSpacing(8);
        getChildren().addAll(textField, tableView);
        setVgrow(tableView, Priority.ALWAYS);
    }

    public void enableCloneSelectedObjectChoice(int nextTrueID) {
        shouldCloneToDesiredIDSwitch.setDisable(false);
        desiredIDTextField.setText(String.valueOf(nextTrueID));

        shouldCloneToDesiredIDSwitch.selectedProperty().addListener((o, oldVal, newVal) ->
                desiredIDTextField.setDisable(!newVal));

        getChildren().addAll(shouldCloneToDesiredIDSwitch, desiredIDTextField);
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public TextField getTextField() {
        return textField;
    }

    public ToggleSwitch getShouldCloneToDesiredIDSwitch() {
        return shouldCloneToDesiredIDSwitch;
    }

    public TextField getDesiredIDTextField() {
        return desiredIDTextField;
    }
}
