package server.requests;

import enums.Color;
import enums.GameState;
import model.ChessBoardFactory;
import model.ChessGame;
import model.Move;
import model.Position;
import server.MessageVisitor;

import java.util.List;

public class ServerRequestHandler implements MessageVisitor {

    private Message response;
    private ChessGame gameInstance;
    private Color player;

    public ServerRequestHandler(ChessGame gameInstance, Color sender){

        this.gameInstance = gameInstance;
        this.player = sender;
    }


    @Override
    public void visit(GetBoard m) {
        response = new GetBoard(ChessBoardFactory.getString(gameInstance.getBoard()));
    }

    @Override
    public void visit(GetValidMoves m) {
        try {
            Position p = Position.fromString(m.getData());
            List<Move> validMoves = gameInstance.getValidMoves(p, player);
            response = new GetValidMoves(Move.stringFromList(validMoves));
        } catch (Exception e) {
            response = new RequestDenied("SYNTAX ERROR");
        }
    }

    @Override
    public void visit(MakeMove m) {
        try {
            Move move = Move.fromString(m.getData());
            GameState state = gameInstance.makeMove(move, player);
            if (state == GameState.INVALID_MOVE){
                response = new RequestDenied("INVALID MOVE");
            } else {
                response = new MakeMove("VALID MOVE");
            }

        } catch (Exception e) {
            response = new RequestDenied(e.getLocalizedMessage());
        }
    }

    @Override
    public void visit(RequestDenied m) {
        response = m;
    }

    @Override
    public Message getResponse() {
        return response;
    }
}
