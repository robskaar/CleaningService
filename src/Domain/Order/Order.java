package Domain.Order;

import java.time.LocalDateTime;
import java.util.ArrayList;
/**
 * @Author Kasper Schou + Jacob Bonefeld
 * @Project CleaningService  -  https://github.com/robskaar
 * @Date 09-06-2020
 **/

public class Order {

    private int ID;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int statusID;
    private int deliveryPointID;
    private int customerID;
    private ArrayList<OrderItem> items;


    public Order(int ID, LocalDateTime startDate, LocalDateTime endDate,
                 int statusID, int deliveryPointID, int customerID) {
        this.ID = ID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.statusID = statusID;
        this.deliveryPointID = deliveryPointID;
        this.customerID = customerID;
        this.items = new ArrayList<>();
    }

    public Order(int ID,int statusID) {
        this.ID = ID;
        this.statusID = statusID;
        this.items = new ArrayList<>();
    }


    public Order(String status, int orderID, int statusID) {
        this.status = status;
        this.ID = orderID;
        this.statusID = statusID;

        this.items = new ArrayList<>();
    }

    public Order(LocalDateTime startDate, int statusID, int deliveryPointID, int customerID) {
        this.startDate = startDate;
        this.statusID = statusID;
        this.deliveryPointID = deliveryPointID;
        this.customerID = customerID;
        this.items = new ArrayList<>();
    }

    public void updateStatus(int status) {
        this.statusID = status;
    }

    public void setStatus(int status) {
        this.statusID = status;
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

    public String getStatus() {
        return status;
    }


    public LocalDateTime getStartDate() {
        return startDate;
    }


    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
