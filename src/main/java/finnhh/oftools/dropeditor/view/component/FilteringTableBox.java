package finnhh.oftools.dropeditor.view.component;

import javafx.animation.PauseTransition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class FilteringTableBox<T> extends VBox {
    private final TableView<T> tableView;
    private final TextField textField;
    private final PauseTransition pause;

    public FilteringTableBox(final Supplier<List<T>> dataGetter) {
        tableView = new TableView<>();

        textField = new TextField();
        textField.getStyleClass().add("add-graphic-text-field");
        textField.setPromptText("Search by name or by e.g. level=8");

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

    public TableView<T> getTableView() {
        return tableView;
    }

    public TextField getTextField() {
        return textField;
    }
}
