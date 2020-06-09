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

public final class AccountHandler {

    public static String currentUser;
    public static int currentCostumerID;
    public static int currentDeliveryPointID;
    public static Boolean isLoggedIn = false;
    public static Role currentRole = null;


    private AccountHandler() {

    }

    /*
    gets the current route of a user - only used from the driver
     */
    public static int getCurrentRoute() {
        DB.selectSQL("SELECT * FROM getDriverRoute('" + currentUser + "')");
        return Integer.parseInt(DB.getData());
    }

    /*
    returns the currentUser logged in
     */
    public static String getCurrentUser() {
        return currentUser;
    }

    /**
     *  registers a costumer
     * @param userName - info input
     * @param password - info input
     * @param firstName - info input
     * @param lastName - info input
     * @param emailAddress - info input
     * @param phoneNumber - info input
     * @param dateOfBirth - info input
     * @param isTemporary - info input
     */
    public static void registerCustomer(String userName, String password, String firstName, String lastName, String emailAddress, String phoneNumber, LocalDate dateOfBirth, int isTemporary) {

        try {

            DB.setDBPropertiesPath(Role.Costumer);
            Connection con = DB.getConnection();
            CallableStatement cstmt = con.prepareCall("{call CleaningService.dbo.createUser(?,?,?,?,?,?,?,?)}");

            cstmt.setString(1, userName);
            cstmt.setString(2, Password.hashPassword(password));
            cstmt.setString(3, firstName);
            cstmt.setString(4, lastName);
            cstmt.setString(5, emailAddress);
            cstmt.setString(6, phoneNumber);
            cstmt.setDate(7, Date.valueOf(dateOfBirth));
            cstmt.setInt(8, isTemporary);

            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * registers a driver
     * @param userName - info input
     * @param password - info input
     * @param firstName - info input
     * @param lastName - info input
     * @param emailAddress - info input
     * @param phoneNumber - info input
     * @param dateOfBirth - info input
     * @param corporateID - info input - the ID we expect a driver to have from the delivery firm.
     *                    manager uses this ID for selecting the driver from a time schedule by their ID from delivery firm
     */
    public static void registerDriver(String userName, String password, String firstName, String lastName, String emailAddress, String phoneNumber, LocalDate dateOfBirth, int corporateID) {

        try {

            DB.setDBPropertiesPath(Role.Driver);
            Connection con = DB.getConnection();
            CallableStatement cstmt = con.prepareCall("{call CleaningService.dbo.createDriverUser(?,?,?,?,?,?,?,?)}");

            cstmt.setString(1, userName);
            cstmt.setString(2, Password.hashPassword(password));
            cstmt.setString(3, firstName);
            cstmt.setString(4, lastName);
            cstmt.setString(5, emailAddress);
            cstmt.setString(6, phoneNumber);
            cstmt.setDate(7, Date.valueOf(dateOfBirth));
            cstmt.setInt(8, corporateID);

            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * log in a user
     * will alter connection used depending on the current emulator
     * @param userName - info input
     * @param password - info input
     * @return returns true if success, else false
     */
    public static Boolean logIn(String userName, String password) {
        try {
            CallableStatement cstmt;
            Connection con = null;
            switch (Controller_Application.currentEmulator) {
                case Costumer:
                    currentRole = Role.Costumer;
                    DB.setDBPropertiesPath(currentRole);
                    con = DB.getConnection();
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInCostumer(?,?,?)}");
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
                    cstmt = con.prepareCall("{call CleaningService.dbo.logInDeliveryPoint(?,?,?)}");
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

            switch (Controller_Application.currentEmulator) {
                case LaundryCentral:
                    cstmt.registerOutParameter(3, Types.VARCHAR);
                    break;
                case Costumer:
                    cstmt.registerOutParameter(3, Types.INTEGER);
                    break;
                case DeliveryPoint:
                    cstmt.registerOutParameter(3, Types.INTEGER);
                    break;
            }

            cstmt.execute();
            String passHash = cstmt.getString(2);
            switch (Controller_Application.currentEmulator) {
                case LaundryCentral:
                    roleName = cstmt.getString(3);
                    if (roleName.equalsIgnoreCase("Laundry Manager")) {
                        currentRole = Role.Laundry_Manager;
                    } else {
                        currentRole = Role.Laundry_Assistant;
                    }
                    break;
                case Costumer:
                    currentCostumerID = cstmt.getInt(3);
                    break;
                case DeliveryPoint:
                    currentDeliveryPointID = cstmt.getInt(3);
            }

            cstmt.close();
            con.close();
            if (Password.checkPassword(password, passHash)) {
                isLoggedIn = true;
                currentUser = userName;
            } else {
                currentRole = null;
                isLoggedIn = false;

            }
            return isLoggedIn;
        } catch (SQLException | IllegalArgumentException ex) {
            ex.getMessage();
            return isLoggedIn;

        }
    }

    /**
     * logoff, reset states.
     */
    public static void logOff() {
        isLoggedIn = false;
        currentUser = null;
    }

}
