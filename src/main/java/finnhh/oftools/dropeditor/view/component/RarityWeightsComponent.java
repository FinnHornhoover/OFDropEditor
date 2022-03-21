package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.Rarity;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.RarityWeights;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class RarityWeightsComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<RarityWeights> rarityWeights;

    private final MainController controller;

    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final VBox contentVBox;
    private final Label idLabel;

    private final List<ChangeListener<Integer>> valueListeners;
    private final EventHandler<MouseEvent> idClickHandler;

    public RarityWeightsComponent(double boxSpacing,
                                  double boxWidth,
                                  MainController controller,
                                  DataComponent parent) {

        rarityWeights = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;

        contentVBox = new VBox(boxSpacing);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.setPadding(new Insets(boxSpacing / 2));
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentVBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        valueListeners = new ArrayList<>();

        idLabel.setText(RarityWeights.class.getSimpleName() + ": null");
        contentVBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(RarityWeights.class)
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        // both makeEditable and setObservable sets the observable, just use a listener here
        rarityWeights.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(RarityWeights.class.getSimpleName() + ": null");
                contentVBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentVBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindListVariables() {
        DoubleExpression totalExpression = contentVBox.getChildren().stream()
                .filter(c -> c instanceof RarityHBox)
                .map(c -> DoubleBinding.doubleExpression(
                        ((RarityHBox) c).getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var children = contentVBox.getChildren().filtered(c -> c instanceof RarityHBox);

        for (int index = 0; index < children.size(); index++) {
            RarityHBox rhb = (RarityHBox) children.get(index);

            rhb.getPercentageLabel().textProperty().bind(
                    DoubleBinding.doubleExpression(rhb.getSpinner().valueProperty())
                            .multiply(100.0)
                            .divide(Bindings.max(1.0, totalExpression))
                            .asString(Locale.US, "%.5f%%")
            );

            rhb.getPercentageSlider().valueProperty().bind(
                    DoubleBinding.doubleExpression(rhb.getSpinner().valueProperty())
                            .multiply(100.0)
                            .divide(Bindings.max(1.0, totalExpression))
            );

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                makeEditable(controller.getDrops());
                rarityWeights.get().getWeights().set(finalIndex, newVal);

                RarityHBox current = (RarityHBox) contentVBox.getChildren()
                        .filtered(c -> c instanceof RarityHBox)
                        .get(finalIndex);
                current.getSpinner().getValueFactory().setValue(newVal);
            });
            rhb.getSpinner().valueProperty().addListener(valueListeners.get(index));
        }
    }

    private void unbindListVariables() {
        var children = contentVBox.getChildren().filtered(c -> c instanceof RarityHBox);

        for (int index = 0; index < children.size(); index++) {
            RarityHBox rhb = (RarityHBox) children.get(index);

            rhb.getPercentageLabel().textProperty().unbind();

            rhb.getPercentageSlider().valueProperty().unbind();

            rhb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
        }

        valueListeners.clear();
    }

    @Override
    public void setObservable(Data data) {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        rarityWeights.set((RarityWeights) data);

        unbindListVariables();
        contentVBox.getChildren().clear();

        if (rarityWeights.isNotNull().get()) {
            ObservableList<Integer> weights = rarityWeights.get().getWeights();

            for (int index = 0; index < Rarity.values().length - 1; index++) {
                int weight = index < weights.size() ? weights.get(index) : 0;

                contentVBox.getChildren().add(new RarityHBox(boxWidth, 2,
                        Rarity.forType(index + 1).getName(), weight));

                // should be safe, since this is what the omission of rarity weights mean
                if (index >= weights.size())
                    weights.add(0);
            }

            bindListVariables();
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public RarityWeights getRarityWeights() {
        return rarityWeights.get();
    }

    public ReadOnlyObjectProperty<RarityWeights> rarityWeightsProperty() {
        return rarityWeights;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    @Override
    public ReadOnlyObjectProperty<RarityWeights> getObservable() {
        return rarityWeights;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class RarityHBox extends HBox {
        private final double boxWidth;
        private final double boxSpacing;
        private final Label nameLabel;
        private final Spinner<Integer> spinner;
        private final Label percentageLabel;
        private final VBox contentVBox;
        private final Slider percentageSlider;

        public RarityHBox(double boxWidth, double boxSpacing, String name, int weight) {
            this.boxWidth = boxWidth;
            this.boxSpacing = boxSpacing;

            nameLabel = new Label(name);
            spinner = new Spinner<>(0, Integer.MAX_VALUE, weight);
            spinner.setEditable(true);
            spinner.getEditor().setOnAction(event -> {
                try {
                    Integer.parseInt(spinner.getEditor().getText());
                    spinner.commitValue();
                } catch (NumberFormatException e) {
                    spinner.cancelEdit();
                }
            });

            percentageLabel = new Label();
            contentVBox = new VBox(boxSpacing, nameLabel, spinner, percentageLabel);
            contentVBox.setMinWidth(boxWidth);
            contentVBox.setMaxWidth(boxWidth);
            contentVBox.setAlignment(Pos.CENTER);

            percentageSlider = new Slider();
            percentageSlider.setShowTickLabels(false);
            percentageSlider.setMouseTransparent(true);
            percentageSlider.setOrientation(Orientation.HORIZONTAL);
            percentageSlider.setPrefWidth(2 * boxWidth);

            setSpacing(boxSpacing);
            getChildren().addAll(contentVBox, percentageSlider);
            setAlignment(Pos.CENTER);
        }

        public double getBoxWidth() {
            return boxWidth;
        }

        public double getBoxSpacing() {
            return boxSpacing;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public Spinner<Integer> getSpinner() {
            return spinner;
        }

        public Label getPercentageLabel() {
            return percentageLabel;
        }

        public VBox getContentVBox() {
            return contentVBox;
        }

        public Slider getPercentageSlider() {
            return percentageSlider;
        }
    }
}
