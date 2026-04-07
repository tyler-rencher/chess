package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import server.Server;
import service.ClearService;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import exception.ResponseException;

import javax.management.Notification;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final UserService userService;
    private final GameService gameService;

    public WebSocketHandler(UserService userService, GameService gameService){
        this.userService = userService;
        this.gameService = gameService;
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


    public void makeMove(Session session, String username, ChessMove move, int gameID){
        try {
            ChessGame game = gameService.getChessGame(gameID);
            GameData gameData = gameService.getGameData(gameID);
            if(game.getTeamTurn() == ChessGame.TeamColor.WHITE){
                if(!Objects.equals(username, gameData.whiteUsername())){
                    connections.broadcastSelf(session, new ErrorServerMessage("Error: Not your turn!"));
                    return;
                }
            } else if(game.getTeamTurn() == ChessGame.TeamColor.BLACK){
                if(!Objects.equals(username, gameData.blackUsername())){
                    connections.broadcastSelf(session, new ErrorServerMessage("Error: Not your turn!"));
                    return;
                }
            }
            if(!game.getGameOver()){
                game.makeMove(move);
                gameService.updateGame(game, gameID);
            } else{
                connections.broadcastSelf(session, new NotificationServerMessage("Game is Over"));
            }
            //I think I might need to update the game in the server db
            connections.broadcast(null,new LoadGameServerMessage(game));
            var message = String.format("%s made move %s", username, move); //FIXME This might not work as a ChessMove
            connections.broadcast(session,new NotificationServerMessage(message));
            checkGameStatus(game);
        } catch(InvalidMoveException e){
            System.out.println(e.getMessage());
            try {
                connections.broadcastSelf(session, new ErrorServerMessage(e.getMessage()));
            } catch(Exception exx){
                System.out.println(exx.getMessage());
            }
        } catch(Exception e){
            System.out.print(e.getMessage());
            try {
                connections.broadcastSelf(session, new ErrorServerMessage(e.toString()));
            } catch(Exception exx){
                System.out.println(exx.getMessage());
            }
        }

    }

    private void checkGameStatus(ChessGame game){
        String message = null;
        if(game.isInCheckmate(game.getTeamTurn())){
            message = String.format("%s is in checkmate", game.getTeamTurn());

        } else if(game.isInStalemate(game.getTeamTurn())){
            message = "Stalemate has occurred";
        } else if(game.isInCheck(game.getTeamTurn())){
            message = String.format("%s is in check!", game.getTeamTurn());
        }

        if(message != null){
            try {
                connections.broadcast(null, new NotificationServerMessage(message));
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    public void leaveGame(Session session, String username, int gameID){
        try {
            connections.remove(session);
            gameService.removeUserFromGame(gameID, username);
            var message = String.format("%s Left the Game", username);
            connections.broadcast(null,new NotificationServerMessage(message));
        } catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public void resign(Session session, String username, int gameID){
        try {
            ChessGame game = gameService.getChessGame(gameID);
            game.setGameOver();
            gameService.updateGame(game, gameID);
            var message = String.format("%s Resigned", username);
            connections.broadcast(null,new NotificationServerMessage(message));
        } catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public void connect(Session session, String username, int gameID, ChessGame.TeamColor color){
        String colorString;
        if(color == ChessGame.TeamColor.WHITE){
            colorString = "white";
        } else if(color == ChessGame.TeamColor.BLACK){
            colorString = "black";
        } else{
            colorString = "observer";
        }

        var message = String.format("%s joined the game as %s", username, colorString);

        try {
            ChessGame game = gameService.getChessGame(gameID);
            connections.add(session);
            connections.broadcast(session, new NotificationServerMessage(message));
            connections.broadcastSelf(session,new LoadGameServerMessage(game));
        } catch(Exception e){
            System.out.println(e.getMessage());
        }


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
                case CONNECT -> connect(session, username, gameId, command.getColor() == null ? null: command.getColor());
                case MAKE_MOVE -> makeMove(session, username, command.getMove(), gameId);
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