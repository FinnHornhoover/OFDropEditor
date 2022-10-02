package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.*;

public class MapContainerTooltipBox extends TilePane {
    public static final int TILE_LIMIT = 9;
    public static final int SOURCE_LIMIT = 4 * TILE_LIMIT;

    protected final MainController controller;

    private final LinkedHashMap<Integer, Node> childMap;

    public MapContainerTooltipBox(MainController controller) {
        this.controller = controller;

        childMap = new LinkedHashMap<>();

        setVgap(2);
        setHgap(2);
        setAlignment(Pos.BOTTOM_CENTER);
        setPrefColumns(3);
        setPrefHeight(0);
    }

    private void addVendorMaps(Pair<Integer, Integer> key) {
        var vendorItemMap = controller.getStaticDataStore().getVendorItemMap();
        var npcInstanceRegionGroupedMap = controller.getStaticDataStore().getNPCInstanceRegionGroupedMap();

        Optional.ofNullable(vendorItemMap.get(key))
                .stream()
                .flatMap(List::stream)
                .forEach(vii -> Optional.ofNullable(npcInstanceRegionGroupedMap.get(vii.npc()))
                        .ifPresent(npcInstanceRegionGroup -> npcInstanceRegionGroup.forEach((instanceID, withinInstanceNPCs) ->
                                withinInstanceNPCs.forEach((mri, npcInfoList) -> {
                                    if (childMap.size() > SOURCE_LIMIT)
                                        return;

                                    MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                    mapTooltipBox.addLabel(String.format("Vendor: %s - %d Taros",
                                            npcInfoList.isEmpty() ?
                                                    "Unknown" :
                                                    npcInfoList.get(0).npcTypeInfo().name(),
                                            vii.vendorPrice()));
                                    mapTooltipBox.addLocations(instanceID, mri);
                                    npcInfoList.forEach(npci -> mapTooltipBox.addMarker(npci.x(), npci.y()));

                                    childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                                }))));
    }

    private void addEggCrateMaps(int id) {
        var eggInstanceRegionGroupedMap = controller.getStaticDataStore().getEggInstanceRegionGroupedMap();

        Optional.ofNullable(eggInstanceRegionGroupedMap.get(id))
                .ifPresent(eggInstanceRegionGroup -> eggInstanceRegionGroup.forEach((instanceID, withinInstanceEggs) ->
                        withinInstanceEggs.forEach((mri, eggInfoList) -> {
                            if (childMap.size() > SOURCE_LIMIT)
                                return;

                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            mapTooltipBox.addLabel("Egg Find");
                            mapTooltipBox.addLocations(instanceID, mri);
                            eggInfoList.forEach(ei -> mapTooltipBox.addMarker(ei.x(), ei.y()));

                            childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                        })));
    }

    private void addRacingCrateMaps(int id) {
        var racingMap = controller.getDrops().getRacing();
        var epInstanceMap = controller.getStaticDataStore().getEPInstanceMap();
        var mapRegionList = controller.getStaticDataStore().getMapRegionInfoList();
        var npcInfoMap = controller.getStaticDataStore().getNpcInfoMap();

        racingMap.values()
                .stream()
                .filter(r -> r.getRewards().contains(id) && epInstanceMap.containsKey(r.getEPID()))
                .forEach(r -> {
                    InstanceInfo ii = epInstanceMap.get(r.getEPID());

                    ii.entryWarps().stream()
                            .flatMap(wi -> Optional.ofNullable(npcInfoMap.get(wi.warpNPC())).stream())
                            .flatMap(List::stream)
                            .filter(npcInfo -> npcInfo.instanceID() == InstanceInfo.OVERWORLD_INSTANCE_ID
                                    && mapRegionList.stream().anyMatch(mri -> mri.coordinatesIncluded(npcInfo.x(), npcInfo.y())))
                            .findFirst()
                            .ifPresent(npcInfo -> {
                                if (childMap.size() > SOURCE_LIMIT)
                                    return;

                                MapRegionInfo mri = mapRegionList.stream()
                                        .filter(mri2 -> mri2.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                                        .findFirst()
                                        .orElseThrow();

                                MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                mapTooltipBox.addLabel("Racing: "
                                        + r.getRankScores().get(r.getRewards().indexOf(id)) + " Min Score");
                                mapTooltipBox.addLocations(ii.id(), mri);
                                mapTooltipBox.addMarker(npcInfo.x(), npcInfo.y());

                                childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                            });
                });
    }

    private void addRacingLocationMaps(int EPID) {
        var epInstanceMap = controller.getStaticDataStore().getEPInstanceMap();
        var mapRegionList = controller.getStaticDataStore().getMapRegionInfoList();
        var npcInfoMap = controller.getStaticDataStore().getNpcInfoMap();

        Optional.ofNullable(epInstanceMap.get(EPID))
                .ifPresent(ii -> ii.entryWarps().stream()
                        .flatMap(wi -> Optional.ofNullable(npcInfoMap.get(wi.warpNPC())).stream())
                        .flatMap(List::stream)
                        .filter(npcInfo -> npcInfo.instanceID() == InstanceInfo.OVERWORLD_INSTANCE_ID
                                && mapRegionList.stream().anyMatch(mri -> mri.coordinatesIncluded(npcInfo.x(), npcInfo.y())))
                        .findFirst()
                        .ifPresent(npcInfo -> {
                            if (childMap.size() > SOURCE_LIMIT)
                                return;

                            MapRegionInfo mri = mapRegionList.stream()
                                    .filter(mri2 -> mri2.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                                    .findFirst()
                                    .orElseThrow();

                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            mapTooltipBox.addLocations(ii.id(), mri);
                            mapTooltipBox.addMarker(npcInfo.x(), npcInfo.y());

                            childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                        }));
    }

    private void addMobLocationMaps(int type, boolean showMobDropLabel) {
        var mobInstanceRegionGroupedMap = controller.getStaticDataStore().getMobInstanceRegionGroupedMap();

        Optional.ofNullable(mobInstanceRegionGroupedMap.get(type))
                .ifPresent(mobInstanceRegionGroup -> mobInstanceRegionGroup.forEach((instanceID, withinInstanceMobs) ->
                        withinInstanceMobs.forEach((mri, mobInfoList) -> {
                            if (childMap.size() > SOURCE_LIMIT)
                                return;

                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            if (showMobDropLabel && !mobInfoList.isEmpty()) {
                                MobTypeInfo mobTypeInfo = mobInfoList.get(0).mobTypeInfo();
                                mapTooltipBox.addLabel(
                                        String.format("Mob Drop: %s (%d)", mobTypeInfo.name(), mobTypeInfo.type()));
                            }
                            mapTooltipBox.addLocations(instanceID, mri);
                            mobInfoList.forEach(mi -> mapTooltipBox.addMarker(mi.x(), mi.y()));

                            childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                        })));
    }

    private void addMobAndEventCrateMaps(int id) {
        var crateMap = controller.getDrops().getCrates();
        var referenceMap = controller.getDrops().getReferenceMap();
        var mobTypeInfoMap = controller.getStaticDataStore().getMobTypeInfoMap();
        var iconMap = controller.getIconManager().getIconMap();

        List<Data> droppers = Optional.ofNullable(crateMap.get(id)).stream()
                .flatMap(crate -> referenceMap.getOrDefault(crate, Set.of()).stream())
                .filter(d -> d instanceof CrateDropType)
                .flatMap(crateDropType -> referenceMap.getOrDefault(crateDropType, Set.of()).stream())
                .filter(d -> d instanceof MobDrop)
                .flatMap(mobDrop -> referenceMap.getOrDefault(mobDrop, Set.of()).stream())
                .toList();

        droppers.stream()
                .filter(d -> d instanceof Mob)
                .flatMap(mob -> Optional.ofNullable(mobTypeInfoMap.get(((Mob) mob).getMobID())).stream())
                .forEach(mobTypeInfo -> addMobLocationMaps(mobTypeInfo.type(), true));

        droppers.stream()
                .filter(d -> d instanceof Event)
                .forEach(event -> {
                    if (childMap.size() > SOURCE_LIMIT)
                        return;

                    int eventID = ((Event) event).getEventID();
                    Label eventLabel = new Label("Event: " + eventID);

                    StandardImageView iconView = new StandardImageView(iconMap, 64);
                    iconView.setImage(EventType.forType(eventID).iconName());

                    VBox vBox = new VBox(2, eventLabel, iconView);
                    vBox.setAlignment(Pos.CENTER);

                    childMap.put(Objects.hashCode(eventID), vBox);
                });
    }

    private void addMissionRewardMaps(Pair<Integer, Integer> key) {
        var rewardMissionMap = controller.getStaticDataStore().getRewardMissionMap();
        var npcInstanceRegionGroupedMap = controller.getStaticDataStore().getNPCInstanceRegionGroupedMap();

        Optional.ofNullable(rewardMissionMap.get(key))
                .stream()
                .flatMap(List::stream)
                .forEach(mi -> Optional.ofNullable(npcInstanceRegionGroupedMap.get(mi.npc()))
                        .ifPresent(npcInstanceRegionGroup -> npcInstanceRegionGroup.forEach((instanceID, withinInstanceNPCs) ->
                                withinInstanceNPCs.forEach((mri, npcInfoList) -> {
                                    if (childMap.size() > SOURCE_LIMIT)
                                        return;

                                    MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                    mapTooltipBox.addLabel(String.format("%s: %s - %s",
                                            mi.typeName(), mi.name(), npcInfoList.isEmpty() ?
                                                    "Unknown" :
                                                    npcInfoList.get(0).npcTypeInfo().name()));
                                    mapTooltipBox.addLocations(instanceID, mri);
                                    npcInfoList.forEach(npci -> mapTooltipBox.addMarker(npci.x(), npci.y()));

                                    childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                                }))));
    }

    private void addCodeItemGraphics(Pair<Integer, Integer> key) {
        var codeItemMap = controller.getDrops().getCodeItems();
        var itemReferenceMap = controller.getDrops().getItemReferences();

        codeItemMap.values().stream()
                .filter(codeItem -> codeItem.getItemReferenceIDs().stream()
                        .flatMap(id -> Optional.ofNullable(itemReferenceMap.get(id)).stream())
                        .anyMatch(itemReference -> itemReference.getItemID() == key.getKey()
                                && itemReference.getType() == key.getValue()))
                .forEach(codeItem -> {
                    if (childMap.size() > SOURCE_LIMIT)
                        return;

                    Label codeItemLabel = new Label("Code Item");
                    Label codeLabel = new Label(codeItem.getCode());
                    codeLabel.getStyleClass().add("mid-label");

                    VBox vBox = new VBox(2, codeItemLabel, codeLabel);
                    vBox.setAlignment(Pos.CENTER);

                    childMap.put(Objects.hashCode(codeItem.getCode()), vBox);
                });
    }

    private void addCrateMaps(int id, boolean includeMissionCrates) {
        var key = new Pair<>(id, ItemType.CRATE.getTypeID());

        addCodeItemGraphics(key);
        addVendorMaps(key);
        addEggCrateMaps(id);
        addRacingCrateMaps(id);
        addMobAndEventCrateMaps(id);
        if (includeMissionCrates)
            addMissionRewardMaps(key);
    }

    private void addCrateItemMaps(int id, int type) {
        var itemReferenceMap = controller.getDrops().getItemReferences();
        var referenceMap = controller.getDrops().getReferenceMap();

        itemReferenceMap.values().stream()
                .filter(itemReference -> itemReference.getItemID() == id && itemReference.getType() == type)
                .flatMap(itemReference -> referenceMap.getOrDefault(itemReference, Set.of()).stream())
                .filter(d -> d instanceof ItemSet)
                .flatMap(itemSet -> referenceMap.getOrDefault(itemSet, Set.of()).stream())
                .filter(d -> d instanceof Crate)
                .map(d -> (Crate) d)
                .forEach(crate -> {
                    addCrateMaps(crate.getCrateID(), type == ItemType.CRATE.getTypeID());
                    if (type != ItemType.CRATE.getTypeID())
                        addCrateItemMaps(crate.getCrateID(), ItemType.CRATE.getTypeID());
                });
    }

    private void displayMaps() {
        getChildren().addAll(childMap.values());
        if (childMap.size() > TILE_LIMIT) {
            getChildren().subList(TILE_LIMIT - 1, childMap.size()).clear();

            String countString = (childMap.size() > SOURCE_LIMIT) ?
                    "" :
                    String.valueOf(childMap.size() - TILE_LIMIT + 1);
            Label plusLabel = new Label("+" + countString + " more");
            plusLabel.getStyleClass().add("big-label");
            getChildren().add(plusLabel);
        }
    }

    protected void arrangeCrateMaps(int id) {
        addCrateMaps(id, true);
        // do this for crates inside crates
        addCrateItemMaps(id, ItemType.CRATE.getTypeID());
        displayMaps();
    }

    protected void arrangeItemMaps(int id, int type) {
        var key = new Pair<>(id ,type);

        addCodeItemGraphics(key);
        addVendorMaps(key);
        addMissionRewardMaps(key);
        addCrateItemMaps(id, type);
        displayMaps();
    }

    protected void arrangeMobLocationMaps(int type) {
        addMobLocationMaps(type, false);
        displayMaps();
    }

    protected void arrangeRacingLocationMaps(int EPID) {
        addRacingLocationMaps(EPID);
        displayMaps();
    }

    protected void clearMaps() {
        childMap.clear();
        getChildren().clear();
    }
}
