package model;

import enums.Color;
import enums.GameState;

import java.util.List;

public class ChessGame {

    private Piece[][] board;
    private ChessBoard logic;
    private Color currentPlayer;

    public ChessGame(){
        newGame();
    }

    public void newGame(){
        board = ChessBoardFactory.getStandardBoard();
        logic = new ChessBoard();
        currentPlayer = Color.WHITE;
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

    // TODO: protocol for pawn promotion

    public synchronized GameState makeMove(Move m, Color player)  {
        if (player == currentPlayer && getValidMoves(m.x(), currentPlayer).contains(m)) {
            board = logic.makeValidMove(m, board);
            currentPlayer = currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
            return getState();
        } else {
            return GameState.INVALID_MOVE;
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
