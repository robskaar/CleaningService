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

    public static String currentUser;
    public static Boolean isLoggedIn = false;
    public static Role currentRole = null;


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
            switch (Controller_Application.currentEmulator){
                case Costumer:
                    cstmt = con.prepareCall("{call Project.dbo.logInCostumer(?,?)}");
                    currentRole = Role.Costumer;
                case Driver:
                    cstmt = con.prepareCall("{call Project.dbo.logInDriver(?,?)}");
                    currentRole = Role.Driver;
                case DeliveryPoint:
                    cstmt = con.prepareCall("{call Project.dbo.logInDeliveryPoint(?,?)}");
                    currentRole = Role.Delivery_Point;
                case LaundryCentral:
                    cstmt = con.prepareCall("{call Project.dbo.logInLaundryAccount(?,?,?)}");
                default:
                    cstmt = null;
            }

            cstmt.setString(1, userName);
            cstmt.registerOutParameter(2, Types.VARCHAR);
            boolean results = cstmt.execute();
            String passHash = cstmt.getString(2);
            if ( Controller_Application.currentEmulator == Emulator.LaundryCentral){
                String roleName = cstmt.getString(3);
                if (roleName.equalsIgnoreCase("Laundry Manager")){
                    currentRole=Role.Laundry_Manager;
                }else{
                    currentRole=Role.Laundry_Assistant;
                }
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
