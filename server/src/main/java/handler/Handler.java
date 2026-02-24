package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class Handler {
    private UserService userService;
    public Handler(){
        userService = new UserService();
    }

    private static <T> getBody(Context context, Class<T> clazz) {
        var body = new Gson().fromJson(context.body(), clazz);

        if (body == null) {
            throw new RuntimeException("missing required body");
        }

        return body;
    }

    public Object loginHandler(Request req, Result res){
        try{
            Gson gson = new Gson();
            LoginResult result = userService.login(getBody(req, LoginRequest.class));
            return gson.toJson(result);
        } catch(DataAccessException e){
            return "ERROR DATA";
        } catch (Exception e) {
            return "ERROR GEN";
        }

    }

}
