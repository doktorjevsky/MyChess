package server;


import enums.Color;
import model.ChessGame;
import server.requests.Message;
import server.requests.ServerRequestHandler;

import java.net.ServerSocket;


public class GameServer {

    private ServerSocket server;
    private ChessGame gameInstance;
    private MessageVisitor messageHandler;

    public GameServer(ServerSocket server, ChessGame gameInstance){
        this.server = server;
        this.gameInstance = gameInstance;

    }

    public void bootServer(){

    }

    public Object handleRequest(Object request, Color player){
        if (!(request instanceof Message msg)){
            return null;
        }
        messageHandler = new ServerRequestHandler(gameInstance, player);
        msg.accept(messageHandler);
        return messageHandler.getResponse();
    }


}
