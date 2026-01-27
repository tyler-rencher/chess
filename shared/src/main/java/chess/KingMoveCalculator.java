package chess;

import java.util.Collection;
import java.util.HashSet;

public class KingMoveCalculator extends PieceMoveCalculator{
    public KingMoveCalculator(){

    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        int [][] possibleMoves = {{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1}};
        for (int[] possibleMove : possibleMoves) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possibleMove[0], myPosition.getColumn() + possibleMove[1]);
            if (isValidMove(board, testPosition, currentPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
        }
        return moves;
    }
}
