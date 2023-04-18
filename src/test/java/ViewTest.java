import client.ChessModel;
import client.Client;
import client.LocalServer;
import message.Message;
import message.MessageType;
import model.Move;
import model.Position;
import org.junit.jupiter.api.Test;
import view.ChessView;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class ViewTest {


    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            try {
                LocalServer.hostNewGame(6000);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        joinGame();
        joinGame();

    }

    private static void joinGame() throws Exception {
        Client client = LocalServer.joinLocalGame("localhost", 6000);
        ChessModel model = new ChessModel(client);
        client.addReader(model);
        client.startClient();
        ChessView view = new ChessView(model);
        model.addObserver(view);


        JFrame frame = new JFrame();
        frame.setPreferredSize(new Dimension(10*64,10*64));
        frame.getContentPane().add(view);
        frame.pack();
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        /*
        model.reload();
        view.update();

         */
    }
}
