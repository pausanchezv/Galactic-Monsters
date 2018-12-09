package com.pausanchezv.puzzle.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Main activity
 */
public class MainActivity extends AppCompatActivity {

    // Variables globals
    private TextView levelKind1, levelKind2, levelKind3;
    private Button buttonKind3;
    private boolean isLevelKind3Disabled;
    private ScrollView buttons;
    private LinearLayout mainScreen, splashScreenLayout;

    // Sounds
    private SoundPool soundPool;
    private int soundButton, soundSlide, soundSplashScreen;

    /**
     * On create
     */
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        levelKind1 = findViewById(R.id.levelKind1);
        levelKind2 = findViewById(R.id.levelKind2);
        levelKind3 = findViewById(R.id.levelKind3);
        buttonKind3 = findViewById(R.id.startGameKind3);
        splashScreenLayout = findViewById(R.id.SplashScreen);

        // Disable button kind 3 if levels < 50
        if (isLevelKind3Disabled) {
            buttonKind3.setAlpha(0.4f);
        }

        buttons = findViewById(R.id.Buttons);
        mainScreen = findViewById(R.id.mainScreen);

        if (!ViewFunctions.showSplashScreen) {
            ViewFunctions.showSplashScreen = true;
            mainScreen.setTranslationY(0);
            mainScreen.setVisibility(View.GONE);
            buttons.setVisibility(View.VISIBLE);
            splashScreenLayout.setVisibility(View.GONE);
        } else {
            doAnimations();
        }

        //GameController.getInstance().getGameData().setNumUserStars(this, 500);
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {
        super.onStart();

        GameController.getInstance().getGameData().prepareData(this);
        levelKind1.setText(String.valueOf(getString(R.string.level) + GameController.getInstance().getGameData().getCurrentLevelNumber(this, GameController.GameModes.MATCHING)));
        levelKind2.setText(String.valueOf(getString(R.string.level) + GameController.getInstance().getGameData().getCurrentLevelNumber(this, GameController.GameModes.CLUSTERING)));
        levelKind3.setText(String.valueOf(getString(R.string.level) + GameController.getInstance().getGameData().getCurrentLevelNumber(this, GameController.GameModes.MIXED)));

        isLevelKind3Disabled = GameController.getInstance().getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MATCHING) < 100 || GameController.getInstance().getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.CLUSTERING) < 100;

        // Enable / disable kind 3
        if (isLevelKind3Disabled) {
            buttonKind3.setAlpha(0.5f);
            levelKind3.setText(R.string.lock_stage);
        }

        // Init sounds
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }

        // Sounds
        soundButton = soundPool.load(this, R.raw.button_s1, 1);
        soundSlide = soundPool.load(this, R.raw.slide_logo, 1);
        soundSplashScreen = soundPool.load(this, R.raw.splash_screen, 1);

        // ADS
        MobileAds.initialize(this, getString(R.string.admob_user_id));
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    /**
     * Animations
     */
    private void doAnimations() {

        // Initial sound splash
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (soundPool != null) {
                    soundPool.play(soundSplashScreen, 1, 1, 0, 0, 1);
                }
            }
        }, 1700);

        // Prepare layouts
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mainScreen.setVisibility(View.VISIBLE);
                splashScreenLayout.setVisibility(View.GONE);
            }
        }, 4000);

        // Animations
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @SuppressLint("NewApi")
            @Override
            public void run() {

                // Tablet adapters
                if (ViewFunctions.isTablet(getApplicationContext())) {
                    ViewGroup.LayoutParams params = mainScreen.getLayoutParams();
                    params.width = (int) (mainScreen.getWidth() / 1.3);
                    mainScreen.setLayoutParams(params);
                    buttons.setLayoutParams(params);
                }

                if (soundPool != null) {
                    soundPool.play(soundSlide, 1, 1, 0, 0, 1);
                }
                ViewFunctions.slideDownLayoutAnimation(mainScreen, 0);

            }
        }, 4500);
    }

    /**
     * Force finish when back button is pressed
     */
    @Override
    public void onBackPressed() {

        if (mainScreen.getVisibility() == View.GONE) {
            buttons.setVisibility(View.GONE);
            mainScreen.setVisibility(View.VISIBLE);
        } else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Play button
     */
    public void play (View view) {
        buttons.setVisibility(View.VISIBLE);
        mainScreen.setVisibility(View.GONE);
        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Start Game Matching
     */
    public void startGameKind1(View view) {

        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }

        if (GameController.getInstance().getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MATCHING) < 2) {
            ViewFunctions.intent(this, Introduction1Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
        } else {
            ViewFunctions.intent(this, Welcome1Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
        }
    }

    /**
     * Start game clustering
     */
    public void startGameKind2(View view) {

        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }

        if (GameController.getInstance().getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.CLUSTERING) < 2) {
            ViewFunctions.intent(this, Introduction2Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
        } else {
            ViewFunctions.intent(this, Welcome2Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
        }
    }

    /**
     * Start game Mixed
     */
    public void startGameKind3(View view) {

        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }

        // Enable / disable kind 3
        if (!isLevelKind3Disabled) {

            if (GameController.getInstance().getCurrentLevelNumberDependingOnMode(this, GameController.GameModes.MIXED) < 2) {
                ViewFunctions.intent(this, Introduction3Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
            } else {
                ViewFunctions.intent(this, Welcome3Activity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
            }

        } else {
            ViewFunctions.infoMessage(this, getString(R.string.unlock_mode), getString(R.string.enable_kind_3_text));
        }
    }

    /**
     * Start game clear data
     */
    public void clearData(View view) {

        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }

        final Activity activity = this;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        GameController.getInstance().getGameData().createNewData(activity);
                        levelKind1.setText(String.valueOf(getString(R.string.level) + String.valueOf(1)));
                        levelKind2.setText(String.valueOf(getString(R.string.level) + String.valueOf(1)));
                        if (!isLevelKind3Disabled)
                            levelKind3.setText(String.valueOf(getString(R.string.level) + String.valueOf(1)));

                        ViewFunctions.intent(activity, MainActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.reset_game_title);
        builder.setMessage(R.string.reset_game).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
    }

    /**
     * Game explaation
     */
    public void gameExplanation(View view) {
        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }
        ViewFunctions.intent(this, GameExplanationActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * Get extra moves
     */
    public void getExtraMoves(View view) {
        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }
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
    }
}
