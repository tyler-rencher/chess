package chess;

import java.util.Collection;

public class RookMoveCalculator extends PieceMoveCalculator {

    public RookMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int [][] possibleMoves = {{1,0},{0,1},{-1,0},{0,-1}};
        return infiniteMoves(possibleMoves, myPosition, board);
    }
}
