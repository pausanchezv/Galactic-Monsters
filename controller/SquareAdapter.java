package com.pausanchezv.puzzle.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;

import com.pausanchezv.puzzle.R;
import com.pausanchezv.puzzle.model.Level;
import com.pausanchezv.puzzle.model.Square;
import com.pausanchezv.puzzle.view.viewclass.SquareImageView;
import com.pausanchezv.puzzle.view.viewclass.SquareTouchHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Square Adapter
 */
public class SquareAdapter extends ArrayAdapter<Square> {

    // Contexts
    private Context context;
    private Activity activity;

    private List<Square> squares;
    private Level level;

    private HashMap<View, Square> viewsAndSquaresMap;

    private static String [] WALL_TYPES = {"wall0", "wall1", "wall2", "wall3", "wall4", "wall5", "wall6", "wall7", "wall8", "wall9", "wall10", "wall11", "wall12"};
    private static String wallType;


    /**
     * Adapter Constructor
     */
    public SquareAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Square> squares, Level level) {
        super(context, resource, squares);

        this.context = context;
        this.squares = squares;
        this.level = level;

        this.viewsAndSquaresMap = new HashMap<>();
    }

    /**
     * Set context from another one
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * Get views and squares map
     */
    public HashMap<View, Square> getViewsAndSquaresMap() {
        return viewsAndSquaresMap;
    }

    /**
     * Add wall tipe
     */
    public void addWallType() {
        wallType = WALL_TYPES[(int) Math.round(Math.random() * 12)];
    }

    /**
     * Gets an item identified by an index.
     */
    @Override
    public Square getItem(int index) {
        return level.getSquaresList().get(index);
    }

    /**
     * Returns the view associated to the passed square
     */
    public View getViewFromSquareObject(Square square) {
        for (View view : viewsAndSquaresMap.keySet()) {
            if (viewsAndSquaresMap.get(view).equals(square)) {
                return view;
            }
        }
        return null;
    }


    /**
     * Swap views
     */
    public void swapViews(Square startSquare, Square goalSquare, Level level) {

        // get views from objects
        View startSquareView = getViewFromSquareObject(startSquare);
        View goalSquareView = getViewFromSquareObject(goalSquare);

        // get views from DOM
        SquareImageView startSquareImage = startSquareView.findViewById(R.id.figureSquare);
        SquareImageView goalSquareImage = goalSquareView.findViewById(R.id.figureSquare);

        // Square images drawable ids
        int idDrawableStartImage = context.getResources().getIdentifier(startSquare.getFigure().toLowerCase().concat(String.valueOf(startSquare.getScope())),"drawable", context.getPackageName());
        int idDrawableGoalImage = context.getResources().getIdentifier(goalSquare.getFigure().toLowerCase().concat(String.valueOf(goalSquare.getScope())),"drawable", context.getPackageName());

        // Swap images
        startSquareImage.setImageResource(idDrawableGoalImage);
        goalSquareImage.setImageResource(idDrawableStartImage);

        // Swap colors
        setSquareColor(startSquareImage, goalSquare.getColor());
        setSquareColor(goalSquareImage, startSquare.getColor());

        // TODO:: Animation effect - Special sound ?
    }

    /**
     * Adapter get view
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get square item
        View squareView = convertView;

        // Inflate XML
        if(squareView == null) {

            squareView = LayoutInflater.from(this.context).inflate(R.layout.square, parent, false);

            // Get square DOM
            Square squareObject = squares.get(position);

            // Prevent index 0 from being repeated!
            boolean exist = false;
            for (Square s: viewsAndSquaresMap.values()) {
                if (s.equals(squareObject)) {
                    exist = true;
                }
            }

            // Relate object and view
            if (!exist) {
                viewsAndSquaresMap.put(squareView, squareObject);
            }

            // Get square layers
            SquareImageView squareImage = squareView.findViewById(R.id.figureSquare);
            SquareImageView squareBackground = squareView.findViewById(R.id.backgroundSquare);

            // Check if the square is a wall
            if (squareObject.getFigure().equals("#")) {
                int id = context.getResources().getIdentifier(wallType, "drawable", context.getPackageName());
                squareBackground.setImageResource(id);
            }

            // if it's not a wall, then fill the square with start & goal colors
            else {

                // Square image
                int id = context.getResources().getIdentifier(squareObject.getFigure().toLowerCase() + squareObject.getColor().toLowerCase(), "drawable", context.getPackageName());
                squareImage.setImageResource(id);

                // If the current state is the goal load a transparent image instead of figure
                if (SquareTouchHandler.isGoalState) {
                    squareImage.setImageResource(R.drawable.square_transparent);
                }

                // Square image padding
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(getSquareMargin(), getSquareMargin(), getSquareMargin(), getSquareMargin());
                squareImage.setLayoutParams(lp);

                // get goal colors
                ArrayList<String> goalColors = level.getSquaresGoalList();

                // Set square goal color
                if (level.getKind() == 1 && goalColors != null) {
                    setSquareColor(squareBackground, level.getSquaresGoalList().get(position));
                } else {
                    setSquareColor(squareBackground, squareObject.getColor());
                }

            }

            // Set square image color
            //setSquareColor(squareImage, squareObject.getColor());

            // Adding drag and drop
            if (GameController.getInstance().isDragActive(activity) && GameController.getInstance().getMovesLeft() > 0 && !level.isGoal()) {

                // Adding touch actions to curr view
                squareView.setOnTouchListener(new SquareTouchHandler.SquareTouchable(this, position, activity, level));

                // Adding drag & drop actions to current view
                squareView.setOnDragListener(new SquareTouchHandler.SquareDraggable(this, squareObject, level));
            }
        }

        return squareView;
    }


    /**
     * Set layer color
     */
    private void setSquareColor(View view, String color) {

        final int sdk = android.os.Build.VERSION.SDK_INT;

        switch (color) {

            case "R":
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.red_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.red_gradient_bg));
                }
                break;

            case "B":
                //view.setBackgroundColor(Color.rgb(0, 0, 255));

                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.blue_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_gradient_bg));
                }
                break;

            case "Y":
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.yellow_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.yellow_gradient_bg));
                }
                break;

            case "O":
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.orange_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_gradient_bg));
                }
                break;

            case "G":

                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.green_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.green_gradient_bg));
                }
                break;

            case "M":
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    view.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.gray_gradient_bg) );
                } else {
                    view.setBackground(ContextCompat.getDrawable(context, R.drawable.gray_gradient_bg));
                }
                break;
        }

    }

    /**
     * Square margin depending on the level rows
     */
    private int getSquareMargin() {
        return 5;
    }
}
