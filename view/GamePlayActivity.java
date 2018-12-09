package com.pausanchezv.puzzle.view;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.pausanchezv.puzzle.R;

import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.controller.GameController.PUZZLE_SIZES;
import com.pausanchezv.puzzle.controller.SquareAdapter;
import com.pausanchezv.puzzle.model.Level;
import com.pausanchezv.puzzle.view.viewclass.AnimationGridWorker;
import com.pausanchezv.puzzle.view.viewclass.SquareTouchHandler;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Game play activity
 */
public class GamePlayActivity extends AppCompatActivity {

    // Controller instances
    private final GameController ctrl = GameController.getInstance();
    private GridView gridView;
    private SquareAdapter squareAdapter;

    // Animations
    public static boolean isAnimationLoop;
    private static AnimationGridWorker mWorkerThread;

    // Music and sounds
    private static MediaPlayer musicLoop;
    private static SoundPool soundPool;
    private static int soundSelection, soundPlace, soundErrorPlace, soundExtraMove;

    /**
     * On create
     */
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_play);

        // Construct matrix sound
        MediaPlayer.create(this, R.raw.construct_matrix).start();

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // DOM elements
        gridView = findViewById(R.id.gameMatrix);
        TextView movesLeftView = findViewById(R.id.movesLeft);
        TextView levelNumberView = findViewById(R.id.levelNumber);

        // Get the current level
        Level level = ctrl.getCurrentLevel();

        // Prevent app crash because of Android Memory Usage
        if (level != null) {

            // Set text to the views
            movesLeftView.setText(getResources().getString(R.string.moves_left).concat(String.valueOf(ctrl.getMovesLeft())).concat(" "));
            levelNumberView.setText(getResources().getString(R.string.level).concat(String.valueOf(ctrl.getCurrentLevelNumber(this))));

            // Set grid dimensions and correct them if is needed
            gridView.setNumColumns(level.getNumCols());

            // Corrections depending on the level
            correctionsDependingOnTheLevel(level.getSize());

            // Create the adapter
            squareAdapter = new SquareAdapter(getApplicationContext(), 0, level.getSquaresList(), level);
            squareAdapter.setActivity(this);

            // Add the type of wall (it's different every time)
            squareAdapter.addWallType();

            // Set the adapter to the grid
            gridView.setAdapter(squareAdapter);

            // Add touch events to the squares
            gridView.setOnItemClickListener(new SquareTouchHandler.SquareClickable(this, squareAdapter, level, gridView));

            // Remove click listener if drag is enabled
            if (ctrl.isDragActive(this)) {
                gridView.setOnItemClickListener(null);
            }

            TextView stars = findViewById(R.id.numStars);
            stars.setText(String.valueOf(ctrl.getGameData().getNumUserStars(this)));

            // Start animation
            ViewFunctions.slideDownLayoutAnimation(gridView, 700);

            // Enable share button
            ImageButton iconShare = findViewById(R.id.IconShare);
            ViewFunctions.enableShareButton(this, iconShare);

            prepareMusicIcon();
            prepareExtraMovesIcon();
            prepareTouchIcon();

            // Show tutorial if needed
            showTutorial();
        }

        else {
            ViewFunctions.intentWithFinish(this, MainActivity.class, null);
        }
    }

    /**
     * OnStart
     */
    @Override
    protected void onStart() {

        super.onStart();

        // Generator is able to generate levels again
        GameController.stopLevelGenerator = false;
        GameController.isGeneratorGeneratingInWelcome = false;

        //ADS
        addBannerAd();
    }

    /**
     * On Resume
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (ctrl.isMusicEnabled(this) && musicLoop == null) {
            constructMusic();
        }
        constructionSounds();

        // Monsters animate
        isAnimationLoop = true;
        startAnimateMonsters(squareAdapter);
    }

    /**
     * Music constructor
     */
    private void constructMusic() {
        musicLoop = MediaPlayer.create(this, R.raw.loop);
        musicLoop.start();
        musicLoop.setVolume(0.8f, 0.8f);
        musicLoop.setLooping(true);
    }

    /**
     * Sound constructor
     */
    private void constructionSounds() {

        //TODO prepared here for when necessary
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION).setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build();
            soundPool = new SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build();
        } else {
            soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        }*/

        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }

        // Sounds
        soundSelection = soundPool.load(this, R.raw.selection_monster, 1);
        soundPlace = soundPool.load(this, R.raw.place_monster_2, 1);
        soundErrorPlace = soundPool.load(this, R.raw.error, 1);
        soundExtraMove = soundPool.load(this, R.raw.splash_screen, 1);
    }

    /**
     * Selection sound
     */
    public static void playSelectionSound() {
        if (soundPool != null) {
            soundPool.play(soundSelection, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Place sound
     */
    public static void playPlaceSound() {
        if (soundPool != null) {
            soundPool.play(soundPlace, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Error placing sound
     */
    public static void playErrorPlaceSound() {
        if (soundPool != null) {
            soundPool.play(soundErrorPlace, 1, 1, 0, 0, 1);
        }
    }

    /**
     * Quit game loop music
     */
    public static void quitMusic() {

        // Free memory music
        if (musicLoop != null) {
            musicLoop.stop();
            musicLoop.release();
            musicLoop = null;
        }
    }

    /**
     * On Stop
     */
    @Override
    protected void onPause() {
        super.onPause();

        quitMusic();

        // Stop animate monsters
        isAnimationLoop = false;
        stopAnimateMonsters();
    }

    /**
     * On Restart
     */
    @Override
    protected void onRestart() {
        super.onRestart();

        if (ctrl.isMusicEnabled(this)) {
            constructMusic();
        }

        // Monsters animate
        isAnimationLoop = true;
        startAnimateMonsters(squareAdapter);
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

        //
        quitMusic();

        // Stop animation matrix
        stopAnimateMonsters();
    }

    /**
     * Animate monsters
     */
    public static void startAnimateMonsters(final SquareAdapter adapter) {
        stopAnimateMonsters();
        mWorkerThread = new AnimationGridWorker("Animation Monsters");
        mWorkerThread.startRunning(adapter);
    }

    /**
     * Stop animation monsters
     */
    private static void stopAnimateMonsters() {
        if (mWorkerThread != null) {
            mWorkerThread.stopTask();
        }
    }

    /**
     * Corrections depending on the level
     */
    @SuppressLint("NewApi")
    private void correctionsDependingOnTheLevel(PUZZLE_SIZES size) {

        // background
        RelativeLayout rLayout = findViewById(R.id.mainContainer);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.background_1);

        switch (size) {

            case S21:
            case S22:
                drawable = res.getDrawable(R.drawable.background_1);
                gridView.setPadding(10, 0, 10, 0);
                break;

            case S32:
                drawable = res.getDrawable(R.drawable.background_2);
                gridView.setPadding(50, 0, 50, 0);
                break;

            case S33:
                drawable = res.getDrawable(R.drawable.background_3);
                gridView.setPadding(10, 0, 10, 0);
                break;

            case S43:
                drawable = res.getDrawable(R.drawable.background_4);
                gridView.setPadding(35, 0, 35, 0);
                break;

            case S44:
                drawable = res.getDrawable(R.drawable.background_5);
                gridView.setPadding(10, 0, 10, 0);
                break;

            case S54:
                drawable = res.getDrawable(R.drawable.background_6);
                gridView.setPadding(20, 0, 20, 0);
                break;

            case S55:
                drawable = res.getDrawable(R.drawable.background_7);
                gridView.setPadding(10, 0, 10, 0);
                break;

            case S65:
                drawable = res.getDrawable(R.drawable.background_8);
                gridView.setPadding(15, 0, 15, 0);
                break;

            case S66:
                drawable = res.getDrawable(R.drawable.background_9);
                gridView.setPadding(10, 0, 10, 0);
                break;

            case S76:
                drawable = res.getDrawable(R.drawable.background_10);
                gridView.setPadding(10, 0, 10, 0);
                break;

            default:
                break;
        }

        // Tabled grid padding
        if (ViewFunctions.isTablet(getApplicationContext())) {
            gridView.setPadding(170, 0, 170, 0);
        }

        rLayout.setBackground(drawable);
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
     * Back
     */
    public void goBack(View view) {
        ViewFunctions.leaveActivityMessage(this, MainActivity.class);
    }


    /**
     * Change squares touching
     */
    public void changeTouching(View view) {

        // Create the adapter
        SquareAdapter squareAdapter = new SquareAdapter(getApplicationContext(), 0, ctrl.getCurrentLevel().getSquaresList(), ctrl.getCurrentLevel());
        squareAdapter.setActivity(this);
        String text;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (ctrl.isDragActive(this)) {

            gridView.setOnItemClickListener(new SquareTouchHandler.SquareClickable(this, squareAdapter, ctrl.getCurrentLevel(), gridView));

            ctrl.setDragActive(this, false);
            text = getString(R.string.click_enabled_text);
            builder.setTitle(R.string.click_enabled_title);

        } else {
            gridView.setOnItemClickListener(null);
            ctrl.setDragActive(this, true);
            text = getString(R.string.drag_enabled_text);
            builder.setTitle(R.string.drag_enabled_title);
        }

        builder.setMessage(text).setPositiveButton(R.string.okay, dialogClickListener).show();

        prepareTouchIcon();

        // Set the adapter to the grid
        gridView.setAdapter(squareAdapter);
    }

    /**
     * Switch music
     */
    public void switchSounds(View view) {

        if (ctrl.isMusicEnabled(this)) {
            findViewById(R.id.IconMusic).setVisibility(View.GONE);
            findViewById(R.id.IconMute).setVisibility(View.VISIBLE);
            ctrl.setMusicEnabled(this, false);
            quitMusic();

        } else {
            findViewById(R.id.IconMute).setVisibility(View.GONE);
            findViewById(R.id.IconMusic).setVisibility(View.VISIBLE);
            ctrl.setMusicEnabled(this, true);
            constructMusic();
        }
    }

    /**
     * Add music icon
     */
    private void prepareMusicIcon() {

        if (ctrl.isMusicEnabled(this)) {
            findViewById(R.id.IconMusic).setVisibility(View.VISIBLE);
            findViewById(R.id.IconMute).setVisibility(View.GONE);
        } else {
            findViewById(R.id.IconMute).setVisibility(View.VISIBLE);
            findViewById(R.id.IconMusic).setVisibility(View.GONE);
        }
    }

    /**
     * Add heart icon
     */
    private void prepareExtraMovesIcon() {

        if (ctrl.getGameData().getUserExtraMoves(this) > 0) {
            findViewById(R.id.IconHeartFilled).setVisibility(View.VISIBLE);
            findViewById(R.id.IconHeart).setVisibility(View.GONE);
        } else {
            findViewById(R.id.IconHeart).setVisibility(View.VISIBLE);
            findViewById(R.id.IconHeartFilled).setVisibility(View.GONE);
        }
    }

    /**
     * Add music icon
     */
    private void prepareTouchIcon() {

        if (ctrl.isDragActive(this)) {
            findViewById(R.id.IconHand).setVisibility(View.VISIBLE);
            findViewById(R.id.IconHandUp).setVisibility(View.GONE);
        } else {
            findViewById(R.id.IconHandUp).setVisibility(View.VISIBLE);
            findViewById(R.id.IconHand).setVisibility(View.GONE);
        }
    }

    /**
     * Extra moves handler
     */
    public void useExtraMoves(View view) {

        final GamePlayActivity instance = this;

        if (ctrl.getGameData().getUserExtraMoves(this) < 1) {
            ViewFunctions.infoMessage(this, getString(R.string.no_moves_e), getString(R.string.get_moves_extra_msg));
        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            ctrl.getGameData().dcrUserExtraMoves(instance);
                            ctrl.setMovesLeft(ctrl.getMovesLeft() + 1);
                            ((TextView) findViewById(R.id.movesLeft)).setText(getString(R.string.moves_left).concat(String.valueOf(ctrl.getMovesLeft())));
                            prepareExtraMovesIcon();
                            if (soundPool != null) {
                                soundPool.play(soundExtraMove, 1, 1, 0, 0, 1);
                            }
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.use_move_extra_title, ctrl.getGameData().getUserExtraMoves(this)));
            builder.setMessage(R.string.use_move_question).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
        }
    }

    /**
     * Show tutorial if needed
     */
    private void showTutorial() {
        if (ctrl.getCurrentLevelNumber(this) == 1 && ctrl.getCurrentGameMode() != GameController.GameModes.MIXED) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    TextView tutorial = findViewById(R.id.tutorialTextView);

                    switch(ctrl.getCurrentGameMode()) {
                        case CLUSTERING:
                            tutorial.setText(R.string.tutorial_gameplay_2);
                            break;
                        case MATCHING:
                            tutorial.setText(R.string.tutorial_gameplay_1);
                            break;
                    }

                    tutorial.setVisibility(View.VISIBLE);

                }
            }, 1500);
        }
    }

    /**
     * Add banner add if necessary
     */
    private void addBannerAd() {

        final GamePlayActivity instance = this;

        if (ctrl.getCurrentPuzzleSize(this) == PUZZLE_SIZES.S33
                || ctrl.getCurrentPuzzleSize(this) == PUZZLE_SIZES.S44
                || ctrl.getCurrentPuzzleSize(this) == PUZZLE_SIZES.S55
                || ctrl.getCurrentPuzzleSize(this) == PUZZLE_SIZES.S66) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    findViewById(R.id.bannerAd).setVisibility(View.VISIBLE);

                    MobileAds.initialize(instance, getString(R.string.admob_user_id));
                    AdView adView = findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    adView.loadAd(adRequest);
                }
            }, 2000);
        }

    }
}