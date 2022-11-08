package finnhh.oftools.dropeditor.view.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.util.Locale;

public class MinMaxChanceBox extends VBox {
    private final Label nameLabel;
    private final StandardSpinner chanceSpinner;
    private final StandardSpinner chanceTotalSpinner;
    private final Separator chanceSeparator;
    private final Slider chanceSlider;
    private final Label chancePercentageLabel;
    private final Pane chanceFractionBox;
    private final Pane contentBox;

    private final ChangeListener<Number> sliderChangeListener;
    private final ChangeListener<Integer> spinnerChangeListener;

    public MinMaxChanceBox(String name) {
        this(name, Orientation.HORIZONTAL, 0.0);
    }

    public MinMaxChanceBox(String name, Orientation orientation, double width) {
        nameLabel = new Label(name + " Chance");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);

        chanceSpinner = new StandardSpinner(0, 1, 0);
        chanceTotalSpinner = new StandardSpinner(1, Integer.MAX_VALUE, 1);

        chanceSeparator = new Separator(orientation == Orientation.HORIZONTAL ?
                Orientation.VERTICAL : Orientation.HORIZONTAL);

        chanceSlider = new Slider(0, 1, 0);
        chanceSlider.setBlockIncrement(1);
        chanceSlider.setMajorTickUnit(1);
        chanceSlider.setMinorTickCount(0);
        chanceSlider.setShowTickLabels(false);
        chanceSlider.setSnapToTicks(true);

        chancePercentageLabel = new Label();
        chancePercentageLabel.setWrapText(true);
        chancePercentageLabel.setTextAlignment(TextAlignment.CENTER);

        if (orientation == Orientation.HORIZONTAL) {
            chanceFractionBox = new HBox(5, chanceSpinner, chanceSeparator, chanceTotalSpinner);
            ((HBox) chanceFractionBox).setAlignment(Pos.CENTER);

            contentBox = new HBox(10, nameLabel, chanceFractionBox, chanceSlider, chancePercentageLabel);
            ((HBox) contentBox).setAlignment(Pos.CENTER_LEFT);
        } else {
            chanceFractionBox = new VBox(2, chanceSpinner, chanceSeparator, chanceTotalSpinner);
            ((VBox) chanceFractionBox).setAlignment(Pos.CENTER);

            contentBox = new VBox(2, nameLabel, chanceFractionBox, chanceSlider, chancePercentageLabel);
            ((VBox) contentBox).setAlignment(Pos.CENTER);

            setMinWidth(width);
            setMaxWidth(width);
        }

        getChildren().add(contentBox);

        sliderChangeListener = (o, oldVal, newVal) -> chanceSpinner.getValueFactory().setValue(newVal.intValue());
        spinnerChangeListener = (o, oldVal, newVal) -> chanceSlider.setValue(newVal);
    }

    public void bindVariables(ChangeListener<Number> valueListener, ChangeListener<Number> totalValueListener) {
        chanceSlider.valueProperty().addListener(sliderChangeListener);
        chanceSpinner.valueProperty().addListener(spinnerChangeListener);

        ((SpinnerValueFactory.IntegerSpinnerValueFactory) chanceSpinner.getValueFactory()).maxProperty()
                .bind(chanceTotalSpinner.valueProperty());
        chanceSlider.maxProperty().bind(chanceTotalSpinner.valueProperty());

        chancePercentageLabel.textProperty().bind(DoubleProperty.doubleExpression(chanceSpinner.valueProperty())
                .multiply(100.0)
                .divide(DoubleProperty.doubleExpression(chanceTotalSpinner.valueProperty()))
                .asString(Locale.US, "%.5f%%"));

        chanceSpinner.valueProperty().addListener(valueListener);
        chanceTotalSpinner.valueProperty().addListener(totalValueListener);
    }

    public void unbindVariables(ChangeListener<Number> valueListener, ChangeListener<Number> totalValueListener) {
        chanceSlider.valueProperty().removeListener(sliderChangeListener);
        chanceSpinner.valueProperty().removeListener(spinnerChangeListener);

        ((SpinnerValueFactory.IntegerSpinnerValueFactory) chanceSpinner.getValueFactory()).maxProperty().unbind();
        chanceSlider.maxProperty().unbind();

        chancePercentageLabel.textProperty().unbind();

        chanceSpinner.valueProperty().removeListener(valueListener);
        chanceTotalSpinner.valueProperty().removeListener(totalValueListener);
    }

    public void cleanUIState() {
        chanceSpinner.getValueFactory().setValue(0);
        chanceTotalSpinner.getValueFactory().setValue(1);
        chanceSlider.setValue(0);
    }

    public void fillUIState(int value, int totalValue) {
        chanceSpinner.getValueFactory().setValue(value);
        chanceTotalSpinner.getValueFactory().setValue(totalValue);
        chanceSlider.setValue(value);
    }

    public Label getNameLabel() {
        return nameLabel;
    }

    public StandardSpinner getChanceSpinner() {
        return chanceSpinner;
    }

    public StandardSpinner getChanceTotalSpinner() {
        return chanceTotalSpinner;
    }

    public Separator getChanceSeparator() {
        return chanceSeparator;
    }

    public Slider getChanceSlider() {
        return chanceSlider;
    }

    public Label getChancePercentageLabel() {
        return chancePercentageLabel;
    }

    public Pane getChanceFractionBox() {
        return chanceFractionBox;
    }

    public Pane getContentBox() {
        return contentBox;
    }
}
