package chess;

import java.util.Collection;
import java.util.HashSet;

public class PawnMoveCalculator extends PieceMoveCalculator{

    public PawnMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPawn = board.getPiece(myPosition);
        int [][] possible;
        int promotionSquare;
        boolean canGoTwo = true;
        if(currentPawn.getTeamColor() == ChessGame.TeamColor.WHITE){
            if(myPosition.getRow() == 2){
                possible = new int [][] {{1,0},{1,1},{1,-1},{2,0}};
            } else{
                possible = new int [][] {{1,0},{1,1},{1,-1}};
            }
            promotionSquare = 8;
        } else{
            if(myPosition.getRow() == 7){
                possible = new int [][] {{-1,0},{-1,1},{-1,-1},{-2,0}};
            } else{
                possible = new int [][] {{-1,0},{-1,1},{-1,-1}};
            }
            promotionSquare = 1;
        }


        for (int[] ints : possible) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + ints[0], myPosition.getColumn() + ints[1]);
            if (isValidMove(board, testPosition, currentPawn.getTeamColor())) {
                if ((ints[1] == 0) && (board.getPiece(testPosition) != null)) { //checks if a move forward and a capture at the same time
                    canGoTwo = false;
                } else if ((ints[1] != 0) && (board.getPiece(testPosition) == null)) {
                } else if ((!canGoTwo) && ((ints[0] == -2) || (ints[0] == 2))) {
                } else {
                    if (testPosition.getRow() == promotionSquare) {
                        moves.addAll(addPromotionMoves(myPosition, testPosition));
                    } else {
                        moves.add(new ChessMove(myPosition, testPosition, null));
                    }
                }
            } else {
                if ((ints[1] == 0) && (board.getPiece(testPosition) != null)) { //checks if a move forward and a capture at the same time
                    canGoTwo = false;
                    continue;
                }
            }
        }
        return moves;
    }

    private Collection<ChessMove> addPromotionMoves(ChessPosition startPosition, ChessPosition endPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        moves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(startPosition,endPosition, ChessPiece.PieceType.KNIGHT));
        return moves;
    }


}
