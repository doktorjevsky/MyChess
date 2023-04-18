package client;

import model.ChessGame;
import server.GameServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer {

    public static void hostNewGame(int port) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        GameServer server = new GameServer(serverSocket, new ChessGame());
        server.bootServer();
    }

    public static Client joinLocalGame(String host, int port) throws IOException {
        Socket socket = new Socket(host, port);
        Client client = new Client(
                new BufferedReader(new InputStreamReader(socket.getInputStream())),
                new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())),
                socket
        );
        return client;

    }
}
