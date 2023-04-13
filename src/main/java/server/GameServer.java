package server;


import model.ChessGame;
import java.net.ServerSocket;


public class GameServer {

    private ServerSocket server;
    private ChessGame gameInstance;

    public GameServer(ServerSocket server, ChessGame gameInstance){
        this.server = server;
        this.gameInstance = gameInstance;

    }

    public void bootServer(){

    }

    /*
    * REQUESTS:
    * GET_MOVES (POSITION)
    * GET_BOARD ()
    * MAKE_MOVE (MOVE)
    *
    *
    * */
    public Object handleRequest(Object request){

    }


}
