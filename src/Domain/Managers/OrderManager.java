package Domain.Managers;

import Domain.LaundryItems.Item;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OrderManager {

    // Used to format SQL DateTime to Java LocalDateTime
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    public static ArrayList<Order> getAllOrders( ) {

        return new ArrayList<>();
    }

    public static ArrayList<Order> getCustomerOrders(String customerName) {
        System.out.println("DEBUGGING customername: " + customerName);
        int orderID;
        int statusID;
        String status;
        DB.selectSQL("SELECT * FROM getCostumerOrder('" + customerName + "')");


        // Stores all orders from result set
        ArrayList<Order> orders = new ArrayList<>();
        // Temporary value used to check for null before parsing
        String temp;
        // Data uses to assert that there is more data
        String data = DB.getData();

        while (!data.equals("|ND|") || DB.isPendingData()) {
            orderID = Integer.parseInt(data);
            status = DB.getData();
            statusID = Integer.parseInt(DB.getData());

            // Adds the order to the array list
            orders.add(new Order(status, orderID, statusID));
            //assigning the data at the end to ensure the correct order.
            data = DB.getData();
        }

        return orders;
    }

    public static void createOrder(int customerID, int orderStatusID, ObservableList<Item> items) throws SQLException {

        int orderID = 0;
        try {
            CallableStatement cstmt;
            Connection con = DB.getConnection();
            cstmt = con.prepareCall("{call CleaningService.dbo.createOrder(?,?,?)}");
            cstmt.setInt(1, orderStatusID);
            cstmt.setInt(2, customerID);
            cstmt.registerOutParameter(3, Types.INTEGER);
            cstmt.execute();
            orderID = cstmt.getInt(3);
            cstmt.close();
            con.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
        createOrderItems(items, orderID);
    }

    private static void createOrderItems(ObservableList<Item> items, int orderID) throws SQLException {
        System.out.println("CreateOrderItems Reached");
        CallableStatement cstmt;
        Connection con = DB.getConnection();
        for (Item item : items
        ) {
            System.out.println("CreateOrderItems Loop Reached");
            try {
                cstmt = con.prepareCall("{call CleaningService.dbo.createOrderItem(?,?,?)}");
                cstmt.setInt(1, orderID);
                cstmt.setInt(2, item.getLaundryItemID());
                cstmt.setBoolean(3, false);
                cstmt.execute();
                cstmt.close();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        con.close();
    }

    public static void setWashStatusInDB(OrderItem orderItem){
        try{
            Connection con = DB.getConnection();
            CallableStatement cstmt = con.prepareCall("{call CleaningService.dbo.setWashedStatus(?)}");
            cstmt.setInt(1, orderItem.getID());
            boolean results = cstmt.execute();
            cstmt.close();
            con.close();
        }catch (SQLException ex){
            ex.printStackTrace();
        }


    }

    public static boolean getWashStatusFromDB(OrderItem orderItem) {
        DB.selectSQL("SELECT * FROM getWashedStatus(" + orderItem.getID() + ")");
        int washed = Integer.parseInt(DB.getData());
        if (washed == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * @param routeID Id of the route where driver needs to pick-up/deliver orders
     * @return Returns array list with orders from the route
     */
    public static ObservableList<Order> getRouteOrders(int routeID, int status) {

        DB.selectSQL("SELECT * FROM getRouteOrder(" + routeID + "," + status + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());

    }

    /**
     *
     * @param orderID -- order ID to search for
     * @return - returns array list with order from the search
     */
    public static ObservableList<Order> getSearchOrder(int orderID) {
        DB.selectSQL("SELECT * FROM getSearchOrder(" +orderID+")");
        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    public static ObservableList<Order> getDeliveryPointOrders(int deliveryPointID, int status) {

        DB.selectSQL("SELECT * FROM getDeliveryPointOrder(" + deliveryPointID + "," + status + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());

    }

    public static ObservableList<Order> getCentralOrders(int statusId) {

        DB.selectSQL("SELECT * FROM getCentralOrder(" + statusId + ")");

        return FXCollections.observableArrayList(convertResultSetToArrayList());
    }

    public static void deleteOrder(int orderID) {
        try {
            CallableStatement cstmt = DB.getConnection().prepareCall("{call CleaningService.dbo.deleteOrder(?)}");
            cstmt.setInt(1, orderID);
            cstmt.execute();
            cstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteOrderItems(int orderItemID) {
        try {
            CallableStatement cstmt = DB.getConnection().prepareCall("{call CleaningService.dbo.deleteOrderItems(?)}");
            cstmt.setInt(1, orderItemID);
            cstmt.execute();
            cstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateOrderDB(Order order) {

        CallableStatement cstmt;

        try {
            cstmt = DB.getConnection().prepareCall("{call CleaningService.dbo.updateOrder(?,?,?)}");
 
            cstmt.setInt(1, order.getStatusID());

            if (order.getEndDate() != null) {
                cstmt.setDate(2, java.sql.Date.valueOf(order.getEndDate().toLocalDate()));
            }
            else {
                cstmt.setDate(2, null);
            }

            cstmt.setInt(3, order.getID());

            cstmt.execute();
            cstmt.close();
            DB.getConnection().close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Only use after making a SQL select query that contains orders
     * <p>
     * This method takes the result set from the DB Class and converts the raw data into
     * an array list which contains instances of orders
     *
     * @return Returns an array list with orders
     */
    private static ArrayList<Order> convertResultSetToArrayList( ) {

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
                int statusID = Integer.parseInt(DB.getData());

                // Adds the order to the array list
                orders.add(new Order(orderID, startDate, endDate, statusID, deliveryPointID, customerID));
            }

        }
        while (true);

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

                order.getOrderItems().add(
                        new OrderItem(orderItemID, laundryItemID, orderID, isWashed, startDateTime, endDateTime));
            }
        }
    }

}
