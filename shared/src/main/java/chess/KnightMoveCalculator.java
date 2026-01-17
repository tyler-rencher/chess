package chess;

import java.util.Collection;
import java.util.HashSet;

public class KnightMoveCalculator extends PieceMoveCalculator{

    public KnightMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentKnight = board.getPiece(myPosition);
        int[][] possible = {{2,1},{2,-1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2},{1,-2}};
        for(int i = 0; i < 8; i++){
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possible[i][0],myPosition.getColumn()+possible[i][1]);
            if(isValidMove(board,testPosition,currentKnight.getTeamColor())){
                moves.add(new ChessMove(myPosition,testPosition,null));
            }
        }
        return moves;
    }
}
