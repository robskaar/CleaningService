package Domain.Managers;

import Domain.LaundryItems.Item;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ItemsManager {

    public static ObservableList<Item> getItems() {
        DB.selectSQL("Select * FROM getLaundryItems()");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    private static ArrayList<Item> convertResultSetToArrayList() {

        // Stores all orders from result set
        ArrayList<Item> Orders = new ArrayList<>();
        // Temporary value used to check for null before parsing
        String temp;
        // Data uses to assert that there is more data
        String data = DB.getData();

        while (!data.equals("|ND|") || DB.isPendingData()) {

            int laundryItemID = Integer.parseInt(data);
            String itemName = DB.getData();
            int price = Integer.parseInt(DB.getData());
            int handlingDuration = Integer.parseInt(DB.getData());

            // Adds the order to the array list
            Orders.add(new Item(laundryItemID, itemName, price, handlingDuration));
            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return Orders;
    }
}
