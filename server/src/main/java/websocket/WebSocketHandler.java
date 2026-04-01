package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.MySQLAuthDAO;
import dataaccess.MySQLUserDAO;
import dataaccess.UnauthorizedException;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Server;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import exception.ResponseException;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private UserService userService = null;

    public WebSocketHandler() {
        try {
            this.userService = new UserService(new MySQLUserDAO(), new MySQLAuthDAO());
        } catch( Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void loadGame(String visitorName, Session session) throws IOException {
        connections.add(session);
        var message = String.format("%s is in the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
    }

    private void exit(String visitorName, Session session) throws IOException {
        var message = String.format("%s left the shop", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
        connections.broadcast(session, notification);
        connections.remove(session);
    }

    public void makeNoise(String petName, String sound) throws ResponseException {
        try {
            var message = String.format("%s says %s", petName, sound);
            var notification = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            connections.broadcast(null, notification);
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void makeMove(Session session, String username, ChessMove move){

    }
    public void leaveGame(Session session, String username, int gameID){

    }
    public void resign(Session session, String username, int gameID){

    }
    public void connect(Session session, String username, int gameID, ChessGame.TeamColor color){

    }

    private String getUsername(String authToken){
        try {
            return userService.getUsernameFromAuthToken(authToken);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void handleMessage(@NotNull WsMessageContext wsMessageContext) throws Exception {
        int gameId = -1;
        Session session = wsMessageContext.session;

        try {
            UserGameCommand command = new Gson().fromJson(
                    wsMessageContext.message(), UserGameCommand.class);
            gameId = command.getGameID();
            String username = getUsername(command.getAuthToken());
            saveSession(gameId, session);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, username, gameId, command.getColor());
                case MAKE_MOVE -> makeMove(session, username, command.getMove());
                case LEAVE -> leaveGame(session, username, gameId);
                case RESIGN -> resign(session, username, gameId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            connections.broadcast(session, new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION));
        }
    }


    private void saveSession(int gameId, Session session) {
    }


}