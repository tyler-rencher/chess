package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;
import service.LoginRequest;
import service.LoginResult;
import service.UserService;

public class LoginHandler implements Handler {
    public String handleRequest(Context context) throws DataAccessException{
        Gson gson = new Gson();
        LoginRequest request = (LoginRequest)gson.fromJson(context.body(), LoginRequest.class);
        UserService service = new UserService();
        LoginResult result = service.login(request);

        return gson.toJson(result);
    }

    @Override
    public void handle(@NotNull Context context) throws Exception {

    }
}
