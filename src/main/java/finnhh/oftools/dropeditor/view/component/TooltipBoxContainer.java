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

public class TooltipBoxContainer extends TilePane {
    protected final int tileLimit;
    protected final int itemLimit;

    protected final MainController controller;

    private final LinkedHashMap<Integer, Node> childMap;

    public TooltipBoxContainer(int tileLimit, int itemLimit, MainController controller) {
        this.tileLimit = tileLimit;
        this.itemLimit = itemLimit;
        this.controller = controller;

        childMap = new LinkedHashMap<>();

        setVgap(2);
        setHgap(2);
        setAlignment(Pos.BOTTOM_CENTER);
        setPrefColumns((int) Math.ceil(Math.sqrt(tileLimit)));
        setPrefHeight(0);
    }

    private void addVendorSourceMaps(Pair<Integer, Integer> key) {
        var vendorItemMap = controller.getStaticDataStore().getVendorItemMap();
        var npcInstanceRegionGroupedMap = controller.getStaticDataStore().getNPCInstanceRegionGroupedMap();

        Optional.ofNullable(vendorItemMap.get(key))
                .stream()
                .flatMap(List::stream)
                .forEach(vii -> Optional.ofNullable(npcInstanceRegionGroupedMap.get(vii.npc()))
                        .ifPresent(npcInstanceRegionGroup -> npcInstanceRegionGroup.forEach((instanceID, withinInstanceNPCs) ->
                                withinInstanceNPCs.forEach((mri, npcInfoList) -> {
                                    if (childMap.size() > itemLimit)
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

    private void addEggCrateSourceMaps(int id) {
        var eggInstanceRegionGroupedMap = controller.getStaticDataStore().getEggInstanceRegionGroupedMap();

        Optional.ofNullable(eggInstanceRegionGroupedMap.get(id))
                .ifPresent(eggInstanceRegionGroup -> eggInstanceRegionGroup.forEach((instanceID, withinInstanceEggs) ->
                        withinInstanceEggs.forEach((mri, eggInfoList) -> {
                            if (childMap.size() > itemLimit)
                                return;

                            MapTooltipBox mapTooltipBox = new MapTooltipBox(controller);

                            mapTooltipBox.addLabel("Egg Find");
                            mapTooltipBox.addLocations(instanceID, mri);
                            eggInfoList.forEach(ei -> mapTooltipBox.addMarker(ei.x(), ei.y()));

                            childMap.put(mapTooltipBox.hashCode(), mapTooltipBox);
                        })));
    }

    private void addRacingCrateSourceMaps(int id) {
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
                                if (childMap.size() > itemLimit)
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
                            if (childMap.size() > itemLimit)
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
                            if (childMap.size() > itemLimit)
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

    private void addMobAndEventCrateSourceMaps(int id) {
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
                    if (childMap.size() > itemLimit)
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

    private void addMissionRewardSourceMaps(Pair<Integer, Integer> key) {
        var rewardMissionMap = controller.getStaticDataStore().getRewardMissionMap();
        var npcInstanceRegionGroupedMap = controller.getStaticDataStore().getNPCInstanceRegionGroupedMap();

        Optional.ofNullable(rewardMissionMap.get(key))
                .stream()
                .flatMap(List::stream)
                .forEach(mi -> Optional.ofNullable(npcInstanceRegionGroupedMap.get(mi.npc()))
                        .ifPresent(npcInstanceRegionGroup -> npcInstanceRegionGroup.forEach((instanceID, withinInstanceNPCs) ->
                                withinInstanceNPCs.forEach((mri, npcInfoList) -> {
                                    if (childMap.size() > itemLimit)
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

    private void addCodeItemSourceGraphics(Pair<Integer, Integer> key) {
        var codeItemMap = controller.getDrops().getCodeItems();
        var itemReferenceMap = controller.getDrops().getItemReferences();

        codeItemMap.values().stream()
                .filter(codeItem -> codeItem.getItemReferenceIDs().stream()
                        .flatMap(id -> Optional.ofNullable(itemReferenceMap.get(id)).stream())
                        .anyMatch(itemReference -> itemReference.getItemID() == key.getKey()
                                && itemReference.getType() == key.getValue()))
                .forEach(codeItem -> {
                    if (childMap.size() > itemLimit)
                        return;

                    Label codeItemLabel = new Label("Code Item");
                    Label codeLabel = new Label(codeItem.getCode());
                    codeLabel.getStyleClass().add("mid-label");

                    VBox vBox = new VBox(2, codeItemLabel, codeLabel);
                    vBox.setAlignment(Pos.CENTER);

                    childMap.put(Objects.hashCode(codeItem.getCode()), vBox);
                });
    }

    private void addItemContentGraphics(int crateID) {
        var crateMap = controller.getDrops().getCrates();
        var itemSetMap = controller.getDrops().getItemSets();
        var itemReferenceMap = controller.getDrops().getItemReferences();
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        Optional.ofNullable(crateMap.get(crateID))
                .map(Crate::getItemSetID)
                .map(itemSetMap::get)
                .stream()
                .flatMap(is -> is.getItemReferenceIDs().stream())
                .flatMap(id -> Optional.ofNullable(itemReferenceMap.get(id)).stream())
                .flatMap(ir -> Optional.ofNullable(itemInfoMap.get(new Pair<>(ir.getItemID(), ir.getType()))).stream())
                .forEach(itemInfo -> {
                    if (childMap.size() > itemLimit)
                        return;

                    Label nameLabel = new Label(itemInfo.name());
                    StandardImageView iconView = new StandardImageView(controller.getIconManager().getIconMap(), 64);
                    iconView.setImage(itemInfo.iconName());

                    VBox vBox = new VBox(2, nameLabel, iconView);
                    vBox.setAlignment(Pos.CENTER);

                    childMap.put(Objects.hashCode(itemInfo), vBox);
                });
    }

    private void addCrateSourceMaps(int id, boolean includeMissionCrates) {
        var key = new Pair<>(id, ItemType.CRATE.getTypeID());

        addCodeItemSourceGraphics(key);
        addVendorSourceMaps(key);
        addEggCrateSourceMaps(id);
        addRacingCrateSourceMaps(id);
        addMobAndEventCrateSourceMaps(id);
        if (includeMissionCrates)
            addMissionRewardSourceMaps(key);
    }

    private void addCrateItemSourceMaps(int id, int type) {
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
                    addCrateSourceMaps(crate.getCrateID(), type == ItemType.CRATE.getTypeID());
                    if (type != ItemType.CRATE.getTypeID())
                        addCrateItemSourceMaps(crate.getCrateID(), ItemType.CRATE.getTypeID());
                });
    }

    private void displayGraphics() {
        getChildren().addAll(childMap.values());
        if (childMap.size() > tileLimit) {
            getChildren().subList(tileLimit - 1, childMap.size()).clear();

            String countString = (childMap.size() > itemLimit) ?
                    "" :
                    String.valueOf(childMap.size() - tileLimit + 1);
            Label plusLabel = new Label("+" + countString + " more");
            plusLabel.getStyleClass().add("big-label");
            getChildren().add(plusLabel);
        }
    }

    public void arrangeCrateSourceMaps(int id) {
        addCrateSourceMaps(id, true);
        // do this for crates inside crates
        addCrateItemSourceMaps(id, ItemType.CRATE.getTypeID());
        displayGraphics();
    }

    public void arrangeItemSourceMaps(int id, int type) {
        var key = new Pair<>(id ,type);

        addCodeItemSourceGraphics(key);
        addVendorSourceMaps(key);
        addMissionRewardSourceMaps(key);
        addCrateItemSourceMaps(id, type);
        displayGraphics();
    }

    public void arrangeItemContentMaps(int crateID) {
        addItemContentGraphics(crateID);
        displayGraphics();
    }

    public void arrangeMobLocationMaps(int type) {
        addMobLocationMaps(type, false);
        displayGraphics();
    }

    public void arrangeRacingLocationMaps(int EPID) {
        addRacingLocationMaps(EPID);
        displayGraphics();
    }

    public void clearGraphics() {
        childMap.clear();
        getChildren().clear();
    }
}
