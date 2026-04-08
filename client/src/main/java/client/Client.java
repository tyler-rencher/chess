package client;

import chess.*;
import exception.ResponseException;
import model.GameData;
import ui.DrawChessBoard;
import websocket.messages.ServerMessage;

import java.util.*;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;
import static ui.EscapeSequences.SET_BG_COLOR_BLACK;
import static ui.EscapeSequences.SET_TEXT_COLOR_WHITE;

public class Client {
    private final ServerFacade server;
    private final WebSocketFacade ws;
    private String authToken;
    private int gameID;
    private boolean inGame;
    private List<GameData> gameList;
    private ChessGame.TeamColor teamColor;
    private boolean resignStart;

    public Client(String serverUrl) throws ResponseException{
        authToken = null;
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl);
        gameID = 0;
        gameList = null;
        inGame = false;
        teamColor = null;
        resignStart = false;
    }

    public void run() {
        System.out.print(SET_BG_COLOR_BLACK);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.println("Welcome Chess! Sign in to start.\n\n");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            switch (cmd) {
                case "register", "r" -> {
                    if(isLoggedIn()){return "Already Logged In";}
                    if(params.length != 3){
                        throw new ResponseException("Error: Bad input on Register!");
                    }
                    authToken = server.registerUser(params);
                    return "Registered!\n" + help();
                }
                case "login" -> {
                    if(isLoggedIn()){
                        return "Already Logged In";
                    }
                    if(params.length != 2){
                        throw new ResponseException("Error: Bad input on Login!");
                    }
                    authToken = server.loginUser(params);
                    return "Logged In!\n" + help();
                }
                case "list" -> {
                    if(!isLoggedIn()){return "Not Logged In";}
                    gameList = new ArrayList<>(server.listGames(authToken));
                    return listGames();
                }
                case "create" -> {
                    if(!isLoggedIn()){return "Not Logged In";}
                    if(params.length != 1){
                        throw new ResponseException("Error: Bad input on Create!");
                    }
                    gameID = server.createGame(authToken,params[0]);
                }
                case "join" -> {
                    if(!isLoggedIn()){return "Not Logged In";}
                    if(params.length != 2){
                        throw new ResponseException("Error: Bad input on join!");
                    }
                    ChessGame.TeamColor color = getColor(params[1]);
                    try{
                        gameID = Integer.parseInt(params[0]);
                    } catch(Exception e){
                        throw new ResponseException("Error: not an integer on game number");
                    }
                    teamColor = color;
                    server.joinGame(authToken, color,gameID);
                    ws.connect(authToken,gameID, color);
                    gameList = new ArrayList<>(server.listGames(authToken));
                    inGame = true;
                }
                case "observe" -> {
                    if(params.length != 1){
                        throw new ResponseException("Error: Bad input on Observe!");
                    }
                    try{
                        gameID = Integer.parseInt(params[0]);
                    } catch(Exception e){
                        throw new ResponseException("Error: not an integer on game number");
                    }
                    ws.connect(authToken,gameID, null);
                    inGame = true;
                    teamColor = WHITE;
                }
                case "logout" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
                    server.logoutUser(authToken);
                    logoutHelper();
                }
                case "quit", "q" -> {
                    quitHelper();
                    return "quit";
                }
                case "help", "h" -> {
                    return help();
                }
                case "clear" -> {
                    authToken = null;
                    gameID = 0;
                    server.clear();
                }
                default -> {
                    if(inGame){
                        return inGameInput(cmd, params);
                    }
                    return "bad input try these:\n" + help();
                }
            }
            return "";
        } catch (ResponseException ex) {
            return ex.toString();
        }
    }

    private void logoutHelper(){
        gameID = 0;
        authToken = null;
        inGame = false;
        teamColor = null;
    }

    private void quitHelper(){
        authToken = null;
        inGame = false;
        teamColor = null;
    }

    private String inGameInput(String cmd, String[] params) throws ResponseException{
        switch (cmd) {
            case "redraw" -> {
                ws.redraw(teamColor == null ? WHITE : teamColor);
                return "";
            }
            case "move" -> {
                if((params.length < 2) || (params.length > 3)){
                    throw new ResponseException("Error: Bad input on move");
                }
                String promotionPiece = params.length == 3 ? params[2] : null;
                ChessMove move = extractMove(params[0], params[1], promotionPiece);
                ws.move(authToken,gameID, move);
                return "";
            }
            case "highlight" -> {
                if(params.length != 1){
                    throw new ResponseException("Error: Bad input on highlight");
                }
                ChessPosition queryPosition = new ChessPosition(extractRow(params[0]),extractColumn(params[0]));
                ws.highlight(teamColor == null ? WHITE : teamColor, queryPosition);

                return "";
            }
            case "leave" -> {
                ws.leave(authToken,gameID);
                inGame = false;
                gameID = 0;
                teamColor = null;
                return "";
            }
            case "resign" ->{
                resignStart = true;
                return "Are you sure you want to Resign?????\n Y or N";
            } case "y" ->{
                if(!resignStart){return "NOT VALID INPUT";}
                resignStart = false;
                ws.resign(authToken,gameID);
                return "";
            } case "n" ->{
                if(!resignStart){return "NOT VALID INPUT";}
                resignStart = false;
                return "";
            }
            default -> {
                return "bad input try these:\n" + help();
            }
        }
    }

    public void confirmResign(){
        System.out.println("Are you sure you want to Resign?????\n Y or N");

    }

    private ChessMove extractMove(String start, String end, String promotion) throws ResponseException{
        if((start.length() != 2) || (end.length() != 2)){
            throw new ResponseException("Error: invalid move");
        }
        //Might need to fix the integer or char thing
        ChessPosition startMove = new ChessPosition(extractRow(start),extractColumn(start));
        ChessPosition endMove = new ChessPosition(extractRow(end),extractColumn(end));
        if(promotion == null){
            return new ChessMove(startMove,endMove, null);
        }
        ChessPiece.PieceType pieceType = extractPieceType(promotion.toLowerCase());
        return new ChessMove(startMove,endMove, pieceType);
    }

    private int extractRow(String position) throws ResponseException{
        char letter = (position.charAt(1));
        if((letter < '1') || letter > '8'){
            throw new ResponseException("Error: bad move");
        }
        int returnVal = letter - '1' + 1;
        return returnVal;
    }

    private int extractColumn(String position) throws ResponseException{
        char letter = Character.toLowerCase(position.charAt(0));
        if((letter < 'a') || letter > 'h'){
            throw new ResponseException("Error: bad move");
        }
        int returnVal = letter - 'a' + 1;
        return returnVal;
    }

    private ChessPiece.PieceType extractPieceType(String promotion) throws ResponseException{
        return switch (promotion) {
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case null -> null;
            default -> throw new ResponseException("Error: invalid promotion piece");
        };

    }

    public String listGames(){
        StringBuilder sb = new StringBuilder();
        sb.append("Game List:\n");
        if(gameList == null){
            return sb.toString();
        }
        gameList.sort(Comparator.comparingInt(GameData::gameID));
        int index = 1;
        for(GameData gameData : gameList){
            sb.append(index);
            sb.append(" " + gameData.gameName());
            sb.append(" WHITE: " + ((gameData.whiteUsername() == null) ? "EMPTY" : gameData.whiteUsername()));
            sb.append(" BLACK: " + ((gameData.blackUsername() == null) ? "EMPTY" : gameData.blackUsername()));
            sb.append("\n");
            index++;
        }
        return sb.toString();
    }

    private ChessGame.TeamColor getColor(String color) throws ResponseException {
        if(color.equalsIgnoreCase("white")){
            return WHITE;
        } else if(color.equalsIgnoreCase("black")){
            return BLACK;
        } else{
            throw new ResponseException("Error: Bad Color Entered");
        }
    }

    public String help() {
        if (!isLoggedIn()) {
            return """
                     -login    <username> <password>
                     -register <username> <password> <email>
                     -help
                     -quit
                    
                    Type "login <username> <password>" to login
                    register <username> <password> <email> to register, "help" for help and "quit" to quit
                    """;
        }
        if(inGame){
            return """
                    redraw - Redraw Chess Board
                    move - <start position> <end position> <promotion piece> ex/ c2 c4 (leave promotion blank if not promoting)
                    highlight - <start position> - highlight legal moves
                    leave - leave game
                    resign - resign game
                    help - show this again
                    
                    You Understand
                    """;
        }
        return """
                list - List All Games
                create - <gameName> - Create a Game
                join - <id> <[WHITE|BLACK]> - Join Game
                observe - <id> - Observe Game
                help - Help
                logout - Logout
                
                Type one of the above commands followed by its parameters designated by the <>
                """;
    }

    private boolean isLoggedIn(){
        return authToken != null;
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " );
    }
}
