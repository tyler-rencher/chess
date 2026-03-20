package client;

import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    private String authToken;
    public Client(String serverUrl){
        authToken = null;
        server = new ServerFacade(serverUrl);
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
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
                case "logout" -> {
                    if(!isLoggedIn()){
                        return "Not Logged In";
                    }
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
                list - List Games
                create - Create Game
                play - Play Game
                observe - Observe Game
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
