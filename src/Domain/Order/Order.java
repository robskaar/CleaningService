package Domain.Order;

import Foundation.Database.DB;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Order {

    private int ID;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int deliveryPointID;
    private int customerID;
    private ArrayList<OrderItem> items;


    public Order(int ID, LocalDateTime startDate, LocalDateTime endDate, String status, int deliveryPointID, int customerID) {
        this.ID = ID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.deliveryPointID = deliveryPointID;
        this.customerID = customerID;
        this.items = new ArrayList<>();
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    /**
     * @return Status message of the order
     */
    public String getStatusMessage() {
        DB.selectSQL("SELECT * FROM getOrderStatus(" + this.status + ")");
        return DB.getData();
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

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "OrderID: " + this.ID + " Start date: " + this.startDate + " End date " + this.endDate +
                " Status: " + this.status + " DeliveryPointID " + this.deliveryPointID + " CustomerID " + this.customerID;
    }
}
