package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.FilterChoice;
import finnhh.oftools.dropeditor.model.ItemInfo;
import finnhh.oftools.dropeditor.model.NanoInfo;
import finnhh.oftools.dropeditor.model.NanoPowerInfo;
import finnhh.oftools.dropeditor.model.data.Crate;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.NanoCapsule;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NanoCapsuleComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<NanoCapsule> nanoCapsule;

    private final MainController controller;

    private final Label nanoNameLabel;
    private final ImageView nanoIconView;
    private final Label nanoTypeLabel;
    private final VBox nanoVBox;

    private final VBox nanoPowerVBox;
    private final NanoCapsuleBox nanoCapsuleBox;
    private final HBox contentHBox;

    private final Label idLabel;
    private final Button removeButton;
    private final HBox idHBox;

    private final EventHandler<MouseEvent> removeClickHandler;
    private final EventHandler<MouseEvent> capsuleRemoveClickHandler;

    public NanoCapsuleComponent(MainController controller, ListView<Data> listView) {
        nanoCapsule = new SimpleObjectProperty<>();

        this.controller = controller;

        nanoNameLabel = new Label();
        nanoNameLabel.setTextAlignment(TextAlignment.CENTER);
        nanoNameLabel.setWrapText(true);

        nanoIconView = new ImageView();
        nanoIconView.setFitWidth(64);
        nanoIconView.setFitHeight(64);
        nanoIconView.setPreserveRatio(true);
        nanoIconView.setCache(true);

        nanoTypeLabel = new Label();
        nanoTypeLabel.setTextAlignment(TextAlignment.CENTER);
        nanoTypeLabel.setWrapText(true);

        nanoVBox = new VBox(2, nanoNameLabel, nanoIconView, nanoTypeLabel);
        nanoVBox.setAlignment(Pos.CENTER);
        nanoVBox.getStyleClass().add("bordered-pane");
        nanoVBox.setMinWidth(160.0);
        nanoVBox.setMaxWidth(160.0);

        nanoPowerVBox = new VBox(10);
        nanoPowerVBox.setAlignment(Pos.CENTER_LEFT);

        nanoCapsuleBox = new NanoCapsuleBox(this.controller);
        nanoCapsuleBox.getStyleClass().add("bordered-pane");
        nanoCapsuleBox.setMinWidth(160.0);
        nanoCapsuleBox.setMaxWidth(160.0);

        contentHBox = new HBox(2, nanoVBox, nanoPowerVBox, nanoCapsuleBox);
        contentHBox.getStyleClass().add("bordered-pane");
        HBox.setHgrow(nanoPowerVBox, Priority.ALWAYS);

        idLabel = new Label();
        idLabel.getStyleClass().add("id-label");

        removeButton = new Button("-");
        removeButton.setMinWidth(USE_COMPUTED_SIZE);
        removeButton.getStyleClass().addAll("remove-button", "slim-button");

        idHBox = new HBox(idLabel, removeButton);

        setSpacing(2);
        setAlignment(Pos.CENTER_LEFT);
        getChildren().addAll(idHBox, contentHBox);

        removeClickHandler = event -> {
            this.controller.getDrops().remove(nanoCapsule.get());
            this.controller.getDrops().getReferenceMap().values().forEach(set -> set.remove(nanoCapsule.get()));
            listView.getItems().remove(nanoCapsule.get());
        };
        capsuleRemoveClickHandler = event -> {
            if (nanoCapsule.isNotNull().get()) {
                nanoCapsule.get().setCrateID(Crate.INT_CRATE_PLACEHOLDER_ID);
                nanoCapsuleBox.setObservable(null);
            }
        };
        nanoCapsuleBox.getIdLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, event ->
                this.controller.showSelectCapsuleMenuForResult().ifPresent(data -> {
                    if (nanoCapsule.isNotNull().get()) {
                        int crateID = ((NanoCapsule) data).getCrateID();
                        nanoCapsule.get().setCrateID(crateID);
                        nanoCapsuleBox.setObservable(controller.getStaticDataStore().getItemInfoMap()
                                .get(new Pair<>(crateID, 9)));
                    }
                }));

        idLabel.setText(getObservableClass().getSimpleName() + ": null");
        contentHBox.setDisable(true);
        setIdDisable(true);

        // both makeEditable and setObservable sets the observable, just use a listener here
        nanoCapsule.addListener((o, oldVal, newVal) -> {
            if (Objects.isNull(newVal)) {
                idLabel.setText(getObservableClass().getSimpleName() + ": null");
                contentHBox.setDisable(true);
                setIdDisable(true);
            } else {
                idLabel.setText(newVal.getIdBinding().getValueSafe());
                contentHBox.setDisable(false);
                setIdDisable(newVal.isMalformed());
            }
        });
    }

    @Override
    public Class<NanoCapsule> getObservableClass() {
        return NanoCapsule.class;
    }

    @Override
    public ReadOnlyObjectProperty<NanoCapsule> getObservable() {
        return nanoCapsule;
    }

    @Override
    public void setObservable(Data data) {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
        nanoCapsuleBox.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, capsuleRemoveClickHandler);

        nanoCapsule.set((NanoCapsule) data);

        nanoPowerVBox.getChildren().clear();

        if (nanoCapsule.isNull().get()) {
            nanoCapsuleBox.setObservable(null);
        } else {
            var iconMap = controller.getIconManager().getIconMap();

            NanoInfo nanoInfo = controller.getStaticDataStore().getNanoInfoMap().get(nanoCapsule.get().getNano());

            String name = Objects.isNull(nanoInfo) ?
                    "UNKNOWN" :
                    nanoInfo.name();
            String type = Objects.isNull(nanoInfo) ?
                    "Unknown" :
                    nanoInfo.type();
            byte[] defaultIcon = iconMap.get("unknown");
            byte[] icon = Objects.isNull(nanoInfo) ?
                    defaultIcon :
                    iconMap.getOrDefault(nanoInfo.iconName(), defaultIcon);
            List<NanoPowerInfo> nanoPowers = Objects.isNull(nanoInfo) ?
                    List.of() :
                    nanoInfo.powers();

            nanoCapsuleBox.setObservable(controller.getStaticDataStore().getItemInfoMap()
                    .get(new Pair<>(nanoCapsule.get().getCrateID(), 9)));
            nanoNameLabel.setText(name);
            nanoIconView.setImage(new Image(new ByteArrayInputStream(icon)));
            nanoTypeLabel.setText(type);
            nanoPowers.stream()
                    .map(npi -> {
                        NanoPowerBox nanoPowerBox = new NanoPowerBox(controller);
                        nanoPowerBox.setObservable(npi);
                        return nanoPowerBox;
                    })
                    .forEach(nanoPowerVBox.getChildren()::add);
        }

        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
        nanoCapsuleBox.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, capsuleRemoveClickHandler);
    }

    @Override
    public Set<FilterChoice> getSearchableValues() {
        var nanoInfoMap = controller.getStaticDataStore().getNanoInfoMap();
        var itemInfoMap = controller.getStaticDataStore().getItemInfoMap();

        Set<FilterChoice> allValues = new HashSet<>(getSearchableValuesForObservable());

        NanoPowerBox prototype = nanoPowerVBox.getChildren().isEmpty() ?
                new NanoPowerBox(controller) :
                (NanoPowerBox) nanoPowerVBox.getChildren().get(0);

        allValues.addAll(getNestedSearchableValues(
                ObservableComponent.getSearchableValuesFor(NanoInfo.class),
                op -> op.map(o -> (NanoCapsule) o)
                        .map(nc -> nanoInfoMap.get(nc.getNano()))
                        .stream().toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                prototype.getSearchableValues(),
                op -> op.map(o -> (NanoCapsule) o)
                        .map(nc -> nanoInfoMap.get(nc.getNano()))
                        .stream()
                        .flatMap(ni -> ni.powers().stream())
                        .toList()
        ));

        allValues.addAll(getNestedSearchableValues(
                nanoCapsuleBox.getSearchableValues(),
                op -> op.map(o -> (NanoCapsule) o)
                        .map(NanoCapsule::getCrateID)
                        .stream()
                        .filter(id -> itemInfoMap.containsKey(new Pair<>(id, 9)))
                        .map(id -> itemInfoMap.get(new Pair<>(id, 9)))
                        .toList()
        ));

        return allValues;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    public NanoCapsule getNanoCapsule() {
        return nanoCapsule.get();
    }

    public ReadOnlyObjectProperty<NanoCapsule> nanoCapsuleProperty() {
        return nanoCapsule;
    }

    public Label getNanoNameLabel() {
        return nanoNameLabel;
    }

    public ImageView getNanoIconView() {
        return nanoIconView;
    }

    public Label getNanoTypeLabel() {
        return nanoTypeLabel;
    }

    public VBox getNanoVBox() {
        return nanoVBox;
    }

    public VBox getNanoPowerVBox() {
        return nanoPowerVBox;
    }

    public NanoCapsuleBox getNanoCapsuleBox() {
        return nanoCapsuleBox;
    }

    public HBox getContentHBox() {
        return contentHBox;
    }

    public HBox getIdHBox() {
        return idHBox;
    }

    public static class NanoPowerBox extends BorderPane implements ObservableComponent<NanoPowerInfo> {
        private final ObjectProperty<NanoPowerInfo> nanoPowerInfo;

        private final MainController controller;

        private final ImageView nanoPowerIconView;
        private final Label nanoPowerNameLabel;
        private final Label nanoPowerCommentLabel;

        public NanoPowerBox(MainController controller) {
            nanoPowerInfo = new SimpleObjectProperty<>();

            this.controller = controller;

            nanoPowerIconView = new ImageView();
            nanoPowerIconView.setFitWidth(32);
            nanoPowerIconView.setFitHeight(32);
            nanoPowerIconView.setPreserveRatio(true);
            nanoPowerIconView.setCache(true);

            nanoPowerNameLabel = new Label();
            nanoPowerNameLabel.setTextAlignment(TextAlignment.CENTER);
            nanoPowerNameLabel.setWrapText(true);

            nanoPowerCommentLabel = new Label();
            nanoPowerCommentLabel.setTextAlignment(TextAlignment.CENTER);
            nanoPowerCommentLabel.setWrapText(true);

            setLeft(nanoPowerIconView);
            setCenter(nanoPowerNameLabel);
            setBottom(nanoPowerCommentLabel);
            setAlignment(nanoPowerNameLabel, Pos.CENTER_LEFT);
            getStyleClass().add("bordered-pane");
        }

        @Override
        public Class<NanoPowerInfo> getObservableClass() {
            return NanoPowerInfo.class;
        }

        @Override
        public ReadOnlyObjectProperty<NanoPowerInfo> getObservable() {
            return nanoPowerInfo;
        }

        @Override
        public void setObservable(NanoPowerInfo data) {
            var iconMap = controller.getIconManager().getIconMap();

            nanoPowerInfo.set(data);

            String name = nanoPowerInfo.isNull().get() ?
                    "UNKNOWN" :
                    String.format("%s (%s)", nanoPowerInfo.get().name(), nanoPowerInfo.get().type());
            String comment = nanoPowerInfo.isNull().get() ?
                    "Unknown" :
                    nanoPowerInfo.get().comment();
            byte[] defaultIcon = iconMap.get("unknown");
            byte[] icon = nanoPowerInfo.isNull().get() ?
                    defaultIcon :
                    iconMap.getOrDefault(nanoPowerInfo.get().iconName(), defaultIcon);

            nanoPowerIconView.setImage(new Image(new ByteArrayInputStream(icon)));
            nanoPowerNameLabel.setText(name);
            nanoPowerCommentLabel.setText(comment);
        }

        public NanoPowerInfo getNanoPowerInfo() {
            return nanoPowerInfo.get();
        }

        public ReadOnlyObjectProperty<NanoPowerInfo> nanoPowerInfoProperty() {
            return nanoPowerInfo;
        }

        public ImageView getNanoPowerIconView() {
            return nanoPowerIconView;
        }

        public Label getNanoPowerNameLabel() {
            return nanoPowerNameLabel;
        }

        public Label getNanoPowerCommentLabel() {
            return nanoPowerCommentLabel;
        }
    }

    public static class NanoCapsuleBox extends VBox implements ObservableComponent<ItemInfo> {
        private final ObjectProperty<ItemInfo> capsuleItemInfo;

        private final MainController controller;

        private final Label nameLabel;
        private final ImageView iconView;
        private final VBox contentVBox;

        private final Label idLabel;
        private final Button removeButton;
        private final HBox idHBox;

        public NanoCapsuleBox(MainController controller) {
            capsuleItemInfo = new SimpleObjectProperty<>();

            this.controller = controller;

            nameLabel = new Label();
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            nameLabel.setWrapText(true);

            iconView = new ImageView();
            iconView.setFitWidth(64);
            iconView.setFitHeight(64);
            iconView.setPreserveRatio(true);
            iconView.setCache(true);

            contentVBox = new VBox(2, nameLabel, iconView);
            contentVBox.setAlignment(Pos.CENTER);
            contentVBox.getStyleClass().add("bordered-pane");
            setVgrow(contentVBox, Priority.ALWAYS);

            idLabel = new Label();
            idLabel.getStyleClass().add("id-label");

            removeButton = new Button("-");
            removeButton.setMinWidth(USE_COMPUTED_SIZE);
            removeButton.getStyleClass().addAll("remove-button", "slim-button");

            idHBox = new HBox(idLabel, removeButton);

            setSpacing(2);
            setAlignment(Pos.CENTER_LEFT);
            getChildren().addAll(idHBox, contentVBox);

            idLabel.setText("Capsule: null");
            contentVBox.setDisable(true);

            capsuleItemInfo.addListener((o, oldVal, newVal) -> {
                if (Objects.isNull(newVal)) {
                    idLabel.setText("Capsule: null");
                    contentVBox.setDisable(true);
                } else {
                    idLabel.setText("Capsule: " + newVal.id());
                    contentVBox.setDisable(false);
                }
            });
        }


        @Override
        public Class<ItemInfo> getObservableClass() {
            return ItemInfo.class;
        }

        @Override
        public ReadOnlyObjectProperty<ItemInfo> getObservable() {
            return capsuleItemInfo;
        }

        @Override
        public void setObservable(ItemInfo data) {
            var iconMap = controller.getIconManager().getIconMap();

            capsuleItemInfo.set(data);

            String name = capsuleItemInfo.isNull().get() ?
                    "UNKNOWN" :
                    capsuleItemInfo.get().name();
            byte[] defaultIcon = iconMap.get("unknown");
            byte[] icon = capsuleItemInfo.isNull().get() ?
                    defaultIcon :
                    iconMap.getOrDefault(capsuleItemInfo.get().iconName(), defaultIcon);

            nameLabel.setText(name);
            iconView.setImage(new Image(new ByteArrayInputStream(icon)));
        }

        public ItemInfo getCapsuleItemInfo() {
            return capsuleItemInfo.get();
        }

        public ReadOnlyObjectProperty<ItemInfo> capsuleItemInfoProperty() {
            return capsuleItemInfo;
        }

        public Label getNameLabel() {
            return nameLabel;
        }

        public ImageView getIconView() {
            return iconView;
        }

        public VBox getContentVBox() {
            return contentVBox;
        }

        public Label getIdLabel() {
            return idLabel;
        }

        public Button getRemoveButton() {
            return removeButton;
        }

        public HBox getIdHBox() {
            return idHBox;
        }
    }
}
