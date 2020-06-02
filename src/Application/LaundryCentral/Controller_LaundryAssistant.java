package Application.LaundryCentral;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import Services.PDF.WashingLabel;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class Controller_LaundryAssistant extends Controller_Application implements Initializable {
    @FXML private Button inboundOrderButton;
    @FXML private Button ongoingOrderButton;
    @FXML private TilePane inboundTilePane;
    @FXML private Label paneText;
    @FXML private Button washingLabelPrintButton;
    @FXML private Button backFromInboundItem;
    @FXML private VBox ongoingVbox;
    @FXML private VBox inboundVbox;
    @FXML private TilePane washingTilePane;
    @FXML private TilePane readyToPickUpTilePane;
    @FXML private Button confirmDoneOrder;
    @FXML private Label orderIDLabel;
    @FXML private Label orderStatusLabel;
    @FXML private TilePane orderItemsTilePane;
    @FXML private VBox ongoingOrderItemVbox;
    @FXML private TextField searchField;
    private Order viewingOrder;
    private int buttonWidth = 50;
    private int buttonHeight = 30;
    private int inboundOrderID = 2;
    private int ongoingOrderID = 3;
    private int outboundOrderID = 4;
    private Order activeOngoingOrder;

    /**
     * initializes the laundryassistant
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Laundry_Assistant);
        initInboundList();
        confirmDoneOrder.setStyle("-fx-background-color: #93C553");
        inboundOrderButton.setOnAction(e -> initInboundList());
        ongoingOrderButton.setOnAction(e -> initOngoingList());
        confirmDoneOrder.setOnAction(e -> {
            activeOngoingOrder.setStatus(4);
            OrderManager.updateOrderDB(activeOngoingOrder);
            initOngoingList();
        });

    }

    /**
     * updates labels, buttons depending on a orderitem is washed or not
     * @param orderItem - the order item
     * @param status - the label with status
     * @param statusButton - the button depending on orderitem status
     * @param allItemsConfirmed - bool to track if all is confirmed
     * @return - returns the bool to work with it later
     */
    private boolean checkIfAllItemsConfirmed(OrderItem orderItem, Label status, Button statusButton, boolean allItemsConfirmed){
        if (OrderManager.getWashStatusFromDB(orderItem)) {
            status.setText("Washed");
            statusButton.setText("Set as not finished");
            statusButton.setStyle("-fx-background-color: #FB323C ");
        }
        else {
            allItemsConfirmed = false;
            status.setText("Not washed");
            statusButton.setText("Set as finished");
            statusButton.setStyle("-fx-background-color: #93C553 ");
        }
        return allItemsConfirmed;
    }

    /**
     * searches an order by the ID put in an textfield.
     * then creates new labels, buttons, etc for it and displays it
     */
    public void searchForOrder( ) {
        ObservableList<Order> outboundOrders = OrderManager.getSearchOrder(Integer.parseInt(searchField.getText()));
        Order order = outboundOrders.get(0);
        resetUIstates(order);
        boolean allItemsConfirmed = true;
        for (OrderItem orderItem : order.getOrderItems()) {
            Label ID = new Label();
            Button statusButton = new Button();

            Label status = new Label();

            allItemsConfirmed = checkIfAllItemsConfirmed(orderItem,status,statusButton,allItemsConfirmed);
            statusButton.setOnAction(ae -> {
                OrderManager.setWashStatusInDB(orderItem);
                searchForOrder();
            });
            ID.setText("Item ID: " + orderItem.getID());
            ID.setContentDisplay(ContentDisplay.CENTER);
            status.setContentDisplay(ContentDisplay.CENTER);

            if (order.getStatusID() != ongoingOrderID) {

                status.setText(order.getStatusMessage());
            }
            statusButton.setContentDisplay(ContentDisplay.CENTER);

            orderItemsTilePane.getChildren().add(ID);
            orderItemsTilePane.getChildren().add(status);
            if (order.getStatusID() == ongoingOrderID) {
                orderItemsTilePane.getChildren().add(statusButton);
                if (allItemsConfirmed) {
                    confirmDoneOrder.setVisible(true);
                }
                else {
                    confirmDoneOrder.setVisible(false);
                }
            }
            ID.setStyle("-fx-font-size: 20");
            status.setStyle("-fx-font-size: 20");
            status.setPadding(new Insets(0, 0, 0, 10));
        }

    }

    /**
     * resets states of UI - panes visible/unvisible etc.
     * @param order - depending on the order, updates UI
     */
    private void resetUIstates(Order order) {
        activeOngoingOrder = order;
        confirmDoneOrder.setVisible(false);
        orderItemsTilePane.getChildren().clear();
        inboundVbox.setVisible(false);
        ongoingVbox.setVisible(false);
        ongoingOrderItemVbox.setVisible(true);
        orderIDLabel.setText("Order ID: " + order.getID());
        orderStatusLabel.setText("Status: " + order.getStatusID());
    }

    /**
     * initialize the inbound list when the inbound orders is clicked
     * will create buttons, and on actions for those buttons to display the orderitems within an order
     */
    public void initInboundList( ) {
        ongoingOrderItemVbox.setVisible(false);
        ongoingVbox.setVisible(false);
        inboundVbox.setVisible(true);
        backFromInboundItem.setVisible(false);
        inboundTilePane.getChildren().clear();
        washingLabelPrintButton.setVisible(false);
        paneText.setText("Arriving today with Driver");
        ObservableList<Order> inboundOrders = OrderManager.getCentralOrders(inboundOrderID);
        for (Order order : inboundOrders) {
            Button orderButton = new Button(String.valueOf(order.getID()));
            orderButton.setMinWidth(50);
            orderButton.setMinHeight(50);
            inboundTilePane.getChildren().add(orderButton);
            orderButton.setOnAction(e -> {
                backFromInboundItem.setVisible(true);
                viewingOrder = order;
                inboundTilePane.getChildren().clear();
                washingLabelPrintButton.setVisible(true);
                paneText.setText("Laundry items in order: #" + order.getID());
                for (OrderItem orderItem : order.getOrderItems()) {
                    Button orderItemButton = new Button(String.valueOf(orderItem.getID()));
                    orderItemButton.setMinWidth(buttonWidth);
                    orderItemButton.setMinHeight(buttonHeight);
                    inboundTilePane.getChildren().add(orderItemButton);
                }
            });
        }
    }

    /**
     * will show ongoing orders and create buttons for it
     */
    public void initOngoingList( ) {
        washingTilePane.getChildren().clear();
        inboundVbox.setVisible(false);
        ongoingOrderItemVbox.setVisible(false);
        ongoingVbox.setVisible(true);
        addOngoingOrderButtons(ongoingOrderID, washingTilePane);

        readyToPickUpTilePane.getChildren().clear();
        addOngoingOrderButtons(outboundOrderID, readyToPickUpTilePane);
    }

    /**
     * will add buttons to the pane with onactions etc. to show the orders and items of these.
     * will also make it possible to complete an order and set it ready for pickup
     * @param orderStatusID - the orderstatusID used to get all orders with that id
     * @param tilePane - the tilepane we are working on
     */
    private void addOngoingOrderButtons(int orderStatusID, TilePane tilePane) {
        ObservableList<Order> outboundOrders = OrderManager.getCentralOrders(orderStatusID);

        for (Order order : outboundOrders) {
            Button button = new Button(String.valueOf(order.getID()));
            button.setMinWidth(50);
            button.setMinHeight(30);
            tilePane.getChildren().add(button);
            button.setOnAction(e -> {
                resetUIstates(order);
                for (OrderItem orderItem : order.getOrderItems()) {
                    Label ID = new Label();
                    Button statusButton = new Button();
                    Label status = new Label();
                    ID.setStyle("-fx-font-size: 20");
                    status.setStyle("-fx-font-size: 20");
                    status.setPadding(new Insets(0, 0, 0, 10));
                    boolean allItemsConfirmed = true;
                    allItemsConfirmed = checkIfAllItemsConfirmed(orderItem,status,statusButton,allItemsConfirmed);

                    statusButton.setOnAction(ae -> {
                        OrderManager.setWashStatusInDB(orderItem);
                        button.fire();
                    });
                    ID.setText("Item ID: " + orderItem.getID());
                    ID.setContentDisplay(ContentDisplay.CENTER);
                    status.setContentDisplay(ContentDisplay.CENTER);

                    statusButton.setContentDisplay(ContentDisplay.CENTER);

                    orderItemsTilePane.getChildren().add(ID);
                    orderItemsTilePane.getChildren().add(status);
                    if (tilePane != readyToPickUpTilePane) {
                        if (allItemsConfirmed) {
                            confirmDoneOrder.setVisible(true);
                        }
                        else {
                            confirmDoneOrder.setVisible(false);
                        }
                        orderItemsTilePane.getChildren().add(statusButton);
                    }
                    else {
                        confirmDoneOrder.setVisible(false);
                        status.setText("Waiting for Driver");
                    }
                }

            });
        }
    }

    /**
     * prints label into a pdf, that then can be printed to defaultprinter with .printlabel method
     */
    public void printLabel( ) {
        /* this sysout is only for exam teachers and censor to see we handle printing
         * instead of just to console, we actually made it a PDF with capability to print
         * it creates PDF with costumer,order,orderitem ID.
         * this can be found in Foundation -> Resources -> Files -> WashingLabels
         */
        System.out.println("washinglabels can be found in Foundation -> Resources -> Files -> WashingLabels");
        for (OrderItem orderItem : viewingOrder.getOrderItems()) {

            WashingLabel washingLabel = new WashingLabel(String.valueOf(orderItem.getID()), 30, viewingOrder.getID(),
                                                         orderItem.getID(), 4,
                                                         "Costumer ID: " + viewingOrder.getCustomerID(),
                                                         "Order ID: " + viewingOrder.getID(),
                                                         "Laundry Item ID: " + orderItem.getID());
            washingLabel.printLabel();
        }
    }
}
