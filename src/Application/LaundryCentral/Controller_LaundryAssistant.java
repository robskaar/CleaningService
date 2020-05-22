package Application.LaundryCentral;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;

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



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Laundry_Assistant);
        initInboundList();
        inboundOrderButton.setOnAction(e->{
            initInboundList();
        });
    }

    private void initInboundList(){
        ObservableList<Order> inboundOrders = OrderManager.getCentralOrders(2);
        int i = 0;
        for (Order order:inboundOrders) {
            Button button = new Button(String.valueOf(order.getID()));
            button.setMinWidth(10);
            button.setMinHeight(10);
          inboundTilePane.getChildren().add(button);

        }

    }
    private void initOngoingList(){
        ObservableList<Order> ongoingOrders = OrderManager.getCentralOrders(3);
    }
    private void initOutBoundList(){
        ObservableList<Order> outboundOrders = OrderManager.getCentralOrders(4);
    }
}
