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
        Handler handler = new Handler(userDAO, authDAO, gameDAO);

        //userService Methods
        javalin.post("/user", handler::registerHandler);
        javalin.post("/session", handler::loginHandler);
        javalin.delete("/session", handler::logoutHandler);
        //Clear Method
        javalin.delete("/db",handler::clearHandler);
        //Game Methods
        javalin.post("/game",handler::createGameHandler);

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
