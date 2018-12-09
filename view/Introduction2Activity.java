package com.pausanchezv.puzzle.view;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Introduction 2
 */
public class Introduction2Activity extends AppCompatActivity {

    // DOM
    private Button startButton;

    // Controller
    private GameController ctrl = GameController.getInstance();

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
        setContentView(R.layout.activity_introduction2);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

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

        // Prepare next level
        handler.postDelayed(prepareNextLevel, 100);
        startButton = findViewById(R.id.start);
    }

    /**
     * Start
     */
    public void start(View view) {
        ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * Next level constructor
     */
    public void constructNextLevel() {

        final Introduction2Activity instance = this;

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

                        while (!ctrl.isNextLevelReady()) {
                            startButton.setVisibility(View.GONE);
                        }

                        // Moves
                        ctrl.addMovesLeft(instance);
                        startButton.setText(getString(R.string.play_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));
                        startButton.setVisibility(View.VISIBLE);
                        ctrl.setNextLevelReady(false);
                    }
                });
            }
        };
        // Do it in an individual thread
        Thread thread = new Thread(runner);
        thread.start();
    }
}
