package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@IdMeaningful
public class Racing extends Data {
    @Expose
    @SerializedName("EPID")
    private final IntegerProperty EPID;
    @Expose
    private final ListProperty<Integer> rankScores;
    @Expose
    private final ListProperty<Integer> rewards;
    @Expose
    private final IntegerProperty timeLimit;
    @Expose
    private final IntegerProperty scoreCap;
    @Expose
    private final IntegerProperty totalPods;
    @Expose
    private final DoubleProperty scaleFactor;
    @Expose
    private final DoubleProperty podFactor;
    @Expose
    private final DoubleProperty timeFactor;
    @Expose
    @SerializedName("EPName")
    private final StringProperty EPName;

    public Racing() {
        EPID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        rankScores = new SimpleListProperty<>(FXCollections.observableArrayList(0, 0, 0, 0, 0));
        rewards = new SimpleListProperty<>(FXCollections.observableArrayList(
                Crate.INT_CRATE_PLACEHOLDER_ID,
                Crate.INT_CRATE_PLACEHOLDER_ID,
                Crate.INT_CRATE_PLACEHOLDER_ID,
                Crate.INT_CRATE_PLACEHOLDER_ID,
                Crate.INT_CRATE_PLACEHOLDER_ID));
        timeLimit = new SimpleIntegerProperty(0);
        scoreCap = new SimpleIntegerProperty(0);
        totalPods = new SimpleIntegerProperty(0);
        scaleFactor = new SimpleDoubleProperty(0.0);
        podFactor = new SimpleDoubleProperty(0.0);
        timeFactor = new SimpleDoubleProperty(0.0);
        EPName = new SimpleStringProperty(null);
    }

    public Racing(Racing other) {
        this.EPID = new SimpleIntegerProperty(other.EPID.get());
        this.rankScores = new SimpleListProperty<>(FXCollections.observableArrayList(other.rankScores.get()));
        this.rewards = new SimpleListProperty<>(FXCollections.observableArrayList(other.rewards.get()));
        this.timeLimit = new SimpleIntegerProperty(other.timeLimit.get());
        this.scoreCap = new SimpleIntegerProperty(other.scoreCap.get());
        this.totalPods = new SimpleIntegerProperty(other.totalPods.get());
        this.scaleFactor = new SimpleDoubleProperty(other.scaleFactor.get());
        this.podFactor = new SimpleDoubleProperty(other.podFactor.get());
        this.timeFactor = new SimpleDoubleProperty(other.timeFactor.get());
        this.EPName = new SimpleStringProperty(other.EPName.get());
    }

    @Override
    public Racing getEditableClone() {
        return new Racing(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        Racing other = (Racing) data;
        this.EPID.set(other.EPID.get());
        this.rankScores.set(FXCollections.observableArrayList(other.rankScores.get()));
        this.rewards.set(FXCollections.observableArrayList(other.rewards.get()));
        this.timeLimit.set(other.timeLimit.get());
        this.scoreCap.set(other.scoreCap.get());
        this.totalPods.set(other.totalPods.get());
        this.scaleFactor.set(other.scaleFactor.get());
        this.podFactor.set(other.podFactor.get());
        this.timeFactor.set(other.timeFactor.get());
        this.EPName.set(other.EPName.get());
    }

    @Override
    public void constructBindings() {
        malformed.bind(EPID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(rankScores.isNull())
                .or(rankScores.sizeProperty().lessThan(5))
                .or(rankScores.sizeProperty().greaterThan(5))
                .or(rewards.isNull())
                .or(rewards.sizeProperty().lessThan(5))
                .or(rewards.sizeProperty().greaterThan(5))
                .or(timeLimit.lessThan(0))
                .or(scoreCap.lessThan(0))
                .or(totalPods.lessThan(0))
                .or(scaleFactor.lessThan(0.0))
                .or(podFactor.lessThan(0.0))
                .or(timeFactor.lessThan(0.0))
                .or(EPName.isNull()));

        id.set(String.valueOf(EPID.get()));
        EPID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> EPID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerListReferences(rewards, drops.getCrates(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerListReferences(rewards, drops.getCrates(), drops.getReferenceMap());
    }

    public int getEPID() {
        return EPID.get();
    }

    public IntegerProperty EPIDProperty() {
        return EPID;
    }

    public void setEPID(int EPID) {
        this.EPID.set(EPID);
    }

    public ObservableList<Integer> getRankScores() {
        return rankScores.get();
    }

    public ListProperty<Integer> rankScoresProperty() {
        return rankScores;
    }

    public void setRankScores(ObservableList<Integer> rankScores) {
        this.rankScores.set(rankScores);
    }

    public ObservableList<Integer> getRewards() {
        return rewards.get();
    }

    public ListProperty<Integer> rewardsProperty() {
        return rewards;
    }

    public void setRewards(ObservableList<Integer> rewards) {
        this.rewards.set(rewards);
    }

    public int getTimeLimit() {
        return timeLimit.get();
    }

    public IntegerProperty timeLimitProperty() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit.set(timeLimit);
    }

    public int getScoreCap() {
        return scoreCap.get();
    }

    public IntegerProperty scoreCapProperty() {
        return scoreCap;
    }

    public void setScoreCap(int scoreCap) {
        this.scoreCap.set(scoreCap);
    }

    public int getTotalPods() {
        return totalPods.get();
    }

    public IntegerProperty totalPodsProperty() {
        return totalPods;
    }

    public void setTotalPods(int totalPods) {
        this.totalPods.set(totalPods);
    }

    public double getScaleFactor() {
        return scaleFactor.get();
    }

    public DoubleProperty scaleFactorProperty() {
        return scaleFactor;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor.set(scaleFactor);
    }

    public double getPodFactor() {
        return podFactor.get();
    }

    public DoubleProperty podFactorProperty() {
        return podFactor;
    }

    public void setPodFactor(double podFactor) {
        this.podFactor.set(podFactor);
    }

    public double getTimeFactor() {
        return timeFactor.get();
    }

    public DoubleProperty timeFactorProperty() {
        return timeFactor;
    }

    public void setTimeFactor(double timeFactor) {
        this.timeFactor.set(timeFactor);
    }

    public String getEPName() {
        return EPName.get();
    }

    public StringProperty EPNameProperty() {
        return EPName;
    }

    public void setEPName(String EPName) {
        this.EPName.set(EPName);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Racing
                && this.EPID.get() == ((Racing) obj).EPID.get()
                && this.rankScores.equals(((Racing) obj).rankScores)
                && this.rewards.equals(((Racing) obj).rewards)
                && this.timeLimit.get() == ((Racing) obj).timeLimit.get()
                && this.scoreCap.get() == ((Racing) obj).scoreCap.get()
                && this.totalPods.get() == ((Racing) obj).totalPods.get()
                && this.scaleFactor.get() == ((Racing) obj).scaleFactor.get()
                && this.podFactor.get() == ((Racing) obj).podFactor.get()
                && this.timeFactor.get() == ((Racing) obj).timeFactor.get()
                && this.EPName.get().equals(((Racing) obj).EPName.get());
    }
}
