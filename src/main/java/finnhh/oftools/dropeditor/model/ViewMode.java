package finnhh.oftools.dropeditor.model;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.*;
import finnhh.oftools.dropeditor.view.component.*;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;

public enum ViewMode {
    MONSTER("Monsters",
            Mob.class,
            MobComponent::new,
            MainController::showAddMobMenuForResult,
            drops -> drops.getMobs()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Mob::getMobID))
                    .toList()),
    CRATE("Crates",
            Crate.class,
            CrateComponent::new,
            MainController::showAddCrateMenuForResult,
            drops -> drops.getCrates()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Crate::getCrateID))
                    .toList()),
    RACING("Racing",
            Racing.class,
            RacingComponent::new,
            MainController::showAddRacingMenuForResult,
            drops -> drops.getRacing()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Racing::getEPID))
                    .toList()),
    CODE_ITEM("Code Items",
            CodeItem.class,
            CodeItemComponent::new,
            MainController::showAddCodeItemMenuForResult,
            drops -> drops.getCodeItems()
                    .values().stream()
                    .sorted(Comparator.comparing(CodeItem::getCode))
                    .toList()),
    ITEM_REFERENCE("Item References",
            ItemReference.class,
            ItemReferenceComponent::new,
            MainController::showAddItemReferenceMenuForResult,
            drops -> drops.getItemReferences()
                    .values().stream()
                    .sorted(Comparator.comparingInt(ItemReference::getItemReferenceID))
                    .toList()),
    NANO_CAPSULE("Nano Capsules",
            NanoCapsule.class,
            NanoCapsuleComponent::new,
            MainController::showAddNanoCapsuleMenuForResult,
            drops -> drops.getNanoCapsules()
                    .values().stream()
                    .sorted(Comparator.comparingInt(NanoCapsule::getNano))
                    .toList()),
    EVENT("Events",
            Event.class,
            EventComponent::new,
            MainController::showAddEventMenuForResult,
            drops -> drops.getEvents()
                    .values().stream()
                    .sorted(Comparator.comparingInt(Event::getEventID))
                    .toList());

    private final String modeString;
    private final Class<? extends Data> dataClass;
    private final Function<MainController, ObservableComponent<?>> componentConstructor;
    private final Function<MainController, Optional<Data>> newDataAdder;
    private final Function<Drops, Collection<? extends Data>> dataGetter;

    ViewMode(String modeString,
             Class<? extends Data> dataClass,
             Function<MainController, ObservableComponent<?>> componentConstructor,
             Function<MainController, Optional<Data>> newDataAdder,
             Function<Drops, Collection<? extends Data>> dataGetter) {
        this.modeString = modeString;
        this.dataClass = dataClass;
        this.componentConstructor = componentConstructor;
        this.newDataAdder = newDataAdder;
        this.dataGetter = dataGetter;
    }

    public String getModeString() {
        return modeString;
    }

    public Class<? extends Data> getDataClass() {
        return dataClass;
    }

    public Function<MainController, ObservableComponent<?>> getComponentConstructor() {
        return componentConstructor;
    }

    public Function<MainController, Optional<Data>> getNewDataAdder() {
        return newDataAdder;
    }

    public Function<Drops, Collection<? extends Data>> getDataGetter() {
        return dataGetter;
    }

    @Override
    public String toString() {
        return "View Mode: " + modeString;
    }
}
