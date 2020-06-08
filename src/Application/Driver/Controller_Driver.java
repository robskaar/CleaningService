package Application.Driver;

import Application.general.Controller_Application;
import Domain.DeliveryPoint.DeliveryPoint;
import Domain.Enums.Role;
import Domain.Managers.AccountHandler;
import Domain.Managers.DeliveryPointHandler;
import Domain.Managers.OrderHandler;
import Domain.Map.GoogleMap;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * @Author Jacob Bonefeld
 * @Project CleaningService
 * @Date 18.05.2020
 **/

public class Controller_Driver extends Controller_Application implements Initializable {

    @FXML
    private BorderPane root;
    /*
    FXML components from Central Orders pane
     */
    @FXML
    private VBox centralOrders;
    @FXML
    private VBox routeOrders;
    @FXML
    private TableView<Order> centralOrderTable;
    @FXML
    private TableColumn<Order, Integer> columnOrderID;
    @FXML
    private TableView<OrderItem> centralItemTable;
    @FXML
    private TableColumn<Integer, OrderItem> columnItemID;
    @FXML
    private TableColumn<Boolean, OrderItem> columnConfirm;
    @FXML
    private Button centralConfirm;

    /*
    FXML components from route orders pane
     */
    @FXML
    private TableView<DeliveryPoint> deliveryPointTable;
    @FXML
    private TableColumn<DeliveryPoint, String> columnDeliveryPoint;
    @FXML
    private TableColumn<Boolean, DeliveryPoint> columnDeliveryConfirm;
    @FXML
    private TableView<Order> deliverTable;
    @FXML
    private TableColumn<Order, Integer> columnDeliverOrders;
    @FXML
    private TableView<Order> pickUpTable;
    @FXML
    private TableColumn<Order, Integer> columnPickUp;
    @FXML
    private TableView<OrderItem> routeOrderItemsTable;
    @FXML
    private TableColumn<Integer, OrderItem> columnOrderItem;
    @FXML
    private TableColumn<Boolean, OrderItem> columnConfirmItem;
    @FXML
    private Label routeLabel;
    @FXML
    private Button routeConfirm;
    @FXML
    private VBox stackOrders;
    @FXML
    private VBox stackMap;
    @FXML
    private Button showOrHideMap;


    private static TableView<Order> currentOrderTable = null;
    private static TableView<OrderItem> currentItemsTable = null;
    private static Order selectedOrder = null;
    private static int currentRoute = 1;
    private static ArrayList<DeliveryPoint> deliveryPoints;
    private GoogleMap googleMap;
    // Order statuses
    private static final int AT_DELIVERY_POINT_READY_FOR_TRANSIT = 1;
    private static final int AT_CLEANING_CENTRAL_READY_FOR_TRANSIT = 4;
    private static final int IN_TRANSIT_TO_DELIVERY_POINT = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Driver);
        currentRoute = AccountHandler.getCurrentRoute();
        initCentralOrderTable();
        initCentralItemTable();
    }

    /**
     * Sets up order table for the laundry central
     */
    private void initCentralOrderTable() {
        // set property for observable object
        columnOrderID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        // Fetch orders from database and display
        centralOrderTable.setItems(OrderHandler.getRouteOrders(currentRoute, AT_CLEANING_CENTRAL_READY_FOR_TRANSIT));

        // Set text to show if table is empty
        setOnEmptyLabel("No more orders", centralOrderTable);

        // Setup listener for when a row is selected
        setSelectListener(centralOrderTable, centralItemTable);
    }

    /**
     * Sets up item table for the laundry central
     */
    private void initCentralItemTable() {
        // Sets text to be displayed when the table is empty
        setOnEmptyLabel("Select Order", centralItemTable);
        // Make table rows unselectable
        centralItemTable.setSelectionModel(null);

        // Set object property
        columnItemID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    /**
     * Sets up table that shows delivery points
     */
    private void initDeliveryPointTable() {

        // Sets up columns property for observable object
        columnDeliveryPoint.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDeliveryConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

        // Fetch delivery points from current route from database
        deliveryPoints = DeliveryPointHandler.getRouteDeliveryPoints(currentRoute);
        deliveryPointTable.setItems(FXCollections.observableArrayList(deliveryPoints));

        // Sets up listener for when a delivery point is selected
        deliveryPointTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDeliveryPoint, newDeliveryPoint) -> {

            // Show corresponding orders
            showDeliveryPointOrders();

            // If there is no orders, then auto confirm delivery point
            if (pickUpTable.getItems().isEmpty() && deliverTable.getItems().isEmpty()) {
                newDeliveryPoint.confirmAllOrdersDone();
            }
        });
    }

    /**
     * Sets up route deliver- and pick up table
     */
    private void initRouteOrderTables() {
        // Add listener for selection of row
        setSelectListener(deliverTable, routeOrderItemsTable);
        setSelectListener(pickUpTable, routeOrderItemsTable);

        // Sets up property to get from observable object
        columnDeliverOrders.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnPickUp.setCellValueFactory(new PropertyValueFactory<>("ID"));

        // Sets column width to fill entire table
        columnDeliverOrders.prefWidthProperty().bind(deliverTable.widthProperty());
        columnPickUp.prefWidthProperty().bind(pickUpTable.widthProperty());

        // Sets text to show when table is empty
        setOnEmptyLabel("Choose Delivery Point", deliverTable);
        setOnEmptyLabel("Choose Delivery Point", pickUpTable);
    }

    /**
     * Sets of table that view route order items
     */
    private void initRouteItemsTable() {
        // Items can not be selected (Only use checkbox)
        routeOrderItemsTable.setSelectionModel(null);

        // Sets which properties to get from the observable objects
        columnOrderItem.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnConfirmItem.setCellValueFactory(new PropertyValueFactory<>("checkBox"));

        // Sets text to show if table is empty
        setOnEmptyLabel("Choose Order", routeOrderItemsTable);
    }

    /**
     * Method called when a delivery point is selected
     * Shows all orders from the delivery point
     */
    public void showDeliveryPointOrders() {
        routeOrderItemsTable.getItems().clear();

        // Gets ID from selected delivery point
        int ID = Integer.parseInt(deliveryPointTable.getSelectionModel().getSelectedItem().getID());

        // Fetch data from DB and insert in table views
        deliverTable.setItems(OrderHandler.getDeliveryPointOrders(ID, IN_TRANSIT_TO_DELIVERY_POINT));
        pickUpTable.setItems(OrderHandler.getDeliveryPointOrders(ID, AT_DELIVERY_POINT_READY_FOR_TRANSIT));

        // Set table view text if empty
        if (deliverTable.getItems().isEmpty()) {
            setOnEmptyLabel("No Orders", deliverTable);
        }
        if (pickUpTable.getItems().isEmpty()) {
            setOnEmptyLabel("No Orders", pickUpTable);
        }
    }

    /**
     * Method called when user clicks confirm order
     * Checks if all items from the order has been confirmed
     */
    public void confirmOrder() {

        boolean isAllItemsConfirmed = true;

        // Assert that an order has is selected
        if (selectedOrder != null) {

            // Check if all order items is confirmed
            for (OrderItem orderItem : selectedOrder.getOrderItems()) {
                if (!orderItem.isChecked()) isAllItemsConfirmed = false;
            }

            if (isAllItemsConfirmed) {

                // Upload new order status to database
                int newStatus = selectedOrder.getStatusID() + 1;
                selectedOrder.updateStatus(newStatus);
                OrderHandler.updateOrderDB(selectedOrder);

                // Show message that order has been confirmed
                showMessage("Confirmed", Color.web("#2ECC71"));
                
                // Remove confirmed order from the list
                if (currentOrderTable.equals(centralOrderTable)) {
                    centralOrderTable.setItems(OrderHandler.getRouteOrders(currentRoute, AT_CLEANING_CENTRAL_READY_FOR_TRANSIT));
                    // Disable and change text on confirm button
                    centralConfirm.setDisable(true);
                    centralConfirm.setText("No orders selected");
                } else {
                    showDeliveryPointOrders();
                    // Disable and change text on confirm button
                    routeConfirm.setDisable(true);
                    routeConfirm.setText("No orders selected");
                    // Check if all orders from delivery point is confirmed
                    isDeliveryPointDone();
                }

                // Remove order items from table
                currentItemsTable.getItems().clear();

                // Select next order on list
                currentOrderTable.requestFocus();
                currentOrderTable.getSelectionModel().select(0);


            } else {
                showMessage("Please confirm all items", Color.web("#E74C3C"));
            }
        } else {
            showMessage("Please select and order", Color.web("#E74C3C"));
        }

    }

    /**
     * Method called when user clicks start route
     * Checks if all orders has been picked up from the laundry central
     */
    public void startRoute() {

        if (centralOrderTable.getItems().isEmpty()) {
            // Change view to route scene if all orders are confirmed
            changeView();
        } else {
            // Show warning message if some orders has not been confirmed
            showMessage("Please confirm all orders", Color.web("#E74C3C"));
        }

    }

    /**
     * Method called when user presses end route
     * Checks if all delivery points has all orders confirmed
     * Logs out if true
     */
    public void endRoute() {

        boolean isAllDeliveryPointsConfirmed = true;

        // Checks if all delivery points is confirmed
        for (DeliveryPoint dp : deliveryPointTable.getItems()) {
            if (!dp.getCheckBox().isSelected()) {
                isAllDeliveryPointsConfirmed = false;

                // Show warning message if user failed to confirm all delivery points
                showMessage("Please confirm all orders", Color.web("#E74C3C"));
                break;
            }
        }

        // Log off if all delivery points is confirmed
        if (isAllDeliveryPointsConfirmed) {
            logOff();
        }
    }

    /**
     * Checks if all orders from a delivery point has been confirmed
     * Selects the checkbox on delivery point table if true
     */
    private void isDeliveryPointDone() {
        if (pickUpTable.getItems().isEmpty() && deliverTable.getItems().isEmpty()) {
            deliveryPointTable.getSelectionModel().getSelectedItem().confirmAllOrdersDone();
        }
    }

    /**
     * Changes the scene from picking up orders from central, to the route scene
     */
    private void changeView() {
        centralOrders.setVisible(false);
        routeOrders.setVisible(true);
        routeLabel.setText("Showing orders from route: " + currentRoute);

        googleMap = GoogleMap.getMap();
        stackMap.getChildren().add(googleMap.getMapView());

        initDeliveryPointTable();
        initRouteOrderTables();
        initRouteItemsTable();
    }

    /**
     * Sets the text to be displayed, when a table is empty
     *
     * @param text  The text to be displayed
     * @param table The table
     */
    private void setOnEmptyLabel(String text, TableView table) {
        Label onEmptyLabel = new Label(text);
        onEmptyLabel.getStyleClass().add("onEmptyLabel");
        table.setPlaceholder(onEmptyLabel);
    }

    /**
     * Creates a listener for order tables. It listens for a selection of a row.
     *
     * @param tableOrder The order table to listen on
     * @param tableItem  The corresponding table, where order table will show its items
     */
    private void setSelectListener(TableView<Order> tableOrder, TableView<OrderItem> tableItem) {

        tableOrder.getSelectionModel().selectedItemProperty().addListener((observableValue, oldOrder, newOrder) -> {

            if (newOrder != null) {
                selectedOrder = newOrder;
                currentOrderTable = tableOrder;
                currentItemsTable = tableItem;

                // Shows the order items
                tableItem.setItems(FXCollections.observableArrayList(selectedOrder.getOrderItems()));

                // Setting text on confirm button
                if (tableOrder.equals(centralOrderTable)) {
                    centralConfirm.setDisable(false);
                    centralConfirm.setText("Confirm pick up of order #" + selectedOrder.getID());
                } else if (tableOrder.equals(pickUpTable)) {
                    deliverTable.getSelectionModel().clearSelection(); // Assert that only deliver table is selected
                    routeConfirm.setDisable(false);
                    routeConfirm.setText("Confirm pick up of order #" + selectedOrder.getID());
                } else if (tableOrder.equals(deliverTable)) {
                    pickUpTable.getSelectionModel().clearSelection(); // Assert that only pick up table is selected
                    routeConfirm.setDisable(false);
                    routeConfirm.setText("Confirm delivery of order #" + selectedOrder.getID());
                }
            }
        });

    }

    /**
     * This method creates a pop up box, which informs user of a message
     *
     * @param message Text to be displayed on screen
     * @param color   Color of the pop up box
     */
    private void showMessage(String message, Color color) {

        // The stack makes it possible to add text on top of a rectangle
        StackPane stack = new StackPane();
        stack.setLayoutX(511);
        stack.setLayoutY(677);
        stack.setOpacity(0);

        // The text to be displayed
        Text text = new Text(message);
        text.setFont(Font.font(20));

        // The pop up box
        Rectangle rectangle = new Rectangle(825, 35, color);

        stack.getChildren().addAll(rectangle, text);
        root.getChildren().add(stack);

        // Transition to fade in the pop up
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), stack);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // When faded in, create a fade out transition
        fadeIn.setOnFinished(event -> {

            FadeTransition fadeOut = new FadeTransition(Duration.millis(1500), stack);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.millis(300));
            fadeOut.play();

            // When fade out is done, remove the stack to get garbage collected
            fadeOut.setOnFinished(event1 -> {
                root.getChildren().remove(stack);
            });
        });
    }

    /**
     * This method switches view from order overview to map with delivery points
     */
    public void showMap(){

        googleMap.setMarkers(deliveryPoints);
        stackOrders.setVisible(false);
        stackMap.setVisible(true);

        showOrHideMap.setText("Show Orders");
        showOrHideMap.setOnAction(event -> {
            showOrders();
        });
    }

    /**
     * This method switches view from map to order overview
     */
    public void showOrders(){
        stackOrders.setVisible(true);
        stackMap.setVisible(false);

        showOrHideMap.setText("Show Map");
        showOrHideMap.setOnAction(event -> {
            showMap();
        });
    }
}
