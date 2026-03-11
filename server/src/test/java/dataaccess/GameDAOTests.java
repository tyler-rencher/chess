package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashSet;

import static java.sql.Types.NULL;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameDAOTests {

    private static GameDAO gameDAOSQL;
    private static GameData testGameData;
    private static String testGameName;
    private static int originalGameID;
    private static String originalGameName;
    private static GameData updateGameData;
    private static GameData badUpdateGameData;

    @BeforeAll
    public static void setUp()  {
        originalGameName = "game";
        testGameName = "test";
        testGameData = new GameData(2,"w","b","g",new ChessGame());

        try {
            gameDAOSQL = new MySQLGameDAO();
            gameDAOSQL.clear();
            originalGameID = gameDAOSQL.createGame(originalGameName);
            updateGameData = new GameData(originalGameID,"w",null,"game",new ChessGame());
            badUpdateGameData = new GameData(NULL,null,null,null,null);

        }catch(Exception e){
            fail("Exception in setUP");
        }
    }
    @BeforeEach
    public void beforeEach(){

    }

    @Test
    @Order(1)
    public void createGamePositiveTest() {
        try {
            int testGameID = gameDAOSQL.createGame(testGameName);
            assertEquals(2,testGameID);
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }

    @Test
    @Order(2)
    public void createGameNegativeTest() {
        try {
            assertThrows(DataAccessException.class,
                    () -> gameDAOSQL.createGame(null));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(3)
    public void updateGamePositiveTest() {
        try {
            assertDoesNotThrow(() -> gameDAOSQL.updateGame(updateGameData));
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    @Order(4)
    public void updateGameNegativeTest() {
        try {
            assertThrows(DataAccessException.class,
                    () -> gameDAOSQL.updateGame(badUpdateGameData));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(5)
    public void getGamePositiveTest() {
        try {
            GameData resultActual = gameDAOSQL.getGame(originalGameID);
            assertEquals(updateGameData,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    @Order(6)
    public void getGameNegativeTest() {
        try {
            assertNull(gameDAOSQL.getGame(6327947));
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(7)
    public void listGamesPositiveTest() {
        try {
            Collection<GameData> resultActual = gameDAOSQL.listGames();
            HashSet<GameData> expectedList = new HashSet<>();
            GameData testGame = gameDAOSQL.getGame(2);
            expectedList.add(updateGameData);
            expectedList.add(testGame);
            assertEquals(expectedList,resultActual); //I don't know how to deal with the auth
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }
    @Test
    @Order(8)
    public void listGamesNegativeTest() {
        try {
            gameDAOSQL.clear();
            Collection<GameData> resultActual = gameDAOSQL.listGames();
            assertEquals(new HashSet<GameData>(), resultActual);
        } catch(Exception e){
            fail();
        }
    }

    @Test
    @Order(9)
    public void clearPositiveTest() {
        try {
            assertDoesNotThrow(() -> gameDAOSQL.clear());
        } catch(Exception e){
            fail("Exception Thrown");
        }
    }


}