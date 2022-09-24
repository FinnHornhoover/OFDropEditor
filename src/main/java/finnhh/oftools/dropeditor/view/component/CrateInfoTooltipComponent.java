package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.ItemInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CrateInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<ItemInfo> {
    private final ObjectProperty<ItemInfo> crateInfo;

    public CrateInfoTooltipComponent(MainController controller) {
        super(controller);
        crateInfo = new SimpleObjectProperty<>();
    }

    @Override
    public Class<ItemInfo> getObservableClass() {
        return ItemInfo.class;
    }

    @Override
    public ReadOnlyObjectProperty<ItemInfo> getObservable() {
        return crateInfo;
    }

    @Override
    public void setObservable(ItemInfo data) {
        crateInfo.set(data);

        clearMaps();

        if (crateInfo.isNotNull().get()) {
            ItemInfo ci = crateInfo.get();
            arrangeMaps(ci.id(), ci.type());
        }
    }
}
