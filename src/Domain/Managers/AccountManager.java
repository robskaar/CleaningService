package Domain.Managers;

import Application.general.Controller_Application;
import Domain.Enums.Emulator;
import Domain.Enums.Role;
import Foundation.Database.DB;
import Services.Passwordmodifier.Password;

import java.sql.*;
import java.time.LocalDate;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public final class AccountManager {

    private static String currentUser;
    public static Boolean isLoggedIn = false;
    public static Role currentRole = null;


    private AccountManager (){

    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void register(String userName, String password, String firstName, String lastName, String emailAddress, String phoneNumber, LocalDate dateOfBirth, int isTemporary) {
        try {
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
            Connection con = null;
            switch (Controller_Application.currentEmulator){
                case Costumer:
                    currentRole = Role.Costumer;
                    DB.setDBPropertiesPath(currentRole);
                    con = DB.getConnection();
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInCostumer(?,?)}");
                    break;
                case Driver:
                    currentRole = Role.Driver;
                    DB.setDBPropertiesPath(currentRole);
                    con = DB.getConnection();
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInDriver(?,?)}");
                    break;
                case DeliveryPoint:
                    currentRole = Role.Delivery_Point;
                    DB.setDBPropertiesPath(currentRole);
                    con = DB.getConnection();
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInDeliveryPoint(?,?)}");
                    break;
                case LaundryCentral:
                    currentRole = Role.Laundry_Manager;
                    DB.setDBPropertiesPath(currentRole);
                    con = DB.getConnection();
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInLaundryAccount(?,?,?)}");
                    break;
                default:
                    DB.setDBPropertiesPath(null);
                    cstmt = null;
                    break;
            }
            String roleName = null;
            cstmt.setString(1, userName);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            if ( Controller_Application.currentEmulator == Emulator.LaundryCentral){
                cstmt.registerOutParameter(3,Types.VARCHAR);
            }
            boolean results = cstmt.execute();
            String passHash = cstmt.getString(2);
            roleName = cstmt.getString(3);
            cstmt.close();
            con.close();
            if (roleName.equalsIgnoreCase("Laundry Manager")){
                currentRole=Role.Laundry_Manager;
            }else{
                currentRole=Role.Laundry_Assistant;
            }
            if (Password.checkPassword(password, passHash)) {
               isLoggedIn = true;
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
