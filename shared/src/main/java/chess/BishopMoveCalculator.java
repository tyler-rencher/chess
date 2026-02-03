package chess;

import java.util.Collection;

public class BishopMoveCalculator extends PieceMoveCalculator{

    public BishopMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int [][] possibleMoves = {{1,-1},{1,1},{-1,1},{-1,-1}};
        return infiniteMoves(possibleMoves, myPosition, board);
    }
}
