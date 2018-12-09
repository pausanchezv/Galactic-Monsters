package com.pausanchezv.puzzle.view;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Level win
 */
public class LevelWinActivity extends AppCompatActivity {

    // Class instance
    private LevelWinActivity instance = this;

    // Controller
    private GameController ctrl = GameController.getInstance();

    // DOM elements
    private Button nextLevelButton, nextLevelAfterAdButton;
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

    // Victory types
    private enum VictoryKind {COMBO, PERFECT, STAR_0, STAR_2, STAR_1}

    // Sounds
    private static SoundPool soundPool;
    private static int soundButton, soundWin;
    private boolean soundHasSounded = false;

    // ADS
    private InterstitialAd mInterstitialAd;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_win);

        // Sound
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }
        soundWin = soundPool.load(this, R.raw.you_win, 1);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nextLevelButton = findViewById(R.id.start);
        nextLevelAfterAdButton= findViewById(R.id.nextLevelAfterAdButton);
        mainContainer = findViewById(R.id.mainContainer);

        LinearLayout mainScreen = findViewById(R.id.mainScreen);

        // Tablet adaption
        ViewFunctions.tabletAdaption(getApplicationContext(), mainScreen);

        // Start animation
        ViewFunctions.slideSimpleAnimation(mainScreen, 500);

        // Enable share button
        Button iconShare = findViewById(R.id.share);
        ViewFunctions.enableShareButton(this, iconShare);

        // Compute victory
        VictoryKind victoryKind = computeVictoryKind();

        // Update user stars
        updateUserStars(victoryKind);
        configStarsContainer(victoryKind);

        // GOOGLE ADS
        if ((ctrl.getCurrentPuzzleSize(this) != GameController.PUZZLE_SIZES.S21
                && ctrl.getCurrentPuzzleSize(this) != GameController.PUZZLE_SIZES.S22
                && ctrl.getCurrentLevelNumber(this) % 2 == 0
                && ctrl.getCurrentLevelNumber(this) > 2)
                || ctrl.getCurrentLevelNumber(this) > 120)  {

            MobileAds.initialize(this, getString(R.string.admob_user_id));
            createInterstitialAd();

        } else {
            // Prepare next level only if Stop Generator flag is disabled
            if (!GameController.stopLevelGenerator) {
                handler.postDelayed(prepareNextLevel, 100);
            }

            findViewById(R.id.nextLevelAfterAdButtonFake).setVisibility(View.GONE);
            nextLevelAfterAdButton.setVisibility(View.VISIBLE);
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
                        soundPool.play(soundWin, 1, 1, 0, 0, 1);
                    }
                }
            }
        }, 300);
    }


    /**
     * Config stars panel
     */
    @SuppressLint("NewApi")
    private void configStarsContainer(VictoryKind victoryKind) {

        ((TextView) findViewById(R.id.newMoves)).setText(getString(R.string.moves_you_had).concat(String.valueOf(GameController.NEW_MOVES_NUM)));
        ((TextView) findViewById(R.id.originalMoves)).setText(getString(R.string.needy_moves).concat(String.valueOf(GameController.ORIGINAL_MOVES_NUM)));
        ((TextView) findViewById(R.id.movesUsed)).setText(getString(R.string.moves_used).concat(String.valueOf(GameController.USED_MOVES_NUM)));

        ImageView logo = findViewById(R.id.perfect);

        if (victoryKind == VictoryKind.COMBO) {
            logo.setBackgroundResource(R.drawable.combo);
            findViewById(R.id.stars5).setVisibility(View.VISIBLE);

        }else if (victoryKind == VictoryKind.PERFECT) {
            logo.setBackgroundResource(R.drawable.perfect);
            findViewById(R.id.stars3).setVisibility(View.VISIBLE);

        } else if (victoryKind == VictoryKind.STAR_2){
            logo.setBackgroundResource(R.drawable.you_win);
            findViewById(R.id.stars2).setVisibility(View.VISIBLE);

        } else if (victoryKind == VictoryKind.STAR_0) {
            logo.setBackgroundResource(R.drawable.you_win);
            findViewById(R.id.stars0).setVisibility(View.VISIBLE);

        } else if (victoryKind == VictoryKind.STAR_1) {
            logo.setBackgroundResource(R.drawable.you_win);
            findViewById(R.id.stars1).setVisibility(View.VISIBLE);
        }
    }

    /**
     * Compute victory stars
     */
    private VictoryKind computeVictoryKind() {

        // Perfect game
        if (GameController.USED_MOVES_NUM < GameController.ORIGINAL_MOVES_NUM) {
            return VictoryKind.COMBO;

        } else if (GameController.USED_MOVES_NUM == GameController.ORIGINAL_MOVES_NUM) {
            return VictoryKind.PERFECT;

        // The user gets 2 stars if the number of movements he has spent is between perfect and losing the level
        } else if (GameController.USED_MOVES_NUM < GameController.NEW_MOVES_NUM && GameController.USED_MOVES_NUM > GameController.ORIGINAL_MOVES_NUM) {
            return VictoryKind.STAR_2;

        // The user gets 0 stars if he has needed extra moves to pass the level
        } else if(GameController.USED_MOVES_NUM > GameController.NEW_MOVES_NUM) {
            return VictoryKind.STAR_0;

        // Otherwise the user will always win 1 star
        } else {
            return VictoryKind.STAR_1;
        }
    }

    /**
     * Next level constructor
     */
    public void constructNextLevel() {

        final TextView progressText = findViewById(R.id.ProgressSpinnerText);
        final LinearLayout progressLayout = findViewById(R.id.ProgressSpinnerLayout);
        final Button startButton = findViewById(R.id.start);

        final TextView level = findViewById(R.id.Level);
        final LinearLayout nextLevelInfo = findViewById(R.id.NextLevelInfo);
        final TextView numRealMoves = findViewById(R.id.NumRealMoves);
        final TextView numAddedMoves = findViewById(R.id.NumAddedMoves);

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

        int levelNumber = ctrl.getCurrentLevelNumber(this);

        // MONSTER: Bishop explanation activity
        if (levelNumber == 7) {
            ViewFunctions.intent(this, BishopActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);

        // MONSTER: Queen explanation activity
        } else if (levelNumber == 49) {
            ViewFunctions.intent(this, QueenActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);

        // SPECIAL MODE: Congratulations Special Stage
        } else if (((ctrl.getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MATCHING) >= 100 && ctrl.getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.CLUSTERING) == 100)
                || ctrl.getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MATCHING) == 100 && ctrl.getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.CLUSTERING) >= 100)
                && ctrl.getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MIXED) == 1){
            ViewFunctions.intent(this, SpecialUnlockedActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);

        // BOARD SIZE GROWS
        } else if (levelNumber == 8 || levelNumber == 20 || levelNumber == 50 || levelNumber == 90 || levelNumber == 150 || levelNumber == 230 || levelNumber == 400 || levelNumber == 600
                || (ctrl.getCurrentLevelNumber(this) == 4 && (ctrl.getCurrentGameMode() == GameController.GameModes.MATCHING || ctrl.getCurrentGameMode() == GameController.GameModes.MIXED)) || (ctrl.getCurrentLevelNumber(this) == 3 && ctrl.getCurrentGameMode() == GameController.GameModes.CLUSTERING)) {
            ViewFunctions.intent(this, ChangePuzzleSize.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);

        } else {
            ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
        }
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
     * Update user stars
     */
    private void updateUserStars(VictoryKind victoryKind) {
        int stars = ctrl.getGameData().getNumUserStars(this);
        int levelStars = (victoryKind == VictoryKind.COMBO) ? 5 : (victoryKind == VictoryKind.PERFECT) ? 3 : (victoryKind == VictoryKind.STAR_2) ? 2 : (victoryKind == VictoryKind.STAR_0) ? 0 : 1;
        ((TextView) findViewById(R.id.starsUser)).setText(getString(R.string.stars_you_have).concat(String.valueOf(ctrl.getGameData().getNumUserStars(this))).concat(" + ").concat(String.valueOf(levelStars)));
        ctrl.getGameData().setNumUserStars(this, stars + levelStars);
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
    }

    /**
     * Go to next level
     */
    private void goToNextLevel() {
        // Show the next level and reload the ad to prepare for the level after.

        ViewFunctions.slideDownLayoutAnimation(findViewById(R.id.mainScreen), 100);
        findViewById(R.id.starsContainer).setVisibility(View.GONE);
        findViewById(R.id.nextLevelContainer).setVisibility(View.VISIBLE);
        findViewById(R.id.logo).setVisibility(View.VISIBLE);
        mInterstitialAd = null;
    }

    /**
     * Ahow ad or error
     */
    public void showInterstitialAd(View view) {
        // Show the ad if it's ready. Otherwise toast and reload the ad.
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();

        } else {
            goToNextLevel();
        }
    }

    /**
     * Load new ad
     */
    private void loadInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Create AD
     */
    private void createInterstitialAd() {

        // Create the InterstitialAd and set the adUnitId (defined in values/strings.xml).
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial_id));
        loadInterstitial();

        mInterstitialAd.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {

                // Prepare next level only if Stop Generator flag is disabled
                if (!GameController.stopLevelGenerator) {
                    handler.postDelayed(prepareNextLevel, 100);
                }

                findViewById(R.id.nextLevelAfterAdButtonFake).setVisibility(View.GONE);
                nextLevelAfterAdButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {

                // Prepare next level only if Stop Generator flag is disabled
                if (!GameController.stopLevelGenerator) {
                    handler.postDelayed(prepareNextLevel, 100);
                }

                findViewById(R.id.nextLevelAfterAdButtonFake).setVisibility(View.GONE);
                nextLevelAfterAdButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdClosed() {

                if (soundPool != null) {
                    soundPool.play(soundButton, 1, 1, 0, 0, 1);
                }
                // Proceed to the next level.
                goToNextLevel();
            }
        });
    }
}
