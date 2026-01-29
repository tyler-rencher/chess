package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        hasMoved = false;
    }
    public ChessPiece(ChessPiece copy) {
        this.pieceColor = copy.pieceColor;
        this.type = copy.type;
        this.hasMoved = copy.hasMoved;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return new PieceMoveCalculator().pieceMoves(board, myPosition);
    }

    //Helper Functions for Castling
    public boolean getHasMoved(){
        return hasMoved;
    }
    public void setHasMoved(){
        hasMoved = true;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, hasMoved);
    }

    @Override
    public String toString() {
        String pieceString = " ";
        if(type == PieceType.BISHOP){
            pieceString = "B";
        } else if(type == PieceType.PAWN){
            pieceString = "P";
        } else if(type == PieceType.KING){
            pieceString = "K";
        } else if(type == PieceType.QUEEN){
            pieceString = "Q";
        } else if(type == PieceType.ROOK){
            pieceString = "R";
        } else if(type == PieceType.KNIGHT){
            pieceString = "N";
        }
        if(pieceColor == ChessGame.TeamColor.BLACK){
            pieceString = pieceString.toLowerCase();
        }

        return pieceString;
    }
}
