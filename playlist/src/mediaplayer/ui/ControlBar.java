package mediaplayer.ui;

import java.io.File;
import java.util.List;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;

import mediaplayer.player.PlayerEngine;
import mediaplayer.playlist.PlaylistManager;

public class ControlBar {

    private final HBox content;
    private final PlayerEngine playerEngine;
    private final PlaylistManager playlistManager;
    private Button muteBtn;

    public ControlBar(PlayerEngine playerEngine, PlaylistManager playlistManager) {
        this.playerEngine = playerEngine;
        this.playlistManager = playlistManager;

        content = new HBox(15);
        content.setId("control-bar");

        // Buttons
        Button addFileBtn = createIconButton("Add Files", "/resources/icons/add.png", 24);
        addFileBtn.setId("btn-add");
        
        Button previousBtn = createIconButton("Previous", "/resources/icons/previous.png", 24);
        Button playBtn = createIconButton("Play", "/resources/icons/play.png", 28);
        Button pauseBtn = createIconButton("Pause", "/resources/icons/pause.png", 28);
        Button stopBtn = createIconButton("Stop", "/resources/icons/stop.png", 24);
        Button nextBtn = createIconButton("Next", "/resources/icons/next.png", 24);
        muteBtn = createIconButton("Mute", "/resources/icons/mute.png", 24);

        previousBtn.getStyleClass().add("control-btn");
        playBtn.getStyleClass().add("control-btn");
        pauseBtn.getStyleClass().add("control-btn");
        stopBtn.getStyleClass().add("control-btn");
        nextBtn.getStyleClass().add("control-btn");
        muteBtn.getStyleClass().add("control-btn");
        addFileBtn.getStyleClass().add("control-btn");

        // VOLUME SLIDER SYNC
        Slider volumeSlider = new Slider(0, 100, 70);
        volumeSlider.setId("volume-slider");
        
        //When Slider moves -> Update Engine
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            double newVol = newVal.doubleValue() / 100.0;
            //Only update if different to prevent infinite loop
            if (Math.abs(playerEngine.volumeProperty().get() - newVol) > 0.01) {
                playerEngine.volumeProperty().set(newVol);
                
                // Unmute if user drags slider up
                if (newVal.doubleValue() > 0 && playerEngine.mutedProperty().get()) {
                    playerEngine.mutedProperty().set(false);
                    muteBtn.getStyleClass().remove("muted-state");
                }
            }
        });
        
        // When Engine updates (from Keyboard) -> Move Slider
        playerEngine.volumeProperty().addListener((obs, oldVal, newVal) -> {
            double newSliderVal = newVal.doubleValue() * 100;
            // Only update if different to prevent infinite loop
            if (Math.abs(volumeSlider.getValue() - newSliderVal) > 0.1) {
                volumeSlider.setValue(newSliderVal);
            }
        });
        

        Label volumeLabel = new Label("Volume:");
        volumeLabel.getStyleClass().add("volume-label");

        // Actions
        addFileBtn.setOnAction(e -> handleAddFiles());
        playBtn.setOnAction(e -> playerEngine.play());
        pauseBtn.setOnAction(e -> playerEngine.pause());
        stopBtn.setOnAction(e -> playerEngine.stop());
        previousBtn.setOnAction(e -> playlistManager.previous());
        nextBtn.setOnAction(e -> playlistManager.next());
        
        muteBtn.setOnAction(e -> {
            playerEngine.toggleMute();
            if (playerEngine.mutedProperty().get()) {
                muteBtn.getStyleClass().add("muted-state");
            } else {
                muteBtn.getStyleClass().remove("muted-state");
            }
        });

        Label separator1 = new Label("|");
        separator1.getStyleClass().add("separator");
        Label separator2 = new Label("|");
        separator2.getStyleClass().add("separator");

        content.getChildren().addAll(
            addFileBtn, separator1, previousBtn, playBtn, pauseBtn, stopBtn, nextBtn, 
            separator2, muteBtn, volumeLabel, volumeSlider
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        content.getChildren().add(spacer);
    }

    private Button createIconButton(String tooltip, String iconPath, int size) {
        Button button = new Button();
        button.setTooltip(new Tooltip(tooltip));
        
        try {
            Image icon = new Image(getClass().getResourceAsStream(iconPath));
            ImageView imageView = new ImageView(icon);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            button.setGraphic(imageView);
        } catch (Exception e) {
            button.setText(tooltip);
        }
        return button;
    }

    private void handleAddFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Media Files");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi", "*.mkv", "*.webm"),
            new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.aac", "*.flac", "*.ogg"),
            new FileChooser.ExtensionFilter("All Media", "*.*")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(null);
        if (files != null) {
            playlistManager.add(files);
        }
    }

    public HBox getContent() {
        return content;
    }
}