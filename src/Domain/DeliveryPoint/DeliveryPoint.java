package Domain.DeliveryPoint;

public class DeliveryPoint {

    private String ID;
    private String name;
    private String address;
    private String zipCode;
    private String email;
    private String phoneNumber;
    private String routeID;

    public DeliveryPoint(String ID, String name, String address, String zipCode, String email, String phoneNumber, String routeID){
        this.ID = ID;
        this.name = name;
        this.address = address;
        this.zipCode = zipCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.routeID = routeID;
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }
}
