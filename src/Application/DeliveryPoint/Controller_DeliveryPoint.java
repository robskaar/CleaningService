package Application.DeliveryPoint;

import Application.general.Controller_Application;
import Domain.LaundryItems.LaundryItems;
import Domain.Managers.ItemsManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import UI.Costumer.ItemBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller_DeliveryPoint extends Controller_Application implements Initializable {
    @FXML
    private TextField phoneNumberSearch;
    @FXML
    private VBox orderPane;
    @FXML
    private VBox orderListPane;

    @FXML
    private VBox startPane;
    @FXML
    private VBox orderItemsPane;
    @FXML
    private VBox laundryItemsPane;

    private ArrayList<LaundryItems> currentOrdersItems = new ArrayList<>();
    private ArrayList<LaundryItems> removedItems = new ArrayList<>();
    private ArrayList<LaundryItems> addedLaundryItems = new ArrayList<>();
    private Order currentOrder;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPane.toFront();
    }

    public void searchOnPhoneNumber() {
        System.out.println("search on phone reached");
        String dateRegex = "^[0-9]{8}$";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(phoneNumberSearch.getText());
        if (matcher.matches()) {
            System.out.println("Matched");
            phoneNumberSearch.setStyle("-fx-border-color: green");
            orderPane.toFront();

            try {
                setOrderListPane(Integer.parseInt(phoneNumberSearch.getText()));
                showItems();
            } catch (InputMismatchException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("not matched");
            phoneNumberSearch.clear();
            phoneNumberSearch.setStyle("-fx-border-color: red");
            phoneNumberSearch.setPromptText("WRONG PhoneNumber");
        }
    }

    private void setOrderListPane(int phoneNumber){
        ArrayList<Order> orders = new ArrayList<>();
        orders = OrderManager.getCustomerOrders(Integer.parseInt(phoneNumberSearch.getText()));
        for (Order order: orders
             ) {
            Label label = new Label(((String.valueOf(order.getID()))));
            label.setOnMouseClicked(mouseEvent -> {
                addOrderItems(order.getID());
                currentOrder = order;
            });
            orderListPane.getChildren().add(label);
        }

    }

    private void addOrderItems(int orderID){
        for (LaundryItems laundryItems : ItemsManager.getorderLaundryItems(orderID)
        ) {
            ItemBox itemBox = new ItemBox(laundryItems);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                removedItems.add(laundryItems);
                orderItemsPane.getChildren().remove(itemBox);


            });
            orderItemsPane.getChildren().add(itemBox);
            currentOrdersItems.add(laundryItems);
        }
    }

    private void showItems() {
        for (LaundryItems laundryItems : ItemsManager.getItems()
        ) {
            ItemBox itemBox = new ItemBox(laundryItems);
            itemBox.setAddButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                addedLaundryItems.add(laundryItems);
                currentOrdersItems.add(laundryItems);
               updateOrderPaneU(laundryItems);
            });
            laundryItemsPane.getChildren().add(itemBox);
        }
    }

    private void confirmOrder(){
        currentOrder.setStatus(1);
        OrderManager.updateOrderDB(currentOrder);
    }

    private void updateOrderPaneU(LaundryItems laundryItems){

        ItemBox itemBox = new ItemBox(laundryItems);
        itemBox.setRemoveButton();
        itemBox.getButton().setOnMouseClicked(mouseEvent -> {
            addedLaundryItems.remove(laundryItems);
            orderItemsPane.getChildren().remove(itemBox);
            currentOrdersItems.add(laundryItems);
        });
        orderItemsPane.getChildren().add(itemBox);
    }

}
