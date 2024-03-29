package finnhh.oftools.dropeditor.view.component;

import javafx.scene.Node;
import javafx.scene.control.Spinner;

import java.util.Objects;

public class StandardSpinner extends Spinner<Integer> {
    public StandardSpinner(int min, int max, int initialValue) {
        super(min, max, initialValue);

        setEditable(true);
        getEditor().setOnAction(event -> {
            try {
                Integer.parseInt(getEditor().getText());
                commitValue();
            } catch (NumberFormatException e) {
                cancelEdit();
            }
        });
        // https://bugs.java.com/bugdatabase/view_bug.do?bug_id=JDK-8252863
        sceneProperty().addListener((o, oldVal, newVal) -> {
            Node increment = lookup(".increment-arrow-button");
            if (Objects.nonNull(increment))
                increment.getOnMouseReleased().handle(null);
            Node decrement = lookup(".decrement-arrow-button");
            if (Objects.nonNull(decrement))
                decrement.getOnMouseReleased().handle(null);
        });
    }
}
