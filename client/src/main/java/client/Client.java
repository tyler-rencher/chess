package client;

import chess.ChessGame;
import model.GameData;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    private String authToken;
    private int gameID;
    private Collection<GameData> gameList;

    public Client(String serverUrl){
        authToken = null;
        server = new ServerFacade(serverUrl);
        gameID = 0;
        gameList = null;
    }

    public void run() {
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
                System.out.print("oh hi mark\n");
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
                    if(isLoggedIn()){
                        return "Already Logged In";
                    }
                    authToken = server.registerUser(params);
                }
                case "login" -> {
                    if(isLoggedIn()){
                        return "Already Logged In";
                    }
                    authToken = server.loginUser(params);
                }
                case "list" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
                    gameList = server.listGames(params);
                }
                case "create" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
                    gameID = server.createGame(authToken,params[1]);
                }
                case "play" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
                    server.joinGame(authToken, ChessGame.TeamColor.WHITE,gameID);
                }
                case "observe" -> server.listGames(params);
                case "logout" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
                    gameID = 0;
                    server.logoutUser(authToken);
                    authToken = null;
                }
                case "quit", "q" -> {
                    authToken = null;
                    return "quit";
                }
                case "help", "h" -> {
                    return help();
                }
                default -> {
                    return "bad input try these:\n" + help();
                }
            };
            return "";
        } catch (ResponseException ex) {
            return ex.toString();
        }
    }

    public String listGames(){
        StringBuilder sb = new StringBuilder();
        if(gameList == null){
            return "";
        }
        int index = 1;
        for(GameData gameData : gameList){
            sb.append(index);
            sb.append(" " + gameData.gameName());
            sb.append("WHITE: " + ((gameData.whiteUsername() == null) ? "EMPTY" : gameData.whiteUsername()));
            sb.append(" BLACK: " + ((gameData.blackUsername() == null) ? "EMPTY" : gameData.blackUsername()));
            sb.append("\n");
            index++;
        }
        return sb.toString();
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
        return """
                list - List All Games
                create - <gameName> - Create a Game
                join - <id> [WHITE|BLACK] - Join Game
                observe - <id> - Observe Game
                help - Help
                logout - Logout
                
                Type 1 to List Games, 2 to Create a Game, 3 to Play a Game, 4 to Observe a Game, 5 for Help and, 6 to Logout
                """;
    }

    private boolean isLoggedIn(){
        return authToken != null;
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " );
    }
}
