package Application.Driver;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller_Driver extends Controller_Application implements Initializable {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Order,Integer> columnOrderID;
    @FXML private TableView<OrderItem> itemTable;
    @FXML private TableColumn<Integer,OrderItem> columnItemID;
    @FXML private TableColumn<Boolean,OrderItem> columnConfirm;

    private static TableRow<Order> selectedRow = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Driver);
        initOrderTable();
        initItemTable();
    }

    private void initOrderTable(){

        columnOrderID.prefWidthProperty().bind(orderTable.widthProperty());
        columnOrderID.setCellValueFactory(new PropertyValueFactory<>("ID"));
        columnOrderID.setCellFactory(column -> {
            TableCell<Order,Integer> cell = new TableCell<>(){

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item.toString());
                    }
                }
            };

            cell.setOnMouseClicked(mouseEvent -> {
                System.out.println("mouse event");
                selectedRow = cell.getTableRow();
            });
            return cell;
        });

        orderTable.setItems(OrderManager.getRouteOrders(1,4));
    }

    private void initItemTable(){
        columnItemID.prefWidthProperty().bind(orderTable.widthProperty().divide(4).multiply(3));
        columnItemID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        columnConfirm.prefWidthProperty().bind(orderTable.widthProperty().divide(4));
        columnConfirm.setCellValueFactory(new PropertyValueFactory<>("checkBox"));
    }

    public void selectOrder(){
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList(orderTable.getSelectionModel().getSelectedItem().getOrderItems());
        itemTable.setItems(orderItems);
    }

    public void isAllItemsConfirmed(){
        boolean result = true;
        for (OrderItem oi : orderTable.getSelectionModel().getSelectedItem().getOrderItems()){
            if(!oi.isChecked()) result = false;
        }

        if(selectedRow != null && result){
            selectedRow.getStyleClass().add("confirmedOrder");
        }

    }


}
