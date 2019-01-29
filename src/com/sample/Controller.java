package com.sample;

import javafx.animation.FadeTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.io.IOException;

public class Controller {
    @FXML
    public RadioButton musicOnOff;
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

    public void initialize() {
        if (score != null && time != null && tries != null) {
            score.textProperty().bind(Game.scoreProperty);
            time.textProperty().bind(Game.timeProperty);
            tries.textProperty().bind(Game.triesProperty);
            animating = false;
        }
        if (musicOnOff != null) {
            if (Main.isMuted) {
                musicOnOff.setSelected(true);
            }
        }
    }

    public void newGameButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            new Game();
            Main.window.hide();
            Main.window.setScene(getScene("game"));
            Main.window.setTitle("The Main Pick");
            Main.window.setMaximized(true);
            Main.playBGM("bgm_game");
            Main.window.show();
        } catch (IOException e) {
            System.out.println("could not change the scene to: game");
        }
    }

    public void menuButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_menu");
            if (Main.window.getTitle().equals("The Main Pick")) {
                Main.window.hide();
                Game.setGameOver();
                Main.window.setScene(getScene("menu"));
                Main.window.setMaximized(false);
                Main.window.show();
            } else {
                Main.window.setScene(getScene("menu"));
            }
            Main.window.setTitle("Main Menu");
        } catch (IOException e) {
            System.out.println("could not change the scene to: game");
        }
    }

    public void gameButtonClicked(ActionEvent event) {
        ObservableList<Node> buttons = pane.getChildren();
        int index = buttons.indexOf(event.getSource());
        int column = index % 10;
        int row = (index - index % 10) / 10;
        if (!((Button) event.getSource()).getStyleClass().toString().equals("button button-treasure") &&
                !((Button) event.getSource()).getStyleClass().toString().equals("button button-uncovered")) {

            FadeTransition transition = new FadeTransition();
            transition.setNode((Button) event.getSource());
            transition.setDuration(new Duration(500));
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
            System.out.println(animating);
            if (!animating) {
                animating = true;
                transition.play();
                Main.playSFX("sfx_card_unfold");
            }
        }

        System.out.printf("button index:%d,row:%d,column:%d\n", index, row, column);
    }

    public void howToPlayButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_how_to");
            Main.window.setScene(getScene("howTo"));
            Main.window.setTitle("How to Play");
        } catch (IOException e) {
            System.out.println("could not change the scene to: how to play");
        }
    }

    public void creditsButtonClicked() {
        try {
            Main.playSFX("sfx_button_clicked");
            Main.playBGM("bgm_credits");
            Main.window.setScene(getScene("credits"));
            Main.window.setTitle("Credits");
        } catch (IOException e) {
            System.out.println("could not change the scene to: credits");
        }
    }

    public void exitButtonClicked() {
        Main.playSFX("sfx_button_clicked");
        Main.window.close();
    }

    public void musicOnOffRadioButtonChecked() {
        Main.playSFX("sfx_toggle");
        if (musicOnOff.isSelected()) {
            Main.isMuted = true;
            Main.mediaPlayerBGM.setVolume(0.0);
            Main.mediaPlayerSFX.setVolume(0.0);
            System.out.println("now selected");
        } else {
            Main.isMuted = false;
            Main.mediaPlayerBGM.setVolume(1.0);
            Main.mediaPlayerSFX.setVolume(1.0);
            System.out.println("unselected");
        }
    }

    private void loadVictoryScene() {
        try {
            Main.window.hide();
            Main.playBGM("bgm_victory");
            Main.window.setScene(getScene("victory"));
            Main.window.setTitle("Victory");
            Main.window.setMaximized(false);
            Main.window.show();
        } catch (IOException e) {
            System.out.println("could not change the scene to: victory");
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