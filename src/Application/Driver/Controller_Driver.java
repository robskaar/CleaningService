package Application.Driver;

import Application.general.Controller_Application;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;

import java.util.ArrayList;

public class Controller_Driver extends Controller_Application {

    public void execFunction(){
        ArrayList<Order> orders = OrderManager.getRouteOrders(1);

        System.out.println(orders.get(1).toString());

        for(OrderItem oi : orders.get(1).getOrderItems()){
            System.out.println(oi.toString());
        }

    }
}
