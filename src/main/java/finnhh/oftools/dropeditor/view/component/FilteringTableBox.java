package finnhh.oftools.dropeditor.view.component;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class FilteringTableBox<T> extends VBox {
    private final TableView<T> tableView;
    private final TextField textField;

    public FilteringTableBox(final Supplier<List<T>> dataGetter) {
        tableView = new TableView<>();

        textField = new TextField();
        textField.getStyleClass().add("add-graphic-text-field");
        textField.setPromptText("Search by name or by e.g. level=8");
        textField.textProperty().addListener((o, oldVal, newVal) -> {
            tableView.getItems().clear();
            tableView.getItems().addAll(dataGetter.get()
                    .stream()
                    .filter(t -> t.toString().toLowerCase(Locale.ENGLISH)
                            .contains(newVal.toLowerCase(Locale.ENGLISH).trim()))
                    .toList());
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
