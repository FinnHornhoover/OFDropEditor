package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
    @Expose
    public String dropDirectory;
    @Expose
    public List<String> patchDirectories;
    @Expose
    public String xdtFile;
    @Expose
    public String saveDirectory;
    @Expose
    public boolean standaloneSave;
    @Expose
    public String iconDirectory;

    public Preferences() {
        patchDirectories = new ArrayList<>();
    }
}
