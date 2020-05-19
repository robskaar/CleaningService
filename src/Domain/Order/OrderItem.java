package Domain.Order;

import java.time.LocalDateTime;

public class OrderItem {

    private boolean isWashed;
    private int ID;
    private int orderID;
    private int laundryItemID;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public OrderItem(int ID, int laundryItemID, int orderID, boolean isWashed, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.isWashed = isWashed;
        this.ID =  ID;
        this.laundryItemID = laundryItemID;
        this.orderID = orderID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public void updateStatus(boolean status){

    }

    @Override
    public String toString() {
        return "ID: " + ID + " OrderID: " + orderID + " laundryItemID " + laundryItemID + " isWashed " + isWashed +
                " start date " + startDateTime + " end date " + endDateTime;
    }

    public boolean isWashed() {
        return isWashed;
    }

    public int getID() {
        return ID;
    }


}
