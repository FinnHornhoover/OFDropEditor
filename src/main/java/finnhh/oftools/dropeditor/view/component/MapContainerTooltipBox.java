package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.InstanceInfo;
import finnhh.oftools.dropeditor.model.MapRegionInfo;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import finnhh.oftools.dropeditor.model.data.*;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.util.Pair;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class MapContainerTooltipBox extends TilePane {
    public static final int TILE_LIMIT = 9;

    protected final MainController controller;

    public MapContainerTooltipBox(MainController controller) {
        this.controller = controller;

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
                                    MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                    mapTooltipBox.addLabel(String.format("Vendor: %s - %d Taros",
                                            npcInfoList.isEmpty() ?
                                                    "Unknown" :
                                                    npcInfoList.get(0).npcTypeInfo().name(),
                                            vii.vendorPrice()));
                                    mapTooltipBox.addLocations(instanceID, mri);
                                    npcInfoList.forEach(npci -> mapTooltipBox.addMarker(npci.x(), npci.y()));

                                    getChildren().add(mapTooltipBox);
                                }))));
    }

    private void addEggMaps(int id) {
        var eggInstanceRegionGroupedMap = controller.getStaticDataStore().getEggInstanceRegionGroupedMap();

        Optional.ofNullable(eggInstanceRegionGroupedMap.get(id))
                .ifPresent(eggInstanceRegionGroup -> eggInstanceRegionGroup.forEach((instanceID, withinInstanceEggs) ->
                        withinInstanceEggs.forEach((mri, eggInfoList) -> {
                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            mapTooltipBox.addLocations(instanceID, mri);
                            eggInfoList.forEach(ei -> mapTooltipBox.addMarker(ei.x(), ei.y()));

                            getChildren().add(mapTooltipBox);
                        })));
    }

    private void addRacingMaps(int id) {
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
                                MapRegionInfo mri = mapRegionList.stream()
                                        .filter(mri2 -> mri2.coordinatesIncluded(npcInfo.x(), npcInfo.y()))
                                        .findFirst()
                                        .get();

                                MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                mapTooltipBox.addLabel("Racing: "
                                        + r.getRankScores().get(r.getRewards().indexOf(id)) + " Min Score");
                                mapTooltipBox.addLocations(ii.id(), mri);
                                mapTooltipBox.addMarker(npcInfo.x(), npcInfo.y());

                                getChildren().add(mapTooltipBox);
                            });
                });
    }

    private void addMobLocationMaps(int type, boolean showMobDropLabel) {
        var mobInstanceRegionGroupedMap = controller.getStaticDataStore().getMobInstanceRegionGroupedMap();

        Optional.ofNullable(mobInstanceRegionGroupedMap.get(type))
                .ifPresent(mobInstanceRegionGroup -> mobInstanceRegionGroup.forEach((instanceID, withinInstanceMobs) ->
                        withinInstanceMobs.forEach((mri, mobInfoList) -> {
                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            if (showMobDropLabel && !mobInfoList.isEmpty()) {
                                MobTypeInfo mobTypeInfo = mobInfoList.get(0).mobTypeInfo();
                                mapTooltipBox.addLabel(
                                        String.format("Mob Drop: %s (%d)", mobTypeInfo.name(), mobTypeInfo.type()));
                            }
                            mapTooltipBox.addLocations(instanceID, mri);
                            mobInfoList.forEach(mi -> mapTooltipBox.addMarker(mi.x(), mi.y()));

                            getChildren().add(mapTooltipBox);
                        })));
    }

    private void addMobAndEventMaps(int id) {
        var crateMap = controller.getDrops().getCrates();
        var referenceMap = controller.getDrops().getReferenceMap();
        var mobTypeInfoMap = controller.getStaticDataStore().getMobTypeInfoMap();

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
                    Label eventLabel = new Label("Event: " + ((Event) event).getEventID());
                    eventLabel.getStyleClass().add("big-label");
                    getChildren().add(eventLabel);
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
                                    MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                                    mapTooltipBox.addLabel(String.format("%s: %s - %s",
                                            mi.typeName(), mi.name(), npcInfoList.isEmpty() ?
                                                    "Unknown" :
                                                    npcInfoList.get(0).npcTypeInfo().name()));
                                    mapTooltipBox.addLocations(instanceID, mri);
                                    npcInfoList.forEach(npci -> mapTooltipBox.addMarker(npci.x(), npci.y()));

                                    getChildren().add(mapTooltipBox);
                                }))));
    }

    private void addMaps(int id, int type) {
        var key = new Pair<>(id, type);

        addVendorMaps(key);
        addEggMaps(id);
        addRacingMaps(id);
        addMobAndEventMaps(id);
        addMissionRewardMaps(key);
    }

    private void addNestedCrateMaps(int id, int type) {
        var itemReferenceMap = controller.getDrops().getItemReferences();
        var referenceMap = controller.getDrops().getReferenceMap();

        itemReferenceMap.values().stream()
                .filter(itemReference -> itemReference.getItemID() == id && itemReference.getType() == type)
                .flatMap(itemReference -> referenceMap.getOrDefault(itemReference, Set.of()).stream())
                .filter(d -> d instanceof ItemSet)
                .flatMap(itemSet -> referenceMap.getOrDefault(itemSet, Set.of()).stream())
                .filter(d -> d instanceof Crate)
                .map(d -> (Crate) d)
                .forEach(crate -> addMaps(crate.getCrateID(), type));
    }

    protected void arrangeMaps(int id, int type) {
        addMaps(id, type);
        addNestedCrateMaps(id, type);

        int prevSize = getChildren().size();
        if (prevSize > TILE_LIMIT) {
            getChildren().subList(TILE_LIMIT - 1, prevSize).clear();

            Label plusLabel = new Label("+" + (prevSize - TILE_LIMIT + 1) + " more");
            plusLabel.getStyleClass().add("big-label");
            getChildren().add(plusLabel);
        }
    }

    protected void arrangeMobLocationMaps(int type) {
        addMobLocationMaps(type, false);
    }

    protected void clearMaps() {
        getChildren().clear();
    }
}
