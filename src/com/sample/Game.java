package com.sample;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.Random;

public class Game {
    static StringProperty scoreProperty;
    static StringProperty timeProperty;
    static StringProperty triesProperty;
    private static int time;
    private static int score;
    private static int tries;
    private static int[][] tiles;
    private static boolean gameIsOver;
    private static boolean firstClick;

    public Game() {
        System.out.println("Game created...");
        tries = 0;
        score = 100000;
        time = 0;
        gameIsOver = false;
        firstClick = false;
        scoreProperty = new SimpleStringProperty("" + score);
        timeProperty = new SimpleStringProperty("0");
        triesProperty = new SimpleStringProperty("0");
        tiles = new int[10][10];
        Random random = new Random();
        int treasureColumn = random.nextInt(10);
        int treasureRow = random.nextInt(10);
        System.out.printf("Treasure location:%d,%d(row,col)\n", treasureRow, treasureColumn);
        tiles[treasureRow][treasureColumn] = 1;
    }

    static boolean firstClickHappened() {
        return firstClick;
    }

    static void setGameOver() {
        gameIsOver = true;
    }

    static boolean isGameIsOver() {
        return gameIsOver;
    }

    static int getScore() {
        return score;
    }

    static void scoreCalculator() {
        time++;
        if (time < 10) {
            score = score - 100;
        } else if (time < 20) {
            score = score - 200;
        } else if (time < 30) {
            score = score - 300;
        } else if (time < 50) {
            score = score - 500;
        } else {
            score = score - 1000;
        }
        if (score < 0) {
            score = 0;
        }
        scoreProperty.setValue("" + score);
        timeProperty.setValue("" + time);
        triesProperty.setValue("" + tries);
        System.out.printf("Score:%s,Time:%s,Tries%s\n", scoreProperty.getValue(), timeProperty.getValue(), triesProperty.getValue());
    }


    static String click(int row, int column) {
        if (!firstClickHappened()) firstClick = true;
        System.out.println(row + "," + column);
        int clickValue = tiles[row][column];
        System.out.println(clickValue);
        if (clickValue == 0) {
            tries++;
            score -= 1000;
        } else {
            setGameOver();
        }
        return (clickValue == 1) ? "button-treasure" : "button-uncovered";
    }
}

