package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, HashSet<Session>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, Session session) {
        if(!connections.containsKey(gameID)){
            connections.put(gameID, new HashSet<>());
        }
        connections.get(gameID).add(session);
    }

    public void remove(int gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(int gameId, Session excludeSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        HashSet<Session> sessionSet = connections.get(gameId);
        for (Session session : sessionSet) {
            if (session.isOpen()) {
                if (!session.equals(excludeSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }

    public void broadcastSelf(int gameId, Session broadcastSession, ServerMessage notification) throws IOException {
        String msg = new Gson().toJson(notification);
        HashSet<Session> sessionSet = connections.get(gameId);
        for (Session session : sessionSet) {
            if (session.isOpen()) {
                if (session.equals(broadcastSession)) {
                    session.getRemote().sendString(msg);
                }
            }
        }
    }
}