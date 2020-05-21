package Domain.Managers;

import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrderManager {

    // Used to format SQL DateTime to Java LocalDateTime
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    public static ArrayList<Order> getAllOrders() {

        return new ArrayList<>();
    }

    public static ArrayList<Order> getCustomerOrders(int customerID) {

        return new ArrayList<>();
    }

    /**
     * @param routeID Id of the route where driver needs to pick-up/deliver orders
     * @return Returns array list with orders from the route
     */
    public static ObservableList<Order> getRouteOrders(int routeID, int status) {

        DB.selectSQL("SELECT * FROM getRouteOrder(" + routeID + "," + status + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());

    }

    public static ArrayList<Order> getCentralOrders() {

        return new ArrayList<>();
    }

    public static void updateOrderDB(Order order) {


    }

    /**
     * Only use after making a SQL select query that contains orders
     * <p>
     * This method takes the result set from the DB Class and converts the raw data into
     * an array list which contains instances of orders
     *
     * @return Returns an array list with orders
     */
    private static ArrayList<Order> convertResultSetToArrayList() {

        // Stores all orders from result set
        ArrayList<Order> orders = new ArrayList<>();
        // Temporary value used to check for null before parsing
        String temp;
        // Data uses to assert that there is more data
        String data;

        do {
            data = DB.getData();
            if (data.equals("|ND|")) {
                break;
            } else {

                int orderID = Integer.parseInt(data);
                int customerID = Integer.parseInt(DB.getData());
                LocalDateTime startDate = LocalDateTime.parse(DB.getData(), formatter);
                LocalDateTime endDate = null;

                // Asserts that end date != null
                if (!(temp = DB.getData()).equals("null")) {
                    endDate = LocalDateTime.parse(temp, formatter);
                }

                int deliveryPointID = Integer.parseInt(DB.getData());
                String status = DB.getData();

                // Adds the order to the array list
                orders.add(new Order(orderID, startDate, endDate, status, deliveryPointID, customerID));
            }

        } while (true);

        addOrderItems(orders);

        return orders;
    }

    /**
     * This method adds all the order items from the database into the array list of orders
     *
     * @param orders Array list of the orders you want to add order items to
     */
    private static void addOrderItems(ArrayList<Order> orders) {

        String temp;
        boolean isWashed;
        int orderItemID;
        int orderID;
        int laundryItemID;
        LocalDateTime startDateTime;
        LocalDateTime endDateTime = null;

        for (Order order : orders) {
            
            DB.selectSQL("SELECT * FROM getOrderItem(" + order.getID() + ")");

            while (!(temp = DB.getData()).equals("|ND|")) {

                orderItemID = Integer.parseInt(temp);
                laundryItemID = Integer.parseInt(DB.getData());
                orderID = Integer.parseInt(DB.getData());
                isWashed = Boolean.parseBoolean(DB.getData());
                startDateTime = LocalDateTime.parse(DB.getData(), formatter);

                if (!(temp = DB.getData()).equals("null")) {
                    endDateTime = LocalDateTime.parse(temp, formatter);
                }
                System.out.println("Adding order item");
                order.getOrderItems().add(new OrderItem(orderItemID, laundryItemID, orderID, isWashed, startDateTime, endDateTime));

            }

        }
    }
}
