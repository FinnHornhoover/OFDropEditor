package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.MiscDropType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;
import java.util.Objects;

public class MiscDropTypeComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MiscDropType> miscDropType;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final TypeVBox potionVBox;
    private final TypeVBox boostVBox;
    private final TypeVBox taroVBox;
    private final TypeVBox fmVBox;
    private final HBox contentHBox;
    private final ScrollPane contentScrollPane;
    private final Label idLabel;

    private final ChangeListener<Number> potionListener;
    private final ChangeListener<Number> boostListener;
    private final ChangeListener<Number> taroListener;
    private final ChangeListener<Number> fmListener;
    private final EventHandler<MouseEvent> idClickHandler;

    public MiscDropTypeComponent(double boxSpacing,
                                 double boxWidth,
                                 MainController controller,
                                 DataComponent parent) {

        miscDropType = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;

        var iconMap = this.controller.getIconManager().getIconMap();
        potionVBox = new TypeVBox(iconMap.get("potions"), boxWidth);
        boostVBox = new TypeVBox(iconMap.get("boosts"), boxWidth);
        taroVBox = new TypeVBox(iconMap.get("taro"), boxWidth);
        fmVBox = new TypeVBox(iconMap.get("fm"), boxWidth);

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

        potionListener = (o, oldVal, newVal) -> {
            if (miscDropType.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropType.get().setPotionAmount(newVal.intValue());
                potionVBox.getAmountSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        boostListener = (o, oldVal, newVal) -> {
            if (miscDropType.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropType.get().setBoostAmount(newVal.intValue());
                boostVBox.getAmountSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        taroListener = (o, oldVal, newVal) -> {
            if (miscDropType.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropType.get().setTaroAmount(newVal.intValue());
                taroVBox.getAmountSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };
        fmListener = (o, oldVal, newVal) -> {
            if (miscDropType.isNotNull().get()) {
                makeEditable(this.controller.getDrops());
                miscDropType.get().setFMAmount(newVal.intValue());
                fmVBox.getAmountSpinner().getValueFactory().setValue(newVal.intValue());
            }
        };

        idLabel.setText(MiscDropType.class.getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        idClickHandler = event -> this.controller.showSelectionMenuForResult(MiscDropType.class)
                .ifPresent(d -> makeEdit(this.controller.getDrops(), d));

        miscDropType.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(MiscDropType.class.getSimpleName() + ": null");
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
        potionVBox.getAmountSpinner().valueProperty().addListener(potionListener);
        boostVBox.getAmountSpinner().valueProperty().addListener(boostListener);
        taroVBox.getAmountSpinner().valueProperty().addListener(taroListener);
        fmVBox.getAmountSpinner().valueProperty().addListener(fmListener);
    }

    private void unbindVariables() {
        potionVBox.getAmountSpinner().valueProperty().removeListener(potionListener);
        boostVBox.getAmountSpinner().valueProperty().removeListener(boostListener);
        taroVBox.getAmountSpinner().valueProperty().removeListener(taroListener);
        fmVBox.getAmountSpinner().valueProperty().removeListener(fmListener);
    }

    @Override
    public void setObservable(Data data) {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);

        miscDropType.set((MiscDropType) data);

        unbindVariables();

        if (miscDropType.isNull().get()) {
            potionVBox.getAmountSpinner().getValueFactory().setValue(0);
            boostVBox.getAmountSpinner().getValueFactory().setValue(0);
            taroVBox.getAmountSpinner().getValueFactory().setValue(0);
            fmVBox.getAmountSpinner().getValueFactory().setValue(0);
        } else {
            potionVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getPotionAmount());
            boostVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getBoostAmount());
            taroVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getTaroAmount());
            fmVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getFMAmount());

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

    public MiscDropType getMiscDropType() {
        return miscDropType.get();
    }

    public ReadOnlyObjectProperty<MiscDropType> miscDropTypeProperty() {
        return miscDropType;
    }

    public TypeVBox getPotionVBox() {
        return potionVBox;
    }

    public TypeVBox getBoostVBox() {
        return boostVBox;
    }

    public TypeVBox getTaroVBox() {
        return taroVBox;
    }

    public TypeVBox getFMVBox() {
        return fmVBox;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }

    @Override
    public ReadOnlyObjectProperty<MiscDropType> getObservable() {
        return miscDropType;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class TypeVBox extends VBox {
        private final Spinner<Integer> amountSpinner;
        private final ImageView iconView;

        public TypeVBox(byte[] icon, double width) {
            this(0, icon, width);
        }

        public TypeVBox(int amountValue, byte[] icon, double width) {
            amountSpinner = new Spinner<>(0, Integer.MAX_VALUE, amountValue);
            amountSpinner.setEditable(true);
            amountSpinner.getEditor().setOnAction(event -> {
                try {
                    Integer.parseInt(amountSpinner.getEditor().getText());
                    amountSpinner.commitValue();
                } catch (NumberFormatException e) {
                    amountSpinner.cancelEdit();
                }
            });

            iconView = new ImageView(new Image(new ByteArrayInputStream(icon)));
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            setSpacing(2);
            getChildren().addAll(amountSpinner, iconView);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);
        }

        public Spinner<Integer> getAmountSpinner() {
            return amountSpinner;
        }

        public ImageView getIconView() {
            return iconView;
        }
    }
}
