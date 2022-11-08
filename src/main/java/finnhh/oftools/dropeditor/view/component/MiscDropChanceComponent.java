package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.MiscDropChance;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.List;

public class MiscDropChanceComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MiscDropChance> miscDropChance;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final MinMaxChanceBox potionVBox;
    private final MinMaxChanceBox boostVBox;
    private final MinMaxChanceBox taroVBox;
    private final MinMaxChanceBox fmVBox;
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
        potionVBox = new MinMaxChanceBox("Potions", Orientation.VERTICAL, boxWidth);
        boostVBox = new MinMaxChanceBox("Boosts", Orientation.VERTICAL, boxWidth);
        taroVBox = new MinMaxChanceBox("Taros", Orientation.VERTICAL, boxWidth);
        fmVBox = new MinMaxChanceBox("FM", Orientation.VERTICAL, boxWidth);

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

        potionChanceListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setPotionDropChance(newVal.intValue()));
        potionChanceTotalListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setPotionDropChanceTotal(newVal.intValue()));
        boostChanceListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setBoostDropChance(newVal.intValue()));
        boostChanceTotalListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setBoostDropChanceTotal(newVal.intValue()));
        taroChanceListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setTaroDropChance(newVal.intValue()));
        taroChanceTotalListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setTaroDropChanceTotal(newVal.intValue()));
        fmChanceListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setFMDropChance(newVal.intValue()));
        fmChanceTotalListener = (o, oldVal, newVal) -> makeEdit(data ->
                ((MiscDropChance) data).setFMDropChanceTotal(newVal.intValue()));
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

    public MinMaxChanceBox getPotionVBox() {
        return potionVBox;
    }

    public MinMaxChanceBox getBoostVBox() {
        return boostVBox;
    }

    public MinMaxChanceBox getTaroVBox() {
        return taroVBox;
    }

    public MinMaxChanceBox getFMVBox() {
        return fmVBox;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }
}
