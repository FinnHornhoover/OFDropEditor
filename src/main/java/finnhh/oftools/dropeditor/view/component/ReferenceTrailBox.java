package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.data.Data;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.io.ByteArrayInputStream;

public class ReferenceTrailBox extends VBox {
    private final ListProperty<Data> dataList;
    private final MainController controller;
    private final double imageWidth;

    public ReferenceTrailBox(double imageWidth, MainController controller, Data data) {
        dataList = new SimpleListProperty<>(FXCollections.observableArrayList(data));

        this.controller = controller;
        this.imageWidth = imageWidth;

        setSpacing(2);
        setAlignment(Pos.TOP_CENTER);
    }

    public ReferenceTrailBox(ReferenceTrailBox other, Data data) {
        this.dataList = new SimpleListProperty<>(FXCollections.observableArrayList(other.dataList));
        this.dataList.add(data);

        this.controller = other.controller;
        this.imageWidth = other.imageWidth;

        setSpacing(2);
        setAlignment(Pos.TOP_CENTER);
    }

    public void constructView() {
        for (int i = 1; i < dataList.size(); i++) {
            if (i > 1) {
                getChildren().add(new ImageView(new Image(new ByteArrayInputStream(
                        controller.getIconManager().getIconMap().get("down")))));
            }
            getChildren().add(new ImageSummaryBox(imageWidth, controller, dataList.get(i)));
        }
    }

    public void destroyView() {
        getChildren().clear();
    }

    public double getApproximateHeight() {
        return imageWidth + Math.max(0, dataList.size() - 2) * (20.0 + imageWidth);
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
