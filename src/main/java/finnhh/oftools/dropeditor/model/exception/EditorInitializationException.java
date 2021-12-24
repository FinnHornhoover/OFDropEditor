package finnhh.oftools.dropeditor.model.exception;

import java.io.IOException;

public class EditorInitializationException extends IOException {
    private final String title;
    private final String content;

    public EditorInitializationException(String title, String content) {
        super(title + "\n" + content);
        this.title = title;
        this.content = content;
    }

    public EditorInitializationException(Throwable t, String title, String content) {
        super(title + "\n" + content, t);
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
