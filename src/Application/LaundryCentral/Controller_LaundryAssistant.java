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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
    @FXML private AnchorPane inboundOrderPane;
    @FXML private AnchorPane ongoingOrderPane;
    @FXML private Button inboundOrderButton;
    @FXML private Button ongoingOrderButton;
    @FXML private TilePane inboundTilePane;
    @FXML private Label paneText;
    @FXML private Button washingLabelPrintButton;
    @FXML private Button backFromInboundItem;
    @FXML private VBox ongoingVbox;
    @FXML private VBox inboundVbox;
    @FXML private TilePane washingTilePane;
    @FXML private TilePane  readyToPickUpTilePane;
    private Order viewingOrder;
    private int buttonWidth = 50;
    private int buttonHeigth= 30;
    private  int inboundOrderID = 2;
    private  int ongoingOrderID = 3;
    private int outboundOrderID = 4;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Laundry_Assistant);
        initInboundList();
        inboundOrderButton.setOnAction(e -> {
            initInboundList();
        });
    }

    public void initInboundList( ) {
        ongoingVbox.setVisible(false);
        inboundVbox.setVisible(true);
        backFromInboundItem.setVisible(false);
        inboundTilePane.getChildren().clear();
        washingLabelPrintButton.setVisible(false);
        paneText.setText("All inbound orders");
        ObservableList<Order> inboundOrders = OrderManager.getCentralOrders(inboundOrderID);
        int i = 0;
        for (Order order : inboundOrders) {
            Button button = new Button(String.valueOf(order.getID()));
            button.setMinWidth(50);
            button.setMinHeight(50);
            inboundTilePane.getChildren().add(button);
            button.setOnAction(e -> {
                initOrderDetails(order);
            });
        }
    }

    public void initOngoingList( ) {
        washingTilePane.getChildren().clear();
        inboundVbox.setVisible(false);
        ongoingVbox.setVisible(true);
        ObservableList<Order> ongoingOrders = OrderManager.getCentralOrders(ongoingOrderID);
        iterateOrders(ongoingOrders, washingTilePane);
        initOutBoundList();
    }

    public void initOutBoundList( ) {
        readyToPickUpTilePane.getChildren().clear();
        ObservableList<Order> outboundOrders = OrderManager.getCentralOrders(outboundOrderID);
        iterateOrders(outboundOrders, readyToPickUpTilePane);
    }

    private void iterateOrders(ObservableList<Order> outboundOrders, TilePane readyToPickUpTilePane) {
        for (Order order: outboundOrders) {
            Button button = new Button(String.valueOf(order.getID()));
            button.setMinWidth(50);
            button.setMinHeight(30);
            readyToPickUpTilePane.getChildren().add(button);
            button.setOnAction(e -> {
                initOrderDetails(order);
            });
        }
    }

    private void initOrderDetails(Order order) {
        backFromInboundItem.setVisible(true);
        viewingOrder = order;
        inboundTilePane.getChildren().clear();
        washingLabelPrintButton.setVisible(true);
        paneText.setText("Laundry items in order: #" + order.getID());
        for (OrderItem orderItem : order.getOrderItems()) {
            Button button = new Button(String.valueOf(orderItem.getID()));
            button.setMinWidth(buttonWidth);
            button.setMinHeight(buttonHeigth);
            inboundTilePane.getChildren().add(button);

        }
    }

    public void printLabel(){
        /* this sysout is only for exam teachers and censor to see we handle printing
        * instead of just to console, we actually made it a PDF with capability to print
        * it creates PDF with costumer,order,orderitem ID.
        * this can be found in Foundation -> Resources -> Files -> WashingLabels
        */
        System.out.println();
        for (OrderItem orderItem:viewingOrder.getOrderItems()) {

            WashingLabel washingLabel = new WashingLabel(String.valueOf(orderItem.getID()), 30, viewingOrder.getID(),
                                                         orderItem.getID(), 4, "Costumer ID: " + viewingOrder.getCustomerID(),
                                                         "Order ID: "+ viewingOrder.getID(),"Laundry Item ID: "+orderItem.getID());
            washingLabel.printLabel();
        }
    }
}
