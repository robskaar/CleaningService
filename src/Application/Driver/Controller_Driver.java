package Application.Driver;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.Managers.AccountManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller_Driver extends Controller_Application implements Initializable {

    @FXML private TableView<Order> orderTable;
    @FXML private TableColumn<Integer,Order> columnOrderID;
    @FXML private TableView<OrderItem> itemTable;
    @FXML private TableColumn<Integer,OrderItem> columnItemID;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Temp fix
        DB.setDBPropertiesPath(Role.Driver);
        initOrderTable();
        initItemTable();
    }

    private void initOrderTable(){
        columnOrderID.prefWidthProperty().bind(orderTable.widthProperty());
        columnOrderID.setCellValueFactory(new PropertyValueFactory<>("ID"));

        orderTable.setItems(OrderManager.getRouteOrders(1,4));
    }

    private void initItemTable(){
        columnItemID.prefWidthProperty().bind(orderTable.widthProperty());
        columnItemID.setCellValueFactory(new PropertyValueFactory<>("ID"));
    }

    public void cellClick(){
        ObservableList<OrderItem> orderItems = FXCollections.observableArrayList(orderTable.getSelectionModel().getSelectedItem().getOrderItems());

        for (OrderItem oi : orderTable.getSelectionModel().getSelectedItem().getOrderItems()){
            System.out.println(oi.toString());
        }
        itemTable.setItems(orderItems);
    }

}
