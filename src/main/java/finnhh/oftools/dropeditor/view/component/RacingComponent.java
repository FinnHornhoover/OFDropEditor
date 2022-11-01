package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Racing;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.*;
import java.util.stream.IntStream;

public class RacingComponent extends HBox implements RootDataComponent {
    private final ObjectProperty<Racing> racing;

    private final MainController controller;
    private final double boxWidth;

    private final RacingInfoComponent racingInfoComponent;

    private final Label timeLimitLabel;
    private final StandardSpinner timeLimitSpinner;
    private final HBox timeLimitHBox;

    private final HBox scoreHBox;
    private final HBox rewardHBox;

    private final VBox racingDataVBox;
    private final ScrollPane contentScrollPane;
    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;
    private final BorderPane racingBorderPane;

    private final List<ScoreVBox> scoreVBoxCache;
    private final List<CrateTypeBoxComponent> crateTypeBoxCache;

    private final ChangeListener<Integer> timeLimitListener;
    private final EventHandler<MouseEvent> racingRemoveClickHandler;
    private final List<ChangeListener<Integer>> spinnerValueListeners;
    private final List<ChangeListener<Crate>> crateValueListeners;
    private final List<EventHandler<MouseEvent>> removeClickHandlers;

    public RacingComponent(MainController controller) {
        racing = new SimpleObjectProperty<>();

        this.controller = controller;
        double boxSpacing = 60.0;
        boxWidth = 160.0;

        racingInfoComponent = new RacingInfoComponent(256.0, controller);

        timeLimitLabel = new Label("Time Limit");
        timeLimitSpinner = new StandardSpinner(0, Integer.MAX_VALUE, 0);
        timeLimitHBox = new HBox(20.0, timeLimitLabel, timeLimitSpinner);
        timeLimitHBox.setAlignment(Pos.CENTER_LEFT);
        timeLimitHBox.getStyleClass().add("bordered-pane");

        scoreHBox = new HBox(boxSpacing);
        scoreHBox.setAlignment(Pos.CENTER_LEFT);
        rewardHBox = new HBox(boxSpacing);
        rewardHBox.setAlignment(Pos.CENTER_LEFT);

        racingDataVBox = new VBox(2, timeLimitHBox, scoreHBox, rewardHBox);
        racingDataVBox.getStyleClass().add("bordered-pane");

        contentScrollPane = new ScrollPane(racingDataVBox);
        contentScrollPane.setFitToHeight(true);
        contentScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        racingBorderPane = new BorderPane();
        racingBorderPane.setTop(idHBox);
        racingBorderPane.setCenter(racingInfoComponent);

        getChildren().addAll(racingBorderPane, contentScrollPane);
        setHgrow(contentScrollPane, Priority.ALWAYS);

        contentScrollPane.prefWidthProperty().bind(this.controller.getMainListView().widthProperty()
                .subtract(racingInfoComponent.widthProperty())
                .subtract(28));
        contentScrollPane.setMaxWidth(USE_PREF_SIZE);

        scoreVBoxCache = new ArrayList<>();
        crateTypeBoxCache = new ArrayList<>();

        timeLimitListener = (o, oldVal, newVal) -> makeEdit(data -> ((Racing) data).setTimeLimit(newVal));
        racingRemoveClickHandler = event -> onRemoveClick();

        spinnerValueListeners = new ArrayList<>();
        crateValueListeners = new ArrayList<>();
        removeClickHandlers = new ArrayList<>();
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
        return List.of(racingInfoComponent, racingDataVBox);
    }

    @Override
    public Class<Racing> getObservableClass() {
        return Racing.class;
    }

    @Override
    public ReadOnlyObjectProperty<Racing> getObservable() {
        return racing;
    }

    @Override
    public void setObservable(Data data) {
        racing.set((Racing) data);
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        racingInfoComponent.setObservableAndState(null);
        timeLimitSpinner.getValueFactory().setValue(0);

        scoreHBox.getChildren().clear();
        rewardHBox.getChildren().clear();
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        racingInfoComponent.setObservableAndState(controller.getStaticDataStore().getEPInstanceMap().get(
                racing.get().getEPID()));
        timeLimitSpinner.getValueFactory().setValue(racing.get().getTimeLimit());

        var scores = racing.get().getRankScores();
        var crateIDs = racing.get().getRewards();

        if (scoreVBoxCache.size() < scores.size()) {
            IntStream.range(0, scores.size() - scoreVBoxCache.size())
                    .mapToObj(i -> new ScoreVBox(boxWidth))
                    .forEach(scoreVBoxCache::add);
        }

        if (crateTypeBoxCache.size() < crateIDs.size()) {
            IntStream.range(0, crateIDs.size() - crateTypeBoxCache.size())
                    .mapToObj(i -> new CrateTypeBoxComponent(boxWidth, controller, this))
                    .forEach(crateTypeBoxCache::add);
        }

        IntStream.range(0, scores.size())
                .mapToObj(i -> {
                    ScoreVBox svb = scoreVBoxCache.get(i);
                    svb.getSpinner().getValueFactory().setValue(scores.get(i));
                    return svb;
                })
                .forEach(scoreHBox.getChildren()::add);

        IntStream.range(0, crateIDs.size())
                .mapToObj(i -> {
                    CrateTypeBoxComponent ctb = crateTypeBoxCache.get(i);
                    ctb.setObservableAndState(controller.getDrops().getCrates().get(crateIDs.get(i)));
                    return ctb;
                })
                .forEach(rewardHBox.getChildren()::add);
    }

    @Override
    public void bindVariablesNonNull() {
        timeLimitSpinner.valueProperty().addListener(timeLimitListener);

        var scoresChildren = scoreHBox.getChildren().filtered(c -> c instanceof ScoreVBox);
        var rewardsChildren = rewardHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        IntStream.range(0, scoresChildren.size())
                .forEach(index -> {
                    ScoreVBox svb = (ScoreVBox) scoresChildren.get(index);
                    CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) rewardsChildren.get(index);

                    spinnerValueListeners.add((o, oldVal, newVal) -> makeEdit(data ->
                            ((Racing) data).getRankScores().set(index, newVal)));
                    crateValueListeners.add((o, oldVal, newVal) -> makeEdit(data ->
                            ((Racing) data).getRewards().set(index,
                                    Objects.isNull(newVal) ? Crate.INT_CRATE_PLACEHOLDER_ID : newVal.getCrateID())));
                    removeClickHandlers.add(event -> makeEdit(data ->
                            ((Racing) data).getRewards().set(index, Crate.INT_CRATE_PLACEHOLDER_ID)));

                    svb.getSpinner().valueProperty().addListener(spinnerValueListeners.get(index));
                    ctb.crateProperty().addListener(crateValueListeners.get(index));
                    ctb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
                });
    }

    @Override
    public void bindVariablesNullable() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, racingRemoveClickHandler);
    }

    @Override
    public void unbindVariables() {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, racingRemoveClickHandler);
        timeLimitSpinner.valueProperty().removeListener(timeLimitListener);

        var scoresChildren = scoreHBox.getChildren().filtered(c -> c instanceof ScoreVBox);
        var rewardsChildren = rewardHBox.getChildren().filtered(c -> c instanceof CrateTypeBoxComponent);

        IntStream.range(0, scoresChildren.size())
                .forEach(index -> {
                    ScoreVBox svb = (ScoreVBox) scoresChildren.get(index);
                    CrateTypeBoxComponent ctb = (CrateTypeBoxComponent) rewardsChildren.get(index);

                    svb.getSpinner().valueProperty().removeListener(spinnerValueListeners.get(index));
                    ctb.crateProperty().removeListener(crateValueListeners.get(index));
                    ctb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandlers.get(index));
                });

        spinnerValueListeners.clear();
        crateValueListeners.clear();
        removeClickHandlers.clear();
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var epInstanceMap = controller.getStaticDataStore().getEPInstanceMap();
        var crateMap = controller.getDrops().getCrates();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        allValues.removeIf(fc -> !fc.valueName().equals("EPID"));

        allValues.addAll(getNestedSearchableValues(
                racingInfoComponent.getSearchableValues(),
                op -> op.map(o -> (Racing) o)
                        .map(r -> epInstanceMap.get(r.getEPID()))
                        .stream().toList()
        ));

        CrateTypeBoxComponent prototype = crateTypeBoxCache.isEmpty() ?
                new CrateTypeBoxComponent(boxWidth, controller, this) :
                crateTypeBoxCache.get(0);
        allValues.addAll(getNestedSearchableValues(
                prototype.getSearchableValues(),
                op -> op.map(o -> (Racing) o)
                        .map(Racing::getRewards)
                        .orElse(FXCollections.emptyObservableList())
                        .stream()
                        .filter(crateMap::containsKey)
                        .map(crateMap::get)
                        .toList()
        ));

        return allValues;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    }

    public Racing getRacing() {
        return racing.get();
    }

    public ReadOnlyObjectProperty<Racing> racingProperty() {
        return racing;
    }

    public RacingInfoComponent getRacingInfoComponent() {
        return racingInfoComponent;
    }

    public Label getTimeLimitLabel() {
        return timeLimitLabel;
    }

    public StandardSpinner getTimeLimitSpinner() {
        return timeLimitSpinner;
    }

    public HBox getTimeLimitHBox() {
        return timeLimitHBox;
    }

    public HBox getScoreHBox() {
        return scoreHBox;
    }

    public HBox getRewardHBox() {
        return rewardHBox;
    }

    public VBox getRacingDataVBox() {
        return racingDataVBox;
    }

    public ScrollPane getContentScrollPane() {
        return contentScrollPane;
    }

    public HBox getIdHBox() {
        return idHBox;
    }

    public BorderPane getRacingBorderPane() {
        return racingBorderPane;
    }

    public static class ScoreVBox extends VBox {
        private final Label scoreLabel;
        private final StandardSpinner spinner;

        public ScoreVBox(double width) {
            scoreLabel = new Label("Score");
            spinner = new StandardSpinner(0, Integer.MAX_VALUE, 0);

            setSpacing(2);
            getChildren().addAll(scoreLabel, spinner);
            setAlignment(Pos.CENTER);
            setMinWidth(width);
            setMaxWidth(width);
        }

        public Label getScoreLabel() {
            return scoreLabel;
        }

        public StandardSpinner getSpinner() {
            return spinner;
        }
    }
}
