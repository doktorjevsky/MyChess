package server;

import enums.Color;
import model.ChessGame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Color player;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private GameServer server;
    private Socket socket;

    public ClientHandler(Color player, Socket socket, GameServer server){
        this.player = player;
        this.server = server;
        this.socket = socket;
        try {
            in  = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()){
                Object request = in.readObject();
                handleRequest(request);
            }

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequest(Object request) {
        Object response = server.handleRequest(request);
        try {
            out.writeObject(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
