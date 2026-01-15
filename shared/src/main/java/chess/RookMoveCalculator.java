package chess;

import java.util.Collection;
import java.util.HashSet;

public class RookMoveCalculator extends PieceMoveCalculator {

    public RookMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentRook = board.getPiece(myPosition);
        //up
        ChessPosition testPosition = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn());
        while(isValidMove(board,testPosition,currentRook.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()+1,testPosition.getColumn());
        }

        //down
        testPosition = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn());
        while(isValidMove(board,testPosition,currentRook.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()-1,testPosition.getColumn());
        }

        //left
        testPosition = new ChessPosition(myPosition.getRow(),myPosition.getColumn()-1);
        while(isValidMove(board,testPosition,currentRook.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow(),testPosition.getColumn()-1);
        }

        //right
        testPosition = new ChessPosition(myPosition.getRow(),myPosition.getColumn()+1);
        while(isValidMove(board,testPosition,currentRook.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow(),testPosition.getColumn()+1);
        }

        return moves;
    }
}
