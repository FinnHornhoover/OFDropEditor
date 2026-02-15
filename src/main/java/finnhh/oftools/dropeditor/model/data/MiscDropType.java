package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MiscDropType extends Data {
    public final static String[] MOB_TYPE_SUGGESTIONS = new String[] { "Single", "Group", "Boss", "Fusion" };

    @Expose
    private final IntegerProperty miscDropTypeID;
    @Expose
    private final IntegerProperty potionAmount;
    @Expose
    private final IntegerProperty boostAmount;
    @Expose
    private final IntegerProperty taroAmount;
    @Expose
    @SerializedName("FMAmount")
    private final IntegerProperty fmAmount;

    public MiscDropType() {
        miscDropTypeID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        potionAmount = new SimpleIntegerProperty(0);
        boostAmount = new SimpleIntegerProperty(0);
        taroAmount = new SimpleIntegerProperty(0);
        fmAmount = new SimpleIntegerProperty(0);
    }

    public MiscDropType(MiscDropType other) {
        this.miscDropTypeID = new SimpleIntegerProperty(other.miscDropTypeID.get());
        this.potionAmount = new SimpleIntegerProperty(other.potionAmount.get());
        this.boostAmount = new SimpleIntegerProperty(other.boostAmount.get());
        this.taroAmount = new SimpleIntegerProperty(other.taroAmount.get());
        this.fmAmount = new SimpleIntegerProperty(other.fmAmount.get());
    }

    @Override
    public MiscDropType getEditableClone() {
        return new MiscDropType(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        MiscDropType other = (MiscDropType) data;
        this.miscDropTypeID.set(other.miscDropTypeID.get());
        this.potionAmount.set(other.potionAmount.get());
        this.boostAmount.set(other.boostAmount.get());
        this.taroAmount.set(other.taroAmount.get());
        this.fmAmount.set(other.fmAmount.get());
    }

    @Override
    public void constructBindings() {
        potionAmount.addListener((o, oldVal, newVal) -> potionAmount.set(Math.max(0, newVal.intValue())));
        boostAmount.addListener((o, oldVal, newVal) -> boostAmount.set(Math.max(0, newVal.intValue())));
        taroAmount.addListener((o, oldVal, newVal) -> taroAmount.set(Math.max(0, newVal.intValue())));
        fmAmount.addListener((o, oldVal, newVal) -> fmAmount.set(Math.max(0, newVal.intValue())));

        malformed.bind(miscDropTypeID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(potionAmount.lessThan(0))
                .or(boostAmount.lessThan(0))
                .or(taroAmount.lessThan(0))
                .or(fmAmount.lessThan(0)));

        id.set(String.valueOf(miscDropTypeID.get()));
        miscDropTypeID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> miscDropTypeID.set(Integer.parseInt(newVal)));
    }

    public int getMiscDropTypeID() {
        return miscDropTypeID.get();
    }

    public IntegerProperty miscDropTypeIDProperty() {
        return miscDropTypeID;
    }

    public void setMiscDropTypeID(int miscDropTypeID) {
        this.miscDropTypeID.set(miscDropTypeID);
    }

    public int getPotionAmount() {
        return potionAmount.get();
    }

    public IntegerProperty potionAmountProperty() {
        return potionAmount;
    }

    public void setPotionAmount(int potionAmount) {
        this.potionAmount.set(potionAmount);
    }

    public int getBoostAmount() {
        return boostAmount.get();
    }

    public IntegerProperty boostAmountProperty() {
        return boostAmount;
    }

    public void setBoostAmount(int boostAmount) {
        this.boostAmount.set(boostAmount);
    }

    public int getTaroAmount() {
        return taroAmount.get();
    }

    public IntegerProperty taroAmountProperty() {
        return taroAmount;
    }

    public void setTaroAmount(int taroAmount) {
        this.taroAmount.set(taroAmount);
    }

    public int getFMAmount() {
        return fmAmount.get();
    }

    public IntegerProperty fmAmountProperty() {
        return fmAmount;
    }

    public void setFMAmount(int fmAmount) {
        this.fmAmount.set(fmAmount);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MiscDropType
                && this.miscDropTypeID.get() == ((MiscDropType) obj).miscDropTypeID.get()
                && this.potionAmount.get() == ((MiscDropType) obj).potionAmount.get()
                && this.boostAmount.get() == ((MiscDropType) obj).boostAmount.get()
                && this.taroAmount.get() == ((MiscDropType) obj).taroAmount.get()
                && this.fmAmount.get() == ((MiscDropType) obj).fmAmount.get();
    }

    public static Map<Integer, String> getSuggestedIDSpaceMapping() {
        return IntStream.range(1, 36 * MOB_TYPE_SUGGESTIONS.length + 1)
                .boxed()
                .collect(Collectors.toUnmodifiableMap(
                        Function.identity(),
                        i -> {
                            int levelSuggestion = 1 + (i - 1) / MOB_TYPE_SUGGESTIONS.length;
                            int mobTypeSuggestion = (i - 1) % MOB_TYPE_SUGGESTIONS.length;
                            return String.format("%dLv %s", levelSuggestion, MOB_TYPE_SUGGESTIONS[mobTypeSuggestion]);
                        }
                ));
    }
}
