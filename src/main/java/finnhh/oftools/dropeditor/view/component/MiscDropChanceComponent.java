package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.MiscDropChance;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class MiscDropChanceComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MiscDropChance> miscDropChance;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final ChanceVBox potionVBox;
    private final ChanceVBox boostVBox;
    private final ChanceVBox taroVBox;
    private final ChanceVBox fmVBox;
    private final HBox contentHBox;
    private final ScrollPane contentScrollPane;
    private final Label idLabel;

    private final ChangeListener<Number> potionChanceListener;
    private final ChangeListener<Number> potionChanceTotalListener;
    private final ChangeListener<Number> boostChanceListener;
    private final ChangeListener<Number> boostChanceTotalListener;
    private final ChangeListener<Number> taroChanceListener;
    private final ChangeListener<Number> taroChanceTotalListener;
    private final ChangeListener<Number> fmChanceListener;
    private final ChangeListener<Number> fmChanceTotalListener;
    private final EventHandler<MouseEvent> idClickHandler;

    public MiscDropChanceComponent(double boxSpacing,
                                   double boxWidth,
                                   MainController controller,
                                   DataComponent parent) {

        miscDropChance = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        potionVBox = new ChanceVBox(boxWidth);
        boostVBox = new ChanceVBox(boxWidth);
        taroVBox = new ChanceVBox(boxWidth);
        fmVBox = new ChanceVBox(boxWidth);

        contentHBox = new HBox(boxSpacing, potionVBox, boostVBox, taroVBox, fmVBox);
        contentHBox.setAlignment(Pos.CENTER);
        contentHBox.getStyleClass().add("bordered-pane");

        contentScrollPane = new ScrollPane(contentHBox);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentScrollPane);
        setAlignment(idLabel, Pos.TOP_LEFT);

        potionChanceListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setPotionDropChance(newVal.intValue()));
        potionChanceTotalListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setPotionDropChanceTotal(newVal.intValue()));
        boostChanceListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setBoostDropChance(newVal.intValue()));
        boostChanceTotalListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setBoostDropChanceTotal(newVal.intValue()));
        taroChanceListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setTaroDropChance(newVal.intValue()));
        taroChanceTotalListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setTaroDropChanceTotal(newVal.intValue()));
        fmChanceListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setFMDropChance(newVal.intValue()));
        fmChanceTotalListener = (o, oldVal, newVal) -> makeEdit(() ->
                miscDropChance.get().setFMDropChanceTotal(newVal.intValue()));
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
        return List.of(contentHBox);
    }

    @Override
    public Class<MiscDropChance> getObservableClass() {
        return MiscDropChance.class;
    }

    @Override
    public ReadOnlyObjectProperty<MiscDropChance> getObservable() {
        return miscDropChance;
    }

    @Override
    public void setObservable(Data data) {
        miscDropChance.set((MiscDropChance) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        potionVBox.cleanUIState();
        boostVBox.cleanUIState();
        taroVBox.cleanUIState();
        fmVBox.cleanUIState();
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        potionVBox.fillUIState(miscDropChance.get().getPotionDropChance(), miscDropChance.get().getPotionDropChanceTotal());
        boostVBox.fillUIState(miscDropChance.get().getBoostDropChance(), miscDropChance.get().getBoostDropChanceTotal());
        taroVBox.fillUIState(miscDropChance.get().getTaroDropChance(), miscDropChance.get().getTaroDropChanceTotal());
        fmVBox.fillUIState(miscDropChance.get().getFMDropChance(), miscDropChance.get().getFMDropChanceTotal());
    }

    @Override
    public void bindVariablesNonNull() {
        potionVBox.bindVariables(potionChanceListener, potionChanceTotalListener);
        boostVBox.bindVariables(boostChanceListener, boostChanceTotalListener);
        taroVBox.bindVariables(taroChanceListener, taroChanceTotalListener);
        fmVBox.bindVariables(fmChanceListener, fmChanceTotalListener);
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        potionVBox.unbindVariables(potionChanceListener, potionChanceTotalListener);
        boostVBox.unbindVariables(boostChanceListener, boostChanceTotalListener);
        taroVBox.unbindVariables(taroChanceListener, taroChanceTotalListener);
        fmVBox.unbindVariables(fmChanceListener, fmChanceTotalListener);
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

    public MiscDropChance getMiscDropChance() {
        return miscDropChance.get();
    }

    public ReadOnlyObjectProperty<MiscDropChance> miscDropChanceProperty() {
        return miscDropChance;
    }

    public ChanceVBox getPotionVBox() {
        return potionVBox;
    }

    public ChanceVBox getBoostVBox() {
        return boostVBox;
    }

    public ChanceVBox getTaroVBox() {
        return taroVBox;
    }

    public ChanceVBox getFMVBox() {
        return fmVBox;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }

    public static class ChanceVBox extends VBox {
        private final StandardSpinner chanceSpinner;
        private final StandardSpinner chanceTotalSpinner;
        private final Separator chanceSeparator;
        private final VBox chanceFractionVBox;
        private final Slider chanceSlider;
        private final Label chancePercentageLabel;

        private final ChangeListener<Number> sliderChangeListener;
        private final ChangeListener<Integer> spinnerChangeListener;

        public ChanceVBox(double width) {
            this(0, 1, width);
        }

        public ChanceVBox(int chanceValue, int chanceTotalValue, double width) {
            chanceSpinner = new StandardSpinner(0, chanceTotalValue, chanceValue);
            chanceTotalSpinner = new StandardSpinner(1, Integer.MAX_VALUE, chanceTotalValue);

            chanceSeparator = new Separator(Orientation.HORIZONTAL);

            chanceFractionVBox = new VBox(2, chanceSpinner, chanceSeparator, chanceTotalSpinner);
            chanceFractionVBox.setAlignment(Pos.CENTER);

            chanceSlider = new Slider(0, chanceTotalValue, chanceValue);
            chanceSlider.setBlockIncrement(1);
            chanceSlider.setMajorTickUnit(1);
            chanceSlider.setMinorTickCount(0);
            chanceSlider.setShowTickLabels(false);
            chanceSlider.setSnapToTicks(true);

            chancePercentageLabel = new Label();

            setSpacing(2);
            getChildren().addAll(chanceFractionVBox, chanceSlider, chancePercentageLabel);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);

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

        public StandardSpinner getChanceSpinner() {
            return chanceSpinner;
        }

        public StandardSpinner getChanceTotalSpinner() {
            return chanceTotalSpinner;
        }

        public Separator getChanceSeparator() {
            return chanceSeparator;
        }

        public VBox getChanceFractionVBox() {
            return chanceFractionVBox;
        }

        public Slider getChanceSlider() {
            return chanceSlider;
        }

        public Label getChancePercentageLabel() {
            return chancePercentageLabel;
        }
    }
}
