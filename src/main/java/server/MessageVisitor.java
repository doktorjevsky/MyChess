package server;

import server.requests.*;

public interface MessageVisitor {

    void visit(GetBoard m);
    void visit(GetValidMoves m);
    void visit(MakeMove m);
    void visit(RequestDenied m);
    Message getResponse();
}
