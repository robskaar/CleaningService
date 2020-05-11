package Domain;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class PasswordTest {


    /**
     * Gets the hash value of a password inserted
     * also, it checks if the hashing + valuation of the hashed password works correctly
     */
    @Test
    public void testPassword(){
        System.out.println("Testing if hashing and checking hash of password methods work");
        System.out.println();
        String passWord = "test";
        String hashedPassword = Password.hashPassword(passWord);
        System.out.println(hashedPassword);
        assertTrue(Password.checkPassword(passWord,hashedPassword));
        System.out.println();
        System.out.println("SUCCESS");
    }

}
