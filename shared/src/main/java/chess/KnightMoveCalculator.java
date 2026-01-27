package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator extends PieceMoveCalculator{
    public KnightMoveCalculator(){}

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        int [][] possibleMoves = {{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2}};
        for (int[] possibleMove : possibleMoves) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possibleMove[0], myPosition.getColumn() + possibleMove[1]);
            if (isValidMove(board, testPosition, currentPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
        }
        return moves;
    }
}
