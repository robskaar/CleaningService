package Domain.Managers;

import Foundation.Database.DB;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.InputMismatchException;

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
        }
        catch (SQLException ex) {
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
