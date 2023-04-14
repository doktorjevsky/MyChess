package model;

import Interfaces.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        return x() + " " + y();
    }

    public static Move fromString(String s) throws Exception {
        String[] m = s.split("\s+");
        if (m.length != 4){
            throw new Exception("Wrong format!");
        }
        return new Move(Position.fromString(m[0] + " " + m[1]), Position.fromString(m[2] + " " + m[3]));
    }

    public static String stringFromList(List<Move> moves){
        StringBuilder sb = new StringBuilder();
        moves.forEach(m -> {
            sb.append(m);
            sb.append(",");
        });
        return sb.substring(0, sb.length()-1);
    }

    public static List<Move> listFromString(String s){
        List<Move> ms = new ArrayList<>();
        String[] ss = s.split(",\s*");
        for (String m : ss){
            try {
                ms.add(Move.fromString(m));
            } catch (Exception ignored) {}
        }
        return ms;
    }
}
