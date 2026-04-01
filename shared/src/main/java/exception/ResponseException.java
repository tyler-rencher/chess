package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;
    final private String message;

    public ResponseException(Code code, String message) {
        super(message);
        this.message = message;
        this.code = code;
    }

    public ResponseException(String message) {
        super(message);
        this.message = message;
        code = Code.ServerError;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        //var status = Code.valueOf(map.get("status").toString());
        String message = map.get("message").toString();
        return new ResponseException( message);
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        return switch (httpStatusCode) {
            case 500 -> Code.ServerError;
            case 400 -> Code.ClientError;
            default -> throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
        };
    }

    @Override
    public String toString() {
        return message;
    }
}