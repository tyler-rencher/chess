package chess;

import java.util.Collection;

public class KnightMoveCalculator extends PieceMoveCalculator{
    public KnightMoveCalculator(){}

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        int [][] possibleMoves = {{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2}};
        return finiteMoves(possibleMoves,myPosition,board);
    }
}
