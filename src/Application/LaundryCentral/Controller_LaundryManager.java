package Application.LaundryCentral;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Foundation.Database.DB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class Controller_LaundryManager extends Controller_LaundryAssistant implements Initializable {

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
    @FXML private BorderPane assistantTaskPane;
    @FXML private  BorderPane initChoicePane;
    @FXML private  BorderPane managementPane;
    @FXML private  BorderPane statisticsPane;
    @FXML private Button showAssistantButton;
    @FXML private  Button showManagementButton;
    @FXML private Button showStatsButton;
    private Order viewingOrder;
    private int buttonWidth = 50;
    private int buttonHeight = 30;
    private int inboundOrderID = 2;
    private int ongoingOrderID = 3;
    private int outboundOrderID = 4;
    private Order activeOngoingOrder;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initChoicePane.setVisible(true);
        statisticsPane.setVisible(false);
        managementPane.setVisible(false);
        assistantTaskPane.setVisible(false);
        DB.setDBPropertiesPath(Role.Laundry_Manager);
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
    @FXML
    private void showPanes(ActionEvent event){
        if (showAssistantButton.equals(event.getSource())) {
            initChoicePane.setVisible(false);
            statisticsPane.setVisible(false);
            managementPane.setVisible(false);
            assistantTaskPane.setVisible(true);
        }
        else if (showManagementButton.equals(event.getSource())){
            initChoicePane.setVisible(false);
            statisticsPane.setVisible(false);
            managementPane.setVisible(true);
            assistantTaskPane.setVisible(false);
        }
        else if (showStatsButton.equals(event.getSource())){
            initChoicePane.setVisible(false);
            statisticsPane.setVisible(true);
            managementPane.setVisible(false);
            assistantTaskPane.setVisible(false);
        }

    }
    public void showManagerMenu(){
        initChoicePane.setVisible(true);
        statisticsPane.setVisible(false);
        managementPane.setVisible(false);
        assistantTaskPane.setVisible(false);
    }
}
