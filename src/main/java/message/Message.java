package message;

import com.google.gson.Gson;

public class Message {

    private final MessageType type;
    private final String data;

    public Message(MessageType type, String data){
        this.type = type;
        this.data = data;
    }

    public MessageType getType(){ return type; }
    public String getData(){ return data; }


    public String asJson(){
        Gson gson = new Gson();
        return gson.toJson(new Message(type, data));
    }

    public static Message fromJson(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, Message.class);
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Message m)){
            return false;
        }
        return m.type == type && m.data.equals(data);
    }
}
