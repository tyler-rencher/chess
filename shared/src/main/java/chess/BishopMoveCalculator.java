package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator extends PieceMoveCalculator{

    public BishopMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        int [][] possibleMoves = {{1,-1},{1,1},{-1,1},{-1,-1}};
        for (int[] possibleMove : possibleMoves) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possibleMove[0], myPosition.getColumn() + possibleMove[1]);
            while (isValidMove(board, testPosition, currentPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, testPosition, null));
                if (board.getPiece(testPosition) != null) {
                    break;
                }
                testPosition = new ChessPosition(testPosition.getRow() + possibleMove[0], testPosition.getColumn() + possibleMove[1]);
            }
        }
        return moves;
    }
}
