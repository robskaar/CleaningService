package Domain.Handlers;

import Domain.LaundryItems.LaundryItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;

/**
 * @Author Kasper Schou
 * @Project CleaningService
 * @Date 09-06-2020
 **/
public class ItemsHandler {

    public static ObservableList<LaundryItem> getItems() {
        DB.selectSQL("Select * FROM getLaundryItems()");
        return FXCollections.observableArrayList(convertResultSetToArrayList(false));
    }

    public static int getItemCount(int itemId){
        int amountOfItem = 0;
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.getAmountByItemType(?,?)}");
            cstmt.setInt(1, itemId);
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.execute();
            amountOfItem = cstmt.getInt(2);
            cstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amountOfItem;
    }

    public static ObservableList<LaundryItem> getorderLaundryItems(int orderID) {
        DB.selectSQL("SELECT * FROM getLaundryOrderItems(" + orderID + ")");
        return FXCollections.observableArrayList(convertResultSetToArrayList(true));
    }

    public static void addNewItem(String name, double newPrice, int newDuration){
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.addItem(?,?,?)}");
            cstmt.setString(1, name );
            cstmt.setDouble(2, newPrice);
            cstmt.setInt(3,newDuration);
            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void updateItem(int itemID, String name, double newPrice, int newDuration){
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.updateItem(?,?,?,?)}");
            cstmt.setString(1, name );
            cstmt.setDouble(2, newPrice);
            cstmt.setInt(3,newDuration);
            cstmt.setInt(4, itemID);
            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static LaundryItem getLaundryItems(int laundryItemID){
        DB.selectSQL("SELECT * FROM getLaundryItem(" + laundryItemID + ")");
        String itemName = DB.getData();
        int price = Integer.parseInt(DB.getData());
        int handlingDuration = Integer.parseInt(DB.getData());
        return new LaundryItem(laundryItemID,itemName,price,handlingDuration);
    }

    private static ArrayList<LaundryItem> convertResultSetToArrayList(boolean hasOrderItemID) {
        int laundryItemID;
        int price;
        int orderItemID;
        int handlingDuration;
        String itemName;

        // Stores all orders from result set
        ArrayList<LaundryItem> Orders = new ArrayList<>();
        // Temporary value used to check for null before parsing
        String temp;
        // Data uses to assert that there is more data
        String data = DB.getData();

        while (!data.equals("|ND|") || DB.isPendingData()) {

            laundryItemID = Integer.parseInt(data);
            itemName = DB.getData();
            price = Integer.parseInt(DB.getData());
            handlingDuration = Integer.parseInt(DB.getData());
            if (hasOrderItemID) {
                orderItemID = Integer.parseInt(DB.getData());
                Orders.add(new LaundryItem(laundryItemID, itemName, price, handlingDuration, orderItemID));
            } else {
                Orders.add(new LaundryItem(laundryItemID, itemName, price, handlingDuration));
            }
            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return Orders;
    }

    public static Double getActualAverageHandlingDuration(int laundryItemID) {
        double actualDuration = 0;
        try {

            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.getActualDuration(?,?)}");
            cstmt.setInt(1, laundryItemID );
            cstmt.registerOutParameter(2, Types.FLOAT);
            cstmt.execute();
            actualDuration = cstmt.getDouble(2);
            cstmt.close();
            con.close();
            return actualDuration;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return actualDuration;

    }
}
