package com.pausanchezv.puzzle.view;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Welcome 2
 */
public class Welcome2Activity extends AppCompatActivity {

    // Controller
    private GameController ctrl = GameController.getInstance();

    // Start level button
    private Button startButton;
    private LinearLayout mainContainer;

    // Handler to prepare the level
    private Handler handler = new Handler();

    // Delay to prepare level
    private Runnable prepareNextLevel = new Runnable() {
        public void run() {
            handler.removeCallbacks(prepareNextLevel);
            constructNextLevel();
        }
    };

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome2);

        // Full Screen
        setTheme(android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        startButton = findViewById(R.id.start);
        mainContainer = findViewById(R.id.mainContainer);

        // Tablet adaption
        ViewFunctions.tabletAdaption(getApplicationContext(), (LinearLayout) findViewById(R.id.mainScreen));
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Disable stop generator flag to be able to generate a level
        GameController.stopLevelGenerator = false;

        // Update game mode
        ctrl.setCurrentGameMode(GameController.GameModes.CLUSTERING);

        // Set bg depending on the current level
        ViewFunctions.setBackgroundActivity(this, mainContainer);

        // Prepare next level
        if (!GameController.isGeneratorGeneratingInWelcome) {
            handler.postDelayed(prepareNextLevel, 100);
        }

        /*// ADS
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                // Prepare next level
                if (!GameController.isGeneratorGeneratingInWelcome) {
                    handler.postDelayed(prepareNextLevel, 100);
                }
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                // Prepare next level
                if (!GameController.isGeneratorGeneratingInWelcome) {
                    handler.postDelayed(prepareNextLevel, 100);
                }
            }
        });*/
    }

    /**
     * Next level constructor
     */
    public void constructNextLevel() {

        GameController.isGeneratorGeneratingInWelcome = true;

        final Welcome2Activity instance = this;
        final TextView progressText = findViewById(R.id.ProgressSpinnerText);
        final LinearLayout progressLayout = findViewById(R.id.ProgressSpinnerLayout);
        final LinearLayout nextLevelInfo = findViewById(R.id.NextLevelInfo);
        final TextView numRealMoves = findViewById(R.id.NumRealMoves);
        final TextView numAddedMoves = findViewById(R.id.NumAddedMoves);
        final TextView level = findViewById(R.id.Level);

        progressText.setText(getString(R.string.generating_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));

        Runnable runner = new Runnable() {

            @Override
            public void run() {

                // Building the level
                GameController.PUZZLE_SIZES size = ctrl.getCurrentPuzzleSize(instance);
                ctrl.setCurrentLevel(ctrl.generateLevelType2(size));

                runOnUiThread(new Runnable() {

                    // Running in background
                    @Override
                    public void run() {

                        while (!ctrl.isNextLevelReady() && !GameController.stopLevelGenerator) {
                            startButton.setVisibility(View.GONE);
                        }

                        if (!GameController.stopLevelGenerator) {

                            // Moves
                            ctrl.addMovesLeft(instance);
                            numRealMoves.setText(getString(R.string.needy_moves).concat(String.valueOf(ctrl.getCurrentLevel().getNumActions())));
                            numAddedMoves.setText(getString(R.string.your_moves).concat(String.valueOf(ctrl.getMovesLeft())));
                            level.setText(getString(R.string.level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));

                            startButton.setText(getString(R.string.play_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));
                            progressLayout.setVisibility(View.GONE);
                            nextLevelInfo.setVisibility(View.VISIBLE);
                        }
                        ctrl.setNextLevelReady(false);
                    }
                });
            }
        };

        // Do it in an individual thread
        Thread thread = new Thread(runner);
        thread.start();
    }

    /**
     * Start game!
     */
    public void start(View view) {
        ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * Disable back click
     */
    @Override
    public void onBackPressed() {

        // Disable generator if the user lefts the activity
        GameController.stopLevelGenerator = true;
        GameController.isGeneratorGeneratingInWelcome = false;
        super.onBackPressed();
    }

}
