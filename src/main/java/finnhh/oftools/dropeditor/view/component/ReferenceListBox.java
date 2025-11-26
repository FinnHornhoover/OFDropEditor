package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.EventType;
import finnhh.oftools.dropeditor.model.ItemType;
import finnhh.oftools.dropeditor.model.data.CodeItem;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Event;
import finnhh.oftools.dropeditor.model.data.ItemReference;
import finnhh.oftools.dropeditor.model.data.Mob;
import finnhh.oftools.dropeditor.model.data.Racing;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReferenceListBox extends ListView<ReferenceTrailBox> {
    private final ListProperty<ReferenceTrailBox> referenceTrails;
    private final ObjectProperty<Data> originData;
    private final StringProperty referenceString;

    private final MainController controller;
    private final double imageWidth;

    public ReferenceListBox(double imageWidth, MainController controller, Data data) {
        referenceTrails = new SimpleListProperty<>(FXCollections.observableArrayList());
        originData = new SimpleObjectProperty<>(data);
        referenceString = new SimpleStringProperty("");

        this.controller = controller;
        this.imageWidth = imageWidth;

        recurseTrails(data);
        // we can exclude reference leaf data if it's not useful down the line
        constructReferenceString(true);

        setOrientation(Orientation.HORIZONTAL);
        setEditable(false);
        setFocusTraversable(false);
        setOnMouseClicked(event -> {
            if (getParent() instanceof TableCell<?,?> tableCell)
                tableCell.getTableView().getSelectionModel().select(tableCell.getTableRow().getIndex());
        });
        setSelectionModel(new NoSelectionModel<>());
        setPrefHeight(referenceTrails.stream()
                .map(ReferenceTrailBox::getApproximateHeight)
                .reduce(0.0, Double::max) + 22.0);
        setCellFactory(cfData -> new ListCell<>() {
            @Override
            protected void updateItem(ReferenceTrailBox referenceTrailBox, boolean empty) {
                super.updateItem(referenceTrailBox, empty);

                Optional.ofNullable(getItem()).ifPresent(ReferenceTrailBox::destroyView);

                if (!empty && Objects.nonNull(referenceTrailBox)) {
                    referenceTrailBox.constructView();
                    setGraphic(referenceTrailBox);
                } else {
                    setGraphic(null);
                }
            }
        });
        getItems().addAll(referenceTrails);
    }

    private void recurseTrails(Data data) {
        int prevSize = 0;
        var trailList = List.of(new ReferenceTrailBox(imageWidth, controller, data));
        int sum = 1;

        while (sum > prevSize) {
            prevSize = sum;

            trailList = trailList.stream()
                    .flatMap(rtc -> Optional.ofNullable(controller.getDrops().getReferenceMap().get(rtc.getLastData()))
                            .map(s -> s.stream().map(d -> new ReferenceTrailBox(rtc, d)))
                            .orElse(Stream.of(rtc)))
                    .toList();

            sum = trailList.stream()
                    .map(rtc -> rtc.getDataList().size())
                    .reduce(0, Integer::sum);
        }

        referenceTrails.addAll(trailList);
    }

    private List<String> getDataRecordString(Data data) {
        var mobTypeInfoMap = controller.getStaticDataStore().getMobTypeInfoMap();
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();
        var epInstanceMap = controller.getStaticDataStore().getEPInstanceMap();
        List<String> infoStrings = new ArrayList<>();

        if (data instanceof CodeItem ci) {
            // we only need to reference the actual code
            infoStrings.add(ci.getCode());

        } else if (data instanceof Crate c) {
            // might need to reference the actual crate level etc.
            var key = new Pair<>(c.getCrateID(), ItemType.CRATE.getTypeID());

            Optional.ofNullable(itemInfoMap.get(key))
                    .map(Object::toString)
                    .ifPresent(infoStrings::add);

        } else if (data instanceof Event e) {
            // event name is enough but we have the type
            EventType eventType = EventType.forType(e.getEventID());

            if (eventType != EventType.NO_EVENT) {
                infoStrings.add(eventType.toString());
            }

        } else if (data instanceof ItemReference ir) {
            // we definitely need the item details
            var key = new Pair<>(ir.getItemID(), ir.getType());

            Optional.ofNullable(itemInfoMap.get(key))
                    .map(Object::toString)
                    .ifPresent(infoStrings::add);

        } else if (data instanceof Mob m) {
            // mob name and level is nice to be able to search
            Optional.ofNullable(mobTypeInfoMap.get(m.getMobID()))
                    .map(Object::toString)
                    .ifPresent(infoStrings::add);

        } else if (data instanceof Racing r) {
            // searching by instance name is useful for racing edits
            Optional.ofNullable(epInstanceMap.get(r.getEPID()))
                    .map(Object::toString)
                    .ifPresent(infoStrings::add);

        } else {
            // fallback, at least let the user search the ID
            infoStrings.add(data.toString());
        }

        return infoStrings;
    }

    private void constructReferenceString(boolean includeReferenceLeafs) {
        if (includeReferenceLeafs) {
            referenceString.set(
                Stream.concat(
                    getDataRecordString(getOriginData()).stream(),
                    getReferenceTrails().stream().flatMap(rt -> getDataRecordString(rt.getLastData()).stream()))
                .collect(Collectors.joining(", "))
            );
        } else {
            referenceString.set(
                getDataRecordString(getOriginData()).stream().collect(Collectors.joining(", "))
            );
        }
    }

    public Data getOriginData() {
        return originData.get();
    }

    public ReadOnlyObjectProperty<Data> originDataProperty() {
        return originData;
    }

    public ObservableList<ReferenceTrailBox> getReferenceTrails() {
        return referenceTrails.get();
    }

    public ReadOnlyListProperty<ReferenceTrailBox> referenceTrailsProperty() {
        return referenceTrails;
    }

    public double getImageWidth() {
        return imageWidth;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": " + referenceString.get();
    }
}
