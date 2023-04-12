import enums.Color;
import enums.Value;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class ChessBoardTests {

    private Piece[][] standardBoard;
    private Piece[][] whiteCheck;
    private Piece[][] castleBoard;
    private Piece[][] kingBoard;
    private ChessBoard cb;

    @BeforeEach
    public void init(){
        standardBoard = ChessBoardFactory.getStandardBoard();

        whiteCheck = new Piece[8][8];
        whiteCheck[0][0] = new Piece(Value.KING, Color.WHITE);
        whiteCheck[0][7] = new Piece(Value.QUEEN, Color.BLACK);

        castleBoard = new Piece[8][8];
        castleBoard[0][0] = new Piece(Value.ROOK, Color.WHITE);
        castleBoard[0][7] = new Piece(Value.ROOK, Color.WHITE);
        castleBoard[0][4] = new Piece(Value.KING, Color.WHITE);
        castleBoard[7][0] = new Piece(Value.ROOK, Color.BLACK);
        castleBoard[7][7] = new Piece(Value.ROOK, Color.BLACK);
        castleBoard[7][4] = new Piece(Value.KING, Color.BLACK);

        kingBoard = new Piece[8][8];
        kingBoard[0][0] = new Piece(Value.KING, Color.WHITE);
        kingBoard[2][2] = new Piece(Value.KING, Color.BLACK);


        cb = new ChessBoard();
    }

    @Test
    public void attackedByQueenTest(){
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,0), whiteCheck));
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,7), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(1,1), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }

    @Test
    public void attackedByBishopTest(){
        whiteCheck[0][7] = null;
        whiteCheck[7][7] = new Piece(Value.BISHOP, Color.BLACK);
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,0), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(6,7), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }

    @Test
    public void attackedByKnightTest(){
        whiteCheck[0][7] = null;
        whiteCheck[2][1] = new Piece(Value.KNIGHT, Color.BLACK);
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,0), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(2,1), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(0,1), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }

    @Test
    public void attackedByPawnTest(){
        whiteCheck[1][1] = new Piece(Value.PAWN, Color.BLACK);
        whiteCheck[0][7] = null;
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,0), whiteCheck));
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(2,0), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(1,0), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }
    @Test
    public void attackedByKingTest(){
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.WHITE, new Position(1,0), whiteCheck));
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.WHITE, new Position(1,1), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.WHITE, new Position(2,2), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }

    @Test
    public void attackedByRookTest(){
        whiteCheck[0][7] = new Piece(Value.ROOK, Color.BLACK);
        Assertions.assertTrue(cb.squareIsAttackedBy(Color.BLACK, new Position(0,0), whiteCheck));
        Assertions.assertFalse(cb.squareIsAttackedBy(Color.BLACK, new Position(1,1), whiteCheck));
        Assertions.assertTrue(cb.inCheck(Color.WHITE, whiteCheck));
    }

    @Test
    public void notIncCheckTest(){
        Assertions.assertFalse(cb.inCheck(Color.WHITE, standardBoard));
        Assertions.assertFalse(cb.inCheck(Color.BLACK, standardBoard));
    }
    @Test
    public void initStateTest(){
        List<Move> wMoves = new ArrayList<>();
        List<Move> bMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                wMoves.addAll(cb.validMovesFrom(Color.WHITE, new Position(j,i), standardBoard));
                bMoves.addAll(cb.validMovesFrom(Color.BLACK, new Position(j,i), standardBoard));
            }
        }
        Assertions.assertEquals(wMoves.size(), bMoves.size());
        Assertions.assertEquals(20, wMoves.size());
    }

    @Test
    public void castleTest(){
        List<Move> whiteKing = cb.validMovesFrom(Color.WHITE, new Position(4,0), castleBoard);
        List<Move> blackKing = cb.validMovesFrom(Color.BLACK, new Position(4,7), castleBoard);

        Assertions.assertTrue(whiteKing.contains(new Move(new Position(4,0), new Position(6,0))));
        Assertions.assertTrue(whiteKing.contains(new Move(new Position(4,0), new Position(2,0))));

        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(6,7))));
        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(2,7))));
    }

    @Test
    public void castleTest2(){
        castleBoard[2][2] = new Piece(Value.ROOK, Color.BLACK);
        List<Move> whiteKing = cb.validMovesFrom(Color.WHITE, new Position(4,0), castleBoard);
        List<Move> blackKing = cb.validMovesFrom(Color.BLACK, new Position(4,7), castleBoard);

        Assertions.assertTrue(whiteKing.contains(new Move(new Position(4,0), new Position(6,0))));
        Assertions.assertFalse(whiteKing.contains(new Move(new Position(4,0), new Position(2,0))));

        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(6,7))));
        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(2,7))));
    }

    @Test
    public void castleTest3(){
        castleBoard[2][5] = new Piece(Value.ROOK, Color.BLACK);
        List<Move> whiteKing = cb.validMovesFrom(Color.WHITE, new Position(4,0), castleBoard);
        List<Move> blackKing = cb.validMovesFrom(Color.BLACK, new Position(4,7), castleBoard);

        Assertions.assertFalse(whiteKing.contains(new Move(new Position(4,0), new Position(6,0))));
        Assertions.assertTrue(whiteKing.contains(new Move(new Position(4,0), new Position(2,0))));

        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(6,7))));
        Assertions.assertTrue(blackKing.contains(new Move(new Position(4,7), new Position(2,7))));
    }

    @Test
    public void kingsTest(){
        List<Move> whiteKing = cb.validMovesFrom(Color.WHITE, new Position(0,0), kingBoard);
        Assertions.assertFalse(whiteKing.contains(new Move(new Position(0,0), new Position(1,1))));
    }

    @Test
    public void enPassantTest(){
        List<Move> moves = List.of(
                new Move(new Position(1,1), new Position(1,3)),
                new Move(new Position(6,7), new Position(5,4)),
                new Move(new Position(1,3), new Position(1,4)),
                new Move(new Position(0,6), new Position(0,4))
        );
        moves.forEach(m -> standardBoard = cb.makeValidMove(m, standardBoard));
        List<Move> enPassant = cb.validMovesFrom(Color.WHITE, new Position(1,4), standardBoard);
        Assertions.assertTrue(enPassant.contains(new Move(new Position(1,4), new Position(0,3))));
        Assertions.assertNull(standardBoard[3][0]);
    }

    @Test
    public void isCheckMate(){
        List<Move> moves = List.of(
                new Move(new Position(4,1), new Position(4,2)),
                new Move(new Position(5,6), new Position(5,5)),
                new Move(new Position(6,6), new Position(6,4)),
                new Move(new Position(3,0), new Position(7,4))
        );
        moves.forEach(m -> standardBoard = cb.makeValidMove(m, standardBoard));
        Assertions.assertTrue(cb.isCheckMate(Color.BLACK, standardBoard));
    }

    @Test
    public void staleMateTest(){
        Piece[][] b = new Piece[8][8];
        b[0][4] = new Piece(Value.KING, Color.WHITE);
        b[1][4] = new Piece(Value.PAWN, Color.BLACK);
        b[2][4] = new Piece(Value.KING, Color.BLACK);

        Assertions.assertTrue(cb.isStaleMate(b));
    }

    private void printBoard(Piece[][] b){
        for (Piece[] r : b){
            for (Piece p : r){
                if (p == null){
                    System.out.print("#");
                } else {
                    System.out.print(p);
                }
            }
            System.out.print("\n");
        }
    }


}
