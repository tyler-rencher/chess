package service;

import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.requests.*;
import service.results.CreateGameResult;
import service.results.ListGamesResult;
import service.results.LoginResult;
import service.results.RegisterResult;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class GameServiceTest {
    private static UserService userService;
    private static GameService gameService;
    private static String authToken;


    @BeforeAll
    public static void setUp()  {
        UserDAO userDAO = new LocalUserDAO();
        AuthDAO authDAO = new LocalAuthDAO();
        GameDAO gameDAO =  new LocalGameDAO();
        userService = new UserService(userDAO,authDAO);
        gameService = new GameService(authDAO, gameDAO);
        try {
            RegisterRequest register = new RegisterRequest("ut","pt","et");
            RegisterResult registerResult = userService.register(register);
            authToken = registerResult.authToken();
        } catch(Exception e){
            fail("Exception in Before Block");
        }
    }

    @BeforeEach
    public void beforeEach(){

    }

    @Test
    @Order(1)
    public void createGamePositiveTest() {
        try {
            CreateGameRequest createRequest = new CreateGameRequest(authToken,"myGame");
            CreateGameResult resultActual = gameService.createGame(createRequest);
            CreateGameResult resultExpected = new CreateGameResult(1);
            assertEquals(resultExpected,resultActual);
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    @Order(1)
    public void createGameNegativeTest() {
        try {
            CreateGameRequest createRequest = new CreateGameRequest(authToken,null);
            assertThrows(BadRequestException.class,
                    () -> gameService.createGame(createRequest));
        } catch(Exception e){
            fail();
        }
    }


    @Test
    @Order(2)
    public void joinGamePositiveTest() {
        try {
            JoinGameRequest joinRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE,1);
            assertDoesNotThrow(() -> gameService.joinGame(joinRequest));
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    @Order(2)
    public void joinGameNegativeTest() {
        try {
            JoinGameRequest joinRequest = new JoinGameRequest(authToken, ChessGame.TeamColor.WHITE,1);
            assertThrows(AlreadyTakenException.class,
                    () -> gameService.joinGame(joinRequest));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(3)
    public void listGamesPositiveTest() {
        try {
            ListGamesRequest listRequest = new ListGamesRequest(authToken);
            ListGamesResult resultActual = gameService.listGames(listRequest);
            HashSet<GameData> gameData = new HashSet<>();
            ChessGame chessGame = gameService.getChessGame(1);
            gameData.add(new GameData(1,null,null,"myGame",chessGame));
            ListGamesResult resultExpected = new ListGamesResult(gameData);
            assertEquals(resultExpected,resultActual);
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    @Order(3)
    public void listGamesNegativeTest() {
        try {
            ListGamesRequest listRequest = new ListGamesRequest(authToken + "bogus");
            assertThrows(UnauthorizedException.class,
                    () -> gameService.listGames(listRequest));
        } catch(Exception e){
            fail();
        }
    }
}
