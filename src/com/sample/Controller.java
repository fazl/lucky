package com.sample;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/* Class Controller is listed as the fx:controller attribute in the fxml GUI layout files.
   This means it holds the methods that listen to GUI events..
 */

public class Controller implements Initializable {
    private static final Duration TRANSITION_LEN = new Duration(150);

    // Following fxml fields will be missing/null unless the scene loaded
    // by FXMLLoader contains the respective widgets.
    // So when menu scene is loaded, only the buttons for new game, howto, credits are available
    // If the newgame button is pressed, then the game scene will load and a new controller object
    // will be created during the FXMLLoader processing.  This time it will only inject the
    // fields on the main game i.e. score, time,  tries, but not the newGame, howTo etc buttons
    // Hence it is necessary to check before using these fields.

    @FXML
    public RadioButton mute;
    @FXML
    public Button newGame;
    @FXML
    public Button howTo;
    @FXML
    public Button credits;
    @FXML
    public Button exit;
    @FXML
    public Button menu;
    @FXML
    public GridPane pane;
    @FXML
    public Label score;
    @FXML
    public Label time;
    @FXML
    public Label tries;

    private boolean animating;

    //Looks like many instances of Controller are created
    //In particular whenever a new game starts..
    //
    private static int instances = 0;
    private final int id;

    //Called via reflection in FXMLLoader during loading of initial scene for primary stage!
    //(Via digging in debugger)
    //
    public Controller() {
        id = ++instances;
        System.out.printf("%s object #%d created\n", this.getClass().getSimpleName(), id);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {  //the correct override..
        animating = false;
        switch( Game.activeWindow){
            case GAME:
                score.textProperty().bind(Game.scoreProperty);
                time.textProperty().bind(Game.timeProperty);
                tries.textProperty().bind(Game.triesProperty);
            case MENU: // mute is on both GAME and MENU screens
                mute.setSelected(Main.isMuted);
        }
    }

    public void newGameButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Game.resetGame();
            Game.activeWindow = ActiveWindow.GAME;
            Main.window.hide();
            Main.window.setScene(getScene("game"));
            Main.window.setTitle("The Main Pick");
            Main.window.setMaximized(false);
            Main.playBGM("bgm_game");
            Main.window.show();
        } catch (IOException e) {
            System.err.println("could not change the scene to: game");
        }
    }

    public void menuButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_menu");
            Game.activeWindow = ActiveWindow.MENU;
            if (Main.window.getTitle().equals("The Main Pick")) {
                Main.window.hide();
                Game.setGameIsOver();
                Main.window.setScene(getScene("menu"));
                Main.window.setMaximized(false);
                Main.window.show();
            } else {
                Main.window.setScene(getScene("menu"));
            }
            Main.window.setTitle("Main Menu");
        } catch (IOException e) {
            System.err.println("could not change the scene to: game");
        }
    }

    public void gameButtonClicked(ActionEvent event) {
        ObservableList<Node> buttons = pane.getChildren();
        Button button = (Button) event.getSource();
        int index = buttons.indexOf(button);
        int column = index % 10;
        int row = (index - index % 10) / 10;
        if (!((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure") &&
                !((Button) event.getSource()).getStyleClass().toString().equals("button button-uncovered")) {

            FadeTransition transition = new FadeTransition();
            transition.setNode((Button) event.getSource());
            transition.setDuration(TRANSITION_LEN);
            transition.setFromValue(1.0);
            transition.setToValue(0.0);
            transition.setCycleCount(1);
            transition.setOnFinished(actionEvent -> {

                if (!((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure") &&
                        !((Button) event.getSource()).getStyleClass().toString().equals("button button-uncovered")) {

                    transition.setFromValue(0.0);
                    transition.setToValue(1.0);
                    System.out.println(((Button) event.getSource()).getStyleClass().toString());
                    ((Button) event.getSource()).getStyleClass().remove("button-covered");
                    ((Button) event.getSource()).getStyleClass().add(Game.click(row, column));
                    transition.play();
                    transition.setOnFinished(ActionEvent -> {
                        animating = false
                        ;
                        if (((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure")) {
                            loadVictoryScene();

                        }
                    });
                }
            });
            System.out.printf("Animating: %b\n", animating);
            if (!animating) {
                animating = true;
                transition.play();
                Main.playSFX("sfx_card_unfold");
            }
        }

        System.out.printf("button:%d (row:%d,col:%d)\n", index, row, column);
    }

    public void howToPlayButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_how_to");
            Main.window.setScene(getScene("howTo"));
            Main.window.setTitle("How to Play");
        } catch (IOException e) {
            System.err.println("could not change the scene to: how to play");
        }
    }

    public void creditsButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_credits");
            Main.window.setScene(getScene("credits"));
            Main.window.setTitle("Credits");
        } catch (IOException e) {
            System.err.println("could not change the scene to: credits");
        }
    }

    public void exitButtonClicked() {
        Main.playSFX("sfx_button_clicked");
        Main.window.close();
    }

    public void muteRadioButtonChecked() {
        Main.playSFX("sfx_toggle");
        if (mute.isSelected()) {
            Main.isMuted = true;
            Main.mediaPlayerBGM.setVolume(0.0);
            Main.mediaPlayerSFX.setVolume(0.0);
            System.out.println("muted");
        } else {
            Main.isMuted = false;
            Main.mediaPlayerBGM.setVolume(1.0);
            Main.mediaPlayerSFX.setVolume(1.0);

            System.out.println("unmuted");
        }
    }

    private void loadVictoryScene() {
        System.out.println("Loading victory scene\n");
        try {
            Main.window.hide();
            Main.playBGM("bgm_victory");
            Game.activeWindow = ActiveWindow.VICTORY;
            Main.window.setScene(getScene("victory"));
            Main.window.setTitle("Victory");
            Main.window.setMaximized(false);
            Main.window.show();
        } catch (IOException e) {
            System.err.println("could not change the scene to: victory");
        }
    }

    private Scene getScene(String name) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(name + ".fxml"));
        if (name.equals("game")) {
            return new Scene(root, 800, 800);
        }
        return new Scene(root, 600, 600);
    }

}