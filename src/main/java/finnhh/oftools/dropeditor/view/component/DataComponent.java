package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.data.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public interface DataComponent extends ObservableComponent<Data> {
    Label getIdLabel();

    List<Pane> getContentPanes();

    DataComponent getParentComponent();

    @Override
    default void cleanUIState() {
        getIdLabel().setText(getObservableClass().getSimpleName() + ": null");
        getContentPanes().forEach(pane -> pane.setDisable(true));
        setIdDisable(true);
    }

    @Override
    default void fillUIState() {
        getIdLabel().setText(getObservable().get().getIdBinding().getValueSafe());
        getContentPanes().forEach(pane -> pane.setDisable(false));
        setIdDisable(getObservable().get().isMalformed());
    }

    default void setIdDisable(boolean disable) {
        getIdLabel().getStyleClass().removeIf("disabled-id"::equals);
        if (disable)
            getIdLabel().getStyleClass().add("disabled-id");
    }

    default DataComponent getRootComponent() {
        DataComponent rootComponent = this;
        while (Objects.nonNull(rootComponent.getParentComponent())) {
            rootComponent = rootComponent.getParentComponent();
        }
        return rootComponent;
    }

    default List<Data> getObjectChain() {
        List<Data> objectChain = new ArrayList<>();
        objectChain.add(getObservable().get());

        DataComponent parent = getParentComponent();
        while (Objects.nonNull(parent)) {
            objectChain.add(parent.getObservable().get());
            parent = parent.getParentComponent();
        }

        return objectChain;
    }

    default void makeReplacement(Data newObject) {
        makeReplacement(getController().getDrops().generateActionKey(), newObject);
    }

    default void makeReplacement(long key, Data newObject) {
        getController().getDrops().makeReplacement(getObjectChain(), key, newObject);
        getRootComponent().refreshObservableAndState();
    }

    default void makeEdit(Consumer<Data> editor) {
        makeEdit(getController().getDrops().generateActionKey(), editor);
    }

    default void makeEdit(long key, Consumer<Data> editor) {
        getController().getDrops().makeEdit(getObjectChain(), key, editor);
        getRootComponent().refreshObservableAndState();
    }
}
