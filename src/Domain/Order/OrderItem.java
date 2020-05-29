package Domain.Order;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    public OrderItem(int ID, int laundryItemID, int orderID, boolean isWashed, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.isWashed = isWashed;
        this.ID =  ID;
        this.laundryItemID = laundryItemID;
        this.orderID = orderID;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.checkBox = new CheckBox();
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


}
