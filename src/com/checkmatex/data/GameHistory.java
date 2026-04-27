package com.checkmatex.data;

public class GameHistory {
    private int id;
    private String winner;
    private String moves;
    private String duration;
    private String dateTime;

    public GameHistory(int id, String winner, String moves, String duration, String dateTime) {
        this.id = id;
        this.winner = winner;
        this.moves = moves;
        this.duration = duration;
        this.dateTime = dateTime;
    }

    public int getId() { return id; }
    public String getWinner() { return winner; }
    public String getMoves() { return moves; }
    public String getDuration() { return duration; }
    public String getDateTime() { return dateTime; }

    @Override
    public String toString() {
        return "Game " + id + " - " + winner + " won (" + dateTime + ")";
    }
}
