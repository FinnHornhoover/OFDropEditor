package finnhh.oftools.dropeditor.view.component;

import javafx.scene.control.Button;

public interface RootDataComponent extends DataComponent {
    Button getRemoveButton();

    default void onRemoveClick() {
        getController().getDrops().remove(getObservable().get());
        getController().getDrops().getReferenceMap().values().forEach(set -> set.remove(getObservable().get()));
        getController().getMainListView().getItems().remove(getObservable().get());
    }

    @Override
    default DataComponent getParentComponent() {
        return null;
    }

    @Override
    default void makeEditable() {
        // do not alter the root ids freely
    }
}
