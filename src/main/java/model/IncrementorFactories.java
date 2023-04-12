package model;

import Interfaces.Incrementor;
import enums.Color;

import java.util.List;

public class IncrementorFactories {


    public static List<Incrementor<Position>> straightIterators(){
        return List.of(
                p -> new Position(p.x() + 1, p.y()),
                p -> new Position(p.x() - 1, p.y()),
                p -> new Position(p.x(), p.y() + 1),
                p -> new Position(p.x(), p.y() - 1)
        );
    }

    public static List<Incrementor<Position>> diagonalIncrementors(){
        return List.of(
                p -> new Position(p.x() + 1, p.y() + 1),
                p -> new Position(p.x() + 1, p.y() - 1),
                p -> new Position(p.x() - 1, p.y() + 1),
                p -> new Position(p.x() - 1, p.y() - 1)
        );
    }

    public static List<Incrementor<Position>> knightIncrementors(){
        return List.of(
                p -> new Position(p.x() + 1, p.y() + 2),
                p -> new Position(p.x() + 1, p.y() - 2),
                p -> new Position(p.x() - 1, p.y() + 2),
                p -> new Position(p.x() - 1, p.y() - 2),
                p -> new Position(p.x() + 2, p.y() + 1),
                p -> new Position(p.x() + 2, p.y() - 1),
                p -> new Position(p.x() - 2, p.y() + 1),
                p -> new Position(p.x() - 2, p.y() - 1)
        );
    }

    public static Incrementor<Position> pawnIncrementor(Color c){
        return c == Color.BLACK ? p -> new Position(p.x(), p.y() - 1) : p -> new Position(p.x(), p.y() + 1);
    }

    public static List<Incrementor<Position>> pawnAttackIncrementors(Color c){
        return c == Color.BLACK ?
                List.of(
                        p -> new Position(p.x() - 1, p.y() - 1),
                        p -> new Position(p.x()+1,p.y()-1))
                :
                List.of(
                        p -> new Position(p.x() - 1, p.y() + 1),
                        p -> new Position(p.x() + 1, p.y() + 1)
                );
    }
}
