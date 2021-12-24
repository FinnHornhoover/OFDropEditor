module finnhh.oftools.dropeditor {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires com.google.gson;

    opens finnhh.oftools.dropeditor to com.google.gson, javafx.fxml;

    exports finnhh.oftools.dropeditor;
    exports finnhh.oftools.dropeditor.model;
    opens finnhh.oftools.dropeditor.model to com.google.gson, javafx.fxml;
    exports finnhh.oftools.dropeditor.model.data;
    opens finnhh.oftools.dropeditor.model.data to com.google.gson, javafx.fxml;
    exports finnhh.oftools.dropeditor.model.exception;
    opens finnhh.oftools.dropeditor.model.exception to com.google.gson, javafx.fxml;
}