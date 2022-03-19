package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.stream.IntStream;

public class ReferenceTrailComponent extends HBox {
    private final ListProperty<Data> dataList;
    private final MainController controller;
    private final double imageWidth;

    public ReferenceTrailComponent(double imageWidth, MainController controller, Data data) {
        dataList = new SimpleListProperty<>(FXCollections.observableArrayList(data));

        this.controller = controller;
        this.imageWidth = imageWidth;

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
    }

    public ReferenceTrailComponent(ReferenceTrailComponent other, Data data) {
        this.dataList = new SimpleListProperty<>(FXCollections.observableArrayList(other.dataList));
        this.dataList.add(data);

        this.controller = other.controller;
        this.imageWidth = other.imageWidth;

        for (int i = 1; i < this.dataList.size(); i++) {
            if (i > 1)
                getChildren().add(new Label(">"));
            getChildren().add(new ImageSummaryComponent(this.imageWidth, this.controller, this.dataList.get(i)));
        }

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
    }

    public ObservableList<Data> getDataList() {
        return dataList.get();
    }

    public ReadOnlyListProperty<Data> dataListProperty() {
        return dataList;
    }

    public double getImageWidth() {
        return imageWidth;
    }

    public Data getOriginData() {
        return dataList.get(0);
    }

    public Data getLastData() {
        return dataList.get(dataList.size() - 1);
    }
}
