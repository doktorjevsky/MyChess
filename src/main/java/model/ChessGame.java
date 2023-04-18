package model;

import enums.Color;
import enums.GameState;
import enums.Value;
import server.GameServer;

import java.util.List;

public class ChessGame {

    private Piece[][] board;
    private ChessBoard logic;
    private Color currentPlayer;
    private GameState state;

    public ChessGame(){
        newGame();
    }

    public void newGame(){
        board = ChessBoardFactory.getStandardBoard();
        logic = new ChessBoard();
        currentPlayer = Color.WHITE;
        state = GameState.CONTINUE;
    }

    public synchronized List<Move> getValidMoves(Position p, Color player){
        return logic.validMovesFrom(player, p, board);
    }

    public synchronized Color getCurrentPlayer(){
        return currentPlayer;
    }

    public synchronized Piece[][] getBoard(){
        Piece[][] out = new Piece[8][8];
        for (int i = 0; i < 8; i++){
            System.arraycopy(board[i], 0, out[i], 0, 8);
        }
        return out;
    }

    // TODO: protocol for pawn promotion. For now: promote to queen

    public synchronized GameState makeMove(Move m, Color player)  {
        if (player == currentPlayer && getValidMoves(m.x(), currentPlayer).contains(m) && state == GameState.CONTINUE) {
            board = logic.makeValidMove(m, board);
            if (wasPawnPromotion(m, player)) {
                logic.putPieceAt(new Piece(Value.QUEEN, player), m.y(), board);
            }
            currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
            state = getState();
            return state;
        } else {
            return GameState.INVALID_MOVE;
        }
    }

    private boolean wasPawnPromotion(Move m, Color player){
        if (player == Color.WHITE){
            return m.y().y() == 7 && (new Piece(Value.PAWN, Color.WHITE)).equals(logic.pieceAt(m.y(), board));
        } else {
            return m.y().y() == 0 && (new Piece(Value.PAWN, Color.BLACK)).equals(logic.pieceAt(m.y(), board));
        }
    }

    private GameState getState(){
        if (logic.isCheckMate(currentPlayer, board)){
            return GameState.GAME_OVER;
        }
        else if (logic.isStaleMate(board)){
            return GameState.DRAW;
        }
        else {
            return GameState.CONTINUE;
        }
    }
}
