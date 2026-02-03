package chess;

import java.util.Collection;

public class KingMoveCalculator extends PieceMoveCalculator{
    public KingMoveCalculator(){

    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int [][] possibleMoves = {{1,-1},{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1}};
        return finiteMoves(possibleMoves,myPosition,board);
    }
}
