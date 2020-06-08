package Domain.Order;

import Domain.LaundryItems.LaundryItem;
import Domain.Handlers.ItemsHandler;
import javafx.scene.control.CheckBox;
import java.time.LocalDateTime;

public class OrderItem {

    private boolean isWashed;
    private int ID;
    private int orderID;
    private int laundryItemID;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private CheckBox checkBox;
    private LaundryItem laundryItem;
    private int deliveryDay;

    public OrderItem(int ID, int laundryItemID, int orderID, boolean isWashed, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.isWashed = isWashed;
        this.ID =  ID;
        this.laundryItemID = laundryItemID;
        this.orderID = orderID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.checkBox = new CheckBox();
    }

    public OrderItem(boolean isWashed, int orderID, int laundryItemID) {
        this.isWashed = isWashed;
        this.orderID = orderID;
        this.laundryItemID = laundryItemID;
    }

    public void updateStatus(boolean status){

    }

    @Override
    public String toString() {
        return "ID: " + ID + " OrderID: " + orderID + " laundryItemID " + laundryItemID + " isWashed " + isWashed +
                " start date " + startDateTime + " end date " + endDateTime;
    }

    public boolean isWashed() {
        return this.isWashed;
    }

    public int getID() {
        return ID;
    }


    public CheckBox getCheckBox() {
        return checkBox;
    }

    public boolean isChecked(){
        return this.checkBox.isSelected();
    }

    public void updateLaundryItem() {
        this.laundryItem = ItemsHandler.getLaundryItems(this.laundryItemID);
    }

    public LaundryItem getLaundryItem() {
        if (laundryItem==null){
            updateLaundryItem();
        }
        return laundryItem;
    }

    public int getDeliveryDay() {
        return deliveryDay;
    }

    public void setDeliveryDay(int deliveryDay) {
        this.deliveryDay = deliveryDay;
    }

    public int getLaundryItemID() {
        return laundryItemID;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }
}
