package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    private static final String[] headersBlack = { "h", "g", "f", "e", "d", "c", "b", "a" };
    private static final String[] headersWhite = { "a", "b", "c", "d", "e", "f", "g", "h" };
    private static final String[] columns = { "1", "2", "3", "4", "5", "6", "7", "8" };
    public static boolean colorSwitch = true;
    private static final String spacer = "   ";

    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        board.resetBoard();
        System.out.print("\nWhiteBoard:\n");
        drawBoardWhite(board);
        System.out.print("\nBlackBoard:\n");
        drawBoardBlack(board);

    }

    public static void drawBoardBlack(ChessBoard board){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawHeaders(out, headersBlack);
        printBoardBlack(out, board);
        drawHeaders(out, headersBlack);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }
    public static void drawBoardWhite(ChessBoard board){
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);
        drawHeaders(out, headersWhite);
        printBoardWhite(out, board);
        drawHeaders(out, headersWhite);
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void drawHeaders(PrintStream out, String[] header){
        setText(out);
        out.print(spacer);
        for (int boardCol = 0; boardCol < 8; ++boardCol) {
            out.print(spacer + " " + header[boardCol] + EMPTY + " ");
        }
        out.print(spacer);
        newLine(out);
    }

    private static void printBoardWhite(PrintStream out, ChessBoard board) {
        for(int row = 0; row < 8; row++){
            for(int rowHeight = 0; rowHeight < 3; rowHeight++){
                setText(out);
                if(rowHeight == 1){
                    out.print(" " + columns[7-row] + " ");
                } else{
                    out.print(spacer);
                }
                for(int col = 0; col < 8; ++col){
                    if(colorSwitch){
                        setLightSquare(out);
                    } else{
                        setDarkSquare(out);
                    }
                    if(rowHeight == 1){
                        out.print(spacer);
                        printPiece(out, board.getPiece(new ChessPosition(8- row, col+1)));
                        out.print(spacer);
                    } else{
                        out.print(spacer + EMPTY + spacer);
                    }
                    colorSwitch = !colorSwitch;
                }
                setText(out);
                if(rowHeight == 1){
                    out.print(" " + columns[7-row] + " ");
                } else{
                    out.print(spacer);
                }
                newLine(out);
            }
            colorSwitch = !colorSwitch;

        }
    }

    private static void printBoardBlack(PrintStream out, ChessBoard board) {
        for(int row = 7; row >= 0; --row){
            for(int rowHeight = 0; rowHeight < 3; rowHeight++){
                setText(out);
                if(rowHeight == 1){
                    out.print(" " + columns[7-row] + " ");
                } else{
                    out.print(spacer);
                }
                for(int col = 7; col >= 0; --col){
                    if(colorSwitch){
                        setLightSquare(out);
                    } else{
                        setDarkSquare(out);
                    }
                    if(rowHeight == 1){
                        out.print(spacer);
                        printPiece(out, board.getPiece(new ChessPosition(8-row, col+1)));
                        out.print(spacer);
                    } else{
                        out.print(spacer + EMPTY + spacer);
                    }
                    colorSwitch = !colorSwitch;
                }
                setText(out);
                if(rowHeight == 1){
                    out.print(" " + columns[7-row] + " ");
                } else{
                    out.print(spacer);
                }
                newLine(out);
            }
            colorSwitch = !colorSwitch;

        }
    }

    private static void printPiece(PrintStream out, ChessPiece piece){
        if(piece == null){
            out.print(EMPTY);
            return;
        }
        if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            setTeamWhite(out);
        } else{
            setTeamBlack(out);
        }
        pieceTypePrinter(out, piece.getPieceType());
    }

    private static void pieceTypePrinter(PrintStream out, ChessPiece.PieceType type){
        if(type == ChessPiece.PieceType.ROOK){
            out.print(BLACK_ROOK);
        } else if(type == ChessPiece.PieceType.BISHOP){
            out.print(BLACK_BISHOP);
        } else if(type == ChessPiece.PieceType.QUEEN){
            out.print(BLACK_QUEEN);
        } else if(type == ChessPiece.PieceType.KING){
            out.print(BLACK_KING);
        } else if(type == ChessPiece.PieceType.PAWN){
            out.print(BLACK_PAWN);
        } else if(type == ChessPiece.PieceType.KNIGHT){
            out.print(BLACK_KNIGHT);
        } else{
            out.print(EMPTY);
        }
    }

    private static void setTeamWhite(PrintStream out){
        out.print(SET_TEXT_COLOR_WHITE);
    }
    private static void setTeamBlack(PrintStream out){
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void newLine(PrintStream out){
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
        out.print("\n");
    }

    private static void setLightSquare(PrintStream out) {
        out.print(SET_BG_COLOR_BLUE);
        out.print(SET_TEXT_COLOR_LIGHT_GREY);
    }

    private static void setDarkSquare(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
        out.print(SET_TEXT_COLOR_BLACK);
    }
    private static void setText(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

}
