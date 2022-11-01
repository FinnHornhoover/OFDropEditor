package finnhh.oftools.dropeditor.model;

import finnhh.oftools.dropeditor.model.data.Data;

public record ReversibleAction(Long key, Data rootData, Runnable redo, Runnable undo) {
}
