package com.pausanchezv.puzzle.view;

import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Game explanation
 */
public class GameExplanationActivity extends AppCompatActivity {

    // Sounds
    private static SoundPool soundPool;
    private static int soundButton;

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_explanation);

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

        // Init sounds
        if (soundPool == null) {
            soundPool = new SoundPool(32, AudioManager.STREAM_MUSIC, 0);
        }

        // Sounds
        soundButton = soundPool.load(this, R.raw.button_s1, 1);
    }

    /**
     * Start
     */
    public void start(View view) {
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
