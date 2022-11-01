package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

@IdMeaningful
public class Event extends Data {
    @Expose
    private final IntegerProperty eventID;
    @Expose
    private final IntegerProperty mobDropID;

    public Event() {
        eventID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
        mobDropID = new SimpleIntegerProperty(INT_PLACEHOLDER_ID);
    }

    public Event(Event other) {
        this.eventID = new SimpleIntegerProperty(other.eventID.get());
        this.mobDropID = new SimpleIntegerProperty(other.mobDropID.get());
    }

    @Override
    public Event getEditableClone() {
        return new Event(this);
    }

    @Override
    public void setFieldsFromData(Data data) {
        Event other = (Event) data;
        this.eventID.set(other.eventID.get());
        this.mobDropID.set(other.mobDropID.get());
    }

    @Override
    public void setChildData(Data data) {
        if (data instanceof MobDrop mobDrop)
            mobDropID.set(mobDrop.getMobDropID());
    }

    @Override
    public void constructBindings() {
        malformed.bind(eventID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)
                .or(mobDropID.lessThanOrEqualTo(INT_PLACEHOLDER_ID)));

        id.set(String.valueOf(eventID.get()));
        eventID.addListener((o, oldVal, newVal) -> id.set(String.valueOf(newVal.intValue())));
        // this shouldn't fail
        id.addListener((o, oldVal, newVal) -> eventID.set(Integer.parseInt(newVal)));
    }

    @Override
    public void registerReferences(Drops drops) {
        registerIntegerReferences(mobDropID, drops.getMobDrops(), drops.getReferenceMap());
    }

    @Override
    public void unregisterReferences(Drops drops) {
        unregisterIntegerReferences(mobDropID, drops.getMobDrops(), drops.getReferenceMap());
    }

    public int getEventID() {
        return eventID.get();
    }

    public IntegerProperty eventIDProperty() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID.set(eventID);
    }

    public int getMobDropID() {
        return mobDropID.get();
    }

    public IntegerProperty mobDropIDProperty() {
        return mobDropID;
    }

    public void setMobDropID(int mobDropID) {
        this.mobDropID.set(mobDropID);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Event
                && this.eventID.equals(((Event) obj).eventID)
                && this.mobDropID.equals(((Event) obj).mobDropID);
    }
}
