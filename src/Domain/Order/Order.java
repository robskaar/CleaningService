package Domain.Order;

import java.time.LocalDate;
import java.util.ArrayList;

public class Order {

    private int ID;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private int deliveryPointID;
    private int customerID;
    private ArrayList<OrderItem> items;

    public Order(int ID, LocalDate startDate, LocalDate endDate, String status, int deliveryPointID, int customerID){
        this.ID =  ID;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.deliveryPointID = deliveryPointID;
        this.customerID = customerID;

        this.items = new ArrayList<>();
    }

    public void updateStatus(String status){

    }

    public ArrayList<OrderItem> getOrderItems(){
        return this.items;
    }
}
