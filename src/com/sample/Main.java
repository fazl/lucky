package com.sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Main extends Application {
    private static Random random = new Random();
    private static final double ONE_MEGA_BYTE = 1024 * 1024;
    static boolean isMuted;
    static Stage window;
    static MediaPlayer mediaPlayerBGM;
    static MediaPlayer mediaPlayerSFX;
    private static HashMap<String, Media> sounds = new HashMap<>();
    private static ArrayList<Double> usage = new ArrayList<>();
    private final String[] SOUND_LIST = {"bgm_credits.mp3", "bgm_game.mp3", "bgm_game_1.mp3", "bgm_game_2.mp3", "bgm_game_3.mp3",
            "bgm_how_to.mp3", "bgm_menu.mp3", "bgm_victory.mp3", "sfx_button_clicked.wav",
            "sfx_card_unfold.wav", "sfx_toggle.wav"
    };

    public static void main(String[] args) {
        launch(args);
        new MemoryTrack(usage);

    }

    static void playBGM(String key) {
        mediaPlayerBGM.stop();
        mediaPlayerBGM.dispose();
        if (key.equals("bgm_game")) {
            String[] suffix = {"", "_1", "_2", "_3"};
            mediaPlayerBGM = new MediaPlayer(
                sounds.get(key + suffix[random.nextInt(suffix.length)])
            );
        } else {
            mediaPlayerBGM = new MediaPlayer(sounds.get(key));
        }
        mediaPlayerBGM.setStartTime(Duration.ZERO);
        mediaPlayerBGM.setCycleCount(MediaPlayer.INDEFINITE);
        if (isMuted) {
            mediaPlayerBGM.setVolume(0.0);
        }else{
            mediaPlayerBGM.play();
        }
    }

    static void playSFX(String key) {
        if (mediaPlayerSFX != null) {
            mediaPlayerSFX.stop();
            mediaPlayerSFX.dispose();
        }
        mediaPlayerSFX = new MediaPlayer(sounds.get(key));
        if (isMuted) {
            mediaPlayerSFX.setVolume(0.0);
        }else {
            mediaPlayerSFX.setVolume(0.25);
            mediaPlayerSFX.play();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        isMuted = false;
        for (String soundName : SOUND_LIST) {
            URL resource = getClass().getResource("/" + soundName);
            String key = soundName.substring(0, soundName.lastIndexOf('.'));
            if( !sounds.containsKey(key)){
                System.out.printf("Adding sound %s -> %s\n", soundName, resource);
                sounds.put(key, new Media(resource.toString()));
            }
        }
        mediaPlayerBGM = new MediaPlayer(sounds.get("bgm_menu"));
        mediaPlayerBGM.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayerBGM.play();
        window = primaryStage;
        Parent rootMenu = FXMLLoader.load(getClass().getResource("menu.fxml"));
        // long running operation runs on different thread
        Thread scoreThread = new Thread(() -> {
            Runnable updater = () -> {
                if (!Game.isGameIsOver() && Game.getScore() != 0 && window.getTitle().equals("The Main Pick") &&
                        Game.firstClickHappened()) {
                    Game.scoreCalculator();
                }
            };

            while (true) {
                try {
                    Runtime r = Runtime.getRuntime();
                    double mbUsed = (r.totalMemory() - r.freeMemory()) /ONE_MEGA_BYTE;
                    System.err.printf("MB used = %f.\n", mbUsed);
                    usage.add(mbUsed);
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted");
                }

                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        });
        // don't let thread prevent JVM shutdown
        scoreThread.setDaemon(true);
        scoreThread.start();
        window.setTitle("Main Menu");
        window.setScene(new Scene(rootMenu, 600, 600));
        window.setResizable(false);
        window.show();
    }
}