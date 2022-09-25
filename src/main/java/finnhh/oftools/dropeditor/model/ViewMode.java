package finnhh.oftools.dropeditor.model;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.*;
import finnhh.oftools.dropeditor.view.component.*;
import javafx.scene.control.ListView;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum ViewMode {
    MONSTER("Monsters",
            MobComponent::new,
            drops -> drops.getMobs()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Mob::getMobID))
                    .toList()),
    CRATE("Crates",
            CrateComponent::new,
            drops -> drops.getCrates()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Crate::getCrateID))
                    .toList()),
    RACING("Racing",
            RacingComponent::new,
            drops -> drops.getRacing()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Racing::getEPID))
                    .toList()),
    CODE_ITEM("Code Items",
            CodeItemComponent::new,
            drops -> drops.getCodeItems()
                    .values().stream()
                    .sorted(Comparator.comparingInt(CodeItem::getCodeID))
                    .toList());

    private final String modeString;
    private final BiFunction<MainController, ListView<Data>, ObservableComponent<?>> componentConstructor;
    private final Function<Drops, Collection<? extends Data>> dataGetter;

    ViewMode(String modeString,
             BiFunction<MainController, ListView<Data>, ObservableComponent<?>> componentConstructor,
             Function<Drops, Collection<? extends Data>> dataGetter) {
        this.modeString = modeString;
        this.componentConstructor = componentConstructor;
        this.dataGetter = dataGetter;
    }

    public String getModeString() {
        return modeString;
    }

    public BiFunction<MainController, ListView<Data>, ObservableComponent<?>> getComponentConstructor() {
        return componentConstructor;
    }

    public Function<Drops, Collection<? extends Data>> getDataGetter() {
        return dataGetter;
    }

    @Override
    public String toString() {
        return "View Mode: " + modeString;
    }
}
