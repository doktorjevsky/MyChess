package client;

import message.Message;

public interface MessageWriter {

    void addReader(MessageReader reader);
    void removeReader(MessageReader reader);
    void removeAll();
    void forwardMessage(Message m);
}
