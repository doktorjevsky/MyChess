package view;

import client.ChessModel;
import client.Client;
import client.LocalServer;
import client.Observer;
import enums.Color;
import enums.GameState;
import message.Message;
import message.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class MainWindow extends JFrame implements Observer {

    private final int BOX_SIZE = 64;
    private final int BOARD_SIZE = BOX_SIZE * 8;
    private ChessModel model;
    private Button joinGame;
    private Button hostGame = new Button("Host Game");
    private ChessView view;
    private Client c;

    public MainWindow(){
        this.setPreferredSize(new Dimension(BOX_SIZE * 10, BOX_SIZE * 10));
        this.setTitle("Chess Client");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        initButtons();
        this.pack();
    }

    private void initButtons(){
        this.getContentPane().removeAll();
        setLayout(new FlowLayout());
        joinGame = new Button("Join Game");
        hostGame = new Button("Host Game");
        add(hostGame);
        add(joinGame);

        joinGame.addActionListener(ae -> {
            getContentPane().removeAll();
            add(joinGame);
            TextField hostAddress = new TextField("host");
            TextField port = new TextField("port");
            hostAddress.setPreferredSize(new Dimension(200,20));
            add(hostAddress);
            add(port);
            joinGame.setLabel("Confirm");
            removeListeners(joinGame);
            setVisible(true);
            joinGame.addActionListener(confirm -> {
                try {
                    setLayout(new BorderLayout());
                    getContentPane().removeAll();
                    String host = hostAddress.getText();
                    setupClient(host, Integer.parseInt(port.getText()));
                } catch (Exception e){
                    JOptionPane.showMessageDialog(MainWindow.this, "Failed to connect");
                    initButtons();
                }
            });
        });

        hostGame.addActionListener(ae -> {
            getContentPane().removeAll();
            add(hostGame);
            TextField port = new TextField("Port");
            port.setPreferredSize(new Dimension(200,20));
            add(port);
            hostGame.setLabel("Confirm");
            removeListeners(hostGame);
            setVisible(true);
            hostGame.addActionListener(l -> {
                new Thread(() -> {
                    try {
                        LocalServer.hostNewGame(Integer.parseInt(port.getText()));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(MainWindow.this, "Failed to boot server");
                        initButtons();
                    }
                }).start();
                try {
                    setLayout(new BorderLayout());
                    getContentPane().removeAll();
                    setupClient("localhost", Integer.parseInt(port.getText()));
                } catch (Exception e){
                    JOptionPane.showMessageDialog(MainWindow.this, "Failed to connect to server");
                }


            });
        });
        pack();
        this.setVisible(true);
    }

    private void setupClient(String host, int port) throws IOException {
        Client client = LocalServer.joinLocalGame(host, port);
        model = new ChessModel(client);
        client.addReader(model);
        view = new ChessView(model);
        model.addObserver(view);
        model.addObserver(this);
        getContentPane().add(view);
        pack();
        getContentPane().setVisible(true);
        setVisible(true);
        client.startClient();
        c = client;
    }

    private void removeListeners(Button b){
        Arrays.stream(b.getActionListeners()).forEach(b::removeActionListener);
    }


    @Override
    public void update() {
        if (model.getGameState().equals(GameState.GAME_OVER)){
            Color winner = model.getWinner();
            Arrays.stream(view.getMouseListeners()).forEach(view::removeMouseListener);
            String winmessage;
            if (winner == null){
                winmessage = "IT'S A DRAW";
            } else {
                winmessage = "THE WINNER IS " + winner;
            }
            model.removeObserver(this);
            JOptionPane.showMessageDialog(MainWindow.this, winmessage);
            model.makeRequest(new Message(MessageType.EXIT_GAME, ""));
            getContentPane().removeAll();
            repaint();
            initButtons();
            c.closeClient();
            //TODO: implement
        }
    }
}
