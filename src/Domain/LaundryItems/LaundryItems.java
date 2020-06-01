package Domain.LaundryItems;

public class LaundryItems {
    private int laundryItemID;
    private int handlingDuration;
    private int orderItemID;
    private String name;
    private double price;

    public LaundryItems(int laundryItemID, String name, double price, int handlingDuration) {
        this.laundryItemID = laundryItemID;
        this.name = name;
        this.price = price;
        this.handlingDuration = handlingDuration;
    }

    public LaundryItems(int laundryItemID, String name, double price, int handlingDuration, int orderItemID) {
        this.laundryItemID = laundryItemID;
        this.name = name;
        this.price = price;
        this.handlingDuration = handlingDuration;
        this.orderItemID = orderItemID;
    }

    public int getLaundryItemID() {
        return laundryItemID;
    }

    public void setLaundryItemID(int laundryItemID) {
        this.laundryItemID = laundryItemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getHandlingDuration() {
        return handlingDuration;
    }

    public void setHandlingDuration(int handlingDuration) {
        this.handlingDuration = handlingDuration;
    }

    public int getOrderItemID() {
        return orderItemID;
    }
}
