package mediaplayer.ui;

import java.io.File;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import mediaplayer.playlist.PlaylistManager;

public class PlaylistPanel {

    private final VBox content;
    private final ListView<File> listView;
    private final PlaylistManager playlistManager;

    public PlaylistPanel(PlaylistManager playlistManager) {
        this.playlistManager = playlistManager;

        content = new VBox(0);
        content.setId("playlist-panel"); // CSS ID
        content.setPrefWidth(300);
        content.setMaxWidth(300);

        // Header
        HBox header = new HBox(10);
        header.setId("playlist-header");
        
        Label titleLabel = new Label("PLAYLIST");
        titleLabel.setId("playlist-title");
        
        Label itemCount = new Label("0 items");
        itemCount.setId("item-count");
        
        playlistManager.getItems().addListener((javafx.collections.ListChangeListener<File>) change -> {
            int size = playlistManager.getItems().size();
            itemCount.setText(size + " item" + (size != 1 ? "s" : ""));
        });
        
        header.getChildren().addAll(titleLabel, new Region(), itemCount);
        HBox.setHgrow(header.getChildren().get(1), Priority.ALWAYS);

        // List View
        listView = new ListView<>();
        listView.setId("playlist-list");
        listView.setItems(playlistManager.getItems());
        
        listView.setCellFactory(param -> new javafx.scene.control.ListCell<File>() {
            @Override
            protected void updateItem(File item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        playlistManager.currentProperty().addListener((obs, oldFile, newFile) -> {
            listView.refresh();
            if (newFile != null) {
                int index = listView.getItems().indexOf(newFile);
                if (index >= 0) {
                    listView.getSelectionModel().select(index);
                    listView.scrollTo(index);
                }
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldFile, newFile) -> {
            if (newFile != null) playlistManager.select(newFile);
        });

        ScrollPane scrollPane = new ScrollPane(listView);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        
        Button removeBtn = new Button("Remove Selected"); 
        removeBtn.setId("btn-remove");
        removeBtn.setTooltip(new Tooltip("Remove Selected"));
        
        // Ensure icon is on the left and text is on the right
        removeBtn.setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
        
        // Load the remove icon
        try {
            Image removeIcon = new Image(getClass().getResourceAsStream("/resources/icons/remove.png"));
            ImageView iconView = new ImageView(removeIcon);
            iconView.setFitWidth(20);
            iconView.setFitHeight(20);
            removeBtn.setGraphic(iconView);
        } catch (Exception e) {
            System.out.println("Could not load remove icon");
        }
        
        
        removeBtn.setOnAction(e -> {
            File selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) playlistManager.remove(selected);
        });

        content.getChildren().addAll(header, scrollPane, removeBtn);
    }

    public VBox getContent() {
        return content;
    }
}