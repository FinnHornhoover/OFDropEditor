package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.ReferenceMode;
import finnhh.oftools.dropeditor.model.data.Data;
import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.scene.control.Label;

import java.util.Objects;

public interface DataComponent extends ObservableComponent<Data> {
    Label getIdLabel();

    DataComponent getParentComponent();

    default void makeEditable(Drops drops) {
        Data oldObject = getObservable().get();
        DataComponent parent = getParentComponent();

        // if not safe to edit object
        if (drops.getReferenceModeFor(oldObject) == ReferenceMode.MULTIPLE) {
            // 1. clone object
            // the cloned object has no set id field, but the values are identical to the old object
            Data newObject = oldObject.getEditableClone();

            // 2. make bindings for the clone object, the id field is now identical to the old object
            newObject.constructBindings();

            // 3. add new object to the drops, the new object is given the first available unique id here
            drops.add(newObject);

            // 4. register other object ids that the new object references
            // also construct change listeners that handle other object id changes
            newObject.registerReferences(drops);

            // 5?. if component has a parent, then make the parent editable as well
            if (Objects.nonNull(parent))
                parent.makeEditable(drops);

            // 6. set new object as the new observable of the component
            // the listeners on observable will now fire
            setObservable(newObject);

            // if component has a parent
            if (Objects.nonNull(parent)) {
                // 7?. update fields of parent to reflect new object's new id
                parent.refreshObservable(drops);

                Data referencer = parent.getObservable().get();
                var referenceMap = drops.getReferenceMap();

                // 8?. remove the reference map ties of old object
                referencer.unregisterReferenced(referenceMap, oldObject);

                // 9?. tie the new object in the reference map
                referencer.registerReferenced(referenceMap, newObject);
            }
        }
    }

    default void makeEdit(Drops drops, Data newObject) {
        Data oldObject = getObservable().get();
        DataComponent parent = getParentComponent();

        // 1?. if component has a parent, then make the parent editable as well
        if (drops.getReferenceModeFor(oldObject) == ReferenceMode.MULTIPLE && Objects.nonNull(parent))
            parent.makeEditable(drops);

        // 2. set new object as the new observable of the component
        // the listeners on observable will now fire
        setObservable(newObject);

        // if component has a parent
        if (Objects.nonNull(parent)) {
            // 3?. update fields of parent to reflect new object's new id
            parent.refreshObservable(drops);

            Data referencer = parent.getObservable().get();
            var referenceMap = drops.getReferenceMap();

            // 4?. remove the reference map ties of old object
            referencer.unregisterReferenced(referenceMap, oldObject);

            // 5?. tie the new object in the reference map
            referencer.registerReferenced(referenceMap, newObject);
        }
    }

    default void refreshObservable(Drops drops) {
    }

    default void setIdDisable(boolean disable) {
        if (disable)
            getIdLabel().getStyleClass().add("disabled-id");
        else
            getIdLabel().getStyleClass().remove("disabled-id");
    }
}
