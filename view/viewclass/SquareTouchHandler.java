package com.pausanchezv.puzzle.view.viewclass;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.controller.GameController;
import com.pausanchezv.puzzle.controller.SquareAdapter;
import com.pausanchezv.puzzle.model.Level;
import com.pausanchezv.puzzle.model.Square;
import com.pausanchezv.puzzle.view.GamePlayActivity;
import com.pausanchezv.puzzle.view.LevelLoseActivity;
import com.pausanchezv.puzzle.view.LevelWinActivity;

import java.util.ArrayList;
import java.util.Map;

/**
 * Square touch handler
 */
public abstract class SquareTouchHandler {

    // Controller
    private static GameController ctrl = GameController.getInstance();

    // Squares
    private static Square startSquare = null;
    private static Square goalSquare = null;
    private static boolean isSwapActive = false;
    public static boolean isGoalState = false;
    private static ArrayList<Square> validGoalSquares = new ArrayList<>();

    // Super globals
    private static GridView gridView;
    private static SquareAdapter adapter;
    private static Activity context;
    private static View globalStartFigure;
    private static Level level;

    // Indicates whether a square has been dropped or not
    private static boolean isDroppedSquare;

    /**
     * Reset all handler values
     */
    private static void resetSquareTouchHandler() {
        startSquare = null;
        goalSquare = null;
        isSwapActive = false;
    }

    /**
     * Square Touch Class
     */
    public static class SquareTouchable implements AdapterView.OnTouchListener {

        // Touch variables
        private int position;
        private View startViewOnDrag = null;

        /**
         * Square Touch Constructor
         */
        public SquareTouchable(SquareAdapter adapter, int position, Activity context, Level level) {

            SquareTouchHandler.adapter = adapter;
            SquareTouchHandler.context = context;
            SquareTouchHandler.level = level;

            this.position = position;

        }

        /**
         * On touch method
         */
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            // Click on the square
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                // start square and view
                startViewOnDrag = view;
                startDrag();

                return true;
            }

            view.performClick();
            return false;
        }

        /**
         * Actions when it start dragging
         */
        private void startDrag() {

            // Getting start square
            startSquare = adapter.getItem(position);
            assert startSquare != null;

            // Doing stuff only if it is not a wall
            if (!startSquare.getFigure().equals("#")) {

                ClipData data = ClipData.newPlainText("", "");
                View figure = startViewOnDrag;

                // If game mode is color matching, inner squares will be moved
                if (level.getKind() == 1) {
                    figure = ((FrameLayout) startViewOnDrag).getChildAt(1);
                }

                // Generating drag shadow
                View.DragShadowBuilder shadowBuilder = new SquareTouchHandler.DragShadowBuilder(figure);
                startViewOnDrag.startDrag(data, shadowBuilder, figure, 0);

                globalStartFigure = figure;

                // Adding valid positions and hiding the current one
                figure.setVisibility(View.INVISIBLE);
                addValidPositions(adapter, startSquare, level);

                // Selection sound
                GamePlayActivity.playSelectionSound();
            }
        }
    }

    /**
     * Draggable class
     */
    public static class SquareDraggable implements AdapterView.OnDragListener {

        // Square
        private Square square;

        /**
         * Draggable constructor
         */
        public SquareDraggable(SquareAdapter adapter, Square square, Level level) {
            this.square = square;
            SquareTouchHandler.adapter = adapter;
            SquareTouchHandler.level = level;
        }

        /**
         * On drag method
         */
        @SuppressLint("NewApi")
        @Override
        public boolean onDrag(View view, DragEvent dragEvent) {

            int action = dragEvent.getAction();

            // Evaluating action drag
            switch (action) {

                // When drag starts
                case DragEvent.ACTION_DRAG_STARTED:
                    isDroppedSquare = false;
                    return dragEvent.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

                // When a figure is dropped
                case DragEvent.ACTION_DROP:

                    removeValidPositionsPoints(adapter);

                    // prevent app crash because of multiple continued dragging
                    removeTouchListeners(adapter);
                    isDroppedSquare = true;

                    // Do nothing if a block is selected as a start square
                    if (startSquare.getFigure().equals("#")) {
                        resetSquareTouchHandler();

                    // All stuff will start if it is not a wall
                    } else {

                        // Selection sound
                        GamePlayActivity.playPlaceSound();

                        goalSquare = square;

                        // Position does not change
                        if (startSquare.equals(goalSquare)) {

                            removeValidPositionsPoints(adapter);
                            resetSquareTouchHandler();

                            // Rebuild all the adapter for each step (like HTML version)
                            adapter = new SquareAdapter(context, 0, level.getSquaresList(), level);
                            adapter.setActivity(context);
                            gridView.setAdapter(adapter);
                            GamePlayActivity.startAnimateMonsters(adapter);

                            return false;
                        }

                        // Wall or invalid move
                        if (goalSquare.getFigure().equals("#") || !validGoalSquares.contains(goalSquare)) {

                            // Error sound
                            GamePlayActivity.playErrorPlaceSound();

                            // Shake square effect
                            shakeInvalidPositionEffect(square, adapter);
                            removeValidPositionsPoints(adapter);

                            // Rebuild all the adapter for each step (like HTML version)
                            adapter = new SquareAdapter(context, 0, level.getSquaresList(), level);
                            adapter.setActivity(context);
                            gridView.setAdapter(adapter);
                            GamePlayActivity.startAnimateMonsters(adapter);

                            // remove start square if movement is invalid
                            startSquare = null;

                            return false;
                        }

                        // Get the views
                        final View startView = adapter.getViewFromSquareObject(startSquare);
                        final View goalView = adapter.getViewFromSquareObject(goalSquare);

                        // Compute distances
                        int startViewX = (startSquare.getCol() == goalSquare.getCol()) ? 0 : (startSquare.getCol() - goalSquare.getCol()) * startView.getWidth();
                        final int goalViewX = (goalSquare.getCol() == startSquare.getCol()) ? 0 : (goalSquare.getCol() - startSquare.getCol()) * goalView.getWidth();
                        int startViewY = (startSquare.getRow() == goalSquare.getRow()) ? 0 : (startSquare.getRow() - goalSquare.getRow()) * startView.getHeight();
                        final int goalViewY = (goalSquare.getRow() == startSquare.getRow()) ? 0 : (goalSquare.getRow() - startSquare.getRow()) * goalView.getHeight();

                        startView.setPadding(0, 0, 0, 0);
                        goalView.setPadding(0, 0, 0, 0);

                        View startImage = ((FrameLayout) startView).getChildAt(0);
                        View goalImage = ((FrameLayout) goalView).getChildAt(0);

                        globalStartFigure.setVisibility(View.VISIBLE);

                        // Update background before swapping the squares
                        if (level.getKind() == 1) {
                            Drawable tempColor = startImage.getBackground();
                            startImage.setBackground(goalImage.getBackground());
                            goalImage.setBackground(tempColor);
                        }

                        // No start animation needed
                        startView.setTranslationX(-startViewX);
                        startView.setTranslationY(-startViewY);

                        // Fast goal animation with end actions
                        goalView.animate()
                                .translationX(-goalViewX)
                                .translationY(-goalViewY)
                                .setDuration(100)
                                .withEndAction(new Runnable() {

                                    // Runnable preventing figure's stuck
                                    @Override
                                    public void run() {

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {

                                                // Swap views
                                                adapter.swapViews(startSquare, goalSquare, level);

                                                // Swap squares on level class
                                                level.swapSquare(startSquare.getRow(), startSquare.getCol(), goalSquare.getRow(), goalSquare.getCol());

                                                // Swap square's properties on its class
                                                startSquare.swapSquare(goalSquare);

                                                // Refill the list of squares
                                                level.fillSquaresList();

                                                // Rebuild all the adapter on each step (like HTML version)
                                                adapter = new SquareAdapter(context, 0, level.getSquaresList(), level);
                                                adapter.setActivity(context);
                                                gridView.setAdapter(adapter);
                                                GamePlayActivity.startAnimateMonsters(adapter);

                                                // Update view and decide if the level is done
                                                doActions(context, level, gridView);

                                                // Restart squares when the swap is done
                                                resetSquareTouchHandler();
                                            }
                                        }, 10);
                                    }
                                }).start();
                    }
                    break;

                // End dragging - It is useful to detect if a square is placed out of limits
                case DragEvent.ACTION_DRAG_ENDED:

                    // Regenerate matrix if a square is dropped out of limits
                    if (!isDroppedSquare) {
                        isDroppedSquare = true;
                        adapter = new SquareAdapter(context, 0, level.getSquaresList(), level);
                        adapter.setActivity(context);
                        gridView.setAdapter(adapter);
                        GamePlayActivity.startAnimateMonsters(adapter);
                    }
                    break;

                default:
                    break;
            }

            return true;
        }
    }

    /**
     * Clickable square
     */
    public static class SquareClickable implements AdapterView.OnItemClickListener {

        /**
         * Clickable square constructor
         */
        public SquareClickable(Activity context, SquareAdapter adapter, Level level, GridView gridView) {
            SquareTouchHandler.adapter = adapter;
            SquareTouchHandler.context = context;
            SquareTouchHandler.level = level;
            SquareTouchHandler.gridView = gridView;

            resetSquareTouchHandler();
            isGoalState = false;
        }

        /**
         * On item click
         */
        @SuppressLint("NewApi")
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            // get the square
            Square square = adapter.getItem(i);
            assert square != null;

            // Do nothing if there is any transition active
            if (isSwapActive) {
                return;
            }

            // Check whether or not the start square is null
            if (startSquare != null) {

                // Assign goal square if there is a start
                goalSquare = square;

                // Invalid move
                if (goalSquare.getFigure().equals("#") || !validGoalSquares.contains(square) && !square.equals(startSquare)) {

                    // Error sound
                    GamePlayActivity.playErrorPlaceSound();

                    // Shake square effect
                    shakeInvalidPositionEffect(square, adapter);
                    return;
                }

                // The first action is to remove the selection effects if it exists
                removeSelectedSquare(startSquare, adapter);

                // Remove selection if start is equal to goal
                if (goalSquare.equals(startSquare)) {

                    // Reset globals and start again
                    resetSquareTouchHandler();
                    removeValidPositionsPoints(adapter);

                    return;
                }

                // Place sound
                GamePlayActivity.playPlaceSound();

                // At this point the swap starts
                isSwapActive = true;

                // Quit selected points
                removeValidPositionsPoints(adapter);

                // Get the views
                final View startView = adapter.getViewFromSquareObject(startSquare);
                final View goalView = adapter.getViewFromSquareObject(goalSquare);

                // Compute distances
                int startViewX = (startSquare.getCol() == goalSquare.getCol()) ? 0 : (startSquare.getCol() - goalSquare.getCol()) * startView.getWidth();
                int goalViewX = (goalSquare.getCol() == startSquare.getCol()) ? 0 : (goalSquare.getCol() - startSquare.getCol()) * goalView.getWidth();
                int startViewY = (startSquare.getRow() == goalSquare.getRow()) ? 0 : (startSquare.getRow() - goalSquare.getRow()) * startView.getHeight();
                int goalViewY = (goalSquare.getRow() == startSquare.getRow()) ? 0 : (goalSquare.getRow() - startSquare.getRow()) * goalView.getHeight();

                startView.setPadding(0,0,0,0);
                goalView.setPadding(0,0,0,0);

                View startImage = ((FrameLayout) startView).getChildAt(0);
                View goalImage = ((FrameLayout) goalView).getChildAt(0);

                // Update background before swapping the squares
                if (level.getKind() == 1) {
                    Drawable tempColor = startImage.getBackground();
                    startImage.setBackground(goalImage.getBackground());
                    goalImage.setBackground(tempColor);
                }

                // Start square animation
                startView.animate()
                        .translationX(-startViewX)
                        .translationY(-startViewY)
                        .setDuration(200)
                        .start();

                // Goal square animation is a bit more longer and it rebuilds the whole matrix in each swap
                goalView.animate()
                        .translationX(-goalViewX)
                        .translationY(-goalViewY)
                        .setDuration(250)
                        .withEndAction(new Runnable() {

                            @Override
                            public void run() {

                                // Handler preventing square stuck in movement
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        // Swap views
                                        adapter.swapViews(startSquare, goalSquare, level);

                                        // Swap squares on level class
                                        level.swapSquare(startSquare.getRow(), startSquare.getCol(), goalSquare.getRow(), goalSquare.getCol());

                                        // Swap square's properties on its class
                                        startSquare.swapSquare(goalSquare);

                                        // Refill the list of squares
                                        level.fillSquaresList();

                                        // Rebuild all the adapter on each step (like HTML version)
                                        adapter = new SquareAdapter(context, 0, level.getSquaresList(), level);
                                        adapter.setActivity(context);
                                        gridView.setAdapter(adapter);
                                        GamePlayActivity.startAnimateMonsters(adapter);

                                        // Update view and decide if the level is done
                                        doActions(context, level, gridView);

                                        // Restart squares when the swap is done
                                        resetSquareTouchHandler();
                                    }
                                }, 10);
                            }
                        }).start();

            // Start square is null here, but it can still be a wall
            } else {

                // Assign start square if it is not a wall
                if (!square.getFigure().equals("#")) {
                    startSquare = square;
                    addSelectedSquare(square, adapter);
                    addValidPositions(adapter, square, level);

                    // Selection sound
                    GamePlayActivity.playSelectionSound();
                }
            }
        }
    }

    /**
     * Actions after each square swap
     */
    private static void doActions(Activity context, Level level, GridView gridView) {

        // Update number of moves left
        ctrl.setMovesLeft(ctrl.getMovesLeft() - 1);
        TextView movesLeft = context.findViewById(R.id.movesLeft);
        movesLeft.setText(context.getResources().getString(R.string.moves_left).concat(String.valueOf(ctrl.getMovesLeft())));
        GameController.USED_MOVES_NUM++;

        // Actions to do if is the goal state
        if (level.isGoal()) {

            // Update goal state
            isGoalState = true;

            // Actions to do when the user wins
            redirectToActivityWin(context, gridView);
        }

        // Actions to do if the player loses
        else if (ctrl.getMovesLeft() < 1) {

            // Actions to do when the user wins
            redirectToActivityLose(context, gridView);
        }
    }

    /**
     * Redirect to the win activity
     */
    private static void redirectToActivityWin(final Activity context, GridView gridView) {

        // Remove click listener from squares
        gridView.setOnItemClickListener(null);

        // Set a new handler
        final Handler handler = new Handler();

        // Delay
        Runnable runner = new Runnable() {

            public void run() {

                //handler.removeCallbacks(prepareNextLevel);
                ctrl.setCurrentLevelNumber(context, ctrl.getCurrentLevelNumber(context) + 1);

                // Redirect
                ViewFunctions.intent(context, LevelWinActivity.class, ViewFunctions.TransitionMode.RIGHT_TO_LEFT);
            }
        };

        // Start runnable
        handler.postDelayed(runner, 1000);
    }

    /**
     * Redirect to the win activity
     */
    private static void redirectToActivityLose(final Activity context, GridView gridView) {

        // Remove click listener from squares
        gridView.setOnItemClickListener(null);

        // Set a new handler
        final Handler handler = new Handler();

        // Delay
        Runnable runner = new Runnable() {
            public void run() {

                // Redirect
                ViewFunctions.intent(context, LevelLoseActivity.class, ViewFunctions.TransitionMode.LEFT_TO_RIGHT);

            }
        };

        // Start runnable
        handler.postDelayed(runner, 1000);
    }

    /**
     * Remove valid moves points
     */
    private static void removeValidPositionsPoints(SquareAdapter adapter) {

        for (Object o : adapter.getViewsAndSquaresMap().entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            Square s = (Square) pair.getValue();

            if (validGoalSquares.contains(s)) {
                View view = ((FrameLayout) pair.getKey()).getChildAt(3);
                view.setAlpha(0);
                view.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Remove touch listeners
     */
    private static void removeTouchListeners(SquareAdapter adapter) {

        for (Object o : adapter.getViewsAndSquaresMap().entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            View view = (View) pair.getKey();

            view.setOnTouchListener(null);
            view.setOnDragListener(null);
        }
    }

    /**
     * Show and save valid goal positions for each figure
     */
    private static void addValidPositions(SquareAdapter adapter, Square square, Level level) {

        // Get both the figure and its scope
        String figure = square.getFigure();
        int scope = square.getScope();

        // Fill the arrays
        validGoalSquares = new ArrayList<>();
        ArrayList<String> invalidDirections = new ArrayList<>();

        // Traverse through the scope
        for (int row = 1; row <= scope; row++) {
            for (int col = 1; col <= scope; col++) {

                // Horizontals
                if (figure.equals("T") || figure.equals("Q")) {

                    // Top
                    Square s = level.getSquareObj(square.getRow() - row, square.getCol());

                    if (s != null && s.isWall()) {
                        invalidDirections.add("top");
                    }

                    if (!invalidDirections.contains("top") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Bottom
                    s = level.getSquareObj(square.getRow() + row, square.getCol());

                    if (s != null && s.isWall()) {
                        invalidDirections.add("bottom");
                    }

                    if (!invalidDirections.contains("bottom") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Left
                    s = level.getSquareObj(square.getRow(), square.getCol() - col);

                    if (s != null && s.isWall()) {
                        invalidDirections.add("left");
                    }

                    if (!invalidDirections.contains("left") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Right
                    s = level.getSquareObj(square.getRow(), square.getCol() + col);

                    if (s != null && s.isWall()) {
                        invalidDirections.add("right");
                    }

                    if (!invalidDirections.contains("right") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                }

                // Diagonals
                if (figure.equals("B") || figure.equals("Q")) {

                    // Top-left
                    Square s = level.getSquareObj(square.getRow() - row, square.getCol() - col);

                    if (col == row && s != null && s.isWall()) {
                        invalidDirections.add("top-left");
                    }

                    if (col == row && !invalidDirections.contains("top-left") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Bottom-left
                    s = level.getSquareObj(square.getRow() + row, square.getCol() - col);

                    if (col == row && s != null && s.isWall()) {
                        invalidDirections.add("bottom-left");
                    }

                    if (col == row && !invalidDirections.contains("bottom-left") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Bottom-right
                    s = level.getSquareObj(square.getRow() + row, square.getCol() + col);

                    if (col == row && s != null && s.isWall()) {
                        invalidDirections.add("bottom-right");
                    }

                    if (col == row && !invalidDirections.contains("bottom-right") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }

                    // Top-Right
                    s = level.getSquareObj(square.getRow() - row, square.getCol() + col);

                    if (col == row && s != null && s.isWall()) {
                        invalidDirections.add("top-right");
                    }

                    if (col == row && !invalidDirections.contains("top-right") && s != null && !s.isWall() && !validGoalSquares.contains(s)) {
                        validGoalSquares.add(s);
                    }
                }
            }
        }


        // Add visual effect
        for (Object o : adapter.getViewsAndSquaresMap().entrySet()) {

            Map.Entry pair = (Map.Entry) o;
            Square s = (Square) pair.getValue();

            if (validGoalSquares.contains(s)) {

                View view = ((FrameLayout) pair.getKey()).getChildAt(3);
                view.setVisibility(View.VISIBLE);
                setAlphaAnimation(view);

            }
        }
    }

    /**
     * Add selected image to square
     */
    private static void addSelectedSquare(Square square, SquareAdapter adapter) {

        if (!square.getFigure().equals("#")) {

            View view = adapter.getViewFromSquareObject(square);
            view = ((FrameLayout) view).getChildAt(2);
            view.setVisibility(View.VISIBLE);

            setAlphaAnimation(view);
        }
    }

    /**
     * Remove selected from square
     */
    private static void removeSelectedSquare(Square square, SquareAdapter adapter) {
        View view = adapter.getViewFromSquareObject(square);
        view = ((FrameLayout) view).getChildAt(2);
        view.setVisibility(View.INVISIBLE);
        view.setAlpha(0);
    }

    /**
     * Set alpha animation
     */
    private static void setAlphaAnimation(View view) {
        view.setAlpha(1);
        Animation animation = new AlphaAnimation(.75f, 1.f);
        animation.setDuration(800);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        view.startAnimation(animation);
    }

    /**
     * Square shake effect
     */
    private static void shakeInvalidPositionEffect(Square square, SquareAdapter adapter) {

        //if (!square.getFigure().equals("#")) {

            View goalView = adapter.getViewFromSquareObject(square);
            Animation shake = new RotateAnimation(-5f, 5f, goalView.getWidth() / 2, goalView.getWidth() / 2);

            // Set the animation's parameters
            shake.setDuration(25);                     // duration in ms
            shake.setRepeatCount(8);                  // -1 = infinite repeated
            shake.setRepeatMode(Animation.REVERSE);    // reverses each repeat
            shake.setFillAfter(false);                 // keep rotation after animation
            goalView.setAnimation(shake);
         //}
    }

    /**
     * InnerStaticClass:: Customize shadow on drag
     */
    public static class DragShadowBuilder extends View.DragShadowBuilder {

        // Scale factor
        private Point scaleFactor;

        /**
         * DragShadowBuilder Constructor.
         *
         * @param view is the curr view
         */
        DragShadowBuilder(View view) {
            super(view);
        }

        @Override
        public void onProvideShadowMetrics(Point size, Point touch) {

            // Base sizes
            float width;
            float height;

            // New shadow size
            switch (ctrl.getCurrentLevel().getSize()) {

                case S21:
                case S22:
                case S32:
                    width = getView().getWidth() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.3f : 1.1f);
                    height = getView().getHeight() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.3f : 1.1f);
                    break;

                case S33:
                case S43:
                    width = getView().getWidth() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.4f : 1.2f);
                    height = getView().getHeight() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.4f : 1.2f);
                    break;

                case S44:
                case S54:
                    width = getView().getWidth() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.5f : 1.3f);
                    height = getView().getHeight() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.5f : 1.3f);
                    break;

                case S55:
                case S65:
                    width = getView().getWidth() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.7f : 1.4f);
                    height = getView().getHeight() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.7f : 1.4f);
                    break;

                default:
                    width = getView().getWidth() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.8f : 1.5f);
                    height = getView().getHeight() * (ctrl.getCurrentLevel().getKind() == 1 ? 1.8f : 1.5f);
                    break;
            }

            // Add touch to the middle of image
            size.set(Math.round(width), Math.round(height));
            scaleFactor = size;
            touch.set(Math.round(width) / 2, Math.round(height) / 2);
        }

        /**
         * Draw shadow method
         *
         * @param canvas canvas
         */
        @Override
        public void onDrawShadow(Canvas canvas) {
            canvas.scale(scaleFactor.x / (float) getView().getWidth(), scaleFactor.y / (float) getView().getHeight());
            getView().draw(canvas);
        }
    }
}
