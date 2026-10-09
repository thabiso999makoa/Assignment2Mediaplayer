package mediaplayer.playlist;

import java.io.File;
import java.util.List;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class PlaylistManager {

    private final ObservableList<File> items = FXCollections.observableArrayList();
    private final ObservableList<File> readOnlyItems =
            FXCollections.unmodifiableObservableList(items);

    // The file that should be playing right now (null = nothing selected).
    private final ReadOnlyObjectWrapper<File> current = new ReadOnlyObjectWrapper<>();

    //Adds files, skipping any already in the list. Returns how many were added
    public int add(List<File> files) {
        int added = 0;
        for (File file : files) {
            if (!items.contains(file)) {
                items.add(file);
                added++;
            }
        }
        // If nothing was selected yet, start with the first item.
        if (current.get() == null && !items.isEmpty()) {
            current.set(items.get(0));
        }
        return added;
    }

    // Removes a file. If it was the current one, nothing is current afterwards.
    public void remove(File file) {
        if (file == null) {
            return;
        }
        if (file.equals(current.get())) {
            current.set(null);
        }
        items.remove(file);
    }

    /** Makes a file in the list the current one. */
    public void select(File file) {
        if (file != null && items.contains(file)) {
            current.set(file);
        }
    }

    public void next() {
        moveBy(1);
    }

    public void previous() {
        moveBy(-1);
    }

    // Called when a track ends: go to the next item, or stay put at the end of the list.
    public void playNextAfterEnd() {
        int index = items.indexOf(current.get());
        if (index >= 0 && index < items.size() - 1) {
            current.set(items.get(index + 1));
        }
    }

    private void moveBy(int step) {
        if (items.isEmpty()) {
            return;
        }
        int index = items.indexOf(current.get());
        // floorMod wraps around: after the last item comes the first, and vice versa.
        int target = (index == -1) ? 0 : Math.floorMod(index + step, items.size());
        current.set(items.get(target));
    }

    

    public ObservableList<File> getItems() {
        return readOnlyItems;
    }

    public ReadOnlyObjectProperty<File> currentProperty() {
        return current.getReadOnlyProperty();
    }

    public File getCurrent() {
        return current.get();
    }
}