package com.example.pract3class;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {
    SharedPreferences themeSettings;
    SharedPreferences.Editor settingsEditor;
    ImageButton imageTheme;
    private Button[][] buttons = new Button[3][3];
    private GameLogic gameLogic;

    // Статистики для разных режимов
    private TextView statisticsTextViewPlayerVsFriend; // Статистика для игры с другом
    private TextView statisticsTextViewPlayerVsBot; // Статистика для игры с ботом
    private boolean isPlayerVsBot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Скрываем текст заголовка
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        themeSettings = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        if (!themeSettings.contains("MODE_NIGHT_NO")) {
            settingsEditor = themeSettings.edit();
            settingsEditor.putBoolean("MODE_NIGHT_NO", false);
            settingsEditor.apply();
        } else {
            setCurentTheme();
        }
        setContentView(R.layout.activity_main);

        imageTheme = findViewById(R.id.img);
        updateImageTheme();
        imageTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_NO", false);
                    settingsEditor.apply();
                    Toast.makeText(MainActivity.this, "Темная тема отключена", Toast.LENGTH_SHORT).show();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    settingsEditor = themeSettings.edit();
                    settingsEditor.putBoolean("MODE_NIGHT_NO", true);
                    settingsEditor.apply();
                    Toast.makeText(MainActivity.this, "Темная тема включена", Toast.LENGTH_SHORT).show();
                }
                restartActivity();
            }
        });
        gameLogic = new GameLogic();
        loadStatistics();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setEnabled(false); // Изначально отключаем кнопки
                buttons[i][j].setOnClickListener(new ButtonClickListener(i, j));
            }
        }

        statisticsTextViewPlayerVsFriend = findViewById(R.id.statisticsTextViewPlayerVsFriend);
        statisticsTextViewPlayerVsBot = findViewById(R.id.statisticsTextViewPlayerVsBot);
        updateStatistics();
        Button resetButtonFriend = findViewById(R.id.resetButtonVsFriend);
        resetButtonFriend.setOnClickListener(v -> resetStatistics(true));

        Button resetButtonBot = findViewById(R.id.resetButtonVsBot);
        resetButtonBot.setOnClickListener(v -> resetStatistics(false));

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioPlayerVsBot) {
                isPlayerVsBot = true;
                gameLogic.setPlayerVsBot(true);
                Toast.makeText(MainActivity.this, "Выбран режим игры с ботом", Toast.LENGTH_SHORT).show(); // Сообщение о режиме
            } else if (checkedId == R.id.radioPlayerVsPlayer) {
                isPlayerVsBot = false;
                gameLogic.setPlayerVsBot(false);
                Toast.makeText(MainActivity.this, "Выбран режим игры с другом", Toast.LENGTH_SHORT).show(); // Сообщение о режиме
            }
            enableGameButtons();
            resetBoard();
            updateStatistics();
        });
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void updateImageTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
            imageTheme.setImageResource(R.drawable.moon);
        } else {
            imageTheme.setImageResource(R.drawable.sun);
        }
    }

    private void setCurentTheme() {
        if (themeSettings.getBoolean("MODE_NIGHT_NO", false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private class ButtonClickListener implements View.OnClickListener {
        private final int row;
        private final int col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            if (gameLogic.makeMove(row, col)) {
                buttons[row][col].setText(String.valueOf(gameLogic.getCurrentPlayer()));

                if (gameLogic.checkForWin()) {
                    gameLogic.incrementWins(gameLogic.getCurrentPlayer());
                    updateStatistics();
                    resetBoard();
                } else if (gameLogic.isBoardFull()) {
                    gameLogic.incrementDraws();
                    updateStatistics();
                    resetBoard();
                } else {
                    gameLogic.switchPlayer();
                    handleBotMove();
                }
            }
        }
    }

    private void enableGameButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(true);
            }
        }
    }
    private void updateButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText(String.valueOf(gameLogic.getCell(i, j)));
            }
        }
    }

    private void resetBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                gameLogic.clearCell(i, j);
            }
        }
    }
    private void resetStatistics(boolean isFriend) {
        if (isFriend) {
            gameLogic.resetStatisticsForFriend();
        } else {
            gameLogic.resetStatisticsForBot();
        }
        updateStatistics();
    }

    private void updateStatistics() {
        statisticsTextViewPlayerVsFriend.setText("Победы X: " + gameLogic.getPlayerXWinsVsFriend() +
                "\nПобеды O: " + gameLogic.getPlayerOWinsVsFriend() +
                "\nНичьи: " + gameLogic.getDrawsVsFriend());
        statisticsTextViewPlayerVsBot.setText("Победы X: " + gameLogic.getPlayerXWinsVsBot() +
                "\nПобеды O: " + gameLogic.getPlayerOWinsVsBot() +
                "\nНичьи: " + gameLogic.getDrawsVsBot());
        saveStatistics();
    }

    private void saveStatistics() {
        SharedPreferences.Editor editor = themeSettings.edit();

        editor.putInt("playerXWinsVsFriend", gameLogic.getPlayerXWinsVsFriend());
        editor.putInt("playerOWinsVsFriend", gameLogic.getPlayerOWinsVsFriend());
        editor.putInt("drawsVsFriend", gameLogic.getDrawsVsFriend());

        editor.putInt("playerXWinsVsBot", gameLogic.getPlayerXWinsVsBot());
        editor.putInt("playerOWinsVsBot", gameLogic.getPlayerOWinsVsBot());
        editor.putInt("drawsVsBot", gameLogic.getDrawsVsBot());
        editor.apply();
    }

    private void loadStatistics() {
        int playerXWinsVSFriend = themeSettings.getInt("playerXWinsVSFriend", 0);
        int playerOWinsVSFriend = themeSettings.getInt("playerOWinsVSFriend", 0);
        int drawsVSFriend = themeSettings.getInt("drawsVsFriend", 0);

        int playerXWinsVSbot = themeSettings.getInt("playerXWinsVSbot", 0);
        int playerOWinsVSbot = themeSettings.getInt("playerOWinsVSbot", 0);
        int drawsVSbot = themeSettings.getInt("drawsVsbot", 0);

        gameLogic.setPlayerXWinsVsFriend(playerXWinsVSFriend);
        gameLogic.setPlayerOWinsVsFriend(playerOWinsVSFriend);
        gameLogic.setDrawsVsFriend(drawsVSFriend);
        gameLogic.setPlayerXWinsVsBot(playerXWinsVSbot);
        gameLogic.setPlayerOWinsVsBot(playerOWinsVSbot);
        gameLogic.setDrawsVsBot(drawsVSbot);
    }

    private void handleBotMove() {
        if (isPlayerVsBot && gameLogic.getCurrentPlayer() == 'O') {
            gameLogic.makeBotMove();
            updateButtons();
            if(gameLogic.checkForWin()){
                gameLogic.incrementWins(gameLogic.getCurrentPlayer());
                updateStatistics();
                resetBoard();
            } else if(gameLogic.isBoardFull()){
                gameLogic.incrementDraws();
                updateStatistics();
                resetBoard();
            }
            gameLogic.switchPlayer();
        }
    }
}

