package mediaplayer.player;

import java.io.File;
import java.util.function.Consumer;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class PlayerEngine {

    private static final double VOLUME_STEP = 0.05;

    private final ObjectProperty<MediaPlayer> mediaPlayer = new SimpleObjectProperty<>();
    private final DoubleProperty volume = new SimpleDoubleProperty(0.7);
    private final BooleanProperty muted = new SimpleBooleanProperty(false);
    private final BooleanProperty playing = new SimpleBooleanProperty(false);

    // Store volume before muting so it can restore it later
    private double savedVolume = 0.7; 

    private Runnable onEndOfMedia = () -> { };
    private Consumer<String> onError = message -> { };

    public void load(File file, boolean autoPlay) {
        disposeCurrent();
        try {
            Media media = new Media(file.toURI().toString());
            MediaPlayer player = new MediaPlayer(media);

            player.volumeProperty().bind(volume);
            player.muteProperty().bind(muted);

            player.setOnPlaying(() -> playing.set(true));
            player.setOnPaused(() -> playing.set(false));
            player.setOnStopped(() -> playing.set(false));
            player.setOnEndOfMedia(() -> {
                player.stop();
                onEndOfMedia.run();
            });
            player.setOnError(() -> onError.accept(
                    "Could not play \"" + file.getName() + "\": "
                            + player.getError().getMessage()));

            mediaPlayer.set(player);
            if (autoPlay) {
                player.play();
            }
        } catch (Exception e) {
            onError.accept("Could not open \"" + file.getName() + "\": " + e.getMessage());
        }
    }

    public void play() {
        MediaPlayer p = mediaPlayer.get();
        if (p != null) p.play();
    }

    public void pause() {
        MediaPlayer p = mediaPlayer.get();
        if (p != null) p.pause();
    }

    public void stop() {
        MediaPlayer p = mediaPlayer.get();
        if (p != null) p.stop();
    }

    public void togglePlayPause() {
        MediaPlayer p = mediaPlayer.get();
        if (p == null) return;
        if (p.getStatus() == MediaPlayer.Status.PLAYING) {
            p.pause();
        } else {
            p.play();
        }
    }

    public void increaseVolume() {
        // If muted and increasing volume, unmute first
        if (muted.get()) toggleMute();
        volume.set(Math.min(1.0, volume.get() + VOLUME_STEP));
    }

    public void decreaseVolume() {
        volume.set(Math.max(0.0, volume.get() - VOLUME_STEP));
        if (volume.get() == 0.0 && !muted.get()) {
            muted.set(true);
        }
    }

    //Mute sets volume to 0, which moves the slider automatically
    public void toggleMute() {
        if (muted.get()) {
            // Unmuting: Restore previous volume
            volume.set(savedVolume);
            muted.set(false);
        } else {
            // Muting: Save current volume and set to 0
            savedVolume = volume.get();
            volume.set(0.0); 
            muted.set(true);
        }
    }

    public void dispose() {
        disposeCurrent();
    }

    private void disposeCurrent() {
        MediaPlayer old = mediaPlayer.get();
        if (old != null) {
            old.volumeProperty().unbind();
            old.muteProperty().unbind();
            old.dispose();
        }
        playing.set(false);
        mediaPlayer.set(null);
    }

    public ObjectProperty<MediaPlayer> mediaPlayerProperty() { return mediaPlayer; }
    public DoubleProperty volumeProperty() { return volume; }
    public BooleanProperty mutedProperty() { return muted; }
    public ReadOnlyBooleanProperty playingProperty() { return playing; }
    public void setOnEndOfMedia(Runnable onEndOfMedia) { this.onEndOfMedia = onEndOfMedia; }
    public void setOnError(Consumer<String> onError) { this.onError = onError; }
}