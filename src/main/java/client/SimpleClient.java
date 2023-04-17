package client;

import com.google.gson.Gson;
import enums.Color;
import message.Message;
import message.MessageType;
import model.*;
import server.GameServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SimpleClient {

    private BufferedReader in;
    private BufferedWriter out;
    private Color player;
    private Scanner sc = new Scanner(System.in);
    private Socket socket;
    private Gson gson = new Gson();

    public SimpleClient(BufferedWriter out, BufferedReader in, Socket socket) throws Exception {
        this.in = in;
        this.out = out;
        this.socket = socket;

        out.write(new Message(MessageType.GET_COLOR, "color plz :)").asJson());
        out.newLine();
        out.flush();
        String clr = in.readLine();
        try {
            Message response = gson.fromJson(clr, Message.class);
            if (response.getData().equals(Color.BLACK.toString())) {
                player = Color.BLACK;
            }
            else if (response.getData().equals(Color.WHITE.toString())) {
                player = Color.WHITE;
            } else {
                System.out.print(Color.BLACK + " " + response.getData());
                throw new Exception("There is no such color!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        switch (args[0]) {
            case "join" -> joinGame(args);
            case "host" -> hostGame(args);
        }
    }
    // "join host port"

    public static void joinGame(String[] args) throws Exception {
        Socket socket = new Socket(args[1], Integer.parseInt(args[2]));
        BufferedReader in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        SimpleClient client = new SimpleClient(out, in, socket);
        client.listenToMessages();
        client.commandHandler();

    }

    // host port
    public static void hostGame(String[] args) throws Exception {
        ServerSocket server = new ServerSocket(Integer.parseInt(args[1]));
        GameServer gameServer = new GameServer(server, new ChessGame());
        gameServer.bootServer(); // TODO decouple
    }

    public void commandHandler(){
        String input = "";
        Gson gson = new Gson();
        while (!input.equals(":q") && socket.isConnected()){
            Message m = null;
            input = sc.nextLine();
            String[] tokens = input.split("\s+");
            try {
                switch (tokens[0].toUpperCase()) {
                    case "B" -> m = new Message(MessageType.GET_BOARD, "");
                    case "M" -> m = new Message(MessageType.MAKE_MOVE, input.substring(2));
                }

                if (m != null){
                    out.write(gson.toJson(m));
                    out.newLine();
                    out.flush();
                } else if (!input.equals(":q")) {
                    System.out.println("**** Error in input ****");

                    out.write(gson.toJson((new Message(MessageType.GET_BOARD, ""))));
                    out.newLine();
                    out.flush();


                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        exitGame("TERMINATED PROGRAM");
    }

    public void listenToMessages(){
        new Thread(() -> {
            try {
                while (socket.isConnected()) {
                    String input = in.readLine();
                    parseInput(Message.fromJson(input));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void parseInput(Message m){
        if (m != null) {
            switch (m.getType()) {
                case BOARD -> printBoard(m.getData());
                case GAME_OVER -> exitGame(m.getData());
            }
        }
    }

    private void exitGame(String data) {
        System.out.println("*** WINNER: " + data + " ***");
        try {
            out.write((new Message(MessageType.EXIT_GAME, "").asJson()));
            out.newLine();
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Exiting game ... ");
    }

    private void printBoard(String data) {
        try {
            Piece[][] board = ChessBoardFactory.fromString(data);
            System.out.println("* * * * * * * * * * * * * *");
            for(Piece[] row : board){
                for (Piece p : row){
                    if (p == null){
                        System.out.print("#");
                    } else {
                        System.out.print(p);
                    }
                }
                System.out.print("\n");
            }
            System.out.println("* * * * * * * * * * * * * *");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
