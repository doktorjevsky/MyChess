package server.requests;

import server.MessageVisitor;

public class GetBoard extends Message{



    public GetBoard(String data){
        super(data);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }
}
