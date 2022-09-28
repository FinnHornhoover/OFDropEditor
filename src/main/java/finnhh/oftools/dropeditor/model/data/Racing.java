package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    }

    public Racing(Racing other) {
        this.EPID = new SimpleIntegerProperty(other.EPID.get());
        this.rankScores = new SimpleListProperty<>(FXCollections.observableArrayList(other.rankScores.get()));
        this.rewards = new SimpleListProperty<>(FXCollections.observableArrayList(other.rewards.get()));
        this.timeLimit = new SimpleIntegerProperty(other.timeLimit.get());
    }

    @Override
    public Racing getEditableClone() {
        return new Racing(this);
    }

    @Override
    public void constructBindings() {
        malformed.bind(EPID.lessThan(0)
                .or(rankScores.isNull())
                .or(rankScores.sizeProperty().lessThan(5))
                .or(rankScores.sizeProperty().greaterThan(5))
                .or(rewards.isNull())
                .or(rewards.sizeProperty().lessThan(5))
                .or(rewards.sizeProperty().greaterThan(5))
                .or(timeLimit.lessThan(0)));

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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Racing
                && this.EPID.equals(((Racing) obj).EPID)
                && this.rankScores.equals(((Racing) obj).rankScores)
                && this.rewards.equals(((Racing) obj).rewards)
                && this.timeLimit.equals(((Racing) obj).timeLimit);
    }
}
