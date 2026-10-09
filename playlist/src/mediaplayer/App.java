package mediaplayer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import mediaplayer.input.KeyboardHandler;
import mediaplayer.player.PlayerEngine;
import mediaplayer.playlist.PlaylistManager;
import mediaplayer.ui.ControlBar;
import mediaplayer.ui.PlaylistPanel;
import mediaplayer.ui.VideoPanel;

import java.net.URL;

public class App extends Application {

    private PlayerEngine playerEngine;
    private PlaylistManager playlistManager;

    @Override
    public void start(Stage primaryStage) {
        // Initialize core components
        playerEngine = new PlayerEngine();
        playlistManager = new PlaylistManager();

        // Create UI components
        VideoPanel videoPanel = new VideoPanel(playerEngine);
        ControlBar controlBar = new ControlBar(playerEngine, playlistManager);
        PlaylistPanel playlistPanel = new PlaylistPanel(playlistManager);

        // Setup layout: Video center, Controls bottom, Playlist right (sidebar)
        BorderPane mainLayout = new BorderPane();
        mainLayout.setCenter(videoPanel.getContent());
        mainLayout.setBottom(controlBar.getContent());
        mainLayout.setRight(playlistPanel.getContent());

        Scene scene = new Scene(mainLayout, 1100, 700);
        scene.setFill(Color.BLACK);

        // Load CSS from src/resources/css/styles.css
        String cssPath = "/resources/css/styles.css";
        URL cssResource = getClass().getResource(cssPath);
        
        if (cssResource != null) {
            scene.getStylesheets().add(cssResource.toExternalForm());
            System.out.println("CSS loaded successfully from: " + cssPath);
        } else {
            System.out.println("Warning: CSS not found at " + cssPath);
        }

        // Attach keyboard controls
        KeyboardHandler keyboardHandler = new KeyboardHandler(playerEngine, playlistManager);
        keyboardHandler.attachToScene(scene);

        // Handle end of media - auto play next
        playerEngine.setOnEndOfMedia(() -> playlistManager.playNextAfterEnd());
        playerEngine.setOnError(message -> System.err.println("Media Error: " + message));

        // Listen for playlist changes to auto-load and play new selection
        playlistManager.currentProperty().addListener((obs, oldFile, newFile) -> {
            if (newFile != null) {
                playerEngine.load(newFile, true);
            }
        });

        primaryStage.setTitle("Media Player - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();
        
        // Request focus so keyboard shortcuts work immediately
        scene.getRoot().requestFocus();
    }

    @Override
    public void stop() {
        if (playerEngine != null) {
            playerEngine.dispose();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}