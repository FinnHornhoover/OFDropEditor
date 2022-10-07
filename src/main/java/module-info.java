module finnhh.oftools.dropeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.google.gson;
    requires org.hildan.fxgson;

    opens finnhh.oftools.dropeditor to com.google.gson, javafx.fxml;
    opens finnhh.oftools.dropeditor.model to com.google.gson, javafx.fxml;
    opens finnhh.oftools.dropeditor.model.data to com.google.gson, javafx.fxml;
    opens finnhh.oftools.dropeditor.model.exception to com.google.gson, javafx.fxml;

    exports finnhh.oftools.dropeditor;
    exports finnhh.oftools.dropeditor.model;
    exports finnhh.oftools.dropeditor.model.data;
    exports finnhh.oftools.dropeditor.model.exception;
    exports finnhh.oftools.dropeditor.view.component;
}