package finnhh.oftools.dropeditor.model;

import java.util.Locale;

public enum EventType {
    NO_EVENT(0, "No Event"),
    KNISHMAS(1, "Knishmas"),
    HALLOWEEN(2, "Halloween"),
    EASTER(3, "Easter"),
    BIRTHDAY_BASH(4, "Birthday Bash"),
    VALENTINES_DAY(5, "Valentine's Day"),
    ST_PATRICKS_DAY(6, "St. Patrick's Day"),
    APRIL_FOOLS(7, "April Fools"),
    MOTHERS_DAY(8, "Mother's Day"),
    INDEPENDENCE_DAY(9, "Independence Day"),
    THANKSGIVING(10, "Thanksgiving"),
    CUSTOM_EVENT(-1, "Custom Event");  // leave this as the last event type

    private final int type;
    private final String name;

    EventType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String iconName() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    public static EventType forType(int type) {
        EventType[] gameEvents = values();
        return (type > -1 && type < gameEvents.length) ?
                gameEvents[type] :
                (type < 0) ? NO_EVENT : CUSTOM_EVENT;
    }

    public static int nextId() {
        return values()[CUSTOM_EVENT.ordinal() - 1].getType() + 1;
    }
}
