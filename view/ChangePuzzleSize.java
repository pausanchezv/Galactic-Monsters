package com.pausanchezv.puzzle.view;

import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Change Puzzle Size Activity
 */
public class ChangePuzzleSize extends AppCompatActivity {

    // Controller
    private GameController ctrl = GameController.getInstance();

    // ADS
    private RewardedVideoAd mRewardedVideoAd;

    // Instance class
    private ChangePuzzleSize instance = this;

    // Buttons
    private Button start, startFake;

    // Sounds
    private static SoundPool soundPool;
    private static int soundWin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_puzzle_size);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Sound
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }
        soundWin = soundPool.load(this, R.raw.you_win, 1);

        // Tablet adaption
        ViewFunctions.tabletAdaption(getApplicationContext(), (LinearLayout) findViewById(R.id.mainScreen));

        // Buttons
        start = findViewById(R.id.start);
        startFake = findViewById(R.id.startFake);

        // GOOGLE ADS
        MobileAds.initialize(this, getString(R.string.admob_user_id));
        createRewardedVideoAd();
        ensureShowContinueButton();

        // Sounds with delay or soundpool has no time to load its sounds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Sounds
                if (soundPool != null) {
                    soundPool.play(soundWin, 1, 1, 0, 0, 1);
                }
            }
        }, 300);
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Prevent app crash because of Android Memory Usage
        if (ctrl.getCurrentLevelNumber(this) == -1) {
            ViewFunctions.intentWithFinish(this, MainActivity.class, null);
        }

        // Set bg depending on the current level
        ViewFunctions.setBackgroundActivity(this, findViewById(R.id.mainContainer));
        ((Button) findViewById(R.id.start)).setText(getString(R.string.play_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(this))));

        // Set increase text
        TextView increaseText = findViewById(R.id.increaseText);
        String size;
        switch (ctrl.getCurrentPuzzleSize(this)) {

            case S21:
                size = "2x1"; break;

            case S22:
                size = "2x2"; break;

            case S32:
                size = "3x2"; break;

            case S33:
                size = "3x3"; break;

            case S43:
                size = "4x3"; break;

            case S44:
                size = "4x4"; break;

            case S54:
                size = "5x4"; break;

            case S55:
                size = "5x5"; break;

            case S65:
                size = "6x5"; break;

            case S66:
                size = "6x6"; break;

            default:
                size = "7x6"; break;
        }

        increaseText.setText(getString(R.string.size_grow_text).concat(size));
    }

    /**
     * Start
     */
    public void start(View view) {
        ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }

    /**
     * On Destroy
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            startFake.setVisibility(View.GONE);
            start.setVisibility(View.VISIBLE);
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
                startFake.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
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
                startFake.setVisibility(View.GONE);
                start.setVisibility(View.VISIBLE);
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

                if (start.getVisibility() == View.GONE) {
                    startFake.setVisibility(View.GONE);
                    start.setVisibility(View.VISIBLE);
                }
            }
        }, 4000);
    }

    /**
     * Dialog on back pressed
     */
    @Override
    public void onBackPressed() {
        ViewFunctions.showSplashScreen = false;
        ViewFunctions.leaveActivityMessage(this, MainActivity.class);
    }
}
