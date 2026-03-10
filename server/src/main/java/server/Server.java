package server;

import dataaccess.*;
import handler.Handler;
import io.javalin.*;

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
        //create Handler
        Handler handler = new Handler(userDAO, authDAO, gameDAO);

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

        //javalin.exception(AlreadyTakenException.class, handler::alreadyTakenExceptionHandler);



    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
