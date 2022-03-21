package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ReferenceListComponent extends ListView<ReferenceTrailComponent> {
    private final ListProperty<ReferenceTrailComponent> referenceTrails;
    private final ObjectProperty<Data> originData;

    private final MainController controller;
    private final double imageWidth;

    public ReferenceListComponent(double imageWidth, MainController controller, Data data) {
        referenceTrails = new SimpleListProperty<>(FXCollections.observableArrayList());
        originData = new SimpleObjectProperty<>(data);

        this.controller = controller;
        this.imageWidth = imageWidth;

        recurseTrails(data);

        setOrientation(Orientation.HORIZONTAL);
        setEditable(false);
        setFocusTraversable(false);
        setOnMouseClicked(event -> {
            if (getParent() instanceof TableCell<?,?> tableCell)
                tableCell.getTableView().getSelectionModel().select(tableCell.getTableRow().getIndex());
        });
        setSelectionModel(new NoSelectionModel<>());
        setPrefHeight(referenceTrails.stream()
                .map(ReferenceTrailComponent::getApproximateHeight)
                .reduce(0.0, Double::max) + 22.0);
        setCellFactory(cfData -> new ListCell<>() {
            @Override
            protected void updateItem(ReferenceTrailComponent referenceTrailComponent, boolean empty) {
                super.updateItem(referenceTrailComponent, empty);

                Optional.ofNullable(getItem()).ifPresent(ReferenceTrailComponent::destroyView);

                if (!empty && Objects.nonNull(referenceTrailComponent)) {
                    referenceTrailComponent.constructView();
                    setGraphic(referenceTrailComponent);
                } else {
                    setGraphic(null);
                }
            }
        });
        getItems().addAll(referenceTrails);
    }

    private void recurseTrails(Data data) {
        int prevSize = 0;
        var trailList = List.of(new ReferenceTrailComponent(imageWidth, controller, data));
        int sum = 1;

        while (sum > prevSize) {
            prevSize = sum;

            trailList = trailList.stream()
                    .flatMap(rtc -> Optional.ofNullable(controller.getDrops().getReferenceMap().get(rtc.getLastData()))
                            .map(s -> s.stream().map(d -> new ReferenceTrailComponent(rtc, d)))
                            .orElse(Stream.of(rtc)))
                    .toList();

            sum = trailList.stream()
                    .map(rtc -> rtc.getDataList().size())
                    .reduce(0, Integer::sum);
        }

        referenceTrails.addAll(trailList);
    }

    public Data getOriginData() {
        return originData.get();
    }

    public ReadOnlyObjectProperty<Data> originDataProperty() {
        return originData;
    }

    public ObservableList<ReferenceTrailComponent> getReferenceTrails() {
        return referenceTrails.get();
    }

    public ReadOnlyListProperty<ReferenceTrailComponent> referenceTrailsProperty() {
        return referenceTrails;
    }

    public double getImageWidth() {
        return imageWidth;
    }
}
