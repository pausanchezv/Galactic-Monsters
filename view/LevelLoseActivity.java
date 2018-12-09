package com.pausanchezv.puzzle.view;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

import java.sql.SQLOutput;

/**
 * Level lose
 */
public class LevelLoseActivity extends AppCompatActivity {

    // Instance class
    private LevelLoseActivity instance = this;

    // Controller
    private GameController ctrl = GameController.getInstance();

    // DOM elements
    private Button nextLevelButton, continueButton, continueButtonFake;
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

    // Sounds
    private static SoundPool soundPool;
    private static int soundButton, soundLose;
    private boolean soundHasSounded = false;

    // ADS
    private RewardedVideoAd mRewardedVideoAd;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_lose);

        // Sound
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }
        soundLose = soundPool.load(this, R.raw.you_lose, 1);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nextLevelButton = findViewById(R.id.start);
        mainContainer = findViewById(R.id.mainContainer);

        continueButtonFake = findViewById(R.id.nextLevelAfterAdButtonFake);
        continueButton = findViewById(R.id.nextLevelAfterAdButton);

        LinearLayout mainScreen =  findViewById(R.id.mainScreen);

        // Tablet adaption
        ViewFunctions.tabletAdaption(getApplicationContext(), mainScreen);

        // Start animation
        ViewFunctions.slideSimpleAnimation(mainScreen, 500);

        // GOOGLE ADS
        MobileAds.initialize(this, getString(R.string.admob_user_id)/*"ca-app-pub-3940256099942544~3347511713"*/);
        createRewardedVideoAd();

        ensureShowContinueButton();

        // Prepare next level only if Stop Generator flag is disabled
        if (!GameController.stopLevelGenerator) {
            handler.postDelayed(prepareNextLevel, 100);
        }
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {

        super.onStart();

        // Prevent app crash because of Android Memory Usage
        if (ctrl.getCurrentLevelNumber(instance) == -1) {
            ViewFunctions.intentWithFinish(this, MainActivity.class, null);
        }

        // Set next level button text
        nextLevelButton.setText(getResources().getString(R.string.play_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));

        // Set bg depending on the current level
        ViewFunctions.setBackgroundActivity(this, mainContainer);

        // Sounds with delay or soundpool has no time to load its sounds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Sounds
                if (soundPool != null) {
                    soundButton = soundPool.load(getApplicationContext(), R.raw.slide_logo, 1);

                    if (!soundHasSounded) {
                        soundHasSounded = true;
                        soundPool.play(soundLose, 1, 1, 0, 0, 1);
                    }
                }
            }
        }, 300);
    }

    /**
     * Next level constructor
     */
    public void constructNextLevel() {

        final TextView progressText = findViewById(R.id.ProgressSpinnerText);
        final LinearLayout progressLayout = findViewById(R.id.ProgressSpinnerLayout);
        final Button startButton = findViewById(R.id.start);

        final LinearLayout nextLevelInfo = findViewById(R.id.NextLevelInfo);
        final TextView numRealMoves = findViewById(R.id.NumRealMoves);
        final TextView numAddedMoves = findViewById(R.id.NumAddedMoves);
        final TextView level = findViewById(R.id.Level);

        progressText.setText(getString(R.string.generating_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(instance))));

        // Prevent app crash because of Android Memory Usage
        if (ctrl == null || ctrl.getCurrentGameMode() == null) {
            ViewFunctions.intentWithFinish(this, MainActivity.class, null);

        } else {

            Runnable runner = new Runnable() {

                @Override
                public void run() {

                    // Building the level
                    GameController.PUZZLE_SIZES size = ctrl.getCurrentPuzzleSize(instance);

                    switch (ctrl.getCurrentGameMode()) {

                        case MATCHING:
                            ctrl.setCurrentLevel(ctrl.generateLevelType1(size));
                            break;

                        case CLUSTERING:
                            ctrl.setCurrentLevel(ctrl.generateLevelType2(size));
                            break;

                        case MIXED:

                            int probability = (int) Math.round(Math.random() * 100);

                            if (probability > 50) {
                                ctrl.setCurrentLevel(ctrl.generateLevelType1(size));
                            } else {
                                ctrl.setCurrentLevel(ctrl.generateLevelType2(size));
                            }
                            break;

                        default:
                            break;
                    }

                    runOnUiThread(new Runnable() {

                        // Running in background
                        @Override
                        public void run() {

                            while (!ctrl.isNextLevelReady() && !GameController.stopLevelGenerator) {
                                nextLevelButton.setVisibility(View.GONE);
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

                            // Enable stop generator flag to not have multiple generators in background
                            GameController.stopLevelGenerator = true;
                        }
                    });
                }
            };

            // Do it in an individual thread
            Thread thread = new Thread(runner);
            thread.start();
        }
    }

    /**
     * Start game!
     */
    public void start(View view) {
        ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * Dialog on back pressed
     */
    @Override
    public void onBackPressed() {
        ViewFunctions.showSplashScreen = false;
        ViewFunctions.leaveActivityMessage(this, MainActivity.class);
    }

    /**
     * Show next level and hide the stars panel
     */
    public void showNextLevel(View view) {
        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }
        ViewFunctions.slideDownLayoutAnimation(findViewById(R.id.mainScreen), 100);
        findViewById(R.id.starsContainer).setVisibility(View.GONE);
        findViewById(R.id.nextLevelContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.logo).setVisibility(View.VISIBLE);
    }

    /**
     * Get extra moves
     */
    public void getExtraMoves(View view) {
        ViewFunctions.intent(this, GetMovesActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * On Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Free music sound
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        mRewardedVideoAd.destroy(this);
    }

    /**
     * Load ads video
     */
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.admob_rewarded_id), new AdRequest.Builder().build());
    }

    /**
     * Show ads video
     */
    public void getExtraMovesVideo(View w) {
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        } else {
            findViewById(R.id.videoBlock).setVisibility(View.GONE);
            continueButtonFake.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Create ads video
     */
    private void createRewardedVideoAd() {

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

            @Override
            public void onRewardedVideoAdLoaded() {
                findViewById(R.id.videoBlock).setVisibility(View.VISIBLE);
                continueButtonFake.setVisibility(View.GONE);
                continueButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRewardedVideoAdOpened() {}

            @Override
            public void onRewardedVideoStarted() {
                findViewById(R.id.videoBlock).setVisibility(View.GONE);
            }

            @Override
            public void onRewardedVideoAdClosed() {}

            @Override
            public void onRewarded(RewardItem reward) {
                ctrl.getGameData().inrUserExtraMoves(instance);
                ViewFunctions.infoMessage(instance, getString(R.string.video_success_title), getString(R.string.video_success_text));
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {}

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                findViewById(R.id.videoBlock).setVisibility(View.GONE);
                continueButtonFake.setVisibility(View.GONE);
                continueButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onRewardedVideoCompleted() {}
        });

        loadRewardedVideoAd();
    }
    /**
     * Continue button will be always visible even if ad fails
     */
    private void ensureShowContinueButton() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (continueButton.getVisibility() == View.GONE) {
                    continueButtonFake.setVisibility(View.GONE);
                    continueButton.setVisibility(View.VISIBLE);
                }
            }
        }, 4000);
    }
}
