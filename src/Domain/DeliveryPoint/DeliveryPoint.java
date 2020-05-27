package Domain.DeliveryPoint;

import javafx.scene.control.CheckBox;

public class DeliveryPoint {

    private String ID;
    private String name;
    private String address;
    private String zipCode;
    private String email;
    private String phoneNumber;
    private String routeID;
    private CheckBox checkBox;

    public DeliveryPoint(String ID, String name, String address, String zipCode, String email, String phoneNumber, String routeID){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.routeID = routeID;
        this.checkBox = new CheckBox();
        checkBox.setDisable(true);
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void confirmAllOrdersDone(){
        this.checkBox.setSelected(true);
    }
}
