package Domain.Order;

import Foundation.Database.DB;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {

    private int ID;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int statusID;
    private int deliveryPointID;
    private int customerID;
    private ArrayList<OrderItem> items;


    public Order(int ID, LocalDateTime startDate, LocalDateTime endDate, int statusID, int deliveryPointID, int customerID) {
        this.ID = ID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusID = statusID;
        this.deliveryPointID = deliveryPointID;
        this.customerID = customerID;
        this.items = new ArrayList<>();
    }

    public Order(String status, int orderID, int statusID) {
        this.status = status;
        this.ID = orderID;
        this.statusID = statusID;

        this.items = new ArrayList<>();
    }

    public void updateStatus(String status) {
        this.status = status;
    public void setStatus(int status) {
        this.statusID = status;
    }

    /**
     * @return Status message of the order
     */
    public String getStatusMessage() {
        DB.selectSQL("SELECT * FROM getOrderStatus(" + this.statusID + ")");
        return DB.getData();
    }

    public int getCustomerID() {
        return customerID;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return this.items;
    }

    public int getID() {
        return ID;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public int getStatusID( ) {
        return statusID;
    }

    @Override
    public String toString() {
        return "OrderID: " + this.ID + " Start date: " + this.startDate + " End date " + this.endDate +
               " Status: " + this.statusID + " DeliveryPointID " + this.deliveryPointID + " CustomerID " + this.customerID;
    }

    public int getStatusID() {
        return statusID;
    }

}
