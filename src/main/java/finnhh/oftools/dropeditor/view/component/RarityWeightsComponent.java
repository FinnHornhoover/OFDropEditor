package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.FilterType;
import finnhh.oftools.dropeditor.model.Rarity;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.RarityWeights;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.IntStream;

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

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass())
                .ifPresent(this::makeReplacement);
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public List<Pane> getContentPanes() {
        return List.of(contentVBox);
    }

    @Override
    public Class<RarityWeights> getObservableClass() {
        return RarityWeights.class;
    }

    @Override
    public ReadOnlyObjectProperty<RarityWeights> getObservable() {
        return rarityWeights;
    }

    @Override
    public void setObservable(Data data) {
        rarityWeights.set((RarityWeights) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        contentVBox.getChildren().clear();
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        ObservableList<Integer> weights = rarityWeights.get().getWeights();

        IntStream.range(0, Rarity.values().length - 1)
                .forEach(index -> {
                    int weight = index < weights.size() ? weights.get(index) : 0;

                    contentVBox.getChildren().add(new RarityHBox(boxWidth, 2,
                            Rarity.forType(index + 1).getName(), weight));

                    // should be safe, since this is what the omission of rarity weights mean
                    if (index >= weights.size())
                        weights.add(0);
                });
    }

    @Override
    public void bindVariablesNonNull() {
        DoubleExpression totalExpression = contentVBox.getChildren().stream()
                .filter(c -> c instanceof RarityHBox)
                .map(c -> DoubleBinding.doubleExpression(
                        ((RarityHBox) c).getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var children = contentVBox.getChildren().filtered(c -> c instanceof RarityHBox);

        IntStream.range(0, children.size())
                .forEach(index -> {
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

                    valueListeners.add((o, oldVal, newVal) -> makeEdit(() ->
                            rarityWeights.get().getWeights().set(index, newVal)));
                    rhb.getSpinner().valueProperty().addListener(valueListeners.get(index));
                });
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        var children = contentVBox.getChildren().filtered(c -> c instanceof RarityHBox);

        IntStream.range(0, children.size())
                .forEach(index -> {
                    RarityHBox rhb = (RarityHBox) children.get(index);

                    rhb.getPercentageLabel().textProperty().unbind();
                    rhb.getPercentageSlider().valueProperty().unbind();
                    rhb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
                });

        valueListeners.clear();
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        String[] rarities = new String[] { "common", "uncommon", "rare", "ultraRare" };

        IntStream.range(0, rarities.length)
                .forEach(index -> {
                    allValues.add(new FilterChoice(
                            FilterType.INTEGER,
                            op -> op.map(o -> (RarityWeights) o)
                                    .map(rw -> Bindings.integerValueAt(rw.getWeights(), index))
                                    .stream().toList(),
                            List.of(rarities[index] + "Weight", getObservableClass().getSimpleName())
                    ));

                    allValues.add(new FilterChoice(
                            FilterType.DOUBLE,
                            op -> op.map(o -> (RarityWeights) o)
                                    .map(rw -> Bindings.integerValueAt(rw.getWeights(), index)
                                            .multiply(100.0)
                                            .divide(Bindings.createIntegerBinding(() ->
                                                            rw.getWeights().stream().reduce(0, Integer::sum),
                                                    rw.getWeights())))
                                    .stream().toList(),
                            List.of(rarities[index] + "Percent", getObservableClass().getSimpleName())
                    ));
                });

        allValues.add(new FilterChoice(
                FilterType.LIST,
                op -> op.map(o -> (RarityWeights) o)
                        .map(rw -> {
                            int total = rw.getWeights().stream().reduce(0, Integer::sum);
                            return new SimpleListProperty<>(FXCollections.observableArrayList(
                                    rw.getWeights().stream().map(w -> 100.0 * w / total).toList()));
                        })
                        .stream().toList(),
                List.of("percents", getObservableClass().getSimpleName())
        ));

        return allValues;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
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

    public static class RarityHBox extends HBox {
        private final double boxWidth;
        private final double boxSpacing;
        private final Label nameLabel;
        private final StandardSpinner spinner;
        private final Label percentageLabel;
        private final VBox contentVBox;
        private final Slider percentageSlider;

        public RarityHBox(double boxWidth, double boxSpacing, String name, int weight) {
            this.boxWidth = boxWidth;
            this.boxSpacing = boxSpacing;

            nameLabel = new Label(name);
            spinner = new StandardSpinner(0, Integer.MAX_VALUE, weight);

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

        public StandardSpinner getSpinner() {
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
