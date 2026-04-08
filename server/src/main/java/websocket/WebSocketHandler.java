package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
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
import java.awt.*;
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
                    connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: Not your turn!"));
                    return;
                }
            } else if(game.getTeamTurn() == ChessGame.TeamColor.BLACK){
                if(!Objects.equals(username, gameData.blackUsername())){
                    connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: Not your turn!"));
                    return;
                }
            }
            if(!game.getGameOver()){
                game.makeMove(move);
                gameService.updateGame(game, gameID);
            } else{
                connections.broadcastSelf(gameID,session, new ErrorServerMessage("Game is Over"));
                return;
            }
            GameData gameData2 = gameService.getGameData(gameID);
            //I think I might need to update the game in the server db
            connections.broadcast(gameID,null,new LoadGameServerMessage(game));
            var message = String.format("%s made move %s", username, moveSerializer(move)); // This might not work as a ChessMove
            connections.broadcast(gameID,session,new NotificationServerMessage(message));
            checkGameStatus(gameData2, gameID);
        } catch(InvalidMoveException e){
            System.out.println(e.getMessage());
            try {
                connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: Invalid Move"));
            } catch(Exception exx){
                System.out.println(exx.getMessage());
            }
        } catch(Exception e){
            System.out.print(e.getMessage());
            try {
                connections.broadcastSelf(gameID,session, new ErrorServerMessage(e.toString()));
            } catch(Exception exx){
                System.out.println(exx.getMessage());
            }
        }

    }

    private String moveSerializer(ChessMove move){
        String returnString = "";
        returnString = returnString + positionSerializer(move.getStartPosition()) + " ";
        returnString = returnString + positionSerializer(move.getEndPosition()) + " ";
        if(move.getPromotionPiece() != null){
            returnString = returnString + move.getPromotionPiece();
        }
        return returnString;
    }
    private String positionSerializer(ChessPosition position){
        char letter = (char) ('a' + position.getColumn() - 1);
        return letter + String.valueOf(position.getRow());
    }

    private void checkGameStatus(GameData gameData, int gameId){
        String message = null;
        String username;
        ChessGame game = gameData.game();
        if(game.getTeamTurn() == ChessGame.TeamColor.WHITE){
            username = gameData.whiteUsername();
        } else if(game.getTeamTurn() == ChessGame.TeamColor.BLACK){
            username = gameData.blackUsername();
        } else{
            username = null;
        }
        if(game.isInCheckmate(game.getTeamTurn())){
            message = String.format("%s is in checkmate", username);
            game.setGameOver();
            try{
                gameService.updateGame(game, gameId);
            } catch(Exception e){
                System.out.println("Error on update game");
            }

        } else if(game.isInStalemate(game.getTeamTurn())){
            message = "Stalemate has occurred";
            game.setGameOver();
            try{
                gameService.updateGame(game, gameId);
            } catch(Exception e){
                System.out.println("Error on update game");
            }
        } else if(game.isInCheck(game.getTeamTurn())){
            message = String.format("%s is in check!", username);
        }

        if(message != null){
            try {
                connections.broadcast(gameId, null, new NotificationServerMessage(message));
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
    }
    public void leaveGame(Session session, String username, int gameID){
        try {
            connections.remove(gameID, session);
            gameService.removeUserFromGame(gameID, username);
            var message = String.format("%s Left the Game", username);
            connections.broadcast(gameID, null,new NotificationServerMessage(message));
        } catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public void resign(Session session, String username, int gameID){
        try {
            ChessGame game = gameService.getChessGame(gameID);
            GameData gameData = gameService.getGameData(gameID);
            if(!((Objects.equals(gameData.blackUsername(), username)) ||
                    (Objects.equals(gameData.whiteUsername(), username)))){
                try {
                    connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: Observer can't Resign"));
                } catch(Exception exx){
                    System.out.println(exx.getMessage());
                }
                return;
            }
            if(game.getGameOver()){
                try {
                    connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: Resign already occurred OITE!"));
                } catch(Exception exx){
                    System.out.println(exx.getMessage());
                }
                return;
            }

            game.setGameOver();
            gameService.updateGame(game, gameID);
            var message = String.format("%s Resigned", username);
            connections.broadcast( gameID,null,new NotificationServerMessage(message));
        } catch(Exception e){
            System.out.print(e.getMessage());
        }
    }
    public void connect(Session session, String username, int gameID){
        String colorString;
        connections.add(gameID,session);
        if(username == null){
            try{
                connections.broadcastSelf(gameID,session, new ErrorServerMessage("Error: username/authToken null"));
                return;
            } catch(Exception e){
                System.out.println(e.getMessage());
            }
        }
        GameData gameData;
        try {
            gameData = gameService.getGameData(gameID);
        } catch(Exception e){
            System.out.println(e.toString());
            return;
        }

        if(Objects.equals(gameData.whiteUsername(), username)){
            colorString = "white";
        } else if(Objects.equals(gameData.blackUsername(), username)){
            colorString = "black";
        } else{
            colorString = "observer";
        }

        var message = String.format("%s joined the game as %s", username, colorString);

        try {

            ChessGame game = gameService.getChessGame(gameID);

            connections.broadcastSelf(gameID,session,new LoadGameServerMessage(game));
            connections.broadcast(gameID,session, new NotificationServerMessage(message));

        } catch(Exception e){
            try {
                connections.broadcastSelf(gameID,session, new ErrorServerMessage(e.toString()));
            } catch(Exception exx){
                System.out.println(exx.getMessage());
            }
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

    @Override
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
                case CONNECT -> connect(session, username, gameId);
                case MAKE_MOVE -> makeMove(session, username, command.getMove(), gameId);
                case LEAVE -> leaveGame(session, username, gameId);
                case RESIGN -> resign(session, username, gameId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            connections.broadcastSelf(gameId,session, new ErrorServerMessage(ex.toString()));
        }
    }


    private void saveSession(int gameId, Session session) {
    }


}