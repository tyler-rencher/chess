package client;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.GameData;
import model.requests.*;
import model.results.CreateGameResult;
import model.results.ListGamesResult;
import model.results.LoginResult;
import model.results.RegisterResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;

import static java.sql.Types.NULL;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String registerUser(String[] params) throws ResponseException {
        RegisterRequest requestModel = new RegisterRequest(params[0], params[1], params[2]);
        var request = buildRequest("POST", "/user", requestModel,"");
        var response = sendRequest(request);
        RegisterResult result = handleResponse(response, RegisterResult.class);
        return result != null ? result.authToken() : null;
    }

    public String loginUser(String[] params) throws ResponseException {
        LoginRequest requestModel = new LoginRequest(params[0], params[1]);
        var request = buildRequest("POST", "/session", requestModel,"");
        var response = sendRequest(request);
        LoginResult result = handleResponse(response, LoginResult.class);
        return result != null ? result.authToken() : null;
    }

    public void logoutUser(String authToken) throws ResponseException {
        LogoutRequest requestModel = new LogoutRequest(authToken);
        var request = buildRequest("DELETE", "/session", requestModel,authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public void clear() throws ResponseException {
        var request = buildRequest("DELETE", "/db", "","");
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    public Collection<GameData> listGames(String authToken) throws ResponseException {
        if(authToken == null){
            throw new ResponseException("Error: please login first");
        }
        ListGamesRequest requestModel = new ListGamesRequest(authToken);
        var request = buildRequest("GET", "/game", requestModel, authToken);
        var response = sendRequest(request);
        ListGamesResult result = handleResponse(response, ListGamesResult.class);
        return result != null ? result.games() : null;
    }

    public int createGame(String authToken, String gameName) throws ResponseException {
        CreateGameRequest requestModel = new CreateGameRequest(authToken, gameName);
        var request = buildRequest("POST", "/game", requestModel,authToken);
        var response = sendRequest(request);
        CreateGameResult result = handleResponse(response, CreateGameResult.class);
        return result != null ? result.gameID() : NULL;
    }

    public void joinGame(String authToken, ChessGame.TeamColor playerColor,int gameID) throws ResponseException {
        JoinGameRequest requestModel = new JoinGameRequest(authToken,playerColor,gameID);
        var request = buildRequest("PUT", "/game", requestModel, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        request.setHeader("authorization", authToken);
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }

            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
