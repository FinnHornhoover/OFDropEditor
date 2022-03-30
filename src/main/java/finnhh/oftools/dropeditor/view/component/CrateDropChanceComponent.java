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
import javafx.scene.control.Spinner;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.*;
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
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        listHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        crateDropChance.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                listHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                listHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindListVariables() {
        DoubleExpression totalExpression = listHBox.getChildren().stream()
                .filter(c -> c instanceof ChanceVBox)
                .map(c -> DoubleBinding.doubleExpression(
                        ((ChanceVBox) c).getSpinner().valueProperty()))
                .reduce(DoubleExpression::add)
                .orElse(Bindings.createDoubleBinding(() -> 0.0));

        var children = listHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

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
                makeEditable(controller.getDrops());
                crateDropChance.get().getCrateTypeDropWeights().set(finalIndex, newVal);

                ChanceVBox current = (ChanceVBox) listHBox.getChildren()
                        .filtered(c -> c instanceof ChanceVBox)
                        .get(finalIndex);
                current.getSpinner().getValueFactory().setValue(newVal);
            });
            cvb.getSpinner().valueProperty().addListener(valueListeners.get(index));
        }
    }

    private void unbindListVariables() {
        var children = listHBox.getChildren().filtered(c -> c instanceof ChanceVBox);

        for (int index = 0; index < children.size(); index++) {
            ChanceVBox cvb = (ChanceVBox) children.get(index);

            cvb.getPercentageLabel().textProperty().unbind();

            cvb.getSpinner().valueProperty().removeListener(valueListeners.get(index));
        }

        valueListeners.clear();
    }

    private void populateListBox() {
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

    public void crateDropAdded() {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        crateDropChance.get().getCrateTypeDropWeights().add(0);

        populateListBox();
        bindListVariables();
    }

    public void crateDropRemoved(int index) {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        crateDropChance.get().getCrateTypeDropWeights().remove(index);

        populateListBox();
        bindListVariables();
    }

    public void crateDropPermuted(List<Integer> indexList) {
        makeEditable(controller.getDrops());

        unbindListVariables();
        listHBox.getChildren().clear();

        var dropWeights = crateDropChance.get().getCrateTypeDropWeights();
        var newDropWeights = IntStream.range(0, dropWeights.size())
                .mapToObj(i -> dropWeights.get(indexList.get(i)))
                .toList();

        dropWeights.clear();
        dropWeights.addAll(newDropWeights);

        populateListBox();
        bindListVariables();
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
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        crateDropChance.set((CrateDropChance) data);

        unbindListVariables();
        listHBox.getChildren().clear();

        if (crateDropChance.isNotNull().get()) {
            populateListBox();
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

        public ChanceVBox(double width) {
            spinner = new Spinner<>(0, Integer.MAX_VALUE, 0);
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
