package client;

import enums.Color;
import message.Message;
import message.MessageType;
import model.Move;
import model.Piece;
import model.Position;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ClientController implements MouseListener, Observer {

    private ChessModel model;
    private Set<Move> moves = new HashSet<>();
    private final int BOX_WIDTH;
    private final int BOX_HEIGHT;

    public ClientController(ChessModel model, int BOX_HEIGHT, int BOX_WIDTH){
        this.model = model;
        this.BOX_HEIGHT = BOX_HEIGHT;
        this.BOX_WIDTH = BOX_WIDTH;
    }

    private void requestMoves(Position p){
        Message m = new Message(MessageType.GET_MOVES, p.toString());
        model.makeRequest(m);
    }

    private boolean inBounds(int x, int y){
        return 0 <= x && x <= BOX_WIDTH * 8 && 0 <= y && y <= BOX_WIDTH * 8;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int y = e.getY() / BOX_HEIGHT;
        int x = e.getX() / BOX_WIDTH;
        Color player = model.getPlayer();
        moves = model.getMoves();
        y = player == Color.WHITE ? 7 - y : y;

        if (inBounds(x * BOX_WIDTH, y * BOX_HEIGHT)){
            Position p = new Position(x, y);
            Move m = null;
            for (Move move : moves){
                if (move.y().equals(p)){
                    m = move;
                    break;
                }
            }
            if (m != null){
                model.makeMove(m);
            } else {
                requestMoves(p);
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void update() {
        moves = model.getMoves();
    }
}
