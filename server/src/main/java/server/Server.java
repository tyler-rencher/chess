package server;

import dataaccess.*;
import handler.Handler;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;
import websocket.WebSocketHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.

        UserDAO userDAO = new LocalUserDAO();
        AuthDAO authDAO = new LocalAuthDAO();
        GameDAO gameDAO = new LocalGameDAO();

        //if change these 3 lines of code between Local and MySQL versions
        try {
            userDAO = new MySQLUserDAO();
            authDAO = new MySQLAuthDAO();
            gameDAO = new MySQLGameDAO();
        } catch(Throwable ex){
            System.out.printf("Unable to instantiate DAOs: %s%n", ex.getMessage());
        }
        UserService userService = new UserService(userDAO, authDAO);
        ClearService clearService = new ClearService(userDAO,authDAO,gameDAO);
        GameService gameService = new GameService(authDAO,gameDAO);

        //create Handler
        Handler handler = new Handler(userService,clearService,gameService);
        WebSocketHandler webSocketHandler = new WebSocketHandler(userService,clearService,gameService);

        //userService Methods
        javalin.post("/user", handler::registerHandler);
        javalin.post("/session", handler::loginHandler);
        javalin.delete("/session", handler::logoutHandler);
        //Clear Method
        javalin.delete("/db",handler::clearHandler);
        //Game Methods
        javalin.post("/game",handler::createGameHandler);
        javalin.get("/game",handler::listGamesHandler);
        javalin.put("/game",handler::joinGameHandler);

        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
