package Application.Costumer;

import Application.General.Controller_Application;
import Domain.LaundryItems.LaundryItem;
import Domain.Handlers.AccountHandler;
import Domain.Handlers.ItemsHandler;
import Domain.Handlers.OrderHandler;
import Domain.Order.Order;
import UI.Costumer.ItemBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @Author Kasper Schou
 * @Project CleaningService
 * @Date 09-06-2020
 **/


public class Controller_Costumer extends Controller_Application implements Initializable {

    @FXML
    private VBox costumerMenu;

    @FXML
    private VBox itemView;

    @FXML
    private VBox itemVbox;

    @FXML
    private Button addOrderButton;

    @FXML
    private VBox orderHistoryPane;


    @FXML
    private VBox onGoingOrderIDPane;

    @FXML
    private VBox onGoingStatusPane;


    @FXML
    private VBox previousOrderOrderID;

    @FXML
    private VBox previousOrderStatus;

    @FXML
    private VBox confirmOrderPane;

    @FXML
    private VBox orderVBox;
    @FXML
    private Button deleteButton;

    @FXML
    private Button confirmOrders;


    @Override
    public void changeToRegisterScene( ) {

    }

    private ObservableList<LaundryItem> addedLaundryItems = FXCollections.observableArrayList();
    private ArrayList<LaundryItem> removedLaundryItems = new ArrayList<>();
    private IntegerBinding listSize = Bindings.size(addedLaundryItems);
    private BooleanBinding listPopulated = listSize.greaterThan(0);
    private ArrayList<ItemBox> itemBoxes = new ArrayList<>();
    private static final int pendingStatusID = 8;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        costumerMenu.toFront();
        addOrderButton.disableProperty().bind(listPopulated.not());
        addOrderButton.textProperty().bind(Bindings.convert(listSize).concat(" Items"));

    }

    /***
     * get's all the items from the DB and adds them to the item pane
     */
    public void showItems() {
        itemView.toFront();

        if (itemBoxes.size() == 0) {
            for (LaundryItem laundryItem : ItemsHandler.getItems()
            ) {
                ItemBox itemBox = new ItemBox(laundryItem);
                itemBox.setAddButton();
                itemBox.getButton().setOnMouseClicked(mouseEvent -> addedLaundryItems.add(laundryItem));
                itemBoxes.add(itemBox);

            }
            itemVbox.getChildren().addAll(itemBoxes);

        }
    }

    /***
     * shows all the order items which is recived from the DB through the Order ID
     * @param orderID ID of the Order
     */
    public void showOrderItems(int orderID) {
        confirmOrderPane.toFront();
        orderVBox.getChildren().clear();
        deleteButton.setOnMouseClicked(mouseEvent -> {
            OrderHandler.deleteOrder(orderID);
            goBack();
        });

        confirmOrders.setOnMouseClicked(mouseEvent -> {
            deleteOrderItems();
            goBack();
        });

        for (LaundryItem laundryItem : ItemsHandler.getorderLaundryItems(orderID)
        ) {
            ItemBox itemBox = new ItemBox(laundryItem);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                removedLaundryItems.add(laundryItem);
                orderVBox.getChildren().remove(itemBox);

            });
            orderVBox.getChildren().add(itemBox);

        }
    }

    /***
     * shows the cart of the customer, which contains added laundryItems
     */
    public void showCart() {
        confirmOrderPane.toFront();
        deleteButton.setOnMouseClicked(mouseEvent -> {
            addedLaundryItems.clear();
            goBack();
        });
        confirmOrders.setOnMouseClicked(mouseEvent -> createOrders());
        for (LaundryItem laundryItem : addedLaundryItems
        ) {
            ItemBox itemBox = new ItemBox(laundryItem);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                addedLaundryItems.remove(laundryItem);
                orderVBox.getChildren().remove(itemBox);
            });
            orderVBox.getChildren().add(itemBox);
        }
    }

    /***
     * logs the customer off
     */
    public void logOff() {
        addedLaundryItems.clear();
        super.logOff();
    }

    /***
     * lets the customer go back to start pane.
     */
    public void goBack() {
        orderVBox.getChildren().clear();
        costumerMenu.toFront();
    }

    /***
     * create orders with the premade status and adds them to the DB
     * does a print out in the system for super users being able to see what happends
     */
    public void createOrders() {
        OrderHandler.createOrder(AccountHandler.currentCostumerID, pendingStatusID, addedLaundryItems);
        System.out.println("Order added with " + addedLaundryItems.size() + " Items");
        addedLaundryItems.clear();
        goBack();
    }

    /***
     * shows the order history of the current costumer.
     */
    public void showOrderHistory() {
        orderHistoryPane.toFront();
        clearOrderHistory();
        for (Order order : OrderHandler.getCustomerOrders(AccountHandler.getCurrentUser())
        ) {
            Label orderID = new Label(String.valueOf(order.getID()));
            Label status = new Label(order.getStatus());

            switch (order.getStatusID()) {
                case 7:
                    previousOrderStatus.getChildren().add(status);
                    previousOrderOrderID.getChildren().add(orderID);
                    break;
                case pendingStatusID:
                    status.setOnMouseClicked(mouseEvent -> showOrderItems(order.getID()));
                    onGoingOrderIDPane.getChildren().add(orderID);
                    onGoingStatusPane.getChildren().add(status);
                    break;
                default:
                    onGoingOrderIDPane.getChildren().add(orderID);
                    onGoingStatusPane.getChildren().add(status);
                    break;
            }
        }
    }

    private void clearOrderHistory() {
        previousOrderOrderID.getChildren().clear();
        previousOrderStatus.getChildren().clear();
        onGoingStatusPane.getChildren().clear();
        onGoingOrderIDPane.getChildren().clear();
    }

    private void deleteOrderItems() {
        for (LaundryItem laundryItem : removedLaundryItems
        ) {
            OrderHandler.deleteOrderItems(laundryItem.getOrderItemID());
        }
    }
}
