package server.requests;

import server.MessageVisitor;

public class RequestDenied extends Message{

    public RequestDenied(String data){
        super(data);
    }

    @Override
    public void accept(MessageVisitor visitor) {
        visitor.visit(this);
    }
}
