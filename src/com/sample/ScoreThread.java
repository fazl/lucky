package com.sample;

import javafx.application.Platform;

public class ScoreThread extends Thread {
    private static final double ONE_MEGA_BYTE = 1024 * 1024;

    @Override
    public void run(){
        Runnable updater = () -> {
            if ( Main.window.getTitle().equals("The Main Pick") ) {
                Game.scoreCalculator();
            }
        };

        while (true) {
            try {
                Runtime r = Runtime.getRuntime();
                double mbUsed = (r.totalMemory() - r.freeMemory()) /ONE_MEGA_BYTE;
                System.err.printf("MB used = %f.\n", mbUsed);
                Main.usage.add(mbUsed);
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println("ScoreThread sleep interrupted!");
            }

            // UI update is run on the Application thread
            Platform.runLater(updater);
        }
    }
}
