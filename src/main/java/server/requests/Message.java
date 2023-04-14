package server.requests;

import server.MessageVisitor;

import java.io.Serializable;

public abstract class Message implements Serializable {

    private String data;

    public Message(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    };

    public abstract void accept(MessageVisitor visitor);

}
