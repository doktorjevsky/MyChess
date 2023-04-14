package server.requests;

import server.MessageVisitor;

public class MakeMove extends Message{


    public MakeMove(String data){
        super(data);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }
}
