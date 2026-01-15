package chess;

import java.util.Collection;
import java.util.List;


public class PieceMoveCalculator {

    public PieceMoveCalculator() {}

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            return new KingMoveCalculator().pieceMoves(board,myPosition);
        }
        return List.of();
    }
}
