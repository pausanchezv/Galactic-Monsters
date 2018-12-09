package com.pausanchezv.puzzle.view.viewclass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;


/**
 * View functions
 */
public final class ViewFunctions {

    // Transition modes
    public enum TransitionMode {RIGHT_TO_LEFT, LEFT_TO_RIGHT}

    // Direction slide animations
    private static boolean slideDirectionTop = true;

    // Splash screen flag
    public static boolean showSplashScreen = true;

    /**
     * Check if is tablet
     */
    public static boolean isTablet(Context activity) {
        try {
            // Compute screen size

            DisplayMetrics dm = activity.getResources().getDisplayMetrics();
            float screenWidth  = dm.widthPixels / dm.xdpi;
            float screenHeight = dm.heightPixels / dm.ydpi;
            double size = Math.sqrt(Math.pow(screenWidth, 2) + Math.pow(screenHeight, 2));

            // Tablet devices have a screen size greater than 6 inches
            return size >= 7;

        } catch(Throwable t) {
            return false;
        }
    }


    /**
     * Tablet size change
     */
    public static void tabletAdaption(final Context context, final LinearLayout layout) {
        // Tablet adapters
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (ViewFunctions.isTablet(context)) {
                    ViewGroup.LayoutParams params = layout.getLayoutParams();
                    params.width = (int) (layout.getWidth() / 1.3);
                    layout.setLayoutParams(params);
                }
            }
        }, 100);
    }

    /**
     * Generic intent
     */
    public static void intent(Activity start, Class<?> goal, TransitionMode mode) {
        Intent intent = new Intent(start, goal);
        start.startActivity(intent);

        if (mode == TransitionMode.LEFT_TO_RIGHT) {
            start.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else {
            start.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
    }

    /**
     * Generic intent
     */
    public static void intent(Activity start, Class<?> goal) {
        Intent intent = new Intent(start, goal);
        start.startActivity(intent);
    }

    /**
     * Finisher intent
     */
    public static void intentWithFinish(Activity start, Class<?> goal, TransitionMode mode) {
        Intent intent = new Intent(start, goal);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        start.startActivity(intent);

        if (mode == TransitionMode.LEFT_TO_RIGHT) {
            start.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        } else if (mode == TransitionMode.RIGHT_TO_LEFT){
            start.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        }
        start.finish();
    }

    /**
     * Message showed when the user wants to leave the activity
     */
    public static void leaveActivityMessage(final Activity start, final Class<?> goal) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {

                    case DialogInterface.BUTTON_POSITIVE:

                        // Disable generator if the user lefts the activity
                        GameController.stopLevelGenerator = true;
                        intentWithFinish(start, goal, TransitionMode.RIGHT_TO_LEFT);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(start);
        builder.setTitle(R.string.are_you_sure);
        builder.setMessage(R.string.leave_activity_question).setPositiveButton(R.string.yes, dialogClickListener).setNegativeButton(R.string.no, dialogClickListener).show();
    }

    /**
     * Set background depending on the level
     */
    @SuppressLint("NewApi")
    public static void setBackgroundActivity(Activity activity, View view) {
        // background
        Resources res = activity.getResources();
        Drawable drawable = res.getDrawable(R.drawable.background_1);

        switch (GameController.getInstance().getCurrentPuzzleSize(activity)) {

            case S22:
                drawable = res.getDrawable(R.drawable.background_1);
                break;

            case S32:
                drawable = res.getDrawable(R.drawable.background_2);
                break;

            case S33:
                drawable = res.getDrawable(R.drawable.background_3);
                break;

            case S43:
                drawable = res.getDrawable(R.drawable.background_4);
                break;

            case S44:
                drawable = res.getDrawable(R.drawable.background_5);
                break;

            case S54:
                drawable = res.getDrawable(R.drawable.background_6);
                break;

            case S55:
                drawable = res.getDrawable(R.drawable.background_7);
                break;

            case S65:
                drawable = res.getDrawable(R.drawable.background_8);
                break;

            case S66:
                drawable = res.getDrawable(R.drawable.background_9);
                break;

            case S76:
                drawable = res.getDrawable(R.drawable.background_10);
                break;

            default:
                break;
        }

        view.setBackground(drawable);
    }

    /**
     * Animations
     */
    @SuppressLint("NewApi")
    public static void slideDownLayoutAnimation(final View layout, final int delay) {

        final int multiplier = slideDirectionTop ? 1 : -1;
        layout.setTranslationY(-3000 * multiplier);

        layout.animate()
                .translationY(300 * multiplier)
                .setDuration(220)
                .rotationY(10)
                .setStartDelay(delay)
                .withEndAction(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        layout.animate()
                                .translationY(-220 * multiplier)
                                .rotationY(-8)
                                .setDuration(150)
                                .setStartDelay(0)
                                .withEndAction(new Runnable() {

                                    @SuppressLint("NewApi")
                                    @Override
                                    public void run() {
                                        layout.animate()
                                                .translationY(150 * multiplier)
                                                .rotationY(6)
                                                .setDuration(110)
                                                .setStartDelay(0)
                                                .withEndAction(new Runnable() {

                                                    @SuppressLint("NewApi")
                                                    @Override
                                                    public void run() {
                                                        layout.animate()
                                                                .translationY(-90 * multiplier)
                                                                .rotationY(-3)
                                                                .setDuration(80)
                                                                .setStartDelay(0)

                                                                .withEndAction(new Runnable() {

                                                                    @Override
                                                                    public void run() {
                                                                        layout.animate()
                                                                                .translationY(50 * multiplier)
                                                                                .rotationY(0)
                                                                                .setDuration(60)
                                                                                .setStartDelay(0)

                                                                                .withEndAction(new Runnable() {

                                                                                    @Override
                                                                                    public void run() {
                                                                                        slideDirectionTop = !slideDirectionTop;
                                                                                        layout.animate()
                                                                                                .translationY(0)
                                                                                                .rotationY(0)
                                                                                                .setDuration(50)
                                                                                                .setStartDelay(0)
                                                                                                .start();
                                                                                    }
                                                                                })
                                                                                .start();
                                                                    }
                                                                }).start();
                                                    }
                                                }).start();
                                    }
                                }).start();
                    }
                }).start();
    }

    /**
     * Animations
     */
    @SuppressLint("NewApi")
    public static void slideSimpleAnimation(final View layout, final int delay) {

        final int multiplier = slideDirectionTop ? 1 : -1;
        layout.setTranslationY(-3000 * multiplier);

        layout.animate()
                .translationY(0)
                .setDuration(2000)
                .setStartDelay(delay)
                .withEndAction(new Runnable() {
                    @SuppressLint("NewApi")
                    @Override
                    public void run() {
                        slideDirectionTop = !slideDirectionTop;
                    }
                }).start();
    }

    /**
     * Enable share button
     */
    public static void enableShareButton(final Activity activity, View view) {

        view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = activity.getString(R.string.message_share_body);
                String shareSubject = activity.getString(R.string.app_name);
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.share_friends)));
            }
        });
    }

    /**
     * Alert with information message
     */
    public static void infoMessage(final Activity activity, String title, String body) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {}
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setMessage(body).setPositiveButton(R.string.okay, dialogClickListener).show();
    }
}
