package client;

import java.util.Arrays;
import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    boolean loggedIn;
    public Client(String serverUrl){
        loggedIn = false;
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome Chess! Sign in to start.");
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
            return switch (cmd) {
                case "register" -> server.registerUser(params);
//                case "rescue" -> rescuePet(params);
//                case "list" -> listPets();
//                case "signout" -> signOut();
//                case "adopt" -> adoptPet(params);
//                case "adoptall" -> adoptAllPets();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (!loggedIn) {
            return """
                    1 - Login
                    2 - Register
                    3 - Help
                    4 - Quit
                    
                    Type 1 to Login, 2 to Register, 3 for Help and, 4 to Quit
                    """;
        }
        return """
                1 - List Games
                2 - Create Game
                3 - Play Game
                4 - Observe Game
                5 - Help
                6 - Logout
                
                Type 1 to List Games, 2 to Create a Game, 3 to Play a Game, 4 to Observe a Game, 5 for Help and, 6 to Logout
                """;
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> " );
    }
}
