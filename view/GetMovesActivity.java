package com.pausanchezv.puzzle.view;

import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Get moves left Activity
 */
public class GetMovesActivity extends AppCompatActivity {

    // Sounds
    private static SoundPool soundPool;
    private static int soundButton, soundSlide;

    // Controller
    private GameController ctrl = GameController.getInstance();

    // DOM
    private TextView numUserStars;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_moves);

        // Full Screen
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Tablet adaption
        ViewFunctions.tabletAdaption(getApplicationContext(), (LinearLayout) findViewById(R.id.mainScreen));

        numUserStars = findViewById(R.id.numUserStars);
        numUserStars.setText(getString(R.string.stars_you_have2, ctrl.getGameData().getNumUserStars(this)));
    }

    /**
     * On start
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Init sounds
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }

        // Sounds
        soundButton = soundPool.load(this, R.raw.button_s1, 1);
        soundSlide = soundPool.load(this, R.raw.splash_screen, 1);
    }

    /**
     * Add move
     */
    public void addMove(View view) {

        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }

        final GetMovesActivity instance = this;

        if (ctrl.getGameData().getNumUserStars(this) < 50) {
            ViewFunctions.infoMessage(this, getString(R.string.no_stars_e), getString(R.string.no_stars_enough));
        } else {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    switch (which) {

                        case DialogInterface.BUTTON_POSITIVE:

                            ctrl.getGameData().setNumUserStars(instance, ctrl.getGameData().getNumUserStars(instance) - 50);
                            ctrl.getGameData().inrUserExtraMoves(instance);
                            numUserStars.setText(getString(R.string.stars_you_have2, ctrl.getGameData().getNumUserStars(instance)));

                            if (soundPool != null) {
                                soundPool.play(soundSlide, 1, 1, 0, 0, 1);
                            }

                            ViewFunctions.infoMessage(instance, getString(R.string.plus_1_move), getString(R.string.given_1_move));
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.get_1_extra_move);
            builder.setMessage(R.string.get_1_move_question).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
        }
    }

    /**
     * Manual back
     */
    public void back(View view) {
        super.onBackPressed();
        if (soundPool != null) {
            soundPool.play(soundButton, 1, 1, 0, 0, 1);
        }
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
