package Domain;

import Foundation.DB;
import Services.Password;

import java.sql.*;
import java.time.LocalDate;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public final class AccountManager {

    public static String currentUser;
    public static Boolean isLoggedIn = false;
    public static String currentRole = "false";

    private AccountManager (){

    }

    public static void register(String userName, String password, String firstName, String lastName, String emailAddress, String phoneNumber, LocalDate dateOfBirth,int isTemporary) {
        try {
            System.out.println(dateOfBirth);
            System.out.println(Date.valueOf(dateOfBirth));
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.create_user(?,?,?,?,?,?,?,?)}");
            cstmt.setString(1, userName);
            cstmt.setString(2, Password.hashPassword(password));
            cstmt.setString(3, firstName);
            cstmt.setString(4, lastName);
            cstmt.setString(5, emailAddress);
            cstmt.setString(6, phoneNumber);
            cstmt.setDate(7, Date.valueOf(dateOfBirth));
            cstmt.setInt(8,isTemporary);
            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static Boolean logIn(String userName, String password) {
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call Project.dbo.logIn(?,?)}");
            cstmt.setString(1, userName);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            boolean results = cstmt.execute();
            String passHash = cstmt.getString(2);
            cstmt.close();
            con.close();
            if (Password.checkPassword(password, passHash)) {
               isLoggedIn = true;
                currentRole = //TODO get the role from db, parse it here and update it depending on acc logging in.
                currentUser = userName;
            } else {
                currentRole = null;
                isLoggedIn = false;
            }
            return isLoggedIn;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return isLoggedIn;
        }
    }

    public static void logOff() {
        isLoggedIn = false;
        currentUser = null;
    }

}
