package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private TeamColor teamColor;
    public ChessGame() {
        this.board = new ChessBoard();
        board.resetBoard();
        teamColor = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamColor = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        HashSet<ChessMove> moves = new HashSet<>(board.getPiece(startPosition).pieceMoves(board, startPosition));
        //FIXME remove invalid moves
        HashSet<ChessMove> validatedMoves = new HashSet<>();
        ChessPiece currentPiece = board.getPiece(startPosition);
        ChessBoard clone = new ChessBoard(board);
        for(ChessMove move: moves){
            addAndRemovePiece(move);
            if(!isInCheck(currentPiece.getTeamColor())){
                validatedMoves.add(move);
            }
            board = new ChessBoard(clone);
        }

        return validatedMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        try {
            ChessPosition startPosition = move.getStartPosition();
            ChessPosition endPosition = move.getEndPosition();
            ChessPiece currentPiece = board.getPiece(startPosition);
            if(currentPiece == null){
                throw new InvalidMoveException("Piece null");
            } else if(currentPiece.getTeamColor() != teamColor){
                throw new InvalidMoveException("Not Your Turn");
            } else if(isInCheck(teamColor)){
                throw new InvalidMoveException("Your in Check!");
            }
            if(validMoves(startPosition).contains(move)){
                if(move.getPromotionPiece() == null){
                    board.addPiece(endPosition,currentPiece);
                } else{
                    board.addPiece(endPosition,new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
                }
                board.addPiece(startPosition,null);
                changeTeamColorTurn();
            } else{
                throw new InvalidMoveException("Move not Valid");
            }
        } catch (InvalidMoveException e) {
            throw new InvalidMoveException(e.toString());
        }
    }

    private void addAndRemovePiece(ChessMove move){
        ChessPosition endPosition = move.getEndPosition();
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece currentPiece = board.getPiece(startPosition);
        if(currentPiece == null){
            return;
        }
        if(move.getPromotionPiece() == null){
            board.addPiece(endPosition,currentPiece);
        } else{
            board.addPiece(endPosition,new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
        }
        board.addPiece(startPosition,null);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = getKingPosition(teamColor);
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition testPosition = new ChessPosition(i+1,j+1);
                if((board.getPiece(testPosition) != null) && (board.getPiece(testPosition).getTeamColor() != teamColor)){
                    if(getEndPositions(testPosition).contains(kingPosition)){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    private void changeTeamColorTurn(){
        if(teamColor == TeamColor.WHITE){
            teamColor = TeamColor.BLACK;
        } else{
            teamColor = TeamColor.WHITE;
        }
    }

    private ChessPosition getKingPosition(TeamColor color){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition testPosition = new ChessPosition(i+1,j+1);
                if((board.getPiece(testPosition) != null) && (board.getPiece(testPosition).equals(new ChessPiece(color, ChessPiece.PieceType.KING)))){
                    return testPosition;
                }
            }
        }
        return null;
    }

    private Collection<ChessPosition> getEndPositions(ChessPosition startPosition){
        HashSet<ChessMove> moves = new HashSet<>(board.getPiece(startPosition).pieceMoves(board,startPosition));
        HashSet<ChessPosition> endPositions = new HashSet<>();
        for(ChessMove move : moves){
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamColor == chessGame.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamColor);
    }
}
