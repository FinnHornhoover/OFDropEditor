package finnhh.oftools.dropeditor.view.component;

import javafx.beans.property.ReadOnlyObjectProperty;

public interface ObservableComponent<T> {
    ReadOnlyObjectProperty<? extends T> getObservable();

    void setObservable(T data);
}
