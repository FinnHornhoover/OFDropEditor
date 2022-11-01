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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;

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
        potionVBox = new TypeVBox(iconMap, 0, "potions", boxWidth);
        boostVBox = new TypeVBox(iconMap, 0, "boosts", boxWidth);
        taroVBox = new TypeVBox(iconMap, 0, "taro", boxWidth);
        fmVBox = new TypeVBox(iconMap, 0, "fm", boxWidth);

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

        potionListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropType) data).setPotionAmount(newVal.intValue()));
        boostListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropType) data).setBoostAmount(newVal.intValue()));
        taroListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropType) data).setTaroAmount(newVal.intValue()));
        fmListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropType) data).setFMAmount(newVal.intValue()));
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
    public Class<MiscDropType> getObservableClass() {
        return MiscDropType.class;
    }

    @Override
    public ReadOnlyObjectProperty<MiscDropType> getObservable() {
        return miscDropType;
    }

    @Override
    public void setObservable(Data data) {
        miscDropType.set((MiscDropType) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        potionVBox.getAmountSpinner().getValueFactory().setValue(0);
        boostVBox.getAmountSpinner().getValueFactory().setValue(0);
        taroVBox.getAmountSpinner().getValueFactory().setValue(0);
        fmVBox.getAmountSpinner().getValueFactory().setValue(0);
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        potionVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getPotionAmount());
        boostVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getBoostAmount());
        taroVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getTaroAmount());
        fmVBox.getAmountSpinner().getValueFactory().setValue(miscDropType.get().getFMAmount());
    }

    @Override
    public void bindVariablesNonNull() {
        potionVBox.getAmountSpinner().valueProperty().addListener(potionListener);
        boostVBox.getAmountSpinner().valueProperty().addListener(boostListener);
        taroVBox.getAmountSpinner().valueProperty().addListener(taroListener);
        fmVBox.getAmountSpinner().valueProperty().addListener(fmListener);
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
        potionVBox.getAmountSpinner().valueProperty().removeListener(potionListener);
        boostVBox.getAmountSpinner().valueProperty().removeListener(boostListener);
        taroVBox.getAmountSpinner().valueProperty().removeListener(taroListener);
        fmVBox.getAmountSpinner().valueProperty().removeListener(fmListener);
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

    public static class TypeVBox extends VBox {
        private final StandardSpinner amountSpinner;
        private final StandardImageView iconView;

        public TypeVBox(Map<String, byte[]> iconMap, int amountValue, String iconName, double width) {
            amountSpinner = new StandardSpinner(0, Integer.MAX_VALUE, amountValue);

            iconView = new StandardImageView(iconMap, 64);
            iconView.setImage(iconName);

            setSpacing(2);
            getChildren().addAll(amountSpinner, iconView);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);
        }

        public StandardSpinner getAmountSpinner() {
            return amountSpinner;
        }

        public StandardImageView getIconView() {
            return iconView;
        }
    }
}
