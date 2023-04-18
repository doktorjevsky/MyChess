package server;

import com.google.gson.Gson;
import enums.Color;
import message.Message;
import message.MessageType;
import model.ChessGame;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Color player;
    private BufferedReader in;
    private BufferedWriter out;
    private GameServer server;
    private Socket socket;

    public ClientHandler(Color player, Socket socket, GameServer server){
        this.player = player;
        this.server = server;
        this.socket = socket;
        try {
            in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public Color getPlayer(){
        return player;
    }

    @Override
    public void run() {
        try {
            while (socket.isConnected()){
                String request = in.readLine(); // from client
                handleRequest(request);
            }

        } catch (IOException e){
           // e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    private void handleRequest(String jsonRequest) {
        try {
            Message clientMessage = Message.fromJson(jsonRequest);
            if (clientMessage.getType() == MessageType.GET_COLOR){
                respond(new Message(MessageType.COLOR, player.toString()).asJson());
            }
            else if (clientMessage.getType() == MessageType.EXIT_GAME){
                closeConnection();
                server.closeServer();
            } else {
                String response = server.handleRequest(clientMessage, player).asJson(); // response from server
                respond(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e){
            Message error = new Message(MessageType.ERROR, "INVALID JSON");
            try {
                respond(error.asJson());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void respond(String msg) throws IOException {
        out.write(msg); // send back response to client
        out.newLine();
        out.flush();
    }

    private void closeConnection(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void broadcast(Message m){
        try {
            respond(m.asJson());
        } catch (IOException e) {
            e.printStackTrace();
            closeConnection();
        }
    }
}
