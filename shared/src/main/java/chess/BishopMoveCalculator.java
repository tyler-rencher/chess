package chess;

import java.util.Collection;
import java.util.HashSet;

public class BishopMoveCalculator extends PieceMoveCalculator{

    public BishopMoveCalculator(){
    }
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentBishop = board.getPiece(myPosition);
        ChessPosition testPosition = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()+1);
        while(isValidMove(board,testPosition,currentBishop.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()+1,testPosition.getColumn()+1);
        }

        testPosition = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()+1);
        while(isValidMove(board,testPosition,currentBishop.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()-1,testPosition.getColumn()+1);
        }

        testPosition = new ChessPosition(myPosition.getRow()-1,myPosition.getColumn()-1);
        while(isValidMove(board,testPosition,currentBishop.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()-1,testPosition.getColumn()-1);
        }
        testPosition = new ChessPosition(myPosition.getRow()+1,myPosition.getColumn()-1);
        while(isValidMove(board,testPosition,currentBishop.getTeamColor())){
            moves.add(new ChessMove(myPosition,testPosition,null));
            if(board.getPiece(testPosition) != null){
                break;
            }
            testPosition = new ChessPosition(testPosition.getRow()+1,testPosition.getColumn()-1);
        }

        return moves;
    }
}
