import model.Move;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class MoveTests {

    private String msString;
    private String msString2;
    private String msString3;


    @BeforeEach
    public void init() {
        msString = "0 0 1 1,2 2 3 3,1 2 3 4";
        msString2 = msString + ",";
        msString3 = "0 0 1   1, 2 2 3 3, 1 2 3 4 ,";

    }

    @Test
    public void parseTest1(){
        String actual = Move.stringFromList(Move.listFromString(msString));
        Assertions.assertEquals(msString, actual);
    }

    @Test
    public void parseTest2(){
        String actual = Move.stringFromList(Move.listFromString(msString2));
        Assertions.assertEquals(msString, actual);
    }

    @Test
    public void parseTest3(){
        String actual = Move.stringFromList(Move.listFromString(msString3));
        Assertions.assertEquals(msString, actual);
    }
}
