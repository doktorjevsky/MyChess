package client;

import enums.Color;
import message.Message;
import message.MessageType;
import model.Piece;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Client implements MessageWriter{

    private BufferedReader in;
    private BufferedWriter out;
    private Socket socket;
    private List<MessageReader> messageReaders = new ArrayList<>();


    public Client(BufferedReader in, BufferedWriter out, Socket socket){
        this.in = in;
        this.out = out;
        this.socket = socket;
    }

    public void startClient(){
        listenToMessages();

    }

    public void closeClient(){
        try {
            socket.close();
            in.close();
            out.close();
        } catch (Exception ignore) {}
    }

    public synchronized void makeRequest(Message m) throws IOException {
        out.write(m.asJson());
        out.newLine();
        out.flush();
    }

    private void listenToMessages(){
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String input = in.readLine();
                    handleInput(input);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        //closeClient();
    }

    // TODO: implement
    private void handleInput(String input){
        try {
            Message m = Message.fromJson(input);
            switch (m.getType()) {
                case BOARD, GAME_OVER, MOVES, COLOR -> forwardMessage(m);
                default -> {}
            }
        } catch (Exception e) {
           // e.printStackTrace();
        }
    }


    @Override
    public void addReader(MessageReader reader) {
        messageReaders.add(reader);
    }

    @Override
    public void removeReader(MessageReader reader) {
        messageReaders.remove(reader);
    }

    @Override
    public void removeAll() {
        messageReaders = new ArrayList<>();
    }

    @Override
    public void forwardMessage(Message m) {
        messageReaders.forEach(mr -> mr.acceptMessage(m));
    }
}
