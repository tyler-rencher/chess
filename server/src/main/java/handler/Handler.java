package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
    public void registerHandler(Context ctx){
        try{
            RegisterResult result = userService.register(getBodyObject(ctx, RegisterRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
            //return gson.toJson(result);
        } catch(DataAccessException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
            //return ctx;

        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
            //return "ERROR GEN";
        }

    }

    public void loginHandler(Context ctx){
        try{
            Gson gson = new Gson();
            LoginResult result = userService.login(getBodyObject(ctx, LoginRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
            //return gson.toJson(result);
        } catch(DataAccessException e){
            ctx.status(400);
            ctx.result(new Gson().toJson(e));
            //return ctx;

        } catch (Exception e) {
            ctx.status(500);
            ctx.result(new Gson().toJson(e));
            //return "ERROR GEN";
        }

    }

}
