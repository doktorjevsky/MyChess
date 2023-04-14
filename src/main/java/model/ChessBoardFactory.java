package model;

import enums.Color;
import enums.Value;

public class ChessBoardFactory {


    public static Piece[][] getStandardBoard(){
        Piece[][] board = new Piece[8][8];
        board[0] = backRow(Color.WHITE);
        board[1] = pawnRow(Color.WHITE);
        board[7] = backRow(Color.BLACK);
        board[6] = pawnRow(Color.BLACK);
        return board;
    }

    private static Piece[] backRow(Color c){
        return new Piece[]{new Piece(Value.ROOK, c), new Piece(Value.KNIGHT, c), new Piece(Value.BISHOP, c),
        new Piece(Value.QUEEN, c), new Piece(Value.KING, c), new Piece(Value.BISHOP, c), new Piece(Value.KNIGHT, c), new Piece(Value.ROOK,c)};
    }

    private static Piece[] pawnRow(Color c){
        Piece[] ps = new Piece[8];
        for (int i = 0; i < 8; i++){
            ps[i] = new Piece(Value.PAWN, c);
        }
        return ps;
    }

    public static Piece[][] fromString(String s) throws Exception {
        if (s.length() != 64){
            throw new Exception("Wrong format!");
        }
        Piece[][] out = new Piece[8][8];
        for (int i = 0; i < s.length(); i++){
            out[i / 8][i % 8] = Piece.fromChar(s.charAt(i));
        }
        return out;
    }

    public static String getString(Piece[][] board){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 64; i++){
            Piece p = board[i/8][i%8];
            if (p == null){
                sb.append(" ");
            } else {
                sb.append(p);
            }
        }
        return sb.toString();
    }
}
