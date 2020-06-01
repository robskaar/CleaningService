package Domain.Managers;

import Domain.LaundryItems.LaundryItems;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ItemsManager {

    public static ObservableList<LaundryItems> getItems() {
        DB.selectSQL("Select * FROM getLaundryItems()");
        return FXCollections.observableArrayList(convertResultSetToArrayList(false));
    }

    public static ObservableList<LaundryItems> getorderLaundryItems(int orderID) {
        DB.selectSQL("SELECT * FROM getLaundryOrderItems(" + orderID + ")");
        return FXCollections.observableArrayList(convertResultSetToArrayList(true));
    }

    private static ArrayList<LaundryItems> convertResultSetToArrayList(boolean hasOrderItemID) {
        int laundryItemID;
        int price;
        int orderItemID;
        int handlingDuration;
        String itemName;

        // Stores all orders from result set
        ArrayList<LaundryItems> Orders = new ArrayList<>();
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
                Orders.add(new LaundryItems(laundryItemID, itemName, price, handlingDuration, orderItemID));
            } else {
                Orders.add(new LaundryItems(laundryItemID, itemName, price, handlingDuration));
            }
            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return Orders;
    }
}
