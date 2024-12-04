package com.example.pract3class;

import java.util.Random;

public class GameLogic {

    private char[][] board = new char[3][3];
    private char currentPlayer = 'X';

    private int playerXWinsVsPlayer = 0;
    private int playerOWinsVsPlayer = 0;
    private int drawsVsPlayer = 0;

    private int playerXWinsVsBot = 0;
    private int playerOWinsVsBot = 0;
    private int drawsVsBot = 0;

    private boolean isPlayerVsBot;

    public GameLogic() {
        currentPlayer = 'X';
        clearBoard();
    }

    public void setPlayerVsBot(boolean isPlayerVsBot) {
        this.isPlayerVsBot = isPlayerVsBot;
    }

    public boolean makeMove(int row, int col) {
        if (board[row][col] == '\0') {
            board[row][col] = currentPlayer;
            return true;
        }
        return false;
    }

    public boolean checkForWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i][0] == currentPlayer && board[i][1] == currentPlayer && board[i][2] == currentPlayer) return true;
            if (board[0][i] == currentPlayer && board[1][i] == currentPlayer && board[2][i] == currentPlayer) return true;
        }
        if (board[0][0] == currentPlayer && board[1][1] == currentPlayer && board[2][2] == currentPlayer) return true;
        return board[0][2] == currentPlayer && board[1][1] == currentPlayer && board[2][0] == currentPlayer;
    }

    public boolean makeBotMove() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (board[row][col] != '\0');

        board[row][col] = currentPlayer;
        return true;
    }

    public char getCell(int row, int col) {
        return board[row][col];
    }

    public boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '\0') return false;
            }
        }
        return true;
    }

    public void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public void incrementWins(char player) {
        if (isPlayerVsBot) {
            if (player == 'X') playerXWinsVsBot++;
            else playerOWinsVsBot++;
        } else {
            if (player == 'X') playerXWinsVsPlayer++;
            else playerOWinsVsPlayer++;
        }
    }

    public void incrementDraws() {
        if (isPlayerVsBot) {
            drawsVsBot++;
        } else {
            drawsVsPlayer++;
        }
    }

    public void resetStatisticsForFriend() {
        playerXWinsVsPlayer = 0;
        playerOWinsVsPlayer = 0;
        drawsVsPlayer = 0;
    }

    public void resetStatisticsForBot() {
        playerXWinsVsBot = 0;
        playerOWinsVsBot = 0;
        drawsVsBot = 0;
    }

    public void clearCell(int row, int col) {
        board[row][col] = '\0';
    }

    public void clearBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                clearCell(i, j);
            }
        }
    }

    public int getPlayerXWinsVsFriend() {
        return playerXWinsVsPlayer;
    }

    public int getPlayerOWinsVsFriend() {
        return playerOWinsVsPlayer;
    }

    public int getDrawsVsFriend() {
        return drawsVsPlayer;
    }

    public int getPlayerXWinsVsBot() {
        return playerXWinsVsBot;
    }

    public int getPlayerOWinsVsBot() {
        return playerOWinsVsBot;
    }

    public int getDrawsVsBot() {
        return drawsVsBot;
    }

    public void setPlayerXWinsVsFriend(int wins) {
        this.playerXWinsVsPlayer = wins;
    }

    public void setPlayerOWinsVsFriend(int wins) {
        this.playerOWinsVsPlayer = wins;
    }

    public void setDrawsVsFriend(int draws) {
        this.drawsVsPlayer = draws;
    }

    public void setPlayerXWinsVsBot(int wins) {
        this.playerXWinsVsBot = wins;
    }

    public void setPlayerOWinsVsBot(int wins) {
        this.playerOWinsVsBot = wins;
    }

    public void setDrawsVsBot(int draws) {
        this.drawsVsBot = draws;
    }
}