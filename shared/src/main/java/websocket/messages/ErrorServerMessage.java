package websocket.messages;

import chess.ChessGame;

public class ErrorServerMessage extends ServerMessage{
    private final String errorMessage;
    public ErrorServerMessage(String message){
        super(ServerMessageType.ERROR);
        this.errorMessage = message;
    }

    public String getMessage(){
        return errorMessage;
    }
}
