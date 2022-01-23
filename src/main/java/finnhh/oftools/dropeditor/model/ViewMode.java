package finnhh.oftools.dropeditor.model;

import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import finnhh.oftools.dropeditor.model.data.Mob;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

public enum ViewMode {
    MONSTER("Monsters",
            drops -> drops.getMobs()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Mob::getMobID))
                    .toList());

    private final String modeString;
    private final Function<Drops, Collection<? extends Data>> dataGetter;

    ViewMode(String modeString,
             Function<Drops, Collection<? extends Data>> dataGetter) {
        this.modeString = modeString;
        this.dataGetter = dataGetter;
    }

    public String getModeString() {
        return modeString;
    }

    public Function<Drops, Collection<? extends Data>> getDataGetter() {
        return dataGetter;
    }

    @Override
    public String toString() {
        return "View Mode: " + modeString;
    }
}
