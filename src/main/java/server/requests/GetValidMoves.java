package server.requests;

import server.MessageVisitor;

public class GetValidMoves extends Message{


    public GetValidMoves(String data){
        super(data);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }
}
