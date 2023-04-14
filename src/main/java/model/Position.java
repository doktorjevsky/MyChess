package model;

import Interfaces.Tuple;

import java.util.Objects;

public class Position implements Tuple<Integer, Integer> {

    private int y;
    private int x;

    public Position(int x, int y){
        this.y = y;
        this.x = x;
    }

    @Override
    public Integer x() {
        return x;
    }

    @Override
    public Integer y() {
        return y;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof Position)){
            return false;
        }
        Position p = (Position) o;
        return p.x == x && p.y == y;
    }

    @Override
    public int hashCode(){
        return Objects.hash(x, y);
    }

    @Override
    public String toString(){
        return x + " " + y;
    }

    public static Position fromString(String s) throws Exception {
        String[] p = s.split("\s+");
        if (p.length != 2){
            throw new Exception("Wrong format!");
        }
        return new Position(Integer.parseInt(p[0]), Integer.parseInt(p[1]));
    }
}
