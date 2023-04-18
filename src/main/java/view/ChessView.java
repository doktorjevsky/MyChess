package view;

import client.ChessModel;
import client.ClientController;
import client.Observer;
import enums.Color;
import enums.GameState;
import enums.Value;
import message.MessageType;
import model.Move;
import model.Piece;
import model.Position;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;
import java.util.List;

import static java.util.Objects.*;

public class ChessView extends JLayeredPane implements Observer {

    private ChessModel model;
    private final int BOX_SIZE = 64;
    private final int BOARD_SIZE = 8 * BOX_SIZE;

    private Map<Piece, BufferedImage> imageMap = new HashMap<>();
    private Map<Integer, BufferedImage> colorMap;
    private MouseListener ml;


    public ChessView(ChessModel model){
        ml = new ClientController(model, BOX_SIZE, BOX_SIZE);
        this.addMouseListener(ml);
        this.model = model;
        initMaps();
    }

    private void initMaps(){
        try {
            imageMap.put(new Piece(Value.PAWN, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_pawn.png"))));
            imageMap.put(new Piece(Value.ROOK, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_rook.png"))));
            imageMap.put(new Piece(Value.KNIGHT, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_knight.png"))));
            imageMap.put(new Piece(Value.BISHOP, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_bishop.png"))));
            imageMap.put(new Piece(Value.QUEEN, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_queen.png"))));
            imageMap.put(new Piece(Value.KING, Color.WHITE), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_king.png"))));
            imageMap.put(new Piece(Value.PAWN, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_pawn.png"))));
            imageMap.put(new Piece(Value.ROOK, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_rook.png"))));
            imageMap.put(new Piece(Value.KNIGHT, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_knight.png"))));
            imageMap.put(new Piece(Value.BISHOP, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_bishop.png"))));
            imageMap.put(new Piece(Value.QUEEN, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_queen.png"))));
            imageMap.put(new Piece(Value.KING, Color.BLACK), ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_king.png"))));
            colorMap = Map.of(
                    0, ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/white_square.png"))),
                    1, ImageIO.read(requireNonNull(getClass().getResource("/chesspieces/black_square.png")))
            );

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g){
        paintComponent(g);
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Piece[][] board      = model.getBoard();
        if (board == null){
            board = new Piece[8][8];
        }
        Color perspective    = model.getPlayer();
        Set<Move> highlight  = model.getMoves();
        BufferedImage boardImage = new BufferedImage(BOARD_SIZE + 5, BOARD_SIZE + 5, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gp = boardImage.createGraphics();
        printBoardWithHighlights(gp, highlight, perspective);

        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){

                int x = col * BOX_SIZE;
                int y = perspective == Color.WHITE ? (7 - row) * BOX_SIZE : row * BOX_SIZE;

                if (board[row][col] != null){
                    BufferedImage pieceImage = imageMap.get(board[row][col]);
                    gp.drawImage(pieceImage, x, y, BOX_SIZE, BOX_SIZE,null);
                }
            }
        }
        g.drawImage(boardImage,0,0, null);

    }

    private void printBoardWithHighlights(Graphics2D gp, Set<Move> highlight, Color perspective){
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){

                int x = col * BOX_SIZE;
                int y = perspective == Color.WHITE ? (7 - row) * BOX_SIZE : row * BOX_SIZE;

                BufferedImage backgroundColor = (row + col) % 2 != 0 ? colorMap.get(0) : colorMap.get(1);
                gp.drawImage(backgroundColor, x, y, BOX_SIZE, BOX_SIZE, null);

                gp.setColor(java.awt.Color.BLACK);
                gp.drawRect(x,y,BOX_SIZE, BOX_SIZE);

                int dummyx = col;
                int dummyy = row;
                if (highlight.stream().anyMatch(m -> m.y().equals(new Position(dummyx, dummyy)) || m.x().equals(new Position(dummyx, dummyy)))) {
                    gp.setColor(new java.awt.Color(0,255,0,75));
                    gp.fillRect(x, y, BOX_SIZE, BOX_SIZE);

                }
            }
        }
    }

    @Override
    public void update() {
        repaint();
    }

}
