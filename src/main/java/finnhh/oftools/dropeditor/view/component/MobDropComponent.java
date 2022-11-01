package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.MobDrop;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MobDropComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<MobDrop> mobDrop;

    private final CrateDropChanceComponent crateDropChanceComponent;
    private final CrateDropTypeComponent crateDropTypeComponent;
    private final MiscDropChanceComponent miscDropChanceComponent;
    private final MiscDropTypeComponent miscDropTypeComponent;

    private final MainController controller;
    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final VBox contentVBox;
    private final Group idGroup;
    private final Label idLabel;

    private final EventHandler<MouseEvent> idClickHandler;

    public MobDropComponent(double boxSpacing,
                            double boxWidth,
                            MainController controller,
                            DataComponent parent) {

        mobDrop = new SimpleObjectProperty<>();

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;

        crateDropChanceComponent = new CrateDropChanceComponent(boxSpacing, boxWidth, controller, this);
        crateDropTypeComponent = new CrateDropTypeComponent(boxSpacing, boxWidth, controller, this);
        miscDropChanceComponent = new MiscDropChanceComponent(boxSpacing, boxWidth, controller, this);
        miscDropTypeComponent = new MiscDropTypeComponent(boxSpacing, boxWidth, controller, this);

        contentVBox = new VBox(miscDropChanceComponent,
                miscDropTypeComponent,
                crateDropChanceComponent,
                crateDropTypeComponent);
        contentVBox.setAlignment(Pos.CENTER);
        contentVBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().addAll("id-label", "vertical");

        idGroup = new Group(idLabel);

        setLeft(idGroup);
        setCenter(contentVBox);

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
    public Class<MobDrop> getObservableClass() {
        return MobDrop.class;
    }

    @Override
    public ReadOnlyObjectProperty<MobDrop> getObservable() {
        return mobDrop;
    }

    @Override
    public void setObservable(Data data) {
        mobDrop.set((MobDrop) data);
    }

    @Override
    public void cleanUIState() {
        DataComponent.super.cleanUIState();

        crateDropChanceComponent.setObservableAndState(null);
        crateDropTypeComponent.setObservableAndState(null);
        miscDropChanceComponent.setObservableAndState(null);
        miscDropTypeComponent.setObservableAndState(null);
    }

    @Override
    public void fillUIState() {
        DataComponent.super.fillUIState();

        var drops = controller.getDrops();

        crateDropChanceComponent.setObservableAndState(
                drops.getCrateDropChances().get(mobDrop.get().getCrateDropChanceID()));
        crateDropTypeComponent.setObservableAndState(
                drops.getCrateDropTypes().get(mobDrop.get().getCrateDropTypeID()));
        miscDropChanceComponent.setObservableAndState(
                drops.getMiscDropChances().get(mobDrop.get().getMiscDropChanceID()));
        miscDropTypeComponent.setObservableAndState(
                drops.getMiscDropTypes().get(mobDrop.get().getMiscDropTypeID()));
    }

    @Override
    public void bindVariablesNullable() {
        idLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public void unbindVariables() {
        idLabel.removeEventHandler(MouseEvent.MOUSE_CLICKED, idClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var crateDropChanceMap = controller.getDrops().getCrateDropChances();
        var crateDropTypeMap = controller.getDrops().getCrateDropTypes();
        var miscDropChanceMap = controller.getDrops().getMiscDropChances();
        var miscDropTypeMap = controller.getDrops().getMiscDropTypes();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.removeIf(fc -> !fc.valueName().equals("mobDropID"));

        allValues.addAll(getNestedSearchableValues(
                crateDropChanceComponent.getSearchableValues(),
                op -> op.map(o -> (MobDrop) o)
                        .map(md -> crateDropChanceMap.get(md.getCrateDropChanceID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                crateDropTypeComponent.getSearchableValues(),
                op -> op.map(o -> (MobDrop) o)
                        .map(md -> crateDropTypeMap.get(md.getCrateDropTypeID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                miscDropChanceComponent.getSearchableValues(),
                op -> op.map(o -> (MobDrop) o)
                        .map(md -> miscDropChanceMap.get(md.getMiscDropChanceID()))
                        .stream().toList()
        ));
        allValues.addAll(getNestedSearchableValues(
                miscDropTypeComponent.getSearchableValues(),
                op -> op.map(o -> (MobDrop) o)
                        .map(md -> miscDropTypeMap.get(md.getMiscDropTypeID()))
                        .stream().toList()
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

    public MobDrop getMobDrop() {
        return mobDrop.get();
    }

    public ObjectProperty<MobDrop> mobDropProperty() {
        return mobDrop;
    }

    public CrateDropChanceComponent getCrateDropChanceComponent() {
        return crateDropChanceComponent;
    }

    public CrateDropTypeComponent getCrateDropTypeComponent() {
        return crateDropTypeComponent;
    }

    public MiscDropChanceComponent getMiscDropChanceComponent() {
        return miscDropChanceComponent;
    }

    public MiscDropTypeComponent getMiscDropTypeComponent() {
        return miscDropTypeComponent;
    }

    public VBox getContentVBox() {
        return contentVBox;
    }

    public Group getIdGroup() {
        return idGroup;
    }
}
