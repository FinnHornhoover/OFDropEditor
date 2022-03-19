package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ReferenceListComponent extends VBox {
    private final ListProperty<ReferenceTrailComponent> referenceTrails;
    private final ObjectProperty<Data> originData;
    private final MainController controller;
    private final double boxSpacing;
    private final double imageWidth;

    public ReferenceListComponent(double boxSpacing,
                                  double imageWidth,
                                  MainController controller,
                                  Data data) {

        referenceTrails = new SimpleListProperty<>(FXCollections.observableArrayList());
        originData = new SimpleObjectProperty<>(data);

        this.controller = controller;
        this.boxSpacing = boxSpacing;
        this.imageWidth = imageWidth;

        recurseTrails(data);
        setSpacing(boxSpacing);
        getChildren().addAll(referenceTrails);
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

    public void constructView() {
        referenceTrails.forEach(ReferenceTrailComponent::constructView);
    }

    public void destroyView() {
        referenceTrails.forEach(ReferenceTrailComponent::destroyView);
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

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getImageWidth() {
        return imageWidth;
    }


}
