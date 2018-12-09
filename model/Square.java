package com.pausanchezv.puzzle.model;


public final class Square {


    private  char color;
    private  char scope;
    private  char figure;
    private  int row;
    private  int col;

    Square(char figure, char scope, char color, int row, int col) {
        this.color = color;
        this.scope = scope;
        this.figure = figure;
        this.row = row;
        this.col = col;
    }

    public int getScope() {
        return Character.getNumericValue(scope);
    }

    public String getColor() {
        return Character.toString(color);
    }

    public String getFigure() {
        return Character.toString(figure);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void swapSquare(Square other) {

        char color = this.color;
        char scope = this.scope;
        char figure = this.figure;

        this.color = other.color;
        this.scope = other.scope;
        this.figure = other.figure;

        other.color = color;
        other.scope = scope;
        other.figure = figure;
    }

    public boolean isWall() {
        return figure == '#';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Square square = (Square) o;

        if (color != square.color) return false;
        if (scope != square.scope) return false;
        if (figure != square.figure) return false;
        if (row != square.row) return false;
        return col == square.col;
    }

    @Override
    public int hashCode() {
        int result = (int) color;
        result = 31 * result + (int) scope;
        result = 31 * result + (int) figure;
        result = 31 * result + row;
        result = 31 * result + col;
        return result;
    }
}
