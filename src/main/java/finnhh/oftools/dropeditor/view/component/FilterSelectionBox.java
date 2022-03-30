package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.FilterCondition;
import finnhh.oftools.dropeditor.model.FilterOperator;
import javafx.collections.FXCollections;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PrefixSelectionChoiceBox;
import org.controlsfx.control.ToggleSwitch;

import java.util.Comparator;
import java.util.Set;

public class FilterSelectionBox extends VBox {
    private final PrefixSelectionChoiceBox<FilterChoice> filterChoiceBox;
    private final PrefixSelectionChoiceBox<FilterOperator> operatorChoiceBox;
    private final TextField valueTextField;
    private final ToggleSwitch allowNullsSwitch;
    private final double boxSpacing;

    public FilterSelectionBox(double boxSpacing, Set<FilterChoice> choiceSet) {
        this.boxSpacing = boxSpacing;

        filterChoiceBox = new PrefixSelectionChoiceBox<>();
        filterChoiceBox.setItems(FXCollections.observableArrayList(choiceSet.stream()
                .sorted(Comparator.comparing(FilterChoice::toString))
                .toList()));

        operatorChoiceBox = new PrefixSelectionChoiceBox<>();
        operatorChoiceBox.setItems(FXCollections.observableArrayList());

        allowNullsSwitch = new ToggleSwitch("Allow Null Values?");
        valueTextField = new TextField();
        valueTextField.setPromptText("Value");

        filterChoiceBox.valueProperty().addListener((o, oldVal, newVal) -> {
            operatorChoiceBox.getItems().clear();
            operatorChoiceBox.getItems().addAll(newVal.filterType().getAllowedOperators());
            operatorChoiceBox.getSelectionModel().select(0);
        });
        filterChoiceBox.getSelectionModel().select(0);

        filterChoiceBox.setMaxWidth(Double.POSITIVE_INFINITY);
        operatorChoiceBox.setMaxWidth(Double.POSITIVE_INFINITY);

        setSpacing(boxSpacing);
        getChildren().addAll(filterChoiceBox, operatorChoiceBox, valueTextField, allowNullsSwitch);
    }

    public FilterCondition getCondition() {
        FilterChoice filterChoice = filterChoiceBox.getValue();
        return new FilterCondition(
                filterChoice,
                operatorChoiceBox.getValue(),
                filterChoice.filterType().convertValue(valueTextField.getText()),
                allowNullsSwitch.isSelected()
        );
    }

    public PrefixSelectionChoiceBox<FilterChoice> getFilterChoiceBox() {
        return filterChoiceBox;
    }

    public PrefixSelectionChoiceBox<FilterOperator> getOperatorChoiceBox() {
        return operatorChoiceBox;
    }

    public ToggleSwitch getAllowNullsSwitch() {
        return allowNullsSwitch;
    }

    public TextField getValueTextField() {
        return valueTextField;
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }
}
