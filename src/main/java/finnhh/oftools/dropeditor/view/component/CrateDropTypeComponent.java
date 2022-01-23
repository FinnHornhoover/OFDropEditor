package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.CrateDropType;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CrateDropTypeComponent extends BorderPane implements DataComponent {
    private final ObjectProperty<CrateDropType> crateDropType;

    private final Drops drops;
    private final Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap;
    private final Map<String, byte[]> iconMap;

    private final double boxSpacing;
    private final double boxWidth;
    private final DataComponent parent;

    private final HBox contentHBox;
    private final Label idLabel;

    private final ListChangeListener<Node> childrenListener;
    private final List<ChangeListener<Crate>> valueListeners;

    public CrateDropTypeComponent(double boxSpacing,
                                  double boxWidth,
                                  Drops drops,
                                  Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                                  Map<String, byte[]> iconMap,
                                  DataComponent parent) {
        this.drops = drops;
        crateDropType = new SimpleObjectProperty<>();
        this.itemInfoMap = itemInfoMap;
        this.iconMap = iconMap;

        this.boxSpacing = boxSpacing;
        this.boxWidth = boxWidth;
        this.parent = parent;
        contentHBox = new HBox(boxSpacing);
        contentHBox.setAlignment(Pos.CENTER);
        contentHBox.getStyleClass().add("bordered-pane");

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        setTop(idLabel);
        setCenter(contentHBox);
        setAlignment(idLabel, Pos.TOP_LEFT);

        childrenListener = change -> {
            makeEditable(this.drops);

            ObservableList<Integer> ids = crateDropType.get().getCrateIDs();
            ids.clear();
            ids.addAll(contentHBox.getChildren().stream()
                    .filter(c -> c instanceof TypeVBox)
                    .map(c -> ((TypeVBox) c).getCrate().getCrateID())
                    .toList());

            bindListVariables();
        };
        valueListeners = new ArrayList<>();

        idLabel.setText(CrateDropType.class.getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        crateDropType.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(CrateDropType.class.getSimpleName() + ": null");
                contentHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    private void bindListVariables() {
        var children = contentHBox.getChildren().filtered(c -> c instanceof TypeVBox);

        for (int index = 0; index < children.size(); index++) {
            TypeVBox cvb = (TypeVBox) children.get(index);

            final int finalIndex = index;
            valueListeners.add((o, oldVal, newVal) -> {
                makeEditable(drops);
                crateDropType.get().getCrateIDs().set(finalIndex, newVal.getCrateID());

                TypeVBox current = (TypeVBox) contentHBox.getChildren()
                        .filtered(c -> c instanceof TypeVBox)
                        .get(finalIndex);
                current.setObservable(newVal);
            });
            cvb.crateProperty().addListener(valueListeners.get(index));
        }
    }

    private void unbindListVariables() {
        var children = contentHBox.getChildren().filtered(c -> c instanceof TypeVBox);

        for (int index = 0; index < children.size(); index++) {
            TypeVBox cvb = (TypeVBox) children.get(index);
            cvb.crateProperty().removeListener(valueListeners.get(index));
        }

        valueListeners.clear();
    }

    @Override
    public void setObservable(Data data) {
        crateDropType.set((CrateDropType) data);

        contentHBox.getChildren().removeListener(childrenListener);
        unbindListVariables();
        contentHBox.getChildren().clear();

        if (crateDropType.isNotNull().get()) {
            contentHBox.getChildren().addAll(crateDropType.get()
                    .getCrateIDs().stream()
                    .map(i -> {
                        TypeVBox tvb = new TypeVBox(boxWidth, drops, itemInfoMap, iconMap, this);
                        tvb.setObservable(drops.getCrates().get(i));
                        return tvb;
                    })
                    .toList());

            bindListVariables();
        }

        contentHBox.getChildren().addListener(childrenListener);
    }

    public double getBoxSpacing() {
        return boxSpacing;
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public CrateDropType getCrateDropType() {
        return crateDropType.get();
    }

    public ReadOnlyObjectProperty<CrateDropType> crateDropTypeProperty() {
        return crateDropType;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    @Override
    public ReadOnlyObjectProperty<CrateDropType> getObservable() {
        return crateDropType;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public DataComponent getParentComponent() {
        return parent;
    }

    public static class TypeVBox extends BorderPane implements DataComponent {
        private final ObjectProperty<Crate> crate;
        private final ObjectProperty<ItemInfo> itemInfo;
        private final ObjectProperty<byte[]> icon;

        private final Drops drops;
        private final Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap;
        private final Map<String, byte[]> iconMap;

        private final DataComponent parent;

        private final Label nameLabel;
        private final Label commentLabel;
        private final ImageView iconView;
        private final VBox contentVBox;
        private final Label idLabel;

        public TypeVBox(double width,
                        Drops drops,
                        Map<Pair<Integer, Integer>, ItemInfo> itemInfoMap,
                        Map<String, byte[]> iconMap,
                        DataComponent parent) {

            this.drops = drops;
            this.itemInfoMap = itemInfoMap;
            this.iconMap = iconMap;

            crate = new SimpleObjectProperty<>();
            itemInfo = new SimpleObjectProperty<>();
            icon = new SimpleObjectProperty<>();

            this.parent = parent;
            nameLabel = new Label();
            commentLabel = new Label();

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            contentVBox = new VBox(2, nameLabel, commentLabel, iconView);
            contentVBox.setAlignment(Pos.CENTER);
            contentVBox.setMinWidth(width);
            contentVBox.setMaxWidth(width);
            contentVBox.getStyleClass().add("bordered-pane");

            idLabel = new Label();
            idLabel.getStyleClass().add("id-label");

            setTop(idLabel);
            setCenter(contentVBox);
            setAlignment(idLabel, Pos.TOP_LEFT);

            idLabel.setText(Crate.class.getSimpleName() + ": null");
            contentVBox.setDisable(true);
            setIdDisable(true);

            // TODO placeholder
            idLabel.setOnMouseClicked(event -> setObservable(this.drops.getCrates().get(100)));

            // both makeEditable and setObservable sets the observable, just use a listener here
            crate.addListener((o, oldVal, newVal) -> {
                if (Objects.isNull(newVal)) {
                    idLabel.setText(Crate.class.getSimpleName() + ": null");
                    contentVBox.setDisable(true);
                    setIdDisable(true);
                } else {
                    idLabel.setText(newVal.getIdBinding().getValueSafe());
                    contentVBox.setDisable(false);
                    setIdDisable(newVal.isMalformed());
                }
            });
        }

        @Override
        public ReadOnlyObjectProperty<Crate> getObservable() {
            return crate;
        }

        @Override
        public void setObservable(Data data) {
            crate.set((Crate) data);

            byte[] defaultIcon = iconMap.get("unknown");

            if (crate.isNull().get()) {
                nameLabel.setText("<INVALID>");
                commentLabel.setText("<INVALID>");
                iconView.setImage(new Image(new ByteArrayInputStream(defaultIcon)));

                contentVBox.setDisable(true);
            } else {
                ItemInfo itemInfo = itemInfoMap.get(new Pair<>(crate.get().getCrateID(), 9));

                String name = Objects.isNull(itemInfo) ? "<INVALID>" : itemInfo.name();
                String comment = Objects.isNull(itemInfo) ? "<INVALID>" :  itemInfo.comment();
                byte[] icon = Objects.isNull(itemInfo) ?
                        defaultIcon :
                        iconMap.getOrDefault(itemInfo.iconName(), defaultIcon);

                this.itemInfo.set(itemInfo);
                this.icon.set(icon);

                nameLabel.setText(name);
                commentLabel.setText(comment);
                iconView.setImage(new Image(new ByteArrayInputStream(icon)));

                contentVBox.setDisable(Objects.isNull(itemInfo));
            }
        }

        public Crate getCrate() {
            return crate.get();
        }

        public ReadOnlyObjectProperty<Crate> crateProperty() {
            return crate;
        }

        public ItemInfo getItemInfo() {
            return itemInfo.get();
        }

        public ReadOnlyObjectProperty<ItemInfo> itemInfoProperty() {
            return itemInfo;
        }

        public byte[] getIcon() {
            return icon.get();
        }

        public ReadOnlyObjectProperty<byte[]> iconProperty() {
            return icon;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public Label getCommentLabel() {
            return commentLabel;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public VBox getContentVBox() {
            return contentVBox;
        }

        @Override
        public Label getIdLabel() {
            return idLabel;
        }

        @Override
        public DataComponent getParentComponent() {
            return parent;
        }
    }
}
