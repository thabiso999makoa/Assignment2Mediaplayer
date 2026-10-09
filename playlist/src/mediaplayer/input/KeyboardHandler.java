package mediaplayer.input;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import mediaplayer.player.PlayerEngine;
import mediaplayer.playlist.PlaylistManager;

public class KeyboardHandler {

    private final PlayerEngine playerEngine;
    private final PlaylistManager playlistManager;

    public KeyboardHandler(PlayerEngine playerEngine, PlaylistManager playlistManager) {
        this.playerEngine = playerEngine;
        this.playlistManager = playlistManager;
    }

    public void attachToScene(Scene scene) {
        // Use addEventFilter to catch keys before controls like Slider steal them
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();

            switch (code) {
                case SPACE:
                    playerEngine.togglePlayPause();
                    event.consume(); 
                    break;

                case S:
                    playerEngine.stop();
                    event.consume();
                    break;

                case N:
                    playlistManager.next();
                    event.consume();
                    break;

                case P:
                    playlistManager.previous();
                    event.consume();
                    break;

                case UP:
                    playerEngine.increaseVolume(); // Engine updates -> Slider moves automatically
                    event.consume(); 
                    break;

                case DOWN:
                    playerEngine.decreaseVolume(); // Engine updates -> Slider moves automatically
                    event.consume(); 
                    break;

                case M:
                    playerEngine.toggleMute();
                    event.consume();
                    break;

                default:
                    break;
            }
        });
    }
}