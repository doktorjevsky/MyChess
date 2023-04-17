import message.Message;
import message.MessageType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MessageTests {

    private Message msg;

    @BeforeEach
    public void init(){
        msg = new Message(MessageType.GET_BOARD, "s dnjom rozjdjenia");
    }

    @Test
    public void msgTest1(){
        Message actual = Message.fromJson(msg.asJson());
        Assertions.assertEquals(msg, actual);
    }
}
