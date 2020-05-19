package Domain.Order;

public class OrderItem {

    private boolean isWashed;
    private int ID;
    private String type;

    public OrderItem(boolean isWashed, int ID, String type){
        this.isWashed = isWashed;
        this.ID =  ID;
        this.type = type;
    }

    public void updateStatus(boolean status){

    }

    public boolean isWashed() {
        return isWashed;
    }

    public int getID() {
        return ID;
    }

    public String getType() {
        return type;
    }
}
