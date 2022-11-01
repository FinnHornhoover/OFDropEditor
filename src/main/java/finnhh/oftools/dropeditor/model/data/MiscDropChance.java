package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class MiscDropChance extends Data {
    @Expose
    private final IntegerProperty miscDropChanceID;
    @Expose
    private final IntegerProperty potionDropChance;
    @Expose
    private final IntegerProperty potionDropChanceTotal;
    @Expose
    private final IntegerProperty boostDropChance;
    @Expose
    private final IntegerProperty boostDropChanceTotal;
    @Expose
    private final IntegerProperty taroDropChance;
    @Expose
    private final IntegerProperty taroDropChanceTotal;
    @Expose
    @SerializedName("FMDropChance")
    private final IntegerProperty fmDropChance;
    @Expose
    @SerializedName("FMDropChanceTotal")
    private final IntegerProperty fmDropChanceTotal;

    public MiscDropChance() {
        miscDropChanceID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        potionDropChance = new SimpleIntegerProperty(0);
        potionDropChanceTotal = new SimpleIntegerProperty(1);
        boostDropChance = new SimpleIntegerProperty(0);
        boostDropChanceTotal = new SimpleIntegerProperty(1);
        taroDropChance = new SimpleIntegerProperty(0);
        taroDropChanceTotal = new SimpleIntegerProperty(1);
        fmDropChance = new SimpleIntegerProperty(0);
        fmDropChanceTotal = new SimpleIntegerProperty(1);
    }

    public MiscDropChance(MiscDropChance other) {
        this.miscDropChanceID = new SimpleIntegerProperty(other.miscDropChanceID.get());
        this.potionDropChance = new SimpleIntegerProperty(other.potionDropChance.get());
        this.potionDropChanceTotal = new SimpleIntegerProperty(other.potionDropChanceTotal.get());
        this.boostDropChance = new SimpleIntegerProperty(other.boostDropChance.get());
        this.boostDropChanceTotal = new SimpleIntegerProperty(other.boostDropChanceTotal.get());
        this.taroDropChance = new SimpleIntegerProperty(other.taroDropChance.get());
        this.taroDropChanceTotal = new SimpleIntegerProperty(other.taroDropChanceTotal.get());
        this.fmDropChance = new SimpleIntegerProperty(other.fmDropChance.get());
        this.fmDropChanceTotal = new SimpleIntegerProperty(other.fmDropChanceTotal.get());
    }

    @Override
    public MiscDropChance getEditableClone() {
        return new MiscDropChance(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        MiscDropChance other = (MiscDropChance) data;
        this.miscDropChanceID.set(other.miscDropChanceID.get());
        this.potionDropChance.set(other.potionDropChance.get());
        this.potionDropChanceTotal.set(other.potionDropChanceTotal.get());
        this.boostDropChance.set(other.boostDropChance.get());
        this.boostDropChanceTotal.set(other.boostDropChanceTotal.get());
        this.taroDropChance.set(other.taroDropChance.get());
        this.taroDropChanceTotal.set(other.taroDropChanceTotal.get());
        this.fmDropChance.set(other.fmDropChance.get());
        this.fmDropChanceTotal.set(other.fmDropChanceTotal.get());
    }

    @Override
    public void constructBindings() {
        potionDropChanceTotal.addListener((o, oldVal, newVal) ->
                potionDropChanceTotal.set(Math.max(1, newVal.intValue())));
        boostDropChanceTotal.addListener((o, oldVal, newVal) ->
                boostDropChanceTotal.set(Math.max(1, newVal.intValue())));
        taroDropChanceTotal.addListener((o, oldVal, newVal) ->
                taroDropChanceTotal.set(Math.max(1, newVal.intValue())));
        fmDropChanceTotal.addListener((o, oldVal, newVal) ->
                fmDropChanceTotal.set(Math.max(1, newVal.intValue())));
        potionDropChance.addListener((o, oldVal, newVal) ->
                potionDropChance.set(Math.min(Math.max(0, newVal.intValue()), potionDropChanceTotal.get())));
        boostDropChance.addListener((o, oldVal, newVal) ->
                boostDropChance.set(Math.min(Math.max(0, newVal.intValue()), boostDropChanceTotal.get())));
        taroDropChance.addListener((o, oldVal, newVal) ->
                taroDropChance.set(Math.min(Math.max(0, newVal.intValue()), taroDropChanceTotal.get())));
        fmDropChance.addListener((o, oldVal, newVal) ->
                fmDropChance.set(Math.min(Math.max(0, newVal.intValue()), fmDropChanceTotal.get())));

        malformed.bind(miscDropChanceID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(potionDropChanceTotal.lessThan(1))
                .or(potionDropChance.lessThan(0))
                .or(potionDropChanceTotal.lessThan(potionDropChance))
                .or(boostDropChanceTotal.lessThan(1))
                .or(boostDropChance.lessThan(0))
                .or(boostDropChanceTotal.lessThan(boostDropChance))
                .or(taroDropChanceTotal.lessThan(1))
                .or(taroDropChance.lessThan(0))
                .or(taroDropChanceTotal.lessThan(taroDropChance))
                .or(fmDropChanceTotal.lessThan(1))
                .or(fmDropChance.lessThan(0))
                .or(fmDropChanceTotal.lessThan(fmDropChance)));

        id.set(String.valueOf(miscDropChanceID.get()));
        miscDropChanceID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> miscDropChanceID.set(Integer.parseInt(newVal)));
    }

    public int getMiscDropChanceID() {
        return miscDropChanceID.get();
    }

    public IntegerProperty miscDropChanceIDProperty() {
        return miscDropChanceID;
    }

    public void setMiscDropChanceID(int miscDropChanceID) {
        this.miscDropChanceID.set(miscDropChanceID);
    }

    public int getPotionDropChance() {
        return potionDropChance.get();
    }

    public IntegerProperty potionDropChanceProperty() {
        return potionDropChance;
    }

    public void setPotionDropChance(int potionDropChance) {
        this.potionDropChance.set(potionDropChance);
    }

    public int getPotionDropChanceTotal() {
        return potionDropChanceTotal.get();
    }

    public IntegerProperty potionDropChanceTotalProperty() {
        return potionDropChanceTotal;
    }

    public void setPotionDropChanceTotal(int potionDropChanceTotal) {
        this.potionDropChanceTotal.set(potionDropChanceTotal);
    }

    public int getBoostDropChance() {
        return boostDropChance.get();
    }

    public IntegerProperty boostDropChanceProperty() {
        return boostDropChance;
    }

    public void setBoostDropChance(int boostDropChance) {
        this.boostDropChance.set(boostDropChance);
    }

    public int getBoostDropChanceTotal() {
        return boostDropChanceTotal.get();
    }

    public IntegerProperty boostDropChanceTotalProperty() {
        return boostDropChanceTotal;
    }

    public void setBoostDropChanceTotal(int boostDropChanceTotal) {
        this.boostDropChanceTotal.set(boostDropChanceTotal);
    }

    public int getTaroDropChance() {
        return taroDropChance.get();
    }

    public IntegerProperty taroDropChanceProperty() {
        return taroDropChance;
    }

    public void setTaroDropChance(int taroDropChance) {
        this.taroDropChance.set(taroDropChance);
    }

    public int getTaroDropChanceTotal() {
        return taroDropChanceTotal.get();
    }

    public IntegerProperty taroDropChanceTotalProperty() {
        return taroDropChanceTotal;
    }

    public void setTaroDropChanceTotal(int taroDropChanceTotal) {
        this.taroDropChanceTotal.set(taroDropChanceTotal);
    }

    public int getFMDropChance() {
        return fmDropChance.get();
    }

    public IntegerProperty fmDropChanceProperty() {
        return fmDropChance;
    }

    public void setFMDropChance(int fmDropChance) {
        this.fmDropChance.set(fmDropChance);
    }

    public int getFMDropChanceTotal() {
        return fmDropChanceTotal.get();
    }

    public IntegerProperty fmDropChanceTotalProperty() {
        return fmDropChanceTotal;
    }

    public void setFMDropChanceTotal(int fmDropChanceTotal) {
        this.fmDropChanceTotal.set(fmDropChanceTotal);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MiscDropChance
                && this.miscDropChanceID.equals(((MiscDropChance) obj).miscDropChanceID)
                && this.potionDropChance.equals(((MiscDropChance) obj).potionDropChance)
                && this.potionDropChanceTotal.equals(((MiscDropChance) obj).potionDropChanceTotal)
                && this.boostDropChance.equals(((MiscDropChance) obj).boostDropChance)
                && this.boostDropChanceTotal.equals(((MiscDropChance) obj).boostDropChanceTotal)
                && this.taroDropChance.equals(((MiscDropChance) obj).taroDropChance)
                && this.taroDropChanceTotal.equals(((MiscDropChance) obj).taroDropChanceTotal)
                && this.fmDropChance.equals(((MiscDropChance) obj).fmDropChance)
                && this.fmDropChanceTotal.equals(((MiscDropChance) obj).fmDropChanceTotal);
    }
}
