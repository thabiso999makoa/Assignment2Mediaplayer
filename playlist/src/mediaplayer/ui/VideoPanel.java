package mediaplayer.ui;

import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;

import mediaplayer.player.PlayerEngine;

public class VideoPanel {

    private final StackPane content;
    private final MediaView mediaView;

    public VideoPanel(PlayerEngine playerEngine) {
        mediaView = new MediaView();
        mediaView.setFitWidth(800);
        mediaView.setFitHeight(500);
        mediaView.setPreserveRatio(true);

        mediaView.mediaPlayerProperty().bind(playerEngine.mediaPlayerProperty());

        content = new StackPane(mediaView);
        content.setId("video-panel"); // CSS ID
        content.setPrefSize(800, 500);
    }

    public StackPane getContent() { return content; }
}