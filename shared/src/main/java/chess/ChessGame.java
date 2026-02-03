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
        //Check if Castling Valid
        if((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && canKingSideCastle(currentPiece.getTeamColor(), startPosition)){
            validatedMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(),7),null));
        }
        if((currentPiece.getPieceType() == ChessPiece.PieceType.KING) && canQueenSideCastle(currentPiece.getTeamColor(), startPosition)){
            validatedMoves.add(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(),3),null));
        }

        return validatedMoves;
    }

    private boolean canCastleGeneric(HashSet<ChessMove> castleMoves){
        ChessBoard clone = new ChessBoard(board);
        for(ChessMove move: castleMoves){
            if(board.getPiece(move.getEndPosition()) != null){
                return false;
            }
            addAndRemovePiece(move);
            if(isInCheck(teamColor)){
                board = new ChessBoard(clone);
                return false;
            }
            board = new ChessBoard(clone);
        }
        return true;
    }

    //King Side Castle Check
    private boolean canKingSideCastle(TeamColor teamColor, ChessPosition kingPosition){
        ChessPosition rookKingSidePosition = new ChessPosition(kingPosition.getRow(),8);
        if((board.getPiece(kingPosition).getHasMoved()) || (isInCheck(teamColor))){
            return false;
        } else if((board.getPiece(rookKingSidePosition) == null) || (board.getPiece(rookKingSidePosition).getHasMoved())){
            return false;
        }
        HashSet<ChessMove> castleMoves = new HashSet<>();
        castleMoves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(),6),null));
        castleMoves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(),7),null));
        return canCastleGeneric(castleMoves);
    }

    //Queen Side Castle Moves
    private boolean canQueenSideCastle(TeamColor teamColor, ChessPosition kingPosition){
        ChessPosition rookQueenSidePosition = new ChessPosition(kingPosition.getRow(),1);
        if((board.getPiece(kingPosition).getHasMoved()) || (isInCheck(teamColor))){
            return false;
        } else if((board.getPiece(rookQueenSidePosition) == null) || (board.getPiece(rookQueenSidePosition).getHasMoved())){
            return false;
        }
        HashSet<ChessMove> castleMoves = new HashSet<>();
        castleMoves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(),4),null));
        castleMoves.add(new ChessMove(kingPosition, new ChessPosition(kingPosition.getRow(),3),null));
        if(board.getPiece(new ChessPosition(kingPosition.getRow(),2)) != null){
            return false;
        }
        return canCastleGeneric(castleMoves);
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
            ChessPiece currentPiece = board.getPiece(startPosition);
            if(currentPiece == null){
                throw new InvalidMoveException("Piece null");
            } else if(currentPiece.getTeamColor() != teamColor){
                throw new InvalidMoveException(String.format("Not Your Turn %s", currentPiece.getTeamColor()));
            } else if(isInCheck(teamColor)){
                throw new InvalidMoveException(String.format("You're in Check %s!", teamColor));
            }
            if(validMoves(startPosition).contains(move)){
                addAndRemovePiece(move);
                //Add Rook Part of Castle Moves
                if(currentPiece.getPieceType() == ChessPiece.PieceType.KING){
                    if(move.equals(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(),7),null))){
                        ChessPosition castleMoveStart = new ChessPosition(startPosition.getRow(),8);
                        ChessPosition castleMoveDestination = new ChessPosition(startPosition.getRow(),6);
                        addAndRemovePiece(new ChessMove(castleMoveStart,castleMoveDestination,null));
                    } else if(move.equals(new ChessMove(startPosition, new ChessPosition(startPosition.getRow(),3),null))){
                        ChessPosition castleMoveStart = new ChessPosition(startPosition.getRow(),1);
                        ChessPosition castleMoveDestination = new ChessPosition(startPosition.getRow(),4);
                        addAndRemovePiece(new ChessMove(castleMoveStart,castleMoveDestination,null));
                    }
                }

                changeTeamColorTurn();
            } else{
                throw new InvalidMoveException(String.format("Invalid Move: %s", move));
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
            board.getPiece(endPosition).setHasMoved();
        } else{
            board.addPiece(endPosition,new ChessPiece(currentPiece.getTeamColor(),move.getPromotionPiece()));
            board.getPiece(endPosition).setHasMoved();
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
        if(isInCheck(teamColor)){
            return mateDetection(teamColor);
        } else{
            return false;
        }
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            return mateDetection(teamColor);
        } else{
            return false;
        }
    }

    private boolean mateDetection(TeamColor teamColor){
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ChessPosition testPosition = new ChessPosition(i+1,j+1);
                if((board.getPiece(testPosition) != null) && (board.getPiece(testPosition).getTeamColor() == teamColor)){
                    if(!validMoves(testPosition).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
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

    //Helper function to Change Team Color
    private void changeTeamColorTurn(){
        if(teamColor == TeamColor.WHITE){
            teamColor = TeamColor.BLACK;
        } else{
            teamColor = TeamColor.WHITE;
        }
    }

    //Helper Function to find the king on the board
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

    //Helper function to take pieceMoves and return a collection of only the EndPositions
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
