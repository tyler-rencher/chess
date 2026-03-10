package server;

import chess.*;

public class ServerMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        Server server = new Server();
        server.run(3306); //Server number
        System.out.println("♕ 240 Chess Server: " + piece);
    }
}
