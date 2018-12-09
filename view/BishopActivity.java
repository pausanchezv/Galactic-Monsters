package com.pausanchezv.puzzle.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.view.viewclass.ViewFunctions;

/**
 * Bishop Activity
 */
public class BishopActivity extends AppCompatActivity {

    // Controller
    private GameController ctrl = GameController.getInstance();

    /**
     * On create
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bishop);

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

        // Prevent app crash because of Android Memory Usage
        if (ctrl.getCurrentLevelNumber(this) == -1) {
            ViewFunctions.intentWithFinish(this, MainActivity.class, null);
        }

        // Set bg depending on the current level
        ViewFunctions.setBackgroundActivity(this, findViewById(R.id.mainContainer));

        ((Button) findViewById(R.id.start)).setText(getString(R.string.play_level).concat(String.valueOf(ctrl.getCurrentLevelNumber(this))));

    }

    /**
     * Start
     */
    public void start(View view) {
        ViewFunctions.intent(this, GamePlayActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
    }
}
