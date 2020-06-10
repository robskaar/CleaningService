package Domain.LaundryItems;

/**
 * @Author Kasper Schou
 * @Project CleaningService
 * @Date 09-06-2020
 **/

public class LaundryItem {
    private int laundryItemID;
    private int handlingDuration;
    private int orderItemID;
    private String name;
    private double price;

    public LaundryItem(int laundryItemID, String name, double price, int handlingDuration) {
        this.laundryItemID = laundryItemID;
        this.name = name;
        this.price = price;
        this.handlingDuration = handlingDuration;
    }

    public LaundryItem(int laundryItemID, String name, double price, int handlingDuration, int orderItemID) {
        this.laundryItemID = laundryItemID;
        this.name = name;
        this.price = price;
        this.handlingDuration = handlingDuration;
        this.orderItemID = orderItemID;
    }

    public int getLaundryItemID() {
        return laundryItemID;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getHandlingDuration() {
        return handlingDuration;
    }

    public int getOrderItemID() {
        return orderItemID;
    }
}
