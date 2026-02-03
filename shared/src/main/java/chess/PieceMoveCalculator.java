package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;


public class PieceMoveCalculator {

    public PieceMoveCalculator() {}

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            return new KingMoveCalculator().pieceMoves(board,myPosition);
        } else if(piece.getPieceType() == ChessPiece.PieceType.BISHOP){
            return new BishopMoveCalculator().pieceMoves(board,myPosition);
        } else if(piece.getPieceType() == ChessPiece.PieceType.ROOK){
            return new RookMoveCalculator().pieceMoves(board,myPosition);
        } else if(piece.getPieceType() == ChessPiece.PieceType.QUEEN){
            return new QueenMoveCalculator().pieceMoves(board,myPosition);
        } else if(piece.getPieceType() == ChessPiece.PieceType.KNIGHT){
            return new KnightMoveCalculator().pieceMoves(board,myPosition);
        } else if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            return new PawnMoveCalculator().pieceMoves(board,myPosition);
        }
        return List.of();
    }

    public boolean isOutOfBounds(ChessPosition testPosition){
        if ((testPosition.getRow() > 8)||(testPosition.getRow() < 1)) { return false;} //check row eligibility
        //check column eligibility
        return (testPosition.getColumn() <= 8) && (testPosition.getColumn() > 0);
    }
    public boolean isValidMove(ChessBoard board, ChessPosition testPosition, ChessGame.TeamColor color){
        if(isOutOfBounds(testPosition)){
            if(board.getPiece(testPosition) == null){ return true;} //check if spot is empty
            else {return board.getPiece(testPosition).getTeamColor() != color;}
        } else{
            return false;
        }
    }
    public Collection<ChessMove> finiteMoves(int [][] possibleMoves, ChessPosition myPosition, ChessBoard board){
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        for (int[] possibleMove : possibleMoves) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possibleMove[0], myPosition.getColumn() + possibleMove[1]);
            if (isValidMove(board, testPosition, currentPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, testPosition, null));
            }
        }
        return moves;
    }

    public Collection<ChessMove> infiniteMoves(int [][] possibleMoves, ChessPosition myPosition, ChessBoard board){
        HashSet<ChessMove> moves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(myPosition);
        for (int[] possibleMove : possibleMoves) {
            ChessPosition testPosition = new ChessPosition(myPosition.getRow() + possibleMove[0], myPosition.getColumn() + possibleMove[1]);
            while (isValidMove(board, testPosition, currentPiece.getTeamColor())) {
                moves.add(new ChessMove(myPosition, testPosition, null));
                if (board.getPiece(testPosition) != null) {
                    break;
                }
                testPosition = new ChessPosition(testPosition.getRow() + possibleMove[0], testPosition.getColumn() + possibleMove[1]);
            }
        }
        return moves;
    }


}
