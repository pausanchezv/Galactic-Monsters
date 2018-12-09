package com.pausanchezv.puzzle.view.viewclass;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.FrameLayout;

import com.pausanchezv.puzzle.controller.SquareAdapter;
import com.pausanchezv.puzzle.view.GamePlayActivity;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Worker handler class
 */
public class AnimationGridWorker extends HandlerThread {

    // Globals
    private Handler animationWorkerHandler;
    private static Runnable animationRunner = null;
    private static Handler animationHandler;
    private boolean keepRunning;

    /**
     * Constructor
     */
    public AnimationGridWorker(String name) {
        super(name);
        keepRunning = true;
        animationHandler = new Handler();
    }

    /**
     * Post task
     */
    private void postTask(Runnable task){
        animationWorkerHandler.post(task);
    }

    /**
     * Stop task
     */
    public void stopTask() {
        animationWorkerHandler.removeCallbacks(null);
        keepRunning = false;
        this.quit();
    }

    /**
     * Handler preparation
     */
    private void prepareHandler(){
        animationWorkerHandler = new Handler(getLooper());
    }

    /**
     * Start runner
     */
    public void startRunning(final SquareAdapter adapter) {

        Runnable task = new Runnable() {

            @Override
            public void run() {

                // Animate while condition
                while (GamePlayActivity.isAnimationLoop && keepRunning) {

                    try {
                        TimeUnit.SECONDS.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Create animation elements
                    animationHandler.removeCallbacks(animationRunner);
                    animationRunner = new Runnable() {

                        @SuppressLint("NewApi")
                        @Override
                        public void run() {

                            int animationType = (int) Math.round(Math.random() * 3);

                            // Traverse through the grid
                            for (Object o : adapter.getViewsAndSquaresMap().entrySet()) {

                                Map.Entry pair = (Map.Entry) o;
                                View view = (FrameLayout) pair.getKey();

                                final View child = ((FrameLayout) view).getChildAt(1);
                                int randNum = (int) Math.round(Math.random() * 100);

                                // Animate 40% of squares
                                if (randNum < 40 && child != null) {

                                    // Select animation type
                                    if (animationType == 0 || animationType == 1) {

                                        child.animate()
                                                .translationY(-50)
                                                .setDuration(100)
                                                .withEndAction(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        child.animate()
                                                                .translationY(30)
                                                                .setDuration(100)
                                                                .withEndAction(new Runnable() {

                                                                    @Override
                                                                    public void run() {
                                                                        child.animate()
                                                                                .translationY(0)
                                                                                .setDuration(150)
                                                                                .start();
                                                                    }
                                                                }).start();
                                                    }
                                                }).start();

                                    } else if (animationType == 2) {

                                        child.animate()
                                                .rotationX(-50f)
                                                .setDuration(150)
                                                .withEndAction(new Runnable() {

                                                    @Override
                                                    public void run() {
                                                        child.animate()
                                                                .rotationX(50f)
                                                                .setDuration(150)
                                                                .withEndAction(new Runnable() {

                                                                    @Override
                                                                    public void run() {
                                                                        child.animate()
                                                                                .rotationX(0f)
                                                                                .setDuration(100)
                                                                                .start();
                                                                    }
                                                                }).start();
                                                    }
                                                }).start();
                                    } else {

                                        child.animate()
                                                .rotationY(180f)
                                                .setDuration(500)
                                                .start();
                                    }
                                }
                            }
                        }
                    };

                    // Post animation
                    animationHandler.post(animationRunner);
                }
            }
        };

        this.start();
        this.prepareHandler();
        this.postTask(task);
    }
}