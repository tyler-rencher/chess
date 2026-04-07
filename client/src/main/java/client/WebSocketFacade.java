package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import exception.ResponseException;

import jakarta.websocket.*;
import ui.DrawChessBoard;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorServerMessage;
import websocket.messages.LoadGameServerMessage;
import websocket.messages.NotificationServerMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashSet;

import static chess.ChessGame.TeamColor.WHITE;

//need to extend Endpoint for websocket to work properly
public class WebSocketFacade extends Endpoint {

    Session session;
    private ChessGame game = null;
    private ChessGame.TeamColor color = null;


    public WebSocketFacade(String url) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");


            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    if(message.contains("LOAD_GAME")){
                        LoadGameServerMessage loadMessage = new Gson().fromJson(message, LoadGameServerMessage.class);
                        game = loadMessage.getGame();
                        DrawChessBoard.drawBoard(game.getBoard(), ((color == WHITE) || (color == null)));
                    } else if(message.contains("NOTIFICATION")){
                        NotificationServerMessage notification = new Gson().fromJson(message, NotificationServerMessage.class);
                        System.out.println(notification.getMessage());
                    } else if(message.contains("ERROR")){
                        ErrorServerMessage notification = new Gson().fromJson(message, ErrorServerMessage.class);
                        System.out.println(notification.getMessage());
                    }
                    //System.out.println(message);
                }
            });

        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    //Endpoint requires this method, but you don't have to do anything
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public ChessGame connect(String authToken, int gameID, ChessGame.TeamColor teamColor) throws ResponseException {
        try {
            color = teamColor;
            var action = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID,teamColor);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
            return game;
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void move(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID,move);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            var action = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(action));
        } catch (IOException ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    public void redraw(ChessGame.TeamColor teamColor){
        DrawChessBoard.drawBoard(game.getBoard(),teamColor == WHITE);
    }

    public void highlight(ChessGame.TeamColor teamColor, ChessPosition position){
        Collection<ChessMove> moveSet = game.validMoves(position);
        DrawChessBoard.highLightBoard(game.getBoard(), teamColor == WHITE, moveSet);

    }


}