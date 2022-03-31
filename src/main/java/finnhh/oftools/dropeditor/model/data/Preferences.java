package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class Preferences {
    @Expose
    private String dropDirectory;
    @Expose
    private List<String> patchDirectories;
    @Expose
    private String xdtFile;
    @Expose
    private String saveDirectory;
    @Expose
    private boolean standaloneSave;
    @Expose
    private String iconDirectory;

    public Preferences() {
        patchDirectories = new ArrayList<>();
    }

    public String getDropDirectory() {
        return dropDirectory;
    }

    public void setDropDirectory(String dropDirectory) {
        this.dropDirectory = dropDirectory;
    }

    public List<String> getPatchDirectories() {
        return patchDirectories;
    }

    public void setPatchDirectories(List<String> patchDirectories) {
        this.patchDirectories = patchDirectories;
    }

    public String getXDTFile() {
        return xdtFile;
    }

    public void setXDTFile(String xdtFile) {
        this.xdtFile = xdtFile;
    }

    public String getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    public boolean isStandaloneSave() {
        return standaloneSave;
    }

    public void setStandaloneSave(boolean standaloneSave) {
        this.standaloneSave = standaloneSave;
    }

    public String getIconDirectory() {
        return iconDirectory;
    }

    public void setIconDirectory(String iconDirectory) {
        this.iconDirectory = iconDirectory;
    }
}
