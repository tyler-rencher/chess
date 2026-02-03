package chess;

import java.util.Collection;
import java.util.HashSet;

public class QueenMoveCalculator extends PieceMoveCalculator{

    public QueenMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        moves.addAll(new RookMoveCalculator().pieceMoves(board,myPosition));
        moves.addAll(new BishopMoveCalculator().pieceMoves(board,myPosition));

        return moves;
    }
}

