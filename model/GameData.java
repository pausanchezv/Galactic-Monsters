package com.pausanchezv.puzzle.model;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.pausanchezv.puzzle.controller.GameController;

/**
 * Game Data Class
 */
public final class GameData {

    // Game variables
    private Level currentLevel;
    private int movesLeft;

    private static final String PREFERENCES = "Preferences";


    /**
     * Get moves left
     */
    public int getMovesLeft() {
        return movesLeft;
    }


    /**
     * Get current level
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }


    /**
     * Set moves left
     */
    public void setMovesLeft(int movesLeft) {
        this.movesLeft = movesLeft;

    }

    /**
     * Set current level number
     */
    public void setCurrentLevel(Level currentLevel) {
        this.currentLevel = currentLevel;
    }

    /**
     * Set current level number
     */
    public void setCurrentLevelNumber(Activity context, int currentLevelNumber, GameController.GameModes currentGameMode) {


        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();


        switch (currentGameMode) {

            case MATCHING:
                editor.putInt("currentLevelNumberMatching", currentLevelNumber);
                break;

            case CLUSTERING:
                editor.putInt("currentLevelNumberClustering", currentLevelNumber);
                break;

            case MIXED:
                editor.putInt("currentLevelNumberMixed", currentLevelNumber);
                break;
        }

        editor.apply();

    }

    /**
     * Get current level number
     */
    public int getCurrentLevelNumber(Activity context, GameController.GameModes currentGameMode) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        if (currentGameMode == null) return -1;

        switch (currentGameMode) {

            case MATCHING:
                return preferences.getInt("currentLevelNumberMatching", 0);

            case CLUSTERING:
                return preferences.getInt("currentLevelNumberClustering", 0);

            case MIXED:
                return preferences.getInt("currentLevelNumberMixed", 0);
        }

        return 1;
    }

    /**
     * Set drag
     */
    public void setDragActive(Activity context, boolean isActive) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isDragActive", isActive);
        editor.apply();
    }

    /**
     * Get drag
     */
    public boolean isDragActive(Activity context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("isDragActive", true);
    }

    /**
     * Set stars
     */
    public void setNumUserStars(Activity context, int num) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt("numUserStars", num);
        editor.apply();
    }

    /**
     * Get Stars
     */
    public int getNumUserStars(Activity context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("numUserStars", 0);
    }

    /**
     * Get extra moves
     */
    public int getUserExtraMoves(Activity context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getInt("userExtraMoves", 0);
    }

    /**
     * Set extra moves
     */
    public void inrUserExtraMoves(Activity context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt("userExtraMoves", getUserExtraMoves(context) + 1);
        editor.apply();
    }

    /**
     * Set stars
     */
    public void dcrUserExtraMoves(Activity context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt("userExtraMoves", getUserExtraMoves(context) - 1);
        editor.apply();
    }

    /**
     * Set stars
     */
    public void setMusicEnabled(Activity context, boolean isEnabled) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean("isMusicEnabled", isEnabled);
        editor.apply();
    }

    /**
     * Get Stars
     */
    public boolean isMusicEnabled(Activity context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return preferences.getBoolean("isMusicEnabled", true);
    }


    /**
     * Clear all data
     */
    public void createNewData(Activity context) {

        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putInt("currentLevelNumberMatching", 1);
        editor.putInt("currentLevelNumberClustering", 1);
        editor.putInt("currentLevelNumberMixed", 1);
        editor.putBoolean("isDragActive", true);
        editor.putInt("numUserStars", 0);
        editor.putInt("userExtraMoves", 0);
        editor.putBoolean("isMusicEnabled", true);

        // Default userId -1 because there is no session yet
        editor.putInt("userId", -1);

        editor.apply();
    }

    /**
     * Prepare data
     */
    public void prepareData(Activity context) {

        SharedPreferences preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);

        int currentLevelNumberMatching = preferences.getInt("currentLevelNumberMatching", 0);
        int currentLevelNumberClustering = preferences.getInt("currentLevelNumberClustering", 0);
        int currentLevelNumberMixed = preferences.getInt("currentLevelNumberMixed", 0);

        // If the level numbers are less than 0 it means that there is a new user
        if (currentLevelNumberMatching < 1 && currentLevelNumberClustering < 1 && currentLevelNumberMixed < 1) {
            createNewData(context);
        }
    }
}
