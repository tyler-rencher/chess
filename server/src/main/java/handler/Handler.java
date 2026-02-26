package handler;

import com.google.gson.Gson;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.UnauthorizedException;
import dataaccess.UserNotFoundException;
import io.javalin.http.Context;

import service.Requests.LoginRequest;
import service.Requests.RegisterRequest;
import service.Results.LoginResult;
import service.Results.RegisterResult;
import service.UserService;

public class Handler {
    private UserService userService;
    public Handler(){
        userService = new UserService();
    }

    private static <T> T getBodyObject(Context context, Class<T> clazz) {
        var bodyObject = new Gson().fromJson(context.body(), clazz);

        if (bodyObject == null) {
            throw new RuntimeException("missing required body");
        }

        return bodyObject;
    }
    public void alreadyTakenExceptionHandler(AlreadyTakenException ex, Context ctx){
        ctx.status(403);
        ctx.result(new Gson().toJson(ex));
    }
    public void registerHandler(Context ctx){
        try{
            RegisterResult result = userService.register(getBodyObject(ctx, RegisterRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch(DataAccessException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
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
            ctx.result(new Gson().toJson(e));;
        }

    }

}
