package model;

import Interfaces.Incrementor;
import enums.Color;
import enums.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class that:
 *    - Given a Position on the board by a player of color Color, returns valid moves
 *    - Given a valid Move and a board (Piece[][]) in a valid state, performs the move
 *      and returns a new board.
 *    - Performs all regular moves and the special moves En Passant and Castling, but not Pawn Promotion
 *
 * */

public class ChessBoard {

    private final Stack<Move> transcript = new Stack<>();

    /**
    * Preconditions: board is in a valid state and m is a valid move
    * Postcondition: returns an updated board that is valid
    * */
    public Piece[][] makeValidMove(Move m, Piece[][] board){
        Piece[][] out;
        Color player = colorAt(m.x(), board);
        switch (Objects.requireNonNull(valueAt(m.x(), board))) {
            case PAWN -> {
                out = movePiece(m, board);
                int dx = Math.abs(m.y().x() - m.x().x());
                if (dx > 0 && pieceAt(m.y(), board) == null){ // en passant
                    if (colorAt(m.x(),board) == Color.WHITE){
                        removePieceAt(new Position(m.y().x(), m.y().y()-1), out);
                    } else {
                        removePieceAt(new Position(m.y().x(), m.y().y()+1), out);
                    }
                }
            }
            case KING -> {
                out = movePiece(m, board);
                int dx = m.y().x() - m.x().x();
                if (dx == 2){
                    out = movePiece(new Move(kingRook(player), new Position(5, getBackRow(player))), out);
                }
                else if (dx == -2){
                    out = movePiece(new Move(queenRook(player), new Position(3, getBackRow(player))), out);
                }
            }
            default -> out = movePiece(m, board);
        }
        transcript.push(m);
        return out;
    }


    /**
     * Returns true if player "c" has lost
     * */
    public boolean isCheckMate(Color c, Piece[][] board){
        return inCheck(c, board) && !hasValidMoves(c, board);
    }

    public boolean isStaleMate(Piece[][] board){
        return !hasValidMoves(Color.WHITE, board) && !inCheck(Color.WHITE, board)
                || !hasValidMoves(Color.BLACK, board) && !inCheck(Color.BLACK, board);
    }

    private boolean hasValidMoves(Color c, Piece[][] board){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (validMovesFrom(c, new Position(j,i), board).size() > 0){
                    return true;
                }
            }
        }
        return false;
    }


    public void putPieceAt(Piece piece, Position p, Piece[][] board){
        board[p.y()][p.x()] = piece;
    }

    public void removePieceAt(Position p, Piece[][] board){
        board[p.y()][p.x()] = null;
    }

    public List<Move> validMovesFrom(Color c, Position p, Piece[][] board){
        List<Move> ms = new ArrayList<>();
        if (c == colorAt(p, board)) {
            switch (Objects.requireNonNull(valueAt(p, board))) {
                case QUEEN -> ms = queenMoves(c, p, board);
                case KING -> ms = kingMoves(c, p, board);
                case BISHOP -> ms = bishopMoves(c, p, board);
                case KNIGHT -> ms = knightMoves(c, p, board);
                case ROOK -> ms = rookMoves(c, p, board);
                case PAWN -> ms = pawnMoves(c, p, board);
            }
            ms = ms.stream().filter(m -> !inCheck(c, movePiece(m, board))).collect(Collectors.toList());
            if (valueAt(p, board) == Value.KING) {
                ms.addAll(castleMoves(c, board));
            }
        }
        return ms;
    }

    private Piece[][] movePiece(Move m, Piece[][] board){
        Piece[][] out = new Piece[8][8];
        for (int i = 0; i < 8; i++){
            System.arraycopy(board[i], 0, out[i], 0, 8);
        }
        putPieceAt(pieceAt(m.x(), out), m.y(), out);
        removePieceAt(m.x(), out);
        return out;
    }

    /*
    * Returns true if the square at position "p" on "board" is attacked by a piece of color "color"
    * */
    public boolean squareIsAttackedBy(Color color, Position p, Piece[][] board){
        return rookMoves(other(color), p, board).stream().anyMatch(
                m -> (new Piece(Value.ROOK, color)).equals(pieceAt(m.y(),board))
                        || (new Piece(Value.QUEEN, color)).equals(pieceAt(m.y(), board)))
                ||
                bishopMoves(other(color), p, board).stream().anyMatch(
                        m -> (new Piece(Value.BISHOP, color)).equals(pieceAt(m.y(), board))
                                || (new Piece(Value.QUEEN, color)).equals(pieceAt(m.y(), board)))
                ||
                knightMoves(other(color), p, board).stream().anyMatch(
                        m -> (new Piece(Value.KNIGHT, color)).equals(pieceAt(m.y(), board)))
                ||
                pawnAttackMoves(other(color), p, board).stream().anyMatch(
                        m -> (new Piece(Value.PAWN, color).equals(pieceAt(m.y(), board))))
                ||
                kingMoves(other(color), p, board).stream().anyMatch(
                        m -> (new Piece(Value.KING, color).equals(pieceAt(m.y(), board)))
                );
    }

    /*
    * returns true if the king of color "color" is in check
    * */
    public boolean inCheck(Color color, Piece[][] board){
        Position kp = kingPosOnBoard(color, board);
        if (kp == null){
            return false;
        } else {
            return squareIsAttackedBy(other(color), kp, board);
        }
    }

    private Position kingPosOnBoard(Color color, Piece[][] board){
        Piece p = new Piece(Value.KING, color);
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (p.equals(pieceAt(new Position(j,i), board))){
                    return new Position(j,i);
                }
            }
        }
        return null;
    }

    /*
    * Returns the (not yet validated) moves of a PIECE of color c at position p in board
    * */
    private List<Move> rookMoves(Color c, Position p, Piece[][] board){
        return iterateWhile(c, p, IncrementorFactories.straightIterators(), board);
    }

    private List<Move> bishopMoves(Color c, Position p, Piece[][] board){
        return iterateWhile(c, p, IncrementorFactories.diagonalIncrementors(), board);
    }

    private List<Move> queenMoves(Color c, Position p, Piece[][] board){
        List<Move> qms = rookMoves(c, p, board);
        qms.addAll(bishopMoves(c, p, board));
        return qms;
    }

    private List<Move> kingMoves(Color c, Position p, Piece[][] board){
        List<Move> ms = new ArrayList<>();
        List<Incrementor<Position>> incs = new ArrayList<>(IncrementorFactories.diagonalIncrementors());
        incs.addAll(IncrementorFactories.straightIterators());
        for (Incrementor<Position> f : incs){
            if (inBounds(f.increment(p)) && c != colorAt(f.increment(p), board)){
                ms.add(new Move(p, f.increment(p)));
            }
        }

        return ms;
    }

    private List<Move> castleMoves(Color c, Piece[][] board){
        List<Move> ms = new ArrayList<>();
        Position kp = kingPosOnBoard(c, board);
        if (hasMoved(kp) || inCheck(c, board)){
            return ms;
        }
        if (!hasMoved(kingRook(c)) && kingSideEmpty(c, board)){
            if (!squareIsAttackedBy(other(c), new Position(5, getBackRow(c)), board)
                    && !squareIsAttackedBy(other(c), new Position(6, getBackRow(c)), board)) {
                ms.add(new Move(kp, new Position(6, getBackRow(c))));
            }
        }
        if (!hasMoved(queenRook(c)) && queenSideEmpty(c, board)){
            if (!squareIsAttackedBy(other(c), new Position(3, getBackRow(c)), board)
                    && !squareIsAttackedBy(other(c), new Position(2, getBackRow(c)), board)){

                ms.add(new Move(kp, new Position(2, getBackRow(c))));
            }
        }
        return ms;
    }

    // TODO: en passant
    private List<Move> pawnMoves(Color c, Position p, Piece[][] board){
        List<Move> moves = new ArrayList<>();
        Incrementor<Position> f = IncrementorFactories.pawnIncrementor(c);
        if (inBounds(f.increment(p)) && !occupied(f.increment(p), board)){
            moves.add(new Move(p, f.increment(p)));
        }

        if (inBounds(f.increment(f.increment(p)))
                && !hasMoved(p)
                && !occupied(f.increment(f.increment(p)), board)
                && !occupied(f.increment(p), board)){
            moves.add(new Move(p, f.increment(f.increment(p))));
        }

        moves.addAll(pawnAttackMoves(c, p, board));
        moves.addAll(enPassantMoves(c, p));
        return moves;
    }

    private List<Move> enPassantMoves(Color c, Position p){
        List<Move> ms = new ArrayList<>();
        if (c == Color.WHITE && p.y() == 4){
            Move lastMove = transcript.empty() ? null : transcript.peek();
            if (lastMove == null){
                return ms;
            }
            else if (lastMove.equals(new Move(new Position(p.x()-1,6), new Position(p.x()-1,4)))){
                ms.add(new Move(p, new Position(p.x()-1, 5)));
            }
            else if (lastMove.equals(new Move(new Position(p.x()+1,6), new Position(p.x()+1,4)))){
                ms.add(new Move(p, new Position(p.x()+1, 5)));
            }
            return ms;
        }
        else if (c == Color.BLACK && p.y() == 3){
            Move lastMove = transcript.empty() ? null : transcript.peek();
            if (lastMove == null){
                return ms;
            }
            else if (lastMove.equals(new Move(new Position(p.x()-1,1), new Position(p.x()-1,3)))){
                ms.add(new Move(p, new Position(p.x()-1, 2)));
            }
            else if (lastMove.equals(new Move(new Position(p.x()+1,1), new Position(p.x()+1,3)))){
                ms.add(new Move(p, new Position(p.x()+1, 2)));
            }
            return ms;
        } else {
            return ms;
        }

    }

    private List<Move> pawnAttackMoves(Color c, Position p, Piece[][] board){
        List<Move> moves = new ArrayList<>();
        List<Incrementor<Position>> attacks = IncrementorFactories.pawnAttackIncrementors(c);
        for (Incrementor<Position> f : attacks){
            if (inBounds(f.increment(p)) && other(c) == colorAt(f.increment(p), board)){
                moves.add(new Move(p, f.increment(p)));
            }
        }
        return moves;
    }

    private List<Move> knightMoves(Color c, Position origin, Piece[][] board){
        List<Move> moves = new ArrayList<>();
        for (Incrementor<Position> f : IncrementorFactories.knightIncrementors()){
            if (inBounds(f.increment(origin)) && c != colorAt(f.increment(origin), board)){
                moves.add(new Move(origin, f.increment(origin)));
            }
        }
        return moves;
    }

    private List<Move> iterateWhile(Color c, Position origin, List<Incrementor<Position>> incs, Piece[][] board){
        List<Move> moves = new ArrayList<>();
        for (Incrementor<Position> f : incs){
            Position t = new Position(origin.x(), origin.y());
            while (inBounds(f.increment(t)) && !occupied(f.increment(t), board)){
                t = f.increment(t);
                moves.add(new Move(origin, t));
            }
            if (inBounds(f.increment(t)) && other(c) == colorAt(f.increment(t), board)){
                moves.add(new Move(origin, f.increment(t)));
            }
        }
        return moves;
    }

    private boolean hasMoved(Position p){
        return transcript.stream().anyMatch(m -> m.y().equals(p));
    }

    public Color other(Color c){
        return c == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private Piece pieceAt(Position p, Piece[][] board){
        return board[p.y()][p.x()];
    }

    private Color colorAt(Position p, Piece[][] board){
        return pieceAt(p, board) == null ? null : pieceAt(p, board).getColor();
    }

    private Value valueAt(Position p, Piece[][] board){
        return pieceAt(p, board) == null ? null : pieceAt(p, board).getValue();
    }

    private boolean occupied(Position p, Piece[][] board){
        return pieceAt(p, board) != null;
    }

    private boolean inBounds(Position p){
        return 0 <= p.x() && p.x() < 8 && 0 <= p.y() && p.y() < 8;
    }

    private boolean kingSideEmpty(Color c, Piece[][] board){
        int y = c == Color.WHITE ? 0 : 7;
        return !occupied(new Position(5, y), board)
                && !occupied(new Position(6, y), board);
    }

    private boolean queenSideEmpty(Color c, Piece[][] board){
        int y = getBackRow(c);
        return !occupied(new Position(3,y),board)
                && !occupied(new Position(2,y), board);
    }

    /*
    * CONSTANTS
    * */

    private Position kingPos(Color c){
        return new Position(4, getBackRow(c));
    }

    private Position kingRook(Color c){
        return new Position(7, getBackRow(c));
    }

    private Position queenRook(Color c){
        return new Position(0, getBackRow(c));
    }

    private int getBackRow(Color c){
        return c == Color.WHITE ? 0 : 7;
    }


}


