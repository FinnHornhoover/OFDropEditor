package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.CrateDropChance;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.IntStream;

public class CrateDropChanceComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<CrateDropChance> crateDropChance;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final MobDropComponent parent;

    private final HBox listHBox;
    private final ScrollPane contentScrollPane;
    private final Label idLabel;

    private final List<ChanceVBox> chanceVBoxCache;

    private final List<ChangeListener<Integer>> valueListeners;
    private final EventHandler<MouseEvent> idClickHandler;

    public CrateDropChanceComponent(double boxSpacing,
                                    double boxWidth,
                                    MainController controller,
                                    MobDropComponent parent) {

        crateDropChance = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        listHBox = new HBox(boxSpacing);
        listHBox.setAlignment(Pos.CENTER);
        listHBox.getStyleClass().add("bordered-pane");

        contentScrollPane = new ScrollPane(listHBox);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentScrollPane);
        setAlignment(idLabel, Pos.TOP_LEFT);

        chanceVBoxCache = new ArrayList<>();

        valueListeners = new ArrayList<>();

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass(),
                        d -> ((CrateDropChance) d).getCrateTypeDropWeights().size() == Optional.ofNullable(this.parent)
                                .map(MobDropComponent::getCrateDropTypeComponent)
                                .map(CrateDropTypeComponent::getCrateDropType)
                                .map(cdt -> cdt.getCrateIDs().size())
                                .orElse(0))
                .ifPresent(this::makeReplacement);
    }

    public void crateDropAdded() {
        makeEdit(() -> crateDropChance.get().getCrateTypeDropWeights().add(0));
    }

    public void crateDropRemoved(int index) {
        makeEdit(() -> crateDropChance.get().getCrateTypeDropWeights().remove(index));
    }

    public void crateDropPermuted(List<Integer> indexList) {
        makeEdit(() -> {
            var dropWeights = crateDropChance.get().getCrateTypeDropWeights();
            var newDropWeights = IntStream.range(0, dropWeights.size())
                    .mapToObj(i -> dropWeights.get(indexList.get(i)))
                    .toList();

            dropWeights.clear();
            dropWeights.addAll(newDropWeights);
        });
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
        return List.of(listHBox);
    }

    @Override
    public Class<CrateDropChance> getObservableClass() {
        return CrateDropChance.class;
    }

    @Override
    public ReadOnlyObjectProperty<CrateDropChance> getObservable() {
        return crateDropChance;
    }

    @Override
    public void setObservable(Data data) {
        crateDropChance.set((CrateDropChance) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        listHBox.getChildren().clear();
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        var dropWeights = crateDropChance.get().getCrateTypeDropWeights();

        if (chanceVBoxCache.size() < dropWeights.size()) {
            IntStream.range(0, dropWeights.size() - chanceVBoxCache.size())
                    .mapToObj(i -> new ChanceVBox(boxWidth))
                    .forEach(chanceVBoxCache::add);
        }

        IntStream.range(0, dropWeights.size())
                .mapToObj(i -> {
                    ChanceVBox cvb = chanceVBoxCache.get(i);
                    cvb.getSpinner().getValueFactory().setValue(dropWeights.get(i));
                    return cvb;
                })
                .forEach(listHBox.getChildren()::add);
    }

    @Override
    public void bindVariablesNonNull() {
        DoubleExpression totalExpression = listHBox.getChildren().stream()
                .filter(c -> c instanceof ChanceVBox)
                .map(c -> DoubleBinding.doubleExpression(
                        ((ChanceVBox) c).getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var children = listHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

        IntStream.range(0, children.size())
                .forEach(index -> {
                    ChanceVBox cvb = (ChanceVBox) children.get(index);

                    cvb.getPercentageLabel().textProperty().bind(
                            DoubleBinding.doubleExpression(cvb.getSpinner().valueProperty())
                                    .multiply(100.0)
                                    .divide(Bindings.max(1.0, totalExpression))
                                    .asString(Locale.US, "%.5f%%")
                    );

                    valueListeners.add((o, oldVal, newVal) -> makeEdit(() ->
                            crateDropChance.get().getCrateTypeDropWeights().set(index, newVal)));
                    cvb.getSpinner().valueProperty().addListener(valueListeners.get(index));
                });
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        var children = listHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

        IntStream.range(0, children.size())
                .forEach(index -> {
                    ChanceVBox cvb = (ChanceVBox) children.get(index);
                    cvb.getPercentageLabel().textProperty().unbind();
                    cvb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
                });

        valueListeners.clear();
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

    public CrateDropChance getCrateDropChance() {
        return crateDropChance.get();
    }

    public ReadOnlyObjectProperty<CrateDropChance> crateDropChanceProperty() {
        return crateDropChance;
    }

    public HBox getListHBox() {
        return listHBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }

    public static class ChanceVBox extends VBox {
        private final StandardSpinner spinner;
        private final Label percentageLabel;

        public ChanceVBox(double width) {
            spinner = new StandardSpinner(0, Integer.MAX_VALUE, 0);
            percentageLabel = new Label();

            setSpacing(2);
            getChildren().addAll(spinner, percentageLabel);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);
        }

        public StandardSpinner getSpinner() {
            return spinner;
        }

        public Label getPercentageLabel() {
            return percentageLabel;
        }
    }
}
