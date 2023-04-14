package model;

import enums.Color;
import enums.Value;

import java.util.Objects;

public class Piece {

    private Value value;
    private Color color;

    public Piece(Value value, Color color){
        this.value = value;
        this.color = color;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Piece)){
            return false;
        }
        Piece p = (Piece) o;
        return p.value == value && p.color == color;
    }

    @Override
    public int hashCode(){
        return Objects.hash(value, color);
    }

    @Override
    public String toString(){
        if (value == Value.KNIGHT){
            return color == Color.BLACK ? "N" : "n";
        }
        return color == Color.BLACK ? value.toString().substring(0,1) : value.toString().substring(0,1).toLowerCase();
    }

    public static Piece fromChar(char ch){
        Value value = valueFromChar(ch);
        if (value == null){
            return null;
        }
        return Character.isLowerCase(ch) ? new Piece(value, Color.WHITE) : new Piece(value, Color.BLACK);
    }

    private static Value valueFromChar(char ch){
        Value out;
        switch (Character.toUpperCase(ch)){
            case 'P' -> out = Value.PAWN;
            case 'R' -> out = Value.ROOK;
            case 'N' -> out = Value.KNIGHT;
            case 'B' -> out = Value.BISHOP;
            case 'Q' -> out = Value.QUEEN;
            case 'K' -> out = Value.KING;
            default -> out = null;
        }
        return out;
    }
}
