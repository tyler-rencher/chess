package handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;

import service.ClearService;
import service.GameService;
import service.requests.*;
import service.results.CreateGameResult;
import service.results.ListGamesResult;
import service.results.LoginResult;
import service.results.RegisterResult;
import service.UserService;

public class Handler {
    private final UserService userService;
    private final ClearService clearService;
    private final GameService gameService;
    public Handler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO,authDAO,gameDAO);
        gameService = new GameService(authDAO,gameDAO);
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);
        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }
        return bodyObject;
    }
    private String getAuthToken(Context ctx){
        if(ctx.header("authorization") == null){
            return null;
        }
        return new Gson().fromJson(ctx.header("authorization"), String.class);
    }
    public void registerHandler(Context ctx){
        try{
            RegisterResult result = userService.register(getBodyObject(ctx, RegisterRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.result(e.toJson());
        } catch(AlreadyTakenException e){
            ctx.status(403);
            ctx.result(e.toJson());
        } catch(Exception e){
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }

    }

    public void loginHandler(Context ctx){
        try{
            LoginResult result = userService.login(getBodyObject(ctx, LoginRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.result(e.toJson());
        } catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }

    public void logoutHandler(Context ctx){
        try{
            LogoutRequest request = new LogoutRequest(getAuthToken(ctx));
            userService.logout(request);
            ctx.status(200);
        } catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
    public void clearHandler(Context ctx){
        try{
            clearService.clear();
            ctx.status(200);
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
    public void createGameHandler(Context ctx){
        try{
            CreateGameRequest requestBody = (getBodyObject(ctx,CreateGameRequest.class));
            CreateGameRequest request = new CreateGameRequest(getAuthToken(ctx),requestBody.gameName());
            CreateGameResult result = gameService.createGame(request);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.result(e.toJson());
        } catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }

    public void listGamesHandler(Context ctx){
        try{
            ListGamesRequest request = new ListGamesRequest(getAuthToken(ctx));
            ListGamesResult result = gameService.listGames(request);;
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }
    public void joinGameHandler(Context ctx){
        try{
            JoinGameRequest requestBody = getBodyObject(ctx, JoinGameRequest.class);
            String authToken = ctx.header("authorization");
            JoinGameRequest requestFull = new JoinGameRequest(authToken,requestBody.playerColor(),requestBody.gameID());
            gameService.joinGame(requestFull);
            ctx.status(200);
        } catch(BadRequestException e){
            ctx.status(400);
            ctx.result(e.toJson());
        } catch(UnauthorizedException e){
            ctx.status(401);
            ctx.result(e.toJson());
        } catch(AlreadyTakenException e){
            ctx.status(403);
            ctx.result(e.toJson());
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
        }
    }

}
