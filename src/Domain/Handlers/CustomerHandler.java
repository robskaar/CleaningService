package Domain.Handlers;

import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.InputMismatchException;
/**
 * @Author Kasper Schou
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 09-06-2020
 **/

public class CustomerHandler {
    private static final byte ISTEMPORARY = 1;

    public static int getCustomerIDByPhoneNumber(int phoneNr) throws InputMismatchException {
        String data;
        int customerID;
        DB.selectSQL("SELECT * FROM getCustomerIDByPhoneNumber(" + phoneNr + ")");
        data = DB.getData();
        if (data.equals("|ND|")){
            throw new InputMismatchException();
        }
        customerID = Integer.parseInt(data);
        return customerID;
    }

    public static ObservableList<Integer> getCostumerID(){
        String data;
        int customerID;
        ArrayList<Integer> customerIDs = new ArrayList<>();
        DB.selectSQL("SELECT * FROM getCustomerID()");
        data = DB.getData();
        while(!data.equals(DB.NOMOREDATA)){
            customerIDs.add(Integer.parseInt(data));
            data = DB.getData();
        }
        return FXCollections.observableArrayList(customerIDs);
    }

    public static int getAge(int customerID){
        CallableStatement cstmt;
        int age = 0;
        Connection con = DB.getConnection();
        try{
            cstmt = con.prepareCall("{call CleaningService.dbo.getAge(?,?)}");
            cstmt.setInt(1,customerID);
            cstmt.registerOutParameter(2, Types.INTEGER);
            boolean results = cstmt.execute();
            age = cstmt.getInt(2);
            cstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return age;
    }

    public static int register(String firstName, String lastName, String emailAddress, String phoneNumber) {
        int costumerID =-1;
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.createTempCustomer(?,?,?,?,?,?)}");
            cstmt.setString(1, firstName);
            cstmt.setString(2, lastName);
            cstmt.setString(3, emailAddress);
            cstmt.setString(4, phoneNumber);
            cstmt.setByte(5, ISTEMPORARY);
            cstmt.registerOutParameter(6, Types.INTEGER);
            boolean results = cstmt.execute();
            costumerID = cstmt.getInt(6);
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return costumerID;
    }

    public static void updateCostumer (String firstName, String lastName, String emailAddress, String phoneNumber,int costumerID) {
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.updateTempCustomer(?,?,?,?,?)}");
            cstmt.setString(1, firstName);
            cstmt.setString(2, lastName);
            cstmt.setString(3, emailAddress);
            cstmt.setString(4, phoneNumber);
            cstmt.setInt(5, costumerID);

            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
