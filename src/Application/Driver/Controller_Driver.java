package Application.Driver;

import Application.general.Controller_Application;
import Domain.DeliveryPoint.DeliveryPoint;
import Domain.Enums.Role;
import Domain.Managers.DeliveryPointManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Driver extends Controller_Application implements Initializable {

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

    private static TableView<Order> currentOrderTable = null;
    private static TableView<OrderItem> currentItemsTable = null;
    private static Order selectedOrder = null;
    private static int currentRoute = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Driver);
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

        columnItemID.prefWidthProperty().bind(centralOrderTable.widthProperty().divide(4).multiply(3));
        columnItemID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        columnConfirm.prefWidthProperty().bind(centralOrderTable.widthProperty().divide(4));
        columnConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    private void initDeliveryPointTable() {
        columnDeliveryPoint.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDeliveryPoint.prefWidthProperty().bind(deliveryPointTable.widthProperty());
        deliveryPointTable.setItems(DeliveryPointManager.getRouteDeliveryPoints(1));
    }

    private void initRouteOrderTables() {
        columnDeliverOrders.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnDeliverOrders.prefWidthProperty().bind(deliverTable.widthProperty());
        setSelectListener(deliverTable, routeOrderItemsTable);

        columnPickUp.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnPickUp.prefWidthProperty().bind(pickUpTable.widthProperty());
        setSelectListener(pickUpTable, routeOrderItemsTable);
    }

    private void initRouteItemsTable() {
        columnOrderItem.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnConfirmItem.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    public void showDeliveryPointOrders() {
        routeOrderItemsTable.getItems().clear();
        int ID = Integer.parseInt(deliveryPointTable.getSelectionModel().getSelectedItem().getID());
        deliverTable.setItems(OrderManager.getDeliveryPointOrders(ID, 5));
        pickUpTable.setItems(OrderManager.getDeliveryPointOrders(ID, 1));
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


                // Remove confirmed order from the list
                if(currentOrderTable.equals(centralOrderTable)){
                    centralOrderTable.setItems(OrderManager.getRouteOrders(currentRoute, 4));
                }
                else{
                    showDeliveryPointOrders();
                }

                // Remove order items from list
                currentItemsTable.getItems().clear();

                // Select next order on list
                currentOrderTable.requestFocus();
                currentOrderTable.getSelectionModel().select(0);


            } else {
                showAlert("Please confirm all items");
            }
        } else {
            showAlert("Please select an order");
        }

    }

    public void isAllOrdersConfirmed() {

        boolean isAllOrdersConfirmed = true;

        if (!centralOrderTable.getItems().isEmpty()) {
            for (Order order : centralOrderTable.getItems()) {
                if (!(order.getStatus().equals("5"))) {
                    isAllOrdersConfirmed = false;
                    showAlert("Please confirm all orders");
                    break;
                }
            }
        }

        if (isAllOrdersConfirmed) {
            changeView();
        }

    }

    public void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Oops");
        alert.setHeaderText(text);
        alert.showAndWait();
    }

    private void changeView() {
        centralOrders.setVisible(false);
        routeOrders.setVisible(true);

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
            }
        });

    }


}
