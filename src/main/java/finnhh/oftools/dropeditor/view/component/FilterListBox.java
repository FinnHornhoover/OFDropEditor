package finnhh.oftools.dropeditor.view.component;

import finnhh.oftools.dropeditor.model.FilterCondition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class FilterListBox extends FlowPane {
    private final ListProperty<FilterCondition> filterConditionList;
    private final List<EventHandler<MouseEvent>> removeHandlers;

    public FilterListBox() {
        filterConditionList = new SimpleListProperty<>(FXCollections.observableArrayList());
        removeHandlers = new ArrayList<>();
    }

    public void addFilter(FilterCondition condition) {
        FilterBox fb = new FilterBox(condition);
        filterConditionList.add(condition);
        getChildren().add(fb);
        removeHandlers.add(event -> removeFilter(condition));
        fb.getRemoveButton().addEventHandler(MouseEvent.MOUSE_CLICKED, removeHandlers.get(removeHandlers.size() - 1));
    }

    public void removeFilter(FilterCondition condition) {
        int index = filterConditionList.indexOf(condition);
        filterConditionList.remove(index);
        FilterBox fb = (FilterBox) getChildren().remove(index);
        fb.getRemoveButton().removeEventHandler(MouseEvent.MOUSE_CLICKED, removeHandlers.get(index));
        removeHandlers.remove(index);
    }

    public void clearFilters() {
        var children = getChildren().filtered(c -> c instanceof FilterBox);

        IntStream.range(0, children.size())
                .forEach(index -> ((FilterBox) children.get(index)).getRemoveButton()
                        .removeEventHandler(MouseEvent.MOUSE_CLICKED, removeHandlers.get(index)));

        getChildren().clear();
        filterConditionList.clear();
        removeHandlers.clear();
    }

    public ObservableList<FilterCondition> getFilterConditionList() {
        return filterConditionList.get();
    }

    public ReadOnlyListProperty<FilterCondition> filterConditionListProperty() {
        return filterConditionList;
    }

    public static class FilterBox extends HBox {
        private final ObjectProperty<FilterCondition> filterCondition;

        private final Label filterLabel;
        private final Button removeButton;

        public FilterBox(FilterCondition filterCondition) {
            this.filterCondition = new SimpleObjectProperty<>(filterCondition);

            filterLabel = new Label(filterCondition.toShortString());
            Tooltip tooltip = new Tooltip(filterCondition.toString());
            tooltip.setShowDelay(Duration.ZERO);
            filterLabel.setTooltip(tooltip);

            removeButton = new Button("-");
            removeButton.setMinWidth(USE_COMPUTED_SIZE);
            removeButton.getStyleClass().addAll("remove-button", "slim-button");

            setSpacing(2);
            getChildren().addAll(filterLabel, removeButton);
            setAlignment(Pos.CENTER_LEFT);
        }

        public FilterCondition getFilterCondition() {
            return filterCondition.get();
        }

        public ReadOnlyObjectProperty<FilterCondition> filterConditionProperty() {
            return filterCondition;
        }

        public Label getFilterLabel() {
            return filterLabel;
        }

        public Button getRemoveButton() {
            return removeButton;
        }
    }
}
