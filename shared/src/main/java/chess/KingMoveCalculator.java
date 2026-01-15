package chess;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class KingMoveCalculator {
    public KingMoveCalculator(){

    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<ChessMove>();
        ChessPiece currentKing = board.getPiece(myPosition);
        for (int i = myPosition.getRow() - 1; i < myPosition.getRow() + 2; i++){
            if((i < 1) || (i > 8)){continue;}
            for (int j = myPosition.getColumn() - 1; j < myPosition.getColumn() + 2; j++){
                if((j < 1) || (j > 8)){continue;}
                ChessPosition testPosition = new ChessPosition(i,j);
                ChessPiece testPiece = board.getPiece(testPosition);
                if((testPiece == null) || (testPiece.getTeamColor() != currentKing.getTeamColor())){
                    moves.add(new ChessMove(myPosition,new ChessPosition(i,j),null));
                }
            }
        }
        return moves;
    }
}
