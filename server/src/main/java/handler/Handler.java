package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.*;

import service.LoginRequest;
import service.LoginResult;
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

    public void loginHandler(Context ctx){
        try{
            Gson gson = new Gson();
            LoginResult result = userService.login(getBodyObject(ctx, LoginRequest.class));
            ctx.status(200);
            ctx.result(result.toString());
            //return gson.toJson(result);
        } catch(DataAccessException e){
            ctx.status(400);
            ctx.result(e.toString());
            //return ctx;

        } catch (Exception e) {
            ctx.status(500);
            ctx.result(e.toString());
            //return "ERROR GEN";
        }

    }

}
