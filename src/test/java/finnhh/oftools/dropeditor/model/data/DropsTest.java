package finnhh.oftools.dropeditor.model.data;

import finnhh.oftools.dropeditor.model.ReferenceMode;
import finnhh.oftools.dropeditor.model.ReversibleAction;
import javafx.beans.binding.MapExpression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DropsTest extends DataTest<Drops> {

    DropsTest() {
        super(new Drops());
        mainData.manageMaps();
    }

    @BeforeEach
    void clearDrops() {
        var maps = List.of(
                drops.crateDropChancesProperty(),
                drops.crateDropTypesProperty(),
                drops.miscDropChancesProperty(),
                drops.miscDropTypesProperty(),
                drops.mobDropsProperty(),
                drops.eventsProperty(),
                drops.mobsProperty(),
                drops.rarityWeightsProperty(),
                drops.itemSetsProperty(),
                drops.cratesProperty(),
                drops.itemReferencesProperty(),
                drops.racingProperty(),
                drops.nanoCapsulesProperty(),
                drops.codeItemsProperty()
        );

        maps.forEach(map -> {
            map.getTrueMap().clear();
            map.getStringMap().clear();
            map.getKeyMap().clear();
        });

        drops.getReferenceMap().clear();

        drops.getUndoStack().clear();
        drops.getRedoStack().clear();
        drops.setCloneObjectsBeforeEditing(true);
    }

    void testSetFieldsFromData(Data data) {
        ItemReference itemReference = new ItemReference();
        fullyConfigure(itemReference);

        drops.setFieldsFromData(data);
        Assertions.assertEquals(1, drops.getItemReferences().size());
    }

    @Test
    @Override
    void testGetEditableClone() {
        Assertions.assertSame(drops, drops.getEditableClone());
    }

    @Test
    @Override
    void testSetFieldsFromInvalidData() {
        testSetFieldsFromData(new Event());
    }

    @Test
    @Override
    void testSetFieldsFromValidData() {
        testSetFieldsFromData(mainData);
    }

    @Test
    @Override
    void testConstructBindings() {
        Drops other = new Drops();
        other.constructBindings();

        Assertions.assertTrue(other.malformedProperty().isBound());
        other.itemReferencesProperty().set(null);
        Assertions.assertTrue(other.isMalformed());
        other.itemReferencesProperty().setDefault();
        Assertions.assertFalse(other.isMalformed());
    }

    @Test
    void testManageMaps() {
        Drops drops = new Drops();

        var maps = List.of(
                drops.crateDropChancesProperty(),
                drops.crateDropTypesProperty(),
                drops.miscDropChancesProperty(),
                drops.miscDropTypesProperty(),
                drops.mobDropsProperty(),
                drops.eventsProperty(),
                drops.mobsProperty(),
                drops.rarityWeightsProperty(),
                drops.itemSetsProperty(),
                drops.cratesProperty(),
                drops.itemReferencesProperty(),
                drops.racingProperty(),
                drops.nanoCapsulesProperty(),
                drops.codeItemsProperty()
        );

        ItemReference itemReference = new ItemReference();
        itemReference.constructBindings();
        drops.itemReferencesProperty().put("0", itemReference);

        CodeItem codeItem = new CodeItem();
        codeItem.setCode("abc");
        codeItem.getItemReferenceIDs().add(itemReference.getItemReferenceID());
        codeItem.constructBindings();
        drops.codeItemsProperty().put("0", codeItem);

        maps.stream()
                .filter(map -> map != drops.itemReferencesProperty() && map != drops.codeItemsProperty())
                .forEach(map -> map.set(null));

        drops.manageMaps();

        Assertions.assertTrue(drops.malformedProperty().isBound());

        for (var map : maps) {
            Assertions.assertTrue(map.isNotNull().get());
            Assertions.assertTrue(map.isMapsSynced());
        }

        var referenceMap = drops.getReferenceMap();
        Assertions.assertTrue(referenceMap.containsKey(itemReference));
        Assertions.assertTrue(referenceMap.get(itemReference).contains(codeItem));
    }

    @Test
    void testGetReferenceModeFor() {
        ItemReference itemReferenceNone = new ItemReference();
        itemReferenceNone.setItemID(123);
        itemReferenceNone.setType(0);
        fullyConfigure(itemReferenceNone);

        ItemReference itemReferenceOne = new ItemReference();
        itemReferenceOne.setItemID(124);
        itemReferenceOne.setType(1);
        fullyConfigure(itemReferenceOne);

        ItemReference itemReferenceMulti = new ItemReference();
        itemReferenceMulti.setItemID(125);
        itemReferenceMulti.setType(2);
        fullyConfigure(itemReferenceMulti);

        CodeItem codeItem1 = new CodeItem();
        codeItem1.setCode("abc");
        codeItem1.getItemReferenceIDs().add(itemReferenceOne.getItemReferenceID());
        fullyConfigure(codeItem1);

        CodeItem codeItem2 = new CodeItem();
        codeItem2.setCode("def");
        codeItem2.getItemReferenceIDs().add(itemReferenceMulti.getItemReferenceID());
        fullyConfigure(codeItem2);

        CodeItem codeItem3 = new CodeItem();
        codeItem3.setCode("ghi");
        codeItem3.getItemReferenceIDs().add(itemReferenceMulti.getItemReferenceID());
        fullyConfigure(codeItem3);

        Assertions.assertSame(ReferenceMode.NONE, drops.getReferenceModeFor(itemReferenceNone));
        Assertions.assertSame(ReferenceMode.UNIQUE, drops.getReferenceModeFor(itemReferenceOne));
        Assertions.assertSame(ReferenceMode.MULTIPLE, drops.getReferenceModeFor(itemReferenceMulti));
    }

    @Test
    void testGetDataMap() {
        var classMapMap = Map.ofEntries(
                Map.entry(CrateDropChance.class, drops.crateDropChancesProperty()),
                Map.entry(CrateDropType.class, drops.crateDropTypesProperty()),
                Map.entry(MiscDropChance.class, drops.miscDropChancesProperty()),
                Map.entry(MiscDropType.class, drops.miscDropTypesProperty()),
                Map.entry(MobDrop.class, drops.mobDropsProperty()),
                Map.entry(Event.class, drops.eventsProperty()),
                Map.entry(Mob.class, drops.mobsProperty()),
                Map.entry(RarityWeights.class, drops.rarityWeightsProperty()),
                Map.entry(ItemSet.class, drops.itemSetsProperty()),
                Map.entry(Crate.class, drops.cratesProperty()),
                Map.entry(ItemReference.class, drops.itemReferencesProperty()),
                Map.entry(Racing.class, drops.racingProperty()),
                Map.entry(NanoCapsule.class, drops.nanoCapsulesProperty()),
                Map.entry(CodeItem.class, drops.codeItemsProperty())
        );

        for (var entry : classMapMap.entrySet()) {
            var opt = drops.getDataMap(entry.getKey());
            Assertions.assertTrue(opt.isPresent());
            Assertions.assertSame(entry.getValue(), opt.get());
        }

        Assertions.assertTrue(drops.getDataMap(Drops.class).isEmpty());
    }

    @Test
    void testAdd() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        var classMapMap = Map.ofEntries(
                Map.entry(CrateDropChance.class, drops.crateDropChancesProperty()),
                Map.entry(CrateDropType.class, drops.crateDropTypesProperty()),
                Map.entry(MiscDropChance.class, drops.miscDropChancesProperty()),
                Map.entry(MiscDropType.class, drops.miscDropTypesProperty()),
                Map.entry(MobDrop.class, drops.mobDropsProperty()),
                Map.entry(Event.class, drops.eventsProperty()),
                Map.entry(Mob.class, drops.mobsProperty()),
                Map.entry(RarityWeights.class, drops.rarityWeightsProperty()),
                Map.entry(ItemSet.class, drops.itemSetsProperty()),
                Map.entry(Crate.class, drops.cratesProperty()),
                Map.entry(ItemReference.class, drops.itemReferencesProperty()),
                Map.entry(Racing.class, drops.racingProperty()),
                Map.entry(NanoCapsule.class, drops.nanoCapsulesProperty()),
                Map.entry(CodeItem.class, drops.codeItemsProperty())
        );

        for (var entry : classMapMap.entrySet()) {
            Data obj = entry.getKey().getConstructor().newInstance();
            obj.constructBindings();

            Assertions.assertSame(obj, drops.add(obj));

            Assertions.assertTrue(entry.getValue().containsKey(Integer.parseInt(obj.getId())));
            Assertions.assertSame(obj, entry.getValue().getTrueMap().get(Integer.parseInt(obj.getId())));
        }

        Assertions.assertNull(drops.add(null));
        Assertions.assertNull(drops.add(drops));
        Assertions.assertTrue(classMapMap.values().stream().allMatch(amp -> amp.size() == 1));
    }

    @Test
    void testRemove() throws NoSuchMethodException, InvocationTargetException,
            InstantiationException, IllegalAccessException {

        var classMapMap = Map.ofEntries(
                Map.entry(CrateDropChance.class, drops.crateDropChancesProperty()),
                Map.entry(CrateDropType.class, drops.crateDropTypesProperty()),
                Map.entry(MiscDropChance.class, drops.miscDropChancesProperty()),
                Map.entry(MiscDropType.class, drops.miscDropTypesProperty()),
                Map.entry(MobDrop.class, drops.mobDropsProperty()),
                Map.entry(Event.class, drops.eventsProperty()),
                Map.entry(Mob.class, drops.mobsProperty()),
                Map.entry(RarityWeights.class, drops.rarityWeightsProperty()),
                Map.entry(ItemSet.class, drops.itemSetsProperty()),
                Map.entry(Crate.class, drops.cratesProperty()),
                Map.entry(ItemReference.class, drops.itemReferencesProperty()),
                Map.entry(Racing.class, drops.racingProperty()),
                Map.entry(NanoCapsule.class, drops.nanoCapsulesProperty()),
                Map.entry(CodeItem.class, drops.codeItemsProperty())
        );

        for (var entry : classMapMap.entrySet()) {
            Data obj = entry.getKey().getConstructor().newInstance();
            obj.constructBindings();
            drops.add(obj);

            Assertions.assertSame(obj, drops.remove(obj));

            Assertions.assertTrue(entry.getValue().isEmpty());
        }

        Assertions.assertNull(drops.remove(drops));
        Assertions.assertNull(drops.remove(null));
        Assertions.assertTrue(classMapMap.values().stream().allMatch(MapExpression::isEmpty));
    }

    @Test
    void testGenerateActionKey() throws InterruptedException {
        Drops drops = new Drops();
        drops.manageMaps();

        long key = drops.generateActionKey();
        for (int i = 0; i < 5; i++) {
            Thread.sleep(2);
            long newKey = drops.generateActionKey();
            Assertions.assertNotEquals(key, newKey);
            key = newKey;
        }
    }

    @Test
    void testRegisterUndo() {
        var action1 = new ReversibleAction(0L, null, () -> {}, () -> {});
        var action2 = new ReversibleAction(1L, null, () -> {}, () -> {});

        drops.registerUndo(action1);
        drops.registerUndo(action2);

        Assertions.assertSame(action2, drops.getUndoStack().get(0));
        Assertions.assertSame(action1, drops.getUndoStack().get(1));
    }

    @Test
    void testRegisterRedo() {
        var action1 = new ReversibleAction(0L, null, () -> {}, () -> {});
        var action2 = new ReversibleAction(1L, null, () -> {}, () -> {});

        drops.registerRedo(action1);
        drops.registerRedo(action2);

        Assertions.assertSame(action2, drops.getRedoStack().get(0));
        Assertions.assertSame(action1, drops.getRedoStack().get(1));
    }

    @Test
    void testClearRedoRegistry() {
        var action1 = new ReversibleAction(0L, null, () -> {}, () -> {});
        var action2 = new ReversibleAction(1L, null, () -> {}, () -> {});
        drops.registerRedo(action1);
        drops.registerRedo(action2);

        drops.clearRedoRegistry();

        Assertions.assertTrue(drops.getRedoStack().isEmpty());
    }

    @Nested
    class MakeEditReplaceTests {
        private MiscDropType miscDropType;
        private MobDrop mobDrop;
        private Mob mobOne;
        private Mob mobTwo;

        @BeforeEach
        void setup() {
            CrateDropType crateDropType = new CrateDropType();
            fullyConfigure(crateDropType);

            CrateDropChance crateDropChance = new CrateDropChance();
            fullyConfigure(crateDropChance);

            MiscDropChance miscDropChance = new MiscDropChance();
            fullyConfigure(miscDropChance);

            miscDropType = new MiscDropType();
            miscDropType.setBoostAmount(20);
            miscDropType.setPotionAmount(22);
            miscDropType.setTaroAmount(40);
            miscDropType.setFMAmount(44);
            fullyConfigure(miscDropType);

            mobDrop = new MobDrop();
            mobDrop.setCrateDropTypeID(crateDropType.getCrateDropTypeID());
            mobDrop.setCrateDropChanceID(crateDropChance.getCrateDropChanceID());
            mobDrop.setMiscDropTypeID(miscDropType.getMiscDropTypeID());
            mobDrop.setMiscDropChanceID(miscDropChance.getMiscDropChanceID());
            fullyConfigure(mobDrop);

            mobOne = new Mob();
            mobOne.setMobID(30);
            mobOne.setMobDropID(mobDrop.getMobDropID());
            fullyConfigure(mobOne);

            mobTwo = new Mob();
            mobTwo.setMobID(60);
            mobTwo.setMobDropID(mobDrop.getMobDropID());
            fullyConfigure(mobTwo);
        }

        @Nested
        class MakeEditableTest {
            @Test
            void needed() {
                var referenceMap = drops.getReferenceMap();
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));
                List<Data> newObjectChain = drops.makeEditable(objectChain, 0, 0L);

                // non-root objects swapped
                MiscDropType newMiscDropType = (MiscDropType) newObjectChain.get(0);
                MobDrop newMobDrop = (MobDrop) newObjectChain.get(1);
                Mob newMob = (Mob) newObjectChain.get(2);

                Assertions.assertNotEquals(newMiscDropType, miscDropType);
                Assertions.assertNotEquals(newMobDrop, mobDrop);
                Assertions.assertEquals(newMob, mobOne);

                // ...with new objects
                Assertions.assertEquals(2, drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2, drops.mobDropsProperty().size());
                Assertions.assertEquals(2, drops.mobsProperty().size());

                // ids and references properly set
                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                // undo 2 makeEditable operations
                Assertions.assertEquals(2, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());

                var optUndo = drops.runUndo();
                Assertions.assertTrue(optUndo.isPresent());
                Assertions.assertSame(newMob, optUndo.get());

                // when undone, we no longer have the new objects
                Assertions.assertEquals(1, drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1, drops.mobDropsProperty().size());
                Assertions.assertEquals(2, drops.mobsProperty().size());

                // ...nor their references
                Assertions.assertFalse(referenceMap.containsKey(newMiscDropType));
                Assertions.assertFalse(referenceMap.containsKey(newMobDrop));

                // redo the 2 undone makeEditable operations
                Assertions.assertTrue(drops.getUndoStack().isEmpty());
                Assertions.assertEquals(2, drops.getRedoStack().size());

                var optRedo = drops.runRedo();
                Assertions.assertTrue(optRedo.isPresent());
                Assertions.assertSame(newMob, optRedo.get());

                // when redone, it should look the exact same as if makeEditable was run once more
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                Assertions.assertEquals(2, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());
            }

            @Test
            void notNeeded() {
                drops.remove(mobTwo);
                mobTwo.unregisterReferences(drops);

                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());

                List<Data> newObjectChain = drops.makeEditable(objectChain, 0, 0L);

                Assertions.assertSame(objectChain, newObjectChain);
                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());
            }

            @Test
            void objectCloningDisabled() {
                drops.setCloneObjectsBeforeEditing(false);

                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                List<Data> newObjectChain = drops.makeEditable(objectChain, 0, 0L);

                Assertions.assertSame(objectChain, newObjectChain);
                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());
            }
        }

        @Nested
        class MakeReplacementTest {
            @Test
            void existingParentWithNewObject() {
                MiscDropType newMiscDropType = new MiscDropType();
                newMiscDropType.setBoostAmount(20);
                newMiscDropType.setPotionAmount(22);
                newMiscDropType.setTaroAmount(40);
                newMiscDropType.setFMAmount(44);
                fullyConfigure(newMiscDropType);

                var referenceMap = drops.getReferenceMap();
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));
                drops.registerRedo(new ReversibleAction(1L, null, () -> {}, () -> {}));

                drops.makeReplacement(objectChain, 0L, newMiscDropType);

                // non-root objects swapped
                MobDrop newMobDrop = (MobDrop) objectChain.get(1);
                Mob newMob = (Mob) objectChain.get(2);

                Assertions.assertNotEquals(newMobDrop, mobDrop);
                Assertions.assertEquals(newMob, mobOne);

                // ...with new objects
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                // ids and references properly set
                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                // undo 1 makeReplace + 1 makeEditable operations
                Assertions.assertEquals(2, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());

                var optUndo = drops.runUndo();
                Assertions.assertTrue(optUndo.isPresent());
                Assertions.assertSame(newMob, optUndo.get());

                // when undone, we no longer have the new objects
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                // ...nor their references
                Assertions.assertFalse(referenceMap.containsKey(newMiscDropType));
                Assertions.assertFalse(referenceMap.containsKey(newMobDrop));

                // redo the 1 makeReplacement + 1 makeEditable undone operations
                Assertions.assertTrue(drops.getUndoStack().isEmpty());
                Assertions.assertEquals(2, drops.getRedoStack().size());

                var optRedo = drops.runRedo();
                Assertions.assertTrue(optRedo.isPresent());
                Assertions.assertSame(newMob, optRedo.get());

                // when redone, it should look the exact same as if the operations were run once more
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                Assertions.assertEquals(2, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());
            }

            @Test
            void replaceWithSameObject() {
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));
                drops.registerRedo(new ReversibleAction(1L, null, () -> {}, () -> {}));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());
                Assertions.assertEquals(1, drops.getRedoStack().size());


                drops.makeReplacement(objectChain, 0L, miscDropType);

                MobDrop newMobDrop = (MobDrop) objectChain.get(1);
                Mob newMob = (Mob) objectChain.get(2);

                Assertions.assertSame(newMobDrop, mobDrop);
                Assertions.assertSame(newMob, mobOne);

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());
                Assertions.assertEquals(1, drops.getRedoStack().size());
            }

            @Test
            void nullParent() {
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType));
                drops.registerRedo(new ReversibleAction(1L, null, () -> {}, () -> {}));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());
                Assertions.assertEquals(1, drops.getRedoStack().size());

                drops.makeReplacement(objectChain, 0L, miscDropType);

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());
                Assertions.assertEquals(1, drops.getRedoStack().size());

            }
        }

        @Nested
        class MakeEditTest {
            @Test
            void makeEditableEdit() {
                var referenceMap = drops.getReferenceMap();
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));

                drops.makeEdit(objectChain, 0L, data -> ((MiscDropType) data).setTaroAmount(999));

                // non-root objects swapped
                MiscDropType newMiscDropType = (MiscDropType) objectChain.get(0);
                MobDrop newMobDrop = (MobDrop) objectChain.get(1);
                Mob newMob = (Mob) objectChain.get(2);

                Assertions.assertEquals(999, newMiscDropType.getTaroAmount());
                Assertions.assertNotEquals(newMiscDropType, miscDropType);
                Assertions.assertNotEquals(newMobDrop, mobDrop);
                Assertions.assertEquals(newMob, mobOne);

                // ...with new objects
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                // ids and references properly set
                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                // undo 1 makeEdit + 2 makeEditable operations
                Assertions.assertEquals(3, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());

                var optUndo = drops.runUndo();
                Assertions.assertTrue(optUndo.isPresent());
                Assertions.assertSame(newMob, optUndo.get());

                Assertions.assertEquals(miscDropType.getTaroAmount(), newMiscDropType.getTaroAmount());

                // when undone, we no longer have the new objects
                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                // ...nor their references
                Assertions.assertFalse(referenceMap.containsKey(newMiscDropType));
                Assertions.assertFalse(referenceMap.containsKey(newMobDrop));

                // redo the 1 makeEdit + 2 makeEditable undone operations
                Assertions.assertTrue(drops.getUndoStack().isEmpty());
                Assertions.assertEquals(3, drops.getRedoStack().size());

                var optRedo = drops.runRedo();
                Assertions.assertTrue(optRedo.isPresent());
                Assertions.assertSame(newMob, optRedo.get());

                Assertions.assertEquals(999, newMiscDropType.getTaroAmount());

                // when redone, it should look the exact same as if operations were run once more
                Assertions.assertEquals(2 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(2 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(2 ,drops.mobsProperty().size());

                Assertions.assertTrue(referenceMap.containsKey(newMiscDropType));
                Assertions.assertTrue(referenceMap.get(newMiscDropType).contains(newMobDrop));
                Assertions.assertEquals(newMiscDropType.getMiscDropTypeID(), newMobDrop.getMiscDropTypeID());
                Assertions.assertTrue(referenceMap.containsKey(newMobDrop));
                Assertions.assertTrue(referenceMap.get(newMobDrop).contains(newMob));
                Assertions.assertEquals(newMobDrop.getMobDropID(), newMob.getMobDropID());

                Assertions.assertEquals(3, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());
            }

            @Test
            void alreadyEditableEdit() {
                drops.remove(mobTwo);
                mobTwo.unregisterReferences(drops);

                int prevTaro = miscDropType.getTaroAmount();
                List<Data> objectChain = new ArrayList<>(List.of(miscDropType, mobDrop, mobOne));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());

                drops.makeEdit(objectChain, 0L, data -> ((MiscDropType) data).setTaroAmount(999));

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());

                Assertions.assertEquals(999, miscDropType.getTaroAmount());

                // undo 1 makeEdit operation
                Assertions.assertEquals(1, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());

                drops.runUndo();

                Assertions.assertEquals(prevTaro, miscDropType.getTaroAmount());

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());

                // redo the 1 makeEdit undone operation
                Assertions.assertTrue(drops.getUndoStack().isEmpty());
                Assertions.assertEquals(1, drops.getRedoStack().size());

                drops.runRedo();

                Assertions.assertEquals(999, miscDropType.getTaroAmount());

                Assertions.assertEquals(1 ,drops.miscDropTypesProperty().size());
                Assertions.assertEquals(1 ,drops.mobDropsProperty().size());
                Assertions.assertEquals(1 ,drops.mobsProperty().size());

                Assertions.assertEquals(1, drops.getUndoStack().size());
                Assertions.assertTrue(drops.getRedoStack().isEmpty());
            }
        }
    }

    @Nested
    class RunUndoRedoTest {
        @Test
        void testUndoRedoStep() {
            int oldVal = 1;
            int newVal = 2;
            ItemReference rootData = new ItemReference();
            List<Integer> mutatedList = new ArrayList<>();
            mutatedList.add(newVal);

            drops.registerUndo(new ReversibleAction(0L,
                    rootData,
                    () -> mutatedList.set(0, newVal),
                    () -> mutatedList.set(0, oldVal)
            ));

            Assertions.assertEquals(newVal, mutatedList.get(0));

            var optUndo = drops.runUndo();

            Assertions.assertTrue(optUndo.isPresent());
            Assertions.assertSame(rootData, optUndo.get());
            Assertions.assertEquals(oldVal, mutatedList.get(0));

            var optRedo = drops.runRedo();

            Assertions.assertTrue(optRedo.isPresent());
            Assertions.assertSame(rootData, optRedo.get());
            Assertions.assertEquals(newVal, mutatedList.get(0));
        }

        @Test
        void testUndoRedoSequence() {
            int oldVal = 1;
            int newVal = 2;
            int addedVal = 3;
            ItemReference rootData1 = new ItemReference();
            ItemReference rootData2 = new ItemReference();
            List<Integer> mutatedList = new ArrayList<>();
            mutatedList.add(newVal);
            mutatedList.add(addedVal);

            drops.registerUndo(new ReversibleAction(0L,
                    rootData1,
                    () -> mutatedList.set(0, newVal),
                    () -> mutatedList.set(0, oldVal)
            ));
            drops.registerUndo(new ReversibleAction(0L,
                    rootData2,
                    () -> mutatedList.add(addedVal),
                    () -> mutatedList.remove(1)
            ));

            Assertions.assertEquals(2, mutatedList.size());
            Assertions.assertEquals(newVal, mutatedList.get(0));
            Assertions.assertEquals(addedVal, mutatedList.get(1));

            var optUndo = drops.runUndo();

            Assertions.assertTrue(optUndo.isPresent());
            Assertions.assertSame(rootData1, optUndo.get());
            Assertions.assertEquals(1, mutatedList.size());
            Assertions.assertEquals(oldVal, mutatedList.get(0));

            var optRedo = drops.runRedo();

            Assertions.assertTrue(optRedo.isPresent());
            Assertions.assertSame(rootData2, optRedo.get());
            Assertions.assertEquals(2, mutatedList.size());
            Assertions.assertEquals(newVal, mutatedList.get(0));
            Assertions.assertEquals(addedVal, mutatedList.get(1));
        }

        @Test
        void testUndoRedoEmpty() {
            Assertions.assertTrue(drops.runUndo().isEmpty());
            Assertions.assertTrue(drops.runRedo().isEmpty());
        }
    }

    @Nested
    class AlternateMapPropertyTest {
        @Test
        void testSyncMaps() {
            Drops.AlternateMapProperty<ItemReference> alternateMap = new Drops.AlternateMapProperty<>();
            int size = 10;

            for (int i = 0; i < size; i++) {
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(i + 1);
                itemReference.setItemID(i * 10 + 1);
                itemReference.setType(i % 5);
                alternateMap.put(String.valueOf(i), itemReference);
            }

            Assertions.assertTrue(alternateMap.getKeyMap().isEmpty());
            Assertions.assertTrue(alternateMap.getTrueMap().isEmpty());
            Assertions.assertFalse(alternateMap.isMapsSynced());

            alternateMap.syncMaps();

            for (int i = 0; i < size; i++) {
                Assertions.assertEquals(alternateMap.getTrueMap().get(i + 1), alternateMap.get(String.valueOf(i)));
                Assertions.assertEquals(alternateMap.getKeyMap().get(i + 1), String.valueOf(i));
            }
            Assertions.assertTrue(alternateMap.isMapsSynced());
        }

        @Test
        void testGetNextTrueID() {
            Drops.AlternateMapProperty<ItemReference> mapEmpty = new Drops.AlternateMapProperty<>();
            Drops.AlternateMapProperty<ItemReference> mapHalf = new Drops.AlternateMapProperty<>();
            Drops.AlternateMapProperty<ItemReference> mapFull = new Drops.AlternateMapProperty<>();
            int size = 10;

            for (int i = 0; i < size; i++) {
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(i + 1);
                itemReference.setItemID(i * 10 + 1);
                itemReference.setType(i % 5);
                // bypass the add logic for now
                mapFull.put(String.valueOf(i), itemReference);
                if (i < size / 2)
                    mapHalf.put(String.valueOf(i), itemReference);
            }

            mapEmpty.syncMaps();
            mapHalf.syncMaps();
            mapFull.syncMaps();

            Assertions.assertEquals(mapEmpty.getIDLowerBound() + 1, mapEmpty.getNextTrueID());
            Assertions.assertEquals(mapHalf.getIDLowerBound() + size / 2 + 2, mapHalf.getNextTrueID());
            Assertions.assertEquals(mapFull.getIDLowerBound() + size + 2, mapFull.getNextTrueID());
        }

        @Test
        void testGetNextID() {
            Drops.AlternateMapProperty<ItemReference> mapEmpty = new Drops.AlternateMapProperty<>();
            Drops.AlternateMapProperty<ItemReference> mapHalf = new Drops.AlternateMapProperty<>();
            Drops.AlternateMapProperty<ItemReference> mapFull = new Drops.AlternateMapProperty<>();
            int size = 10;

            for (int i = 0; i < size; i++) {
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(i + 1);
                itemReference.setItemID(i * 10 + 1);
                itemReference.setType(i % 5);
                // bypass the add logic for now
                mapFull.put(String.valueOf(i), itemReference);
                if (i < size / 2)
                    mapHalf.put(String.valueOf(i), itemReference);
            }

            mapEmpty.syncMaps();
            mapHalf.syncMaps();
            mapFull.syncMaps();

            Assertions.assertEquals(String.valueOf(mapEmpty.getIDLowerBound() + 1), mapEmpty.getNextID());
            Assertions.assertEquals(String.valueOf(mapHalf.getIDLowerBound() + size / 2 + 1), mapHalf.getNextID());
            Assertions.assertEquals(String.valueOf(mapFull.getIDLowerBound() + size + 1), mapFull.getNextID());
        }

        @Test
        void testSetDefault() {
            Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
            var stringMap = irMap.getStringMap();
            stringMap.put("0", new ItemReference());

            irMap.setDefault();

            Assertions.assertNotSame(stringMap, irMap.getStringMap());
            Assertions.assertTrue(irMap.getStringMap().isEmpty());
        }

        @Nested
        class TestAdd {
            @Test
            void validAndIdKept() {
                Drops.AlternateMapProperty<Crate> cMap = new Drops.AlternateMapProperty<>();
                cMap.syncMaps();

                int crateID = 123;
                int itemSetID = 66;
                Crate crate1 = new Crate();
                crate1.setCrateID(crateID);
                crate1.setItemSetID(itemSetID);
                crate1.constructBindings();

                Crate crate2 = new Crate();
                crate2.setCrateID(crateID);
                crate2.setItemSetID(itemSetID + 1);
                crate2.constructBindings();

                Assertions.assertSame(crate1, cMap.add(crate1));
                Assertions.assertNull(cMap.add(crate2));

                Assertions.assertEquals(crate1, cMap.getTrueMap().get(crateID));
                Assertions.assertTrue(cMap.getKeyMap().containsKey(crateID));
                Assertions.assertEquals(crate1, cMap.getStringMap().get(cMap.getKeyMap().get(crateID)));
                Assertions.assertEquals(crateID, crate1.getCrateID());
                Assertions.assertEquals(itemSetID, crate1.getItemSetID());
            }

            @Test
            void validAndIdAssigned() {
                Drops.AlternateMapProperty<Crate> cMap = new Drops.AlternateMapProperty<>(Crate.INT_CRATE_PLACEHOLDER_ID);
                cMap.syncMaps();

                int crateID = Crate.INT_CRATE_PLACEHOLDER_ID + 1;
                Crate crate = new Crate();
                crate.constructBindings();

                Assertions.assertSame(crate, cMap.add(crate));

                Assertions.assertEquals(crate, cMap.getTrueMap().get(crateID));
                Assertions.assertTrue(cMap.getKeyMap().containsKey(crateID));
                Assertions.assertEquals(crate, cMap.getStringMap().get(cMap.getKeyMap().get(crateID)));
                Assertions.assertEquals(1, crate.getCrateID());
            }

            @Test
            void validAndIdNotMeaningful() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(123);
                itemReference.constructBindings();

                Assertions.assertSame(itemReference, irMap.add(itemReference));

                Assertions.assertEquals(itemReference, irMap.getTrueMap().get(irID));
                Assertions.assertTrue(irMap.getKeyMap().containsKey(irID));
                Assertions.assertEquals(itemReference, irMap.getStringMap().get(irMap.getKeyMap().get(irID)));
                Assertions.assertEquals(irID, itemReference.getItemReferenceID());
            }

            @Test
            void notSynced() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                Assertions.assertNull(irMap.add(new ItemReference()));
                Assertions.assertTrue(irMap.getTrueMap().isEmpty());
                Assertions.assertTrue(irMap.getKeyMap().isEmpty());
                Assertions.assertTrue(irMap.getStringMap().isEmpty());
            }

            @Test
            void nullAdd() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                Assertions.assertThrows(NullPointerException.class, () -> irMap.add(null));
                Assertions.assertTrue(irMap.getTrueMap().isEmpty());
                Assertions.assertTrue(irMap.getKeyMap().isEmpty());
                Assertions.assertTrue(irMap.getStringMap().isEmpty());
            }
        }

        @Nested
        class TestRemove {
            @Test
            void validRemoveID() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                irMap.add(itemReference);

                Assertions.assertEquals(itemReference, irMap.getTrueMap().get(irID));
                Assertions.assertTrue(irMap.getKeyMap().containsKey(irID));
                Assertions.assertEquals(itemReference, irMap.getStringMap().get(irMap.getKeyMap().get(irID)));
                Assertions.assertEquals(irID, itemReference.getItemReferenceID());

                ItemReference result = irMap.remove(irID);

                Assertions.assertEquals(itemReference, result);
                Assertions.assertTrue(irMap.getTrueMap().isEmpty());
                Assertions.assertTrue(irMap.getKeyMap().isEmpty());
                Assertions.assertTrue(irMap.getStringMap().isEmpty());
            }

            @Test
            void validRemoveObject() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                irMap.add(itemReference);

                Assertions.assertEquals(itemReference, irMap.getTrueMap().get(irID));
                Assertions.assertTrue(irMap.getKeyMap().containsKey(irID));
                Assertions.assertEquals(itemReference, irMap.getStringMap().get(irMap.getKeyMap().get(irID)));
                Assertions.assertEquals(irID, itemReference.getItemReferenceID());

                ItemReference result = irMap.remove(itemReference);

                Assertions.assertEquals(itemReference, result);
                Assertions.assertTrue(irMap.getTrueMap().isEmpty());
                Assertions.assertTrue(irMap.getKeyMap().isEmpty());
                Assertions.assertTrue(irMap.getStringMap().isEmpty());
            }

            @Test
            void notSynced() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(irID);
                itemReference.constructBindings();
                irMap.getStringMap().put(String.valueOf(irID), itemReference);
                irMap.getKeyMap().put(irID, String.valueOf(irID));
                irMap.getTrueMap().put(irID, itemReference);

                Assertions.assertNull(irMap.remove(irID));
                Assertions.assertNull(irMap.remove(itemReference));
            }

            @Test
            void nullRemove() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                //noinspection DataFlowIssue
                Assertions.assertThrows(NullPointerException.class, () -> irMap.remove(null));
            }

            @Test
            void missingRemoveID() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                irMap.add(itemReference);

                Assertions.assertNull(irMap.remove(irID + 99));
                Assertions.assertTrue(irMap.getTrueMap().containsKey(irID));
                Assertions.assertTrue(irMap.getKeyMap().containsKey(irID));
                Assertions.assertTrue(irMap.getStringMap().containsKey(irMap.getKeyMap().get(irID)));
            }

            @Test
            void missingRemoveObject() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                ItemReference other = new ItemReference();
                other.setItemReferenceID(irID + 99);
                other.constructBindings();

                irMap.add(itemReference);

                Assertions.assertNull(irMap.remove(other));
                Assertions.assertTrue(irMap.getTrueMap().containsKey(irID));
                Assertions.assertTrue(irMap.getKeyMap().containsKey(irID));
                Assertions.assertTrue(irMap.getStringMap().containsKey(irMap.getKeyMap().get(irID)));
            }
        }

        @Nested
        class TestContainsKey {
            @Test
            void validExistingKey() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                irMap.add(itemReference);

                Assertions.assertTrue(irMap.containsKey(irID));
            }

            @Test
            void validUnknownKey() {
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();
                irMap.syncMaps();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.constructBindings();

                irMap.add(itemReference);

                Assertions.assertFalse(irMap.containsKey(irID + 99));
            }

            @Test
            void notSynced() {
                // noinspection MismatchedQueryAndUpdateOfCollection
                Drops.AlternateMapProperty<ItemReference> irMap = new Drops.AlternateMapProperty<>();

                int irID = ItemReference.INT_PLACEHOLDER_ID + 1;
                ItemReference itemReference = new ItemReference();
                itemReference.setItemReferenceID(irID);
                itemReference.constructBindings();
                irMap.getStringMap().put(String.valueOf(irID), itemReference);
                irMap.getKeyMap().put(irID, String.valueOf(irID));
                irMap.getTrueMap().put(irID, itemReference);

                Assertions.assertFalse(irMap.containsKey(irID));
            }
        }
    }
}
