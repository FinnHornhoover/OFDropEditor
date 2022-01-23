package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.data.CrateDropChance;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CrateDropChanceComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<CrateDropChance> crateDropChance;

    private final Drops drops;

    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final HBox contentHBox;
    private final Label idLabel;

    private final ListChangeListener<Node> childrenListener;
    private final List<ChangeListener<Integer>> valueListeners;

    public CrateDropChanceComponent(double boxSpacing,
                                    double boxWidth,
                                    Drops drops,
                                    DataComponent parent) {
        this.drops = drops;
        crateDropChance = new SimpleObjectProperty<>();

        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        contentHBox = new HBox(boxSpacing);
        contentHBox.setAlignment(Pos.CENTER);
        contentHBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentHBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        childrenListener = change -> {
            makeEditable(this.drops);
            unbindListVariables();

            ObservableList<Integer> weights = crateDropChance.get().getCrateTypeDropWeights();
            weights.clear();
            weights.addAll(contentHBox.getChildren().stream()
                    .filter(c -> c instanceof ChanceVBox)
                    .map(c -> ((ChanceVBox) c).getSpinner().getValue())
                    .toList());

            bindListVariables();
        };
        valueListeners = new ArrayList<>();

        idLabel.setText(CrateDropChance.class.getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        crateDropChance.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(CrateDropChance.class.getSimpleName() + ": null");
                contentHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindListVariables() {
        DoubleExpression totalExpression = contentHBox.getChildren().stream()
                .map(c -> DoubleBinding.doubleExpression(
                        ((ChanceVBox) c).getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var children = contentHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

        for (int index = 0; index < children.size(); index++) {
            ChanceVBox cvb = (ChanceVBox) children.get(index);

            cvb.getPercentageLabel().textProperty().bind(
                    DoubleBinding.doubleExpression(cvb.getSpinner().valueProperty())
                            .multiply(100.0)
                            .divide(Bindings.max(1.0, totalExpression))
                            .asString(Locale.US, "%.5f%%")
            );

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                makeEditable(drops);
                crateDropChance.get().getCrateTypeDropWeights().set(finalIndex, newVal);

                ChanceVBox current = (ChanceVBox) contentHBox.getChildren()
                        .filtered(c -> c instanceof ChanceVBox)
                        .get(finalIndex);
                current.getSpinner().getValueFactory().setValue(newVal);
            });
            cvb.getSpinner().valueProperty().addListener(valueListeners.get(index));
        }
    }

    private void unbindListVariables() {
        var children = contentHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

        for (int index = 0; index < children.size(); index++) {
            ChanceVBox cvb = (ChanceVBox) children.get(index);

            cvb.getPercentageLabel().textProperty().unbind();

            cvb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
        }

        valueListeners.clear();
    }

    @Override
    public void setObservable(Data data) {
        crateDropChance.set((CrateDropChance) data);

        contentHBox.getChildren().removeListener(childrenListener);
        unbindListVariables();
        contentHBox.getChildren().clear();

        if (crateDropChance.isNotNull().get()) {
            contentHBox.getChildren().addAll(crateDropChance.get()
                    .getCrateTypeDropWeights().stream()
                    .map(i -> new ChanceVBox(boxWidth, i))
                    .toList());

            bindListVariables();
        }

        contentHBox.getChildren().addListener(childrenListener);
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public CrateDropChance getCrateDropChance() {
        return crateDropChance.get();
    }

    public ReadOnlyObjectProperty<CrateDropChance> crateDropChanceProperty() {
        return crateDropChance;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    @Override
    public ReadOnlyObjectProperty<CrateDropChance> getObservable() {
        return crateDropChance;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class ChanceVBox extends VBox {
        private final Spinner<Integer> spinner;
        private final Label percentageLabel;

        public ChanceVBox(double width, int dropWeightValue) {
            var spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, Integer.MAX_VALUE, dropWeightValue);
            spinner = new Spinner<>(spinnerValueFactory);
            spinner.setEditable(true);

            percentageLabel = new Label();

            setSpacing(2);
            getChildren().addAll(spinner, percentageLabel);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);
        }

        public Spinner<Integer> getSpinner() {
            return spinner;
        }

        public Label getPercentageLabel() {
            return percentageLabel;
        }
    }
}
