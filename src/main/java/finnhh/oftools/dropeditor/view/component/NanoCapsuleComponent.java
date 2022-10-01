package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.*;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class NanoCapsuleComponent extends VBox implements RootDataComponent {
    private final ObjectProperty<NanoCapsule> nanoCapsule;

    private final MainController controller;

    private final Label nanoNameLabel;
    private final StandardImageView nanoIconView;
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
    private final EventHandler<MouseEvent> capsuleIdClickHandler;

    public NanoCapsuleComponent(MainController controller) {
        nanoCapsule = new SimpleObjectProperty<>();

        this.controller = controller;

        nanoNameLabel = new Label();
        nanoNameLabel.setTextAlignment(TextAlignment.CENTER);
        nanoNameLabel.setWrapText(true);

        nanoIconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);

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

        removeClickHandler = event -> onRemoveClick();
        capsuleRemoveClickHandler = event -> makeEdit(() ->
                nanoCapsule.get().setCrateID(Crate.INT_CRATE_PLACEHOLDER_ID));
        capsuleIdClickHandler = event -> this.controller.showSelectCapsuleMenuForResult().ifPresent(d ->
                makeEdit(() -> nanoCapsule.get().setCrateID(((NanoCapsule) d).getCrateID())));
    }

    @Override
    public MainController getController() {
        return controller;
    }

    @Override
    public Label getIdLabel() {
        return idLabel;
    }

    @Override
    public List<Pane> getContentPanes() {
        return List.of(contentHBox);
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
        nanoCapsule.set((NanoCapsule) data);
    }

    @Override
    public void cleanUIState() {
        RootDataComponent.super.cleanUIState();

        nanoPowerVBox.getChildren().clear();

        nanoNameLabel.setText("UNKNOWN");
        nanoIconView.cleanImage();
        nanoTypeLabel.setText("Unknown");
        nanoCapsuleBox.setObservableAndState(null);
    }

    @Override
    public void fillUIState() {
        RootDataComponent.super.fillUIState();

        NanoInfo nanoInfo = controller.getStaticDataStore().getNanoInfoMap().get(nanoCapsule.get().getNano());

        if (Objects.nonNull(nanoInfo)) {
            int crateID = nanoCapsule.get().getCrateID();
            nanoCapsuleBox.setObservableAndState(crateID == Crate.INT_CRATE_PLACEHOLDER_ID ?
                    null :
                    controller.getStaticDataStore().getItemInfoMap().get(new Pair<>(crateID, ItemType.CRATE.getTypeID())));
            nanoNameLabel.setText(nanoInfo.name());
            nanoIconView.setImage(nanoInfo.iconName());
            nanoTypeLabel.setText(nanoInfo.type());
            nanoInfo.powers().stream()
                    .map(npi -> {
                        NanoPowerBox nanoPowerBox = new NanoPowerBox(controller);
                        nanoPowerBox.setObservableAndState(npi);
                        return nanoPowerBox;
                    })
                    .forEach(nanoPowerVBox.getChildren()::add);
        }
    }

    @Override
    public void bindVariablesNullable() {
        removeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
        nanoCapsuleBox.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, capsuleRemoveClickHandler);
        nanoCapsuleBox.getIdLabel().addEventHandler(MouseEvent.MOUSE_CLICKED, capsuleIdClickHandler);
    }

    @Override
    public void unbindVariables() {
        removeButton.removeEventHandler(MouseEvent.MOUSE_CLICKED, removeClickHandler);
        nanoCapsuleBox.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, capsuleRemoveClickHandler);
        nanoCapsuleBox.getIdLabel().removeEventHandler(MouseEvent.MOUSE_CLICKED, capsuleIdClickHandler);
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
                        .filter(id -> itemInfoMap.containsKey(new Pair<>(id, ItemType.CRATE.getTypeID())))
                        .map(id -> itemInfoMap.get(new Pair<>(id, ItemType.CRATE.getTypeID())))
                        .toList()
        ));

        return allValues;
    }

    @Override
    public Button getRemoveButton() {
        return removeButton;
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

    public StandardImageView getNanoIconView() {
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

        private final StandardImageView nanoPowerIconView;
        private final Label nanoPowerNameLabel;
        private final Label nanoPowerCommentLabel;

        public NanoPowerBox(MainController controller) {
            nanoPowerInfo = new SimpleObjectProperty<>();

            this.controller = controller;

            nanoPowerIconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 32);

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
        public MainController getController() {
            return controller;
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
            nanoPowerInfo.set(data);
        }

        @Override
        public void cleanUIState() {
            nanoPowerNameLabel.setText("UNKNOWN");
            nanoPowerCommentLabel.setText("Unknown");
            nanoPowerIconView.cleanImage();
        }

        @Override
        public void fillUIState() {
            nanoPowerNameLabel.setText(String.format("%s (%s)",
                    nanoPowerInfo.get().name(), nanoPowerInfo.get().type()));
            nanoPowerCommentLabel.setText(nanoPowerInfo.get().comment());
            nanoPowerIconView.setImage(nanoPowerInfo.get().iconName());
        }

        public NanoPowerInfo getNanoPowerInfo() {
            return nanoPowerInfo.get();
        }

        public ReadOnlyObjectProperty<NanoPowerInfo> nanoPowerInfoProperty() {
            return nanoPowerInfo;
        }

        public StandardImageView getNanoPowerIconView() {
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
        private final StandardImageView iconView;
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

            iconView = new StandardImageView(this.controller.getIconManager().getIconMap(), 64);

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
        }

        @Override
        public MainController getController() {
            return controller;
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
            capsuleItemInfo.set(data);
        }

        @Override
        public void cleanUIState() {
            idLabel.setText("Capsule: null");
            contentVBox.setDisable(true);
            idLabel.getStyleClass().removeIf("disabled-id"::equals);
            idLabel.getStyleClass().add("disabled-id");

            nameLabel.setText("UNKNOWN");
            iconView.cleanImage();
        }

        @Override
        public void fillUIState() {
            idLabel.setText("Capsule: " + capsuleItemInfo.get().id());
            contentVBox.setDisable(false);
            idLabel.getStyleClass().removeIf("disabled-id"::equals);

            nameLabel.setText(capsuleItemInfo.get().name());
            iconView.setImage(capsuleItemInfo.get().iconName());
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

        public StandardImageView getIconView() {
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
