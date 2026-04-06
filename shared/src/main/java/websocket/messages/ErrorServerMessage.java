package websocket.messages;

import chess.ChessGame;

public class ErrorServerMessage extends ServerMessage{
    private final String message;
    public ErrorServerMessage(String message){
        super(ServerMessageType.ERROR);
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
