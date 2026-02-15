package finnhh.oftools.dropeditor.view.component;

import javafx.animation.PauseTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private final BooleanProperty idAboveSwitchEnabled;

    private final TableView<T> tableView;
    private final TextField textField;
    private final ToggleSwitch shouldUseOneIDAbove;
    private final PauseTransition pause;

    public FilteringTableBox(final Supplier<List<T>> dataGetter) {
        idAboveSwitchEnabled = new SimpleBooleanProperty(false);

        tableView = new TableView<>();

        textField = new TextField();
        textField.getStyleClass().add("add-graphic-text-field");
        textField.setPromptText("Search by name or by e.g. level=8");

        shouldUseOneIDAbove = new ToggleSwitch("Try cloning selected object at ID + 1 ?");
        Tooltip tooltip = new Tooltip(
                "Turn ON to use a copied version of the selected object " +
                "at one ID above it.\nWill only work if that ID is not taken.");
        tooltip.setShowDelay(Duration.ZERO);
        shouldUseOneIDAbove.setTooltip(tooltip);

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

        idAboveSwitchEnabled.addListener((o, oldVal, newVal) -> {
            if (newVal) {
                getChildren().add(shouldUseOneIDAbove);
                shouldUseOneIDAbove.setDisable(false);
            } else {
                getChildren().remove(shouldUseOneIDAbove);
                shouldUseOneIDAbove.setDisable(true);
            }
            shouldUseOneIDAbove.setSelected(false);
        });
    }

    public boolean isIDAboveSwitchEnabled() {
        return idAboveSwitchEnabled.get();
    }

    public BooleanProperty idAboveSwitchEnabledProperty() {
        return idAboveSwitchEnabled;
    }

    public void setIDAboveSwitchEnabled(boolean idAboveSwitchEnabled) {
        this.idAboveSwitchEnabled.set(idAboveSwitchEnabled);
    }

    public TableView<T> getTableView() {
        return tableView;
    }

    public TextField getTextField() {
        return textField;
    }

    public ToggleSwitch getShouldUseOneIDAbove() {
        return shouldUseOneIDAbove;
    }
}
