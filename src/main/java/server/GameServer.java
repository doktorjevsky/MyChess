package server;


import enums.Color;
import enums.GameState;
import message.Message;
import message.MessageType;
import model.ChessBoardFactory;
import model.ChessGame;
import model.Move;
import model.Position;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class GameServer {

    private ServerSocket server;
    private ChessGame gameInstance;
    private List<ClientHandler> clients = new ArrayList<>();
    private final Color[] player = new Color[]{Color.WHITE, Color.BLACK};

    public GameServer(ServerSocket server, ChessGame gameInstance){
        this.server = server;
        this.gameInstance = gameInstance;

    }

    // TODO: let the hosting player choose color
    public void bootServer() {
        try {
            for (int i = 0; i < 2; i++){
                Socket s = server.accept();
                ClientHandler client = new ClientHandler(player[i], s, this);
                clients.add(client);
                new Thread(client).start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeServer(){
        try {
            server.close();
        } catch (Exception ignore) {}
    }

    private String getBoard(){
        return ChessBoardFactory.getString(gameInstance.getBoard());
    }

    private String makeMove(String move, Color player) throws Exception {
        GameState state = gameInstance.makeMove(Move.fromString(move), player);
        switch (state) {
            case GAME_OVER -> {
                    update(new Message(MessageType.BOARD, getBoard()));
                    update(new Message(MessageType.GAME_OVER, player.toString()));
            }
            case DRAW      -> {
                update(new Message(MessageType.BOARD, getBoard()));
                update(new Message(MessageType.GAME_OVER, "DRAW"));
            }
            case CONTINUE  -> update(new Message(MessageType.BOARD, getBoard()));
        }
        return ChessBoardFactory.getString(gameInstance.getBoard());
    }

    private String getMoves(String position, Color player) throws Exception {
        return Move.stringFromList
                (gameInstance
                        .getValidMoves(
                                Position.
                                        fromString(position), player));
    }

    public synchronized Message handleRequest(Message request, Color player) throws Exception {
        Message s;
        switch (request.getType()) {
            case GET_MOVES -> s = new Message(MessageType.MOVES, getMoves(request.getData(), player));
            case MAKE_MOVE -> s = new Message(MessageType.BOARD, makeMove(request.getData(), player));
            default        -> s = new Message(MessageType.BOARD, getBoard());
        }
        return s;
    }

    private void update(Message m){
        clients.forEach(c -> c.broadcast(m));
    }



}
