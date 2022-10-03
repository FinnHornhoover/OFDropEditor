package finnhh.oftools.dropeditor;

import finnhh.oftools.dropeditor.model.*;
import finnhh.oftools.dropeditor.model.data.*;
import finnhh.oftools.dropeditor.view.component.*;
import finnhh.oftools.dropeditor.view.util.NoSelectionModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.ToggleSwitch;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MainController {
    @FXML
    protected ChoiceBox<ViewMode> viewModeChoiceBox;
    @FXML
    protected FilterListBox filterListBox;
    @FXML
    protected ListView<Data> mainListView;
    @FXML
    protected Label infoLabel;
    @FXML
    protected Label lastSavedLabel;
    @FXML
    protected ToggleSwitch autoSaveSwitch;

    protected Map<ViewMode, ObservableComponent<?>> rootPrototypeMap;
    protected Service<Boolean> saveService;
    protected Timeline saveTimeline;
    protected Drops drops;
    protected JSONManager jsonManager;
    protected IconManager iconManager;
    protected StaticDataStore staticDataStore;
    protected MainApplication application;

    @FXML
    protected void onAddFilter() {
        showFilterMenuForResult().ifPresent(filterListBox::addFilter);
    }

    @FXML
    protected void onNewButtonPressed() {
        viewModeChoiceBox.getValue().getNewDataAdder().apply(this).ifPresent(data -> {
            data.constructBindings();
            drops.add(data);
            data.registerReferences(drops);
            refreshList();
            mainListView.scrollTo(data);
        });
    }

    public void setup() {
        rootPrototypeMap = new HashMap<>();
        for (ViewMode viewMode : ViewMode.values())
            rootPrototypeMap.put(viewMode, viewMode.getComponentConstructor().apply(this));

        saveService = new Service<>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        if (autoSaveSwitch.isSelected())
                            jsonManager.save(drops);
                        return autoSaveSwitch.isSelected();
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        if (getValue()) {
                            Platform.runLater(() -> {
                                LocalDateTime time = LocalDateTime.now();
                                lastSavedLabel.setText(String.format("Last Saved: %02d:%02d",
                                        time.getHour(), time.getMinute()));
                            });
                        }
                    }
                };
            }
        };

        LocalDateTime time = LocalDateTime.now();
        lastSavedLabel.setText(String.format("Last Saved: %02d:%02d", time.getHour(), time.getMinute()));

        saveTimeline = new Timeline(new KeyFrame(Duration.minutes(1), event -> saveService.restart()));
        saveTimeline.setCycleCount(Timeline.INDEFINITE);
        saveTimeline.play();

        autoSaveSwitch.selectedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal)
                saveService.restart();
        });

        viewModeChoiceBox.getItems().addAll(ViewMode.values());
        viewModeChoiceBox.setValue(ViewMode.MONSTER);

        final MainController controller = this;
        mainListView.setCellFactory(cfData -> new ListCell<>() {
            private final MobComponent mobComponent;
            private final CrateComponent crateComponent;
            private final RacingComponent racingComponent;
            private final CodeItemComponent codeItemComponent;
            private final ItemReferenceComponent itemReferenceComponent;
            private final NanoCapsuleComponent nanoCapsuleComponent;
            private final EventComponent eventComponent;

            {
                mobComponent = new MobComponent(controller);
                crateComponent = new CrateComponent(controller);
                racingComponent = new RacingComponent(controller);
                codeItemComponent = new CodeItemComponent(controller);
                itemReferenceComponent = new ItemReferenceComponent(controller);
                nanoCapsuleComponent = new NanoCapsuleComponent(controller);
                eventComponent = new EventComponent(controller);
            }

            @Override
            protected void updateItem(Data data, boolean empty) {
                super.updateItem(data, empty);

                Node graphic = null;

                if (!empty) {
                    switch (viewModeChoiceBox.getValue()) {
                        case MONSTER -> {
                            mobComponent.setObservableAndState(data);
                            graphic = mobComponent;
                        }
                        case CRATE -> {
                            crateComponent.setObservableAndState(data);
                            graphic = crateComponent;
                        }
                        case RACING -> {
                            racingComponent.setObservableAndState(data);
                            graphic = racingComponent;
                        }
                        case CODE_ITEM -> {
                            codeItemComponent.setObservableAndState(data);
                            graphic = codeItemComponent;
                        }
                        case ITEM_REFERENCE -> {
                            itemReferenceComponent.setObservableAndState(data);
                            graphic = itemReferenceComponent;
                        }
                        case NANO_CAPSULE -> {
                            nanoCapsuleComponent.setObservableAndState(data);
                            graphic = nanoCapsuleComponent;
                        }
                        case EVENT -> {
                            eventComponent.setObservableAndState(data);
                            graphic = eventComponent;
                        }
                    }
                }

                setGraphic(graphic);
            }
        });

        mainListView.setEditable(false);
        mainListView.setFocusTraversable(false);
        mainListView.setSelectionModel(new NoSelectionModel<>());

        viewModeChoiceBox.valueProperty().addListener((o, oldVal, newVal) -> {
            filterListBox.clearFilters();
            refreshList();
        });

        filterListBox.filterConditionListProperty().addListener(
                (ListChangeListener<FilterCondition>) change -> refreshList());

        refreshList();
    }

    public void stop() {
        saveTimeline.stop();
    }

    public void refreshList() {
        mainListView.getItems().clear();
        var dataList = viewModeChoiceBox.getValue().getDataGetter().apply(drops);
        mainListView.getItems().addAll(dataList.stream()
                .filter(d -> filterListBox.getFilterConditionList().stream()
                        .allMatch(fc -> fc.conditionSatisfied(d)))
                .toList());
        infoLabel.setText(String.format("Showing %d items out of %d",
                mainListView.getItems().size(), dataList.size()));
        mainListView.scrollTo(0);
        Platform.runLater(mainListView::refresh);
    }

    public final <T, V> TableColumn<T, V> getTableColumn(String name, double fixedWidth, Function<T, V> valueGetter) {
        TableColumn<T, V> column = new TableColumn<>(name);

        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(valueGetter.apply(cellData.getValue())));
        if (fixedWidth > 0) {
            column.setMinWidth(fixedWidth);
            column.setMaxWidth(fixedWidth);
        }

        return column;
    }

    public <T> TableColumn<T, String> getIconColumn(Function<T, String> iconNameGetter) {
        TableColumn<T, String> iconColumn = getTableColumn("Icon", 68.0, iconNameGetter);

        iconColumn.setCellFactory(cfData -> new TableCell<>() {
            private final StandardImageView iconView;

            {
                iconView = new StandardImageView(iconManager.getIconMap(), 64);
            }

            @Override
            protected void updateItem(String iconName, boolean empty) {
                super.updateItem(iconName, empty);

                if (!empty) {
                    iconView.setImage(iconName);
                    setGraphic(iconView);
                } else {
                    setGraphic(null);
                }
            }
        });

        return iconColumn;
    }

    @SafeVarargs
    public final <T> TableView<T> getTableGraphic(Supplier<List<T>> dataGetter,
                                                  Supplier<TableColumn<T, ?>>... columnMakers) {
        TableView<T> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        for (var columnMaker : columnMakers)
            tableView.getColumns().add(columnMaker.get());

        tableView.getItems().addAll(dataGetter.get());

        return tableView;
    }

    public TableView<ReferenceListBox> getReferenceGraphic(Class<? extends Data> dataClass,
                                                           Predicate<Data> filterCondition) {
        return getTableGraphic(
                () -> drops.getDataMap(dataClass).stream()
                        .flatMap(dataMap -> dataMap.values().stream())
                        .filter(filterCondition)
                        .map(d -> new ReferenceListBox(64.0, this, d))
                        .toList(),
                () -> getTableColumn("ID", 68.0,
                        rlb -> new ImageSummaryBox(64.0, this, rlb.getOriginData())),
                () -> {
                    TableColumn<ReferenceListBox, ReferenceListBox> referenceColumn = getTableColumn(
                            "References", -1.0, Function.identity());

                    referenceColumn.setCellFactory(cfData -> new TableCell<>() {
                        @Override
                        protected void updateItem(ReferenceListBox referenceListBox, boolean empty) {
                            super.updateItem(referenceListBox, empty);
                            setGraphic((!empty && Objects.nonNull(referenceListBox)) ? referenceListBox : null);
                        }
                    });

                    return referenceColumn;
                }
        );
    }

    public TableView<MobTypeInfo> getMobAdditionGraphic() {
        return getTableGraphic(
                () -> staticDataStore.getMobTypeInfoMap().values().stream()
                        .filter(mti -> !drops.mobsProperty().containsKey(mti.type()))
                        .sorted(Comparator.comparingInt(MobTypeInfo::level).thenComparing(MobTypeInfo::type))
                        .toList(),
                () -> getIconColumn(MobTypeInfo::iconName),
                () -> getTableColumn("Type ID", 68.0, MobTypeInfo::type),
                () -> getTableColumn("Level", 68.0, MobTypeInfo::level),
                () -> getTableColumn("Name", -1.0, MobTypeInfo::name)
        );
    }

    public TableView<ItemInfo> getCrateAdditionGraphic() {
        return getTableGraphic(
                () -> staticDataStore.getItemInfoMap().values().stream()
                        .filter(ii -> ii.type() == ItemType.CRATE && !drops.cratesProperty().containsKey(ii.id()))
                        .sorted(Comparator.comparingInt(ItemInfo::id))
                        .toList(),
                () -> getIconColumn(ItemInfo::iconName),
                () -> getTableColumn("ID", 68.0, ItemInfo::id),
                () -> getTableColumn("Level", 68.0, ItemInfo::contentLevel),
                () -> getTableColumn("Name", -1.0, ItemInfo::name),
                () -> getTableColumn("Comment", -1.0, ItemInfo::comment)
        );
    }

    public TableView<InstanceInfo> getRacingAdditionGraphic() {
        return getTableGraphic(
                () -> staticDataStore.getEPInstanceMap().values().stream()
                        .filter(ii -> !drops.racingProperty().containsKey(ii.EPID()))
                        .sorted(Comparator.comparingInt(InstanceInfo::EPID))
                        .toList(),
                () -> getIconColumn(ii -> String.format("ep_small_%02d", ii.EPID())),
                () -> getTableColumn("EPID", 68.0, InstanceInfo::EPID),
                () -> getTableColumn("Name", -1.0, InstanceInfo::name),
                () -> getTableColumn("Entry", -1.0, ii -> {
                    MapRegionInfo mapRegionInfo = InstanceInfo.getOverworldNPCLocations(
                            staticDataStore.getMapRegionInfoList(), ii.getEntryWarpNPCs(staticDataStore.getNpcInfoMap()))
                            .stream()
                            .findFirst()
                            .orElse(MapRegionInfo.UNKNOWN);
                    return mapRegionInfo.areaName() + " - " + mapRegionInfo.zoneName();
                })
        );
    }

    public TableView<ItemInfo> getItemReferenceAdditionGraphic() {
        return getTableGraphic(
                () -> {
                    Set<ItemInfo> itemInfoSet = new HashSet<>(staticDataStore.getItemInfoMap().values());

                    drops.getItemReferences().values().stream()
                            .flatMap(ir -> Optional.ofNullable(staticDataStore.getItemInfoMap()
                                    .get(new Pair<>(ir.getItemID(), ir.getType()))).stream())
                            .forEach(itemInfoSet::remove);

                    return itemInfoSet.stream()
                            .sorted(Comparator.comparing(ItemInfo::type).thenComparing(ItemInfo::id))
                            .toList();
                },
                () -> getIconColumn(ItemInfo::iconName),
                () -> getTableColumn("ID", 68.0, ItemInfo::id),
                () -> getTableColumn("Type", 100.0, ItemInfo::type),
                () -> getTableColumn("Level", 68.0, ItemInfo::requiredLevel),
                () -> getTableColumn("Content Level", 132.0, ItemInfo::contentLevel),
                () -> getTableColumn("Name", -1.0, ItemInfo::name),
                () -> getTableColumn("Comment", -1.0, ItemInfo::comment)
        );
    }

    public TableView<ItemInfo> getCapsuleSelectionGraphic() {
        return getTableGraphic(
                () -> {
                    Set<Integer> crateIDSet = drops.getNanoCapsules().values().stream()
                            .map(NanoCapsule::getCrateID)
                            .collect(Collectors.toSet());
                    return staticDataStore.getItemInfoMap().values().stream()
                            .filter(ii -> ii.type() == ItemType.CRATE && ii.name().contains("Nano")
                                    && ii.name().contains("Capsule") && !crateIDSet.contains(ii.id()))
                            .sorted(Comparator.comparingInt(ItemInfo::id))
                            .toList();
                },
                () -> getIconColumn(ItemInfo::iconName),
                () -> getTableColumn("ID", 68.0, ItemInfo::id),
                () -> getTableColumn("Name", -1.0, ItemInfo::name),
                () -> getTableColumn("Comment", -1.0, ItemInfo::comment)
        );
    }

    public TableView<NanoInfo> getNanoCapsuleAdditionGraphic() {
        return getTableGraphic(
                () -> {
                    Set<Integer> nanoIDSet = drops.getNanoCapsules().values().stream()
                            .map(NanoCapsule::getNano)
                            .collect(Collectors.toSet());
                    return staticDataStore.getNanoInfoMap().values().stream()
                            .filter(ni -> !nanoIDSet.contains(ni.id()))
                            .sorted(Comparator.comparingInt(NanoInfo::id))
                            .toList();
                },
                () -> getIconColumn(NanoInfo::iconName),
                () -> getTableColumn("Name", -1.0, NanoInfo::name),
                () -> getTableColumn("Type", -1.0, NanoInfo::type),
                () -> getTableColumn("Powers", -1.0, ni -> ni.powers().stream()
                        .map(NanoPowerInfo::type)
                        .collect(Collectors.joining(", ")))
        );
    }

    public TableView<EventType> getEventAdditionGraphic() {
        return getTableGraphic(
                () -> Arrays.stream(EventType.values())
                        .filter(et -> et != EventType.NO_EVENT && !drops.getEvents().containsKey(et.getTypeID()))
                        .sorted(Comparator.comparingInt(EventType::getTypeID))
                        .toList(),
                () -> getIconColumn(EventType::iconName),
                () -> getTableColumn("Name", -1.0, EventType::getName)
        );
    }

    public TextField getCodeItemAdditionGraphic() {
        TextField textField = new TextField();
        textField.getStyleClass().add("code-text-field");
        return textField;
    }

    public Optional<FilterCondition> showFilterMenuForResult() {
        return application.showFilterSelectionAlert("New Filter", "Filter Selection", new FilterSelectionBox(10.0,
                rootPrototypeMap.get(viewModeChoiceBox.getValue()).getSearchableValues()));
    }

    public Optional<Data> showSelectionMenuForResult(Class<? extends Data> dataClass,
                                                     Predicate<Data> filterCondition) {
        return application.showSelectionAlert(
                dataClass.getSimpleName() + " Selection",
                "Please select one:",
                getReferenceGraphic(dataClass, filterCondition),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ReferenceListBox::getOriginData));
    }

    public Optional<Data> showSelectionMenuForResult(Class<? extends Data> dataClass) {
        return showSelectionMenuForResult(dataClass, d -> true);
    }

    public Optional<Data> showAddMobMenuForResult() {
        return application.showSelectionAlert(
                "Add New Mob",
                "Please select one to add:",
                getMobAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(mti -> {
                            Mob mob = new Mob();
                            mob.setMobID(mti.type());
                            return mob;
                        }));
    }

    public Optional<Data> showAddCrateMenuForResult() {
        return application.showSelectionAlert(
                "Add New Crate",
                "Please select one to add:",
                getCrateAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ii -> {
                            Crate crate = new Crate();
                            crate.setCrateID(ii.id());
                            return crate;
                        }));
    }

    public Optional<Data> showAddRacingMenuForResult() {
        return application.showSelectionAlert(
                "Add New Racing Object",
                "Please select one to add:",
                getRacingAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ii -> {
                            Racing racing = new Racing();
                            racing.setEPID(ii.EPID());
                            return racing;
                        }));
    }

    public Optional<Data> showAddCodeItemMenuForResult() {
        return application.showSelectionAlert(
                "Add New Code Item",
                "Please type the new code:",
                getCodeItemAdditionGraphic(),
                textField -> Optional.ofNullable(textField.getText())
                        .filter(text -> !text.isBlank() && drops.getCodeItems().values().stream().noneMatch(ci ->
                                ci.getCode().toLowerCase(Locale.ENGLISH).equals(text.toLowerCase(Locale.ENGLISH))))
                        .map(text -> {
                            CodeItem codeItem = new CodeItem();
                            codeItem.setCode(text.replaceAll("[^A-Za-z0-9]+", "").toLowerCase(Locale.ENGLISH));
                            return codeItem;
                        }));
    }

    public Optional<Data> showAddItemReferenceMenuForResult() {
        return application.showSelectionAlert(
                "Add New Item Reference",
                "Please select one to add:",
                getItemReferenceAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ii -> {
                            ItemReference itemReference = new ItemReference();
                            itemReference.setItemID(ii.id());
                            itemReference.setType(ii.type().getTypeID());
                            return itemReference;
                        }));
    }

    public Optional<Data> showSelectCapsuleMenuForResult() {
        return application.showSelectionAlert(
                "Capsule Selection",
                "Please select one:",
                getCapsuleSelectionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ii -> {
                            NanoCapsule nanoCapsule = new NanoCapsule();
                            nanoCapsule.setCrateID(ii.id());
                            return nanoCapsule;
                        })
        );
    }

    public Optional<Data> showAddNanoCapsuleMenuForResult() {
        return application.showSelectionAlert(
                "Add New Nano Capsule",
                "Please select Nano to add a Capsule for:",
                getNanoCapsuleAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(ni -> {
                            NanoCapsule nanoCapsule = new NanoCapsule();
                            nanoCapsule.setNano(ni.id());
                            return nanoCapsule;
                        }));
    }

    public Optional<Data> showAddEventMenuForResult() {
        return application.showSelectionAlert(
                "Add New Event Setting",
                "Please select one to add:",
                getEventAdditionGraphic(),
                tableView -> Optional.ofNullable(tableView.getSelectionModel().getSelectedItem())
                        .map(et -> {
                            Event event = new Event();
                            event.setEventID((et == EventType.CUSTOM_EVENT) ?
                                    Math.max(EventType.nextId(), drops.eventsProperty().getNextTrueID()) :
                                    et.getTypeID());
                            return event;
                        }));
    }

    public ListView<Data> getMainListView() {
        return mainListView;
    }

    public void setJSONManager(JSONManager jsonManager) {
        this.jsonManager = jsonManager;
    }

    public JSONManager getJsonManager() {
        return jsonManager;
    }

    public void setIconManager(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public IconManager getIconManager() {
        return iconManager;
    }

    public void setDrops(Drops drops) {
        this.drops = drops;
    }

    public Drops getDrops() {
        return drops;
    }

    public void setStaticDataStore(StaticDataStore staticDataStore) {
        this.staticDataStore = staticDataStore;
    }

    public StaticDataStore getStaticDataStore() {
        return staticDataStore;
    }

    public void setApplication(MainApplication application) {
        this.application = application;
    }

    public MainApplication getApplication() {
        return application;
    }
}