package handler;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.http.Context;

import service.ClearService;
import service.Requests.LoginRequest;
import service.Requests.LogoutRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;
import service.UserService;

public class Handler {
    private final UserService userService;
    private final ClearService clearService;
    public Handler(UserDAO userDAO, AuthDAO authDAO, GameDAO gameDAO){
        userService = new UserService(userDAO, authDAO);
        clearService = new ClearService(userDAO,authDAO,gameDAO);
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }
//    public void alreadyTakenExceptionHandler(AlreadyTakenException ex, Context ctx){
//        ctx.status(403);
//        ctx.result(new Gson().toJson(ex));
//    }
    public void registerHandler(Context ctx){
        try{
            RegisterResult result = userService.register(getBodyObject(ctx, RegisterRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(UserNotFoundException e){
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
        } catch(UserNotFoundException e){
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
            LogoutRequest request = new LogoutRequest(new Gson().fromJson(ctx.header("authorization"), String.class));
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

}
