package com.checkmatex.logic;

import javax.swing.Timer;


public class TimerManager {

    private int whiteTimeRemaining; // in seconds
    private int blackTimeRemaining; // in seconds
    private Timer timer;
    private int activeColor;
    private Runnable updateCallback;
    private Runnable timeoutCallback;

    public TimerManager(int initialMinutes, Runnable updateCallback, Runnable timeoutCallback) {
        this.whiteTimeRemaining = initialMinutes * 60;
        this.blackTimeRemaining = initialMinutes * 60;
        this.updateCallback = updateCallback;
        this.timeoutCallback = timeoutCallback;
        this.activeColor = -1; // Not started

        timer = new Timer(1000, e -> {
            if (activeColor == com.checkmatex.utils.Constants.WHITE) {
                whiteTimeRemaining--;
                if (whiteTimeRemaining <= 0) handleTimeout();
            } else if (activeColor == com.checkmatex.utils.Constants.BLACK) {
                blackTimeRemaining--;
                if (blackTimeRemaining <= 0) handleTimeout();
            }
            if (updateCallback != null) updateCallback.run();
        });
    }

    public void start(int color) {
        this.activeColor = color;
        timer.start();
    }

    public void stop() {
        timer.stop();
        this.activeColor = -1;
    }

    public void switchTurn(int newColor) {
        this.activeColor = newColor;
        if (!timer.isRunning()) timer.start();
    }

    private void handleTimeout() {
        timer.stop();
        if (timeoutCallback != null) timeoutCallback.run();
    }

    public int getWhiteTime() { return whiteTimeRemaining; }
    public int getBlackTime() { return blackTimeRemaining; }
    
    public void setTimes(int whiteTime, int blackTime) {
        this.whiteTimeRemaining = whiteTime;
        this.blackTimeRemaining = blackTime;
        if (updateCallback != null) updateCallback.run();
    }

    public static String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }
}
