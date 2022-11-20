package finnhh.oftools.dropeditor.model.data;

import com.google.gson.annotations.Expose;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Preferences {
    @Expose
    private final StringProperty dropDirectory;
    @Expose
    private final ListProperty<String> patchDirectories;
    @Expose
    private final StringProperty xdtFile;
    @Expose
    private final StringProperty saveDirectory;
    @Expose
    private final BooleanProperty standaloneSave;
    @Expose
    private final StringProperty iconDirectory;

    public Preferences() {
        dropDirectory = new SimpleStringProperty("");
        patchDirectories = new SimpleListProperty<>(FXCollections.observableArrayList());
        xdtFile = new SimpleStringProperty("");
        saveDirectory = new SimpleStringProperty("");
        standaloneSave = new SimpleBooleanProperty(false);
        iconDirectory = new SimpleStringProperty("");
    }

    public String getDropDirectory() {
        return dropDirectory.get();
    }

    public StringProperty dropDirectoryProperty() {
        return dropDirectory;
    }

    public void setDropDirectory(String dropDirectory) {
        this.dropDirectory.set(dropDirectory);
    }

    public ObservableList<String> getPatchDirectories() {
        return patchDirectories.get();
    }

    public ListProperty<String> patchDirectoriesProperty() {
        return patchDirectories;
    }

    public void setPatchDirectories(ObservableList<String> patchDirectories) {
        this.patchDirectories.set(patchDirectories);
    }

    public String getXDTFile() {
        return xdtFile.get();
    }

    public StringProperty xdtFileProperty() {
        return xdtFile;
    }

    public void setXDTFile(String xdtFile) {
        this.xdtFile.set(xdtFile);
    }

    public String getSaveDirectory() {
        return saveDirectory.get();
    }

    public StringProperty saveDirectoryProperty() {
        return saveDirectory;
    }

    public void setSaveDirectory(String saveDirectory) {
        this.saveDirectory.set(saveDirectory);
    }

    public boolean isStandaloneSave() {
        return standaloneSave.get();
    }

    public BooleanProperty standaloneSaveProperty() {
        return standaloneSave;
    }

    public void setStandaloneSave(boolean standaloneSave) {
        this.standaloneSave.set(standaloneSave);
    }

    public String getIconDirectory() {
        return iconDirectory.get();
    }

    public StringProperty iconDirectoryProperty() {
        return iconDirectory;
    }

    public void setIconDirectory(String iconDirectory) {
        this.iconDirectory.set(iconDirectory);
    }

    public boolean isOverwritingDropDirectory() {
        return dropDirectory.get().equals(saveDirectory.get());
    }

    public boolean isOverwritingAnyPatchDirectory() {
        int index = patchDirectories.indexOf(saveDirectory.get());
        return index > -1 && index < patchDirectories.size();
    }

    public boolean isOverwritingLastPatchDirectory() {
        int index = patchDirectories.indexOf(saveDirectory.get());
        return index > -1 && index == patchDirectories.size() - 1;
    }
}
