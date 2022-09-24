package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.MainController;
import finnhh.oftools.dropeditor.model.MobTypeInfo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class MobInfoTooltipComponent extends MapContainerTooltipBox implements ObservableComponent<MobTypeInfo> {
    private final ObjectProperty<MobTypeInfo> mobTypeInfo;

    public MobInfoTooltipComponent(MainController controller) {
        super(controller);
        mobTypeInfo = new SimpleObjectProperty<>();
    }

    @Override
    public Class<MobTypeInfo> getObservableClass() {
        return MobTypeInfo.class;
    }

    @Override
    public ReadOnlyObjectProperty<MobTypeInfo> getObservable() {
        return mobTypeInfo;
    }

    @Override
    public void setObservable(MobTypeInfo data) {
        mobTypeInfo.set(data);

        clearMaps();

        if (mobTypeInfo.isNotNull().get())
            arrangeMobLocationMaps(mobTypeInfo.get().type());
    }
}
