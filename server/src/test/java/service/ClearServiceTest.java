package service;

import dataaccess.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.requests.CreateGameRequest;
import service.requests.LogoutRequest;
import service.requests.RegisterRequest;
import service.results.RegisterResult;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

public class ClearServiceTest {
    private static ClearService clearService;
    private static UserService userService;
    private static GameService gameService;


    @BeforeAll
    public static void setUp()  {
        UserDAO userDAO = new LocalUserDAO();
        AuthDAO authDAO = new LocalAuthDAO();
        GameDAO gameDAO =  new LocalGameDAO();
        clearService = new ClearService(userDAO, authDAO,gameDAO);
        userService = new UserService(userDAO,authDAO);
        gameService = new GameService(authDAO, gameDAO);
    }

    @Test
    public void clearPositiveTest() {
        try {
            RegisterResult result = userService.register(new RegisterRequest("1","1","1"));
            gameService.createGame(new CreateGameRequest(result.authToken(),"2"));
            clearService.clear();
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

}