package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import finnhh.oftools.dropeditor.model.ReferenceMode;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.*;

public class Drops extends Data {
    @Expose
    private final AlternateMapProperty<CrateDropChance> crateDropChances;
    @Expose
    private final AlternateMapProperty<CrateDropType> crateDropTypes;
    @Expose
    private final AlternateMapProperty<MiscDropChance> miscDropChances;
    @Expose
    private final AlternateMapProperty<MiscDropType> miscDropTypes;
    @Expose
    private final AlternateMapProperty<MobDrop> mobDrops;
    @Expose
    private final AlternateMapProperty<Event> events;
    @Expose
    private final AlternateMapProperty<Mob> mobs;
    @Expose
    private final AlternateMapProperty<RarityWeights> rarityWeights;
    @Expose
    private final AlternateMapProperty<ItemSet> itemSets;
    @Expose
    private final AlternateMapProperty<Crate> crates;
    @Expose
    private final AlternateMapProperty<ItemReference> itemReferences;
    @Expose
    private final AlternateMapProperty<Racing> racing;
    @Expose
    private final AlternateMapProperty<NanoCapsule> nanoCapsules;
    @Expose
    private final AlternateMapProperty<CodeItem> codeItems;

    private final MapProperty<Data, Set<Data>> referenceMap;

    public Drops() {
        crateDropChances = new AlternateMapProperty<>();
        crateDropTypes = new AlternateMapProperty<>();
        miscDropChances = new AlternateMapProperty<>();
        miscDropTypes = new AlternateMapProperty<>();
        mobDrops = new AlternateMapProperty<>();
        events = new AlternateMapProperty<>();
        mobs = new AlternateMapProperty<>();
        rarityWeights = new AlternateMapProperty<>();
        itemSets = new AlternateMapProperty<>();
        crates = new AlternateMapProperty<>();
        itemReferences = new AlternateMapProperty<>();
        racing = new AlternateMapProperty<>();
        nanoCapsules = new AlternateMapProperty<>();
        codeItems = new AlternateMapProperty<>();

        referenceMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
    }

    @Override
    public Drops getEditableClone() {
        // do not clone
        return this;
    }

    @Override
    public void constructBindings() {
        malformed.bind(crateDropChances.isNull()
                .or(crateDropTypes.isNull())
                .or(miscDropChances.isNull())
                .or(miscDropTypes.isNull())
                .or(mobDrops.isNull())
                .or(events.isNull())
                .or(mobs.isNull())
                .or(rarityWeights.isNull())
                .or(itemSets.isNull())
                .or(crates.isNull())
                .or(itemReferences.isNull())
                .or(racing.isNull())
                .or(nanoCapsules.isNull())
                .or(codeItems.isNull()));
    }

    public void manageMaps() throws NumberFormatException {
        constructBindings();

        var maps = List.of(
                crateDropChances,
                crateDropTypes,
                miscDropChances,
                miscDropTypes,
                mobDrops,
                events,
                mobs,
                rarityWeights,
                itemSets,
                crates,
                itemReferences,
                racing,
                nanoCapsules,
                codeItems
        );

        for (var map : maps) {
            if (map.isNull().get())
                map.setDefault();
            map.syncMaps();
        }

        // do after synced
        maps.forEach(map -> map.registerReferences(this));
    }

    public ReferenceMode getReferenceModeFor(Data data) {
        return Optional.ofNullable(data)
                .map(referenceMap::get)
                .map(set -> ReferenceMode.forSize(set.size()))
                .orElse(ReferenceMode.NONE);
    }

    public Optional<AlternateMapProperty<? extends Data>> getDataMap(Class<? extends Data> dataClass) {
        var classMapMap = Map.ofEntries(
                Map.entry(CrateDropChance.class, crateDropChances),
                Map.entry(CrateDropType.class, crateDropTypes),
                Map.entry(MiscDropChance.class, miscDropChances),
                Map.entry(MiscDropType.class, miscDropTypes),
                Map.entry(MobDrop.class, mobDrops),
                Map.entry(Event.class, events),
                Map.entry(Mob.class, mobs),
                Map.entry(RarityWeights.class, rarityWeights),
                Map.entry(ItemSet.class, itemSets),
                Map.entry(Crate.class, crates),
                Map.entry(ItemReference.class, itemReferences),
                Map.entry(Racing.class, racing),
                Map.entry(NanoCapsule.class, nanoCapsules),
                Map.entry(CodeItem.class, codeItems)
        );

        return classMapMap.entrySet().stream()
                .filter(e -> dataClass.isAssignableFrom(e.getKey()))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    public Data add(Data data) {
        if (data instanceof CrateDropChance)
            return crateDropChances.add((CrateDropChance) data);
        else if (data instanceof CrateDropType)
            return crateDropTypes.add((CrateDropType) data);
        else if (data instanceof MiscDropChance)
            return miscDropChances.add((MiscDropChance) data);
        else if (data instanceof MiscDropType)
            return miscDropTypes.add((MiscDropType) data);
        else if (data instanceof MobDrop)
            return mobDrops.add((MobDrop) data);
        else if (data instanceof Event)
            return events.add((Event) data);
        else if (data instanceof Mob)
            return mobs.add((Mob) data);
        else if (data instanceof RarityWeights)
            return rarityWeights.add((RarityWeights) data);
        else if (data instanceof ItemSet)
            return itemSets.add((ItemSet) data);
        else if (data instanceof Crate)
            return crates.add((Crate) data);
        else if (data instanceof ItemReference)
            return itemReferences.add((ItemReference) data);
        else if (data instanceof Racing)
            return racing.add((Racing) data);
        else if (data instanceof NanoCapsule)
            return nanoCapsules.add((NanoCapsule) data);
        else if (data instanceof CodeItem)
            return codeItems.add((CodeItem) data);
        else
            return null;
    }

    public Data remove(Data data) {
        if (data instanceof CrateDropChance)
            return crateDropChances.remove((CrateDropChance) data);
        else if (data instanceof CrateDropType)
            return crateDropTypes.remove((CrateDropType) data);
        else if (data instanceof MiscDropChance)
            return miscDropChances.remove((MiscDropChance) data);
        else if (data instanceof MiscDropType)
            return miscDropTypes.remove((MiscDropType) data);
        else if (data instanceof MobDrop)
            return mobDrops.remove((MobDrop) data);
        else if (data instanceof Event)
            return events.remove((Event) data);
        else if (data instanceof Mob)
            return mobs.remove((Mob) data);
        else if (data instanceof RarityWeights)
            return rarityWeights.remove((RarityWeights) data);
        else if (data instanceof ItemSet)
            return itemSets.remove((ItemSet) data);
        else if (data instanceof Crate)
            return crates.remove((Crate) data);
        else if (data instanceof ItemReference)
            return itemReferences.remove((ItemReference) data);
        else if (data instanceof Racing)
            return racing.remove((Racing) data);
        else if (data instanceof NanoCapsule)
            return nanoCapsules.remove((NanoCapsule) data);
        else if (data instanceof CodeItem)
            return codeItems.remove((CodeItem) data);
        else
            return null;
    }

    public ObservableMap<Integer, CrateDropChance> getCrateDropChances() {
        return crateDropChances.getTrueMap();
    }

    public AlternateMapProperty<CrateDropChance> crateDropChancesProperty() {
        return crateDropChances;
    }

    public ObservableMap<Integer, CrateDropType> getCrateDropTypes() {
        return crateDropTypes.getTrueMap();
    }

    public AlternateMapProperty<CrateDropType> crateDropTypesProperty() {
        return crateDropTypes;
    }

    public ObservableMap<Integer, MiscDropChance> getMiscDropChances() {
        return miscDropChances.getTrueMap();
    }

    public AlternateMapProperty<MiscDropChance> miscDropChancesProperty() {
        return miscDropChances;
    }

    public ObservableMap<Integer, MiscDropType> getMiscDropTypes() {
        return miscDropTypes.getTrueMap();
    }

    public AlternateMapProperty<MiscDropType> miscDropTypesProperty() {
        return miscDropTypes;
    }

    public ObservableMap<Integer, MobDrop> getMobDrops() {
        return mobDrops.getTrueMap();
    }

    public AlternateMapProperty<MobDrop> mobDropsProperty() {
        return mobDrops;
    }

    public ObservableMap<Integer, Event> getEvents() {
        return events.getTrueMap();
    }

    public AlternateMapProperty<Event> eventsProperty() {
        return events;
    }

    public ObservableMap<Integer, Mob> getMobs() {
        return mobs.getTrueMap();
    }

    public AlternateMapProperty<Mob> mobsProperty() {
        return mobs;
    }

    public ObservableMap<Integer, RarityWeights> getRarityWeights() {
        return rarityWeights.getTrueMap();
    }

    public AlternateMapProperty<RarityWeights> rarityWeightsProperty() {
        return rarityWeights;
    }

    public ObservableMap<Integer, ItemSet> getItemSets() {
        return itemSets.getTrueMap();
    }

    public AlternateMapProperty<ItemSet> itemSetsProperty() {
        return itemSets;
    }

    public ObservableMap<Integer, Crate> getCrates() {
        return crates.getTrueMap();
    }

    public AlternateMapProperty<Crate> cratesProperty() {
        return crates;
    }

    public ObservableMap<Integer, ItemReference> getItemReferences() {
        return itemReferences.getTrueMap();
    }

    public AlternateMapProperty<ItemReference> itemReferencesProperty() {
        return itemReferences;
    }

    public ObservableMap<Integer, Racing> getRacing() {
        return racing.getTrueMap();
    }

    public AlternateMapProperty<Racing> racingProperty() {
        return racing;
    }

    public ObservableMap<Integer, NanoCapsule> getNanoCapsules() {
        return nanoCapsules.getTrueMap();
    }

    public AlternateMapProperty<NanoCapsule> nanoCapsulesProperty() {
        return nanoCapsules;
    }

    public ObservableMap<Integer, CodeItem> getCodeItems() {
        return codeItems.getTrueMap();
    }

    public AlternateMapProperty<CodeItem> codeItemsProperty() {
        return codeItems;
    }

    public ObservableMap<Data, Set<Data>> getReferenceMap() {
        return referenceMap.get();
    }

    public ReadOnlyMapProperty<Data, Set<Data>> referenceMapProperty() {
        return referenceMap;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Drops
                && this.crateDropChances.equals(((Drops) obj).crateDropChances)
                && this.crateDropTypes.equals(((Drops) obj).crateDropTypes)
                && this.miscDropChances.equals(((Drops) obj).miscDropChances)
                && this.miscDropTypes.equals(((Drops) obj).miscDropTypes)
                && this.mobDrops.equals(((Drops) obj).mobDrops)
                && this.events.equals(((Drops) obj).events)
                && this.mobs.equals(((Drops) obj).mobs)
                && this.rarityWeights.equals(((Drops) obj).rarityWeights)
                && this.itemSets.equals(((Drops) obj).itemSets)
                && this.crates.equals(((Drops) obj).crates)
                && this.itemReferences.equals(((Drops) obj).itemReferences)
                && this.racing.equals(((Drops) obj).racing)
                && this.nanoCapsules.equals(((Drops) obj).nanoCapsules)
                && this.codeItems.equals(((Drops) obj).codeItems);
    }

    public static class AlternateMapProperty<V extends Data> extends SimpleMapProperty<String, V> {
        private final MapProperty<Integer, V> trueMap;
        private final MapProperty<Integer, String> keyMap;
        private final BooleanProperty mapsSynced;

        public AlternateMapProperty() {
            super(FXCollections.observableMap(new LinkedHashMap<>()));
            trueMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
            keyMap = new SimpleMapProperty<>(FXCollections.observableMap(new LinkedHashMap<>()));
            mapsSynced = new SimpleBooleanProperty(false);
        }

        public ObservableMap<String, V> getStringMap() {
            return get();
        }

        public ReadOnlyMapProperty<String, V> stringMapProperty() {
            return this;
        }

        public ObservableMap<Integer, V> getTrueMap() {
            return trueMap.get();
        }

        public ReadOnlyMapProperty<Integer, V> trueMapProperty() {
            return trueMap;
        }

        public ObservableMap<Integer, String> getKeyMap() {
            return keyMap.get();
        }

        public ReadOnlyMapProperty<Integer, String> keyMapProperty() {
            return keyMap;
        }

        public boolean isMapsSynced() {
            return mapsSynced.get();
        }

        public ReadOnlyBooleanProperty mapsSyncedProperty() {
            return mapsSynced;
        }

        public void syncMaps() throws NumberFormatException {
            if (!mapsSynced.get()) {
                for (var entry : Set.copyOf(entrySet())) {
                    String key = entry.getKey();
                    V value = entry.getValue();
                    value.constructBindings();
                    int trueKey = Integer.parseInt(value.getId());

                    if (keyMap.containsKey(trueKey)) {
                        remove(key);
                        keyMap.remove(trueKey);
                        trueMap.remove(trueKey);
                    }

                    keyMap.put(trueKey, key);
                    trueMap.put(trueKey, value);
                }
                mapsSynced.set(true);
            }
        }

        public void registerReferences(Drops drops) {
            values().forEach(value -> value.registerReferences(drops));
        }

        public int getNextTrueID() {
            return trueMap.keySet().stream().reduce(INT_PLACEHOLDER_ID, Math::max) + 1;
        }

        public String getNextID() {
            return String.valueOf(keySet().stream()
                    .map(s -> {
                        try {
                            return Integer.parseInt(s);
                        } catch (NumberFormatException e) {
                            return INT_PLACEHOLDER_ID;
                        }
                    })
                    .reduce(INT_PLACEHOLDER_ID, Math::max) + 1);
        }

        public V add(V data) {
            if (!mapsSynced.get())
                return null;

            int intKey = getNextTrueID();
            String id = data.getId();

            if (data.getClass().isAnnotationPresent(IdMeaningful.class) &&
                    !id.equals(PLACEHOLDER_ID) && !id.equals(UNSET_ID)) {
                try {
                    intKey = Integer.parseInt(id);
                } catch (NumberFormatException ignored) {
                }
            }

            if (trueMap.containsKey(intKey))
                return null;

            String key = getNextID();
            data.setId(String.valueOf(intKey));

            trueMap.put(intKey, data);
            keyMap.put(intKey, key);
            put(key, data);
            return data;
        }

        public V remove(int trueKey) {
            if (!mapsSynced.get())
                return null;

            V value = trueMap.remove(trueKey);
            String key = keyMap.get(trueKey);

            keyMap.remove(trueKey);
            remove(key);
            return value;
        }

        public V remove(V data) {
            if (!mapsSynced.get())
                return null;

            int trueKey = Integer.parseInt(data.getId());
            V value = trueMap.remove(trueKey);
            String key = keyMap.get(trueKey);

            keyMap.remove(trueKey);
            remove(key);
            return value;
        }

        public boolean containsKey(int trueKey) {
            if (!mapsSynced.get())
                return false;

            String key = keyMap.get(trueKey);
            return Objects.nonNull(key) && containsKey(key);
        }

        public void setDefault() {
            set(FXCollections.observableMap(new LinkedHashMap<>()));
        }
    }
}
