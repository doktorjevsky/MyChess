import Interfaces.Tuple;

import java.util.Objects;

public class Move implements Tuple<Position, Position> {

    private Position from;
    private Position to;

    public Move(Position from, Position to){
        this.from = from;
        this.to   = to;
    }

    @Override
    public Position x() {
        return from;
    }

    @Override
    public Position y() {
        return to;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Move)){
            return false;
        }
        Move m = (Move) o;
        return from.equals(m.from) && to.equals(m.to);
    }

    @Override
    public int hashCode(){
        return Objects.hash(from, to);
    }

    @Override
    public String toString(){
        return "<" + x() + ", " + y() + ">";
    }
}
