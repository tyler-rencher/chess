package dataaccess;

import com.google.gson.Gson;

import java.util.Map;

public class UnauthorizedException extends Exception {
    public UnauthorizedException(String message) {
        super(message);
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage()));
    }
}
