package client;

import enums.Color;
import enums.GameState;
import message.Message;
import message.MessageType;
import model.ChessBoardFactory;
import model.Move;
import model.Piece;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChessModel implements MessageReader, Observable {


    private Piece[][] board;
    private Client client;
    private Set<Move> moves = new HashSet<>();
    private List<Observer> observers = new ArrayList<>();
    private Color player;
    private GameState state;
    private Color winner = null;

    public ChessModel(Client client){
        this.client = client;
        reload();
        this.state = GameState.CONTINUE;
    }

    public void reload(){
        try {
            client.makeRequest(new Message(MessageType.GET_BOARD, ""));
            client.makeRequest(new Message(MessageType.GET_COLOR, ""));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Piece[][] getBoard(){
        return board;
    }

    public Set<Move> getMoves(){
        return moves;
    }

    public Color getPlayer(){
        return player;
    }

    public void makeMove(Move m){
        try {
            client.makeRequest(new Message(MessageType.MAKE_MOVE, m.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeRequest(Message m){
        try {
            client.makeRequest(m);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameState getGameState(){
        return state;
    }

    public Color getWinner(){
        return winner;
    }

    @Override
    public void acceptMessage(Message m) {
        switch (m.getType()) {
            case BOARD -> {
                try {
                    board = ChessBoardFactory.fromString(m.getData());
                    moves = new HashSet<>();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case MOVES -> moves = new HashSet<>(Move.listFromString(m.getData()));
            case COLOR -> {
                try {
                    setColor(m.getData());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            case GAME_OVER -> {
                moves = new HashSet<>();
                state = GameState.GAME_OVER;
                if (!m.getData().equals("DRAW")){
                    if (m.getData().equals(Color.WHITE.toString())){
                        winner = Color.WHITE;
                    }
                    else if (m.getData().equals(Color.BLACK.toString())){
                        winner = Color.BLACK;
                    }
                }
            }
            default -> {}
        }
        notifyObservers();
    }

    private void setColor(String s) throws Exception {
        switch (s) {
            case "WHITE" -> { player = Color.WHITE; }
            case "BLACK" -> { player = Color.BLACK; }
            default -> throw new Exception("Color does not exist");
        }
    }

    @Override
    public void addObserver(Observer o) {
        observers.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    @Override
    public void removeAll() {
        observers = new ArrayList<>();
    }

    @Override
    public void notifyObservers() {
        observers.forEach(Observer::update);
    }
}
