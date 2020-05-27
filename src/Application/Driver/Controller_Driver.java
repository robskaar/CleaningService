package Application.Driver;

import Application.general.Controller_Application;
import Domain.DeliveryPoint.DeliveryPoint;
import Domain.Enums.Role;
import Domain.Managers.AccountManager;
import Domain.Managers.DeliveryPointManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Driver extends Controller_Application implements Initializable {

    @FXML
    private BorderPane root;
    @FXML
    private Button routeConfirm;
    @FXML
    private Button centralConfirm;
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

    private static TableView<Order> currentOrderTable = null;
    private static TableView<OrderItem> currentItemsTable = null;
    private static Order selectedOrder = null;
    private static int currentRoute;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Driver);
        currentRoute = 1;
        initCentralOrderTable();
        initCentralItemTable();
    }

    private void initCentralOrderTable() {
        centralOrderTable.setPlaceholder(getOnEmptyLabel("No more orders"));

        columnOrderID.prefWidthProperty().bind(centralOrderTable.widthProperty());
        columnOrderID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        centralOrderTable.setItems(OrderManager.getRouteOrders(currentRoute, 4));
        setSelectListener(centralOrderTable, centralItemTable);
    }

    private void initCentralItemTable() {
        centralItemTable.setPlaceholder(getOnEmptyLabel("Select Order"));
        centralItemTable.setSelectionModel(null);

        columnItemID.prefWidthProperty().bind(centralOrderTable.widthProperty().divide(4).multiply(3));
        columnItemID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        columnConfirm.prefWidthProperty().bind(centralOrderTable.widthProperty().divide(4));
        columnConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private void initDeliveryPointTable() {
        columnDeliveryPoint.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDeliveryPoint.prefWidthProperty().bind(deliveryPointTable.widthProperty().divide(10).multiply(9));

        columnDeliveryConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        columnDeliveryConfirm.prefWidthProperty().bind(deliveryPointTable.widthProperty().divide(10));

        deliveryPointTable.setItems(DeliveryPointManager.getRouteDeliveryPoints(1));
        deliveryPointTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldDeliveryPoint, newDeliveryPoint) -> {

            showDeliveryPointOrders();

            if (pickUpTable.getItems().isEmpty() && deliverTable.getItems().isEmpty()) {
                newDeliveryPoint.confirmAllOrdersDone();
            }
        });

    }

    private void initRouteOrderTables() {
        columnDeliverOrders.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnDeliverOrders.prefWidthProperty().bind(deliverTable.widthProperty());
        setSelectListener(deliverTable, routeOrderItemsTable);
        deliverTable.setPlaceholder(getOnEmptyLabel("Choose Delivery Point"));

        columnPickUp.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnPickUp.prefWidthProperty().bind(pickUpTable.widthProperty());
        setSelectListener(pickUpTable, routeOrderItemsTable);
        pickUpTable.setPlaceholder(getOnEmptyLabel("Choose Delivery Point"));
    }

    private void initRouteItemsTable() {
        routeOrderItemsTable.setSelectionModel(null);
        columnOrderItem.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnConfirmItem.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
        routeOrderItemsTable.setPlaceholder(getOnEmptyLabel("Choose order"));
    }

    public void showDeliveryPointOrders() {
        routeOrderItemsTable.getItems().clear();
        int ID = Integer.parseInt(deliveryPointTable.getSelectionModel().getSelectedItem().getID());

        // Insert data to table views
        deliverTable.setItems(OrderManager.getDeliveryPointOrders(ID, 5));
        pickUpTable.setItems(OrderManager.getDeliveryPointOrders(ID, 1));

        // Set table view text if empty
        if (deliverTable.getItems().isEmpty()) {
            deliverTable.setPlaceholder(getOnEmptyLabel("No Orders"));
        }
        if (pickUpTable.getItems().isEmpty()) {
            pickUpTable.setPlaceholder(getOnEmptyLabel("No Orders"));
        }

    }

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
                String newStatus = String.valueOf(Integer.parseInt(selectedOrder.getStatus()) + 1);
                selectedOrder.updateStatus(newStatus);
                OrderManager.updateOrderDB(selectedOrder);

                showMessage("Confirmed", Color.web("#2ECC71"));
                // Remove confirmed order from the list
                if (currentOrderTable.equals(centralOrderTable)) {
                    centralOrderTable.setItems(OrderManager.getRouteOrders(currentRoute, 4));
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

                // Remove order items from list
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

    public void startRoute() {

        if (centralOrderTable.getItems().isEmpty()) {
            changeView();
        } else {
            showMessage("Please confirm all orders", Color.web("#E74C3C"));
        }
        /*
        boolean isAllOrdersConfirmed = true;

        if (!centralOrderTable.getItems().isEmpty()) {
            for (Order order : centralOrderTable.getItems()) {
                if (!(order.getStatus().equals("5"))) {
                    isAllOrdersConfirmed = false;
                    showMessage("Please confirm all orders",Color.web("#E74C3C"));
                    break;
                }
            }
        }

        if (isAllOrdersConfirmed) {
            changeView();
        }

         */

    }

    public void endRoute() {
        boolean isAllDeliveryPointsConfirmed = true;

        for(DeliveryPoint dp : deliveryPointTable.getItems()){
            if(!dp.getCheckBox().isSelected()){
                isAllDeliveryPointsConfirmed = false;
                showMessage("Please confirm all orders", Color.web("#E74C3C"));
                break;
            }
        }

        if(isAllDeliveryPointsConfirmed){
            AccountManager.logOff();
        }
    }

    private void isDeliveryPointDone() {
        if (pickUpTable.getItems().isEmpty() && deliverTable.getItems().isEmpty()) {
            deliveryPointTable.getSelectionModel().getSelectedItem().confirmAllOrdersDone();
        }
    }

    private void changeView() {
        centralOrders.setVisible(false);
        routeOrders.setVisible(true);
        routeLabel.setText("Showing orders from route: " + currentRoute);

        initDeliveryPointTable();
        initRouteOrderTables();
        initRouteItemsTable();
    }

    private Label getOnEmptyLabel(String text) {
        Label onEmptyLabel = new Label(text);
        onEmptyLabel.getStyleClass().add("onEmptyLabel");
        return onEmptyLabel;
    }

    private void setSelectListener(TableView<Order> tableOrder, TableView<OrderItem> tableItem) {

        tableOrder.getSelectionModel().selectedItemProperty().addListener((observableValue, oldOrder, newOrder) -> {

            if (newOrder != null) {
                selectedOrder = newOrder;
                currentOrderTable = tableOrder;
                currentItemsTable = tableItem;
                tableItem.setItems(FXCollections.observableArrayList(selectedOrder.getOrderItems()));

                // Setting text on confirm button

                if (tableOrder.equals(centralOrderTable)) {
                    centralConfirm.setDisable(false);
                    centralConfirm.setText("Confirm pick up of order #" + selectedOrder.getID());
                } else if (tableOrder.equals(pickUpTable)) {
                    deliverTable.getSelectionModel().clearSelection();
                    routeConfirm.setDisable(false);
                    routeConfirm.setText("Confirm pick up of order #" + selectedOrder.getID());
                } else if (tableOrder.equals(deliverTable)) {
                    pickUpTable.getSelectionModel().clearSelection();
                    routeConfirm.setDisable(false);
                    routeConfirm.setText("Confirm delivery of order #" + selectedOrder.getID());
                }
            }
        });

    }

    private void showMessage(String message, Color color) {

        StackPane stack = new StackPane();
        stack.setLayoutX(510);
        stack.setLayoutY(678);
        stack.setOpacity(0);

        Text text = new Text(message);
        text.setFont(Font.font(20));

        Rectangle rectangle = new Rectangle(832, 40, color);

        stack.getChildren().addAll(rectangle, text);
        root.getChildren().add(stack);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), stack);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        fadeIn.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(1500), stack);
            fadeOut.setFromValue(1);
            fadeOut.setToValue(0);
            fadeOut.setDelay(Duration.millis(300));
            fadeOut.play();
            fadeOut.setOnFinished(event1 -> {
                root.getChildren().remove(stack);
            });
        });
    }

}
