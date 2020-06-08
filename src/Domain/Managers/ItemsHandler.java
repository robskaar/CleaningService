package Domain.Managers;

import Domain.LaundryItems.LaundryItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ItemsHandler {

    public static ObservableList<LaundryItem> getItems() {
        DB.selectSQL("Select * FROM getLaundryItems()");
        return FXCollections.observableArrayList(convertResultSetToArrayList(false));
    }

    public static ObservableList<LaundryItem> getorderLaundryItems(int orderID) {
        DB.selectSQL("SELECT * FROM getLaundryOrderItems(" + orderID + ")");
        return FXCollections.observableArrayList(convertResultSetToArrayList(true));
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
}
