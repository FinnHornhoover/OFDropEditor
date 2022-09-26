package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.data.Drops;
import javafx.scene.control.Button;

public interface RootDataComponent extends DataComponent {
    @Override
    default DataComponent getParentComponent() {
        return null;
    }

    @Override
    default void makeEditable(Drops drops) {
        // do not alter the root ids freely
    }

    Button getRemoveButton();
}
