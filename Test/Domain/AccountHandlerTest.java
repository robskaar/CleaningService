package Domain;

import Application.general.Controller_Application;
import Domain.Enums.Emulator;
import Domain.Managers.AccountHandler;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class AccountHandlerTest {

    /**
     * fields used to test with, should also be present in database (note password is stored hashed not plaintext)
     *  so if you dont have this user in you test environment/DB, run the program, register it and then run the test
     */
    String userName ="MarcusHansen";
    String passWord = "Mh12345!";

    /**
     * Test's that log In connects to Database, logs a user in, stores username and generally successful login.
     */
    @Test
    public void successfulLogIn() {
        Controller_Application.currentEmulator = Emulator.Driver;
        System.out.println("Testing if log in method / procedure works");
        System.out.println();
        assertTrue(AccountHandler.logIn(userName, passWord));
        System.out.println();
        System.out.println("SUCCESS");
    }

}