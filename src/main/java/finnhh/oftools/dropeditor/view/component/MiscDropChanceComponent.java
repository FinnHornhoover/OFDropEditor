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
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Objects;

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

        potionChanceListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setPotionDropChance(newVal.intValue());
                potionVBox.getChanceSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        potionChanceTotalListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setPotionDropChanceTotal(newVal.intValue());
                potionVBox.getChanceTotalSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        boostChanceListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setBoostDropChance(newVal.intValue());
                boostVBox.getChanceSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        boostChanceTotalListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setBoostDropChanceTotal(newVal.intValue());
                boostVBox.getChanceTotalSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        taroChanceListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setTaroDropChance(newVal.intValue());
                taroVBox.getChanceSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        taroChanceTotalListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setTaroDropChanceTotal(newVal.intValue());
                taroVBox.getChanceTotalSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        fmChanceListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setFMDropChance(newVal.intValue());
                fmVBox.getChanceSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        fmChanceTotalListener = (o, oldVal, newVal) -> {
            if (miscDropChance.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropChance.get().setFMDropChanceTotal(newVal.intValue());
                fmVBox.getChanceTotalSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(getObservableClass())
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        // both makeEditable and setObservable sets the observable, just use a listener here
        miscDropChance.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                contentHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindVariables() {
        potionVBox.getChanceSpinner().valueProperty().addListener(potionChanceListener);
        potionVBox.getChanceTotalSpinner().valueProperty().addListener(potionChanceTotalListener);
        boostVBox.getChanceSpinner().valueProperty().addListener(boostChanceListener);
        boostVBox.getChanceTotalSpinner().valueProperty().addListener(boostChanceTotalListener);
        taroVBox.getChanceSpinner().valueProperty().addListener(taroChanceListener);
        taroVBox.getChanceTotalSpinner().valueProperty().addListener(taroChanceTotalListener);
        fmVBox.getChanceSpinner().valueProperty().addListener(fmChanceListener);
        fmVBox.getChanceTotalSpinner().valueProperty().addListener(fmChanceTotalListener);
    }

    private void unbindVariables() {
        potionVBox.getChanceSpinner().valueProperty().removeListener(potionChanceListener);
        potionVBox.getChanceTotalSpinner().valueProperty().removeListener(potionChanceTotalListener);
        boostVBox.getChanceSpinner().valueProperty().removeListener(boostChanceListener);
        boostVBox.getChanceTotalSpinner().valueProperty().removeListener(boostChanceTotalListener);
        taroVBox.getChanceSpinner().valueProperty().removeListener(taroChanceListener);
        taroVBox.getChanceTotalSpinner().valueProperty().removeListener(taroChanceTotalListener);
        fmVBox.getChanceSpinner().valueProperty().removeListener(fmChanceListener);
        fmVBox.getChanceTotalSpinner().valueProperty().removeListener(fmChanceTotalListener);
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
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        miscDropChance.set((MiscDropChance) data);

        unbindVariables();

        if (miscDropChance.isNull().get()) {
            potionVBox.getChanceSpinner().getValueFactory().setValue(0);
            potionVBox.getChanceTotalSpinner().getValueFactory().setValue(1);
            boostVBox.getChanceSpinner().getValueFactory().setValue(0);
            boostVBox.getChanceTotalSpinner().getValueFactory().setValue(1);
            taroVBox.getChanceSpinner().getValueFactory().setValue(0);
            taroVBox.getChanceTotalSpinner().getValueFactory().setValue(1);
            fmVBox.getChanceSpinner().getValueFactory().setValue(0);
            fmVBox.getChanceTotalSpinner().getValueFactory().setValue(1);
        } else {
            potionVBox.getChanceSpinner().getValueFactory().setValue(miscDropChance.get().getPotionDropChance());
            potionVBox.getChanceTotalSpinner().getValueFactory().setValue(miscDropChance.get().getPotionDropChanceTotal());
            boostVBox.getChanceSpinner().getValueFactory().setValue(miscDropChance.get().getBoostDropChance());
            boostVBox.getChanceTotalSpinner().getValueFactory().setValue(miscDropChance.get().getBoostDropChanceTotal());
            taroVBox.getChanceSpinner().getValueFactory().setValue(miscDropChance.get().getTaroDropChance());
            taroVBox.getChanceTotalSpinner().getValueFactory().setValue(miscDropChance.get().getTaroDropChanceTotal());
            fmVBox.getChanceSpinner().getValueFactory().setValue(miscDropChance.get().getFMDropChance());
            fmVBox.getChanceTotalSpinner().getValueFactory().setValue(miscDropChance.get().getFMDropChanceTotal());

            bindVariables();
        }

        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
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

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class ChanceVBox extends VBox {
        private final Spinner<Integer> chanceSpinner;
        private final Spinner<Integer> chanceTotalSpinner;
        private final Separator chanceSeparator;
        private final VBox chanceFractionVBox;
        private final Slider chanceSlider;
        private final Label chancePercentageLabel;

        public ChanceVBox(double width) {
            this(0, 1, width);
        }

        public ChanceVBox(int chanceValue, int chanceTotalValue, double width) {
            var spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0, chanceTotalValue, chanceValue);
            chanceSpinner = new Spinner<>(spinnerValueFactory);
            chanceSpinner.setEditable(true);
            chanceSpinner.getEditor().setOnAction(event -> {
                try {
                    Integer.parseInt(chanceSpinner.getEditor().getText());
                    chanceSpinner.commitValue();
                } catch (NumberFormatException e) {
                    chanceSpinner.cancelEdit();
                }
            });
            chanceTotalSpinner = new Spinner<>(1, Integer.MAX_VALUE, chanceTotalValue);
            chanceTotalSpinner.setEditable(true);
            chanceTotalSpinner.getEditor().setOnAction(event -> {
                try {
                    Integer.parseInt(chanceTotalSpinner.getEditor().getText());
                    chanceTotalSpinner.commitValue();
                } catch (NumberFormatException e) {
                    chanceTotalSpinner.cancelEdit();
                }
            });

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

            chanceSlider.valueProperty().addListener((o, oldVal, newVal) ->
                    chanceSpinner.getValueFactory().setValue(newVal.intValue()));
            chanceSpinner.valueProperty().addListener((o, oldVal, newVal) ->
                    chanceSlider.setValue(newVal));

            spinnerValueFactory.maxProperty().bind(chanceTotalSpinner.valueProperty());
            chanceSlider.maxProperty().bind(chanceTotalSpinner.valueProperty());

            chancePercentageLabel.textProperty().bind(DoubleProperty.doubleExpression(chanceSpinner.valueProperty())
                    .multiply(100.0)
                    .divide(DoubleProperty.doubleExpression(chanceTotalSpinner.valueProperty()))
                    .asString(Locale.US, "%.5f%%"));
        }

        public Spinner<Integer> getChanceSpinner() {
            return chanceSpinner;
        }

        public Spinner<Integer> getChanceTotalSpinner() {
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
