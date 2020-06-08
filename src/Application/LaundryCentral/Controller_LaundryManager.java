package Application.LaundryCentral;

import Domain.DeliveryPoint.DeliveryPoint;
import Domain.Driver.Driver;
import Domain.Enums.Role;
import Domain.LaundryItems.Item;
import Domain.Managers.*;
import Domain.Order.Order;
import Domain.Route.Route;
import Foundation.Database.DB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.*;

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
    @FXML private BorderPane initChoicePane;
    @FXML private BorderPane managementPane;
    @FXML private BorderPane statisticsPane;
    @FXML private Button showAssistantButton;
    @FXML private Button showManagementButton;
    @FXML private Button showStatsButton;
    @FXML private TilePane editItemsPane;
    @FXML private VBox currentItemsBox;
    @FXML private Button confirmChanges;
    @FXML private VBox servicesVbox;
    @FXML private VBox assignDriverVbox;
    @FXML private PieChart chart;
    @FXML private ChoiceBox driverChoiceBox;
    @FXML private ChoiceBox routeChoiceBox;

    private Order viewingOrder;
    private int buttonWidth = 50;
    private int buttonHeight = 30;
    private int inboundOrderID = 2;
    private int ongoingOrderID = 3;
    private int outboundOrderID = 4;
    private Order activeOngoingOrder;
    private Item itemEditing;
    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    Label labelName = new Label("Item Name: ");
    Label labelDuration = new Label("Handling Duration: ");
    Label labelPrice = new Label("Price: ");
    TextField itemName = new TextField();
    TextField itemDuration = new TextField();
    TextField itemPrice = new TextField();

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
        statisticsPane.setCenter(chart);
        chart.setMinSize(600,600);
    }

    @FXML
    private void showPanes(ActionEvent event) {
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        if (showAssistantButton.equals(event.getSource())) {

            initChoicePane.setVisible(false);
            statisticsPane.setVisible(false);
            managementPane.setVisible(false);
            assistantTaskPane.setVisible(true);
        }
        else if (showManagementButton.equals(event.getSource())) {
            initChoicePane.setVisible(false);
            statisticsPane.setVisible(false);
            managementPane.setVisible(true);
            assistantTaskPane.setVisible(false);
            getItems();
        }
        else if (showStatsButton.equals(event.getSource())) {
            addDeliveryPointStatistics();
            primaryStage.setWidth(1200);
            primaryStage.setHeight(900);
            initChoicePane.setVisible(false);
            statisticsPane.setVisible(true);
            managementPane.setVisible(false);
            assistantTaskPane.setVisible(false);
        }

    }

    public void showManagerMenu( ) {
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        initChoicePane.setVisible(true);
        statisticsPane.setVisible(false);
        managementPane.setVisible(false);
        assistantTaskPane.setVisible(false);
    }

    public void getItems( ) {
        servicesVbox.setVisible(true);
        assignDriverVbox.setVisible(false);
        currentItemsBox.getChildren().clear();
        editItemsPane.getChildren().clear();
        confirmChanges.setVisible(false);
        ObservableList<Item> items = ItemsManager.getItems();
        for (Item item : items) {
            Button itemButton = new Button(item.getName());
            itemButton.prefHeight(30);
            itemButton.setMinWidth(100);
            currentItemsBox.getChildren().add(itemButton);

            itemButton.setOnAction(e -> {
                confirmChanges.setOnAction(actionEvent->{
                updateItems();
                });
                confirmChanges.setVisible(true);
                itemEditing = item;
                editItemsPane.getChildren().clear();
                labelName.setPrefWidth(120);
                itemName.setText(item.getName());
                itemName.setStyle("-fx-background-color: white");
                itemName.setPrefWidth(50);
                labelDuration.setPrefWidth(120);
                itemDuration.setText(String.valueOf(item.getHandlingDuration()));
                itemDuration.setPrefWidth(50);
                itemDuration.setStyle("-fx-background-color: white");
                labelPrice.setPrefWidth(120);
                itemPrice.setText(String.valueOf(item.getPrice()));
                itemPrice.setPrefWidth(50);
                itemPrice.setStyle("-fx-background-color: white");
                editItemsPane.getChildren().add(labelName);
                editItemsPane.getChildren().add(itemName);
                editItemsPane.getChildren().add(labelDuration);
                editItemsPane.getChildren().add(itemDuration);
                editItemsPane.getChildren().add(labelPrice);
                editItemsPane.getChildren().add(itemPrice);
            });
        }
        Button newItemButton = new Button("add new service");
        newItemButton.setStyle("-fx-background-color: #93C553");
        newItemButton.prefHeight(30);
        newItemButton.setMinWidth(100);
        currentItemsBox.getChildren().add(newItemButton);
        newItemButton.setOnAction(e->{
            confirmChanges.setOnAction(actionEvent->{
            addItem();
            });
                confirmChanges.setVisible(true);
                editItemsPane.getChildren().clear();
                labelName.setPrefWidth(120);
                itemName.setPromptText("Name of service");
                itemName.clear();
                itemName.setStyle("-fx-background-color: white");
                itemName.setPrefWidth(50);
                labelDuration.setPrefWidth(120);
                itemDuration.setPromptText("Handling Duration");
                itemDuration.clear();
                itemDuration.setPrefWidth(50);
                itemDuration.setStyle("-fx-background-color: white");
                labelPrice.setPrefWidth(120);
                itemPrice.setPromptText("Price for service");
                itemPrice.setPrefWidth(50);
                itemPrice.clear();
                itemPrice.setStyle("-fx-background-color: white");
                editItemsPane.getChildren().add(labelName);
                editItemsPane.getChildren().add(itemName);
                editItemsPane.getChildren().add(labelDuration);
                editItemsPane.getChildren().add(itemDuration);
                editItemsPane.getChildren().add(labelPrice);
                editItemsPane.getChildren().add(itemPrice);
            });
    }

    public void addItem(){
        double newPrice = Double.parseDouble(itemPrice.getText());
        int newDuration = Integer.parseInt(itemDuration.getText());
        String newName = itemName.getText();
        ItemsManager.addNewItem(newName,newPrice,newDuration);
        getItems();
    }
    public void updateItems(){
        double newPrice = Double.parseDouble(itemPrice.getText());
        int newDuration = Integer.parseInt(itemDuration.getText());
        String newName = itemName.getText();
        int itemID = itemEditing.getLaundryItemID();
        ItemsManager.updateItem(itemID,newName,newPrice,newDuration);
        getItems();
    }

    public void getDriversAndRoutes(){
        assignDriverVbox.setVisible(true);
        servicesVbox.setVisible(false);
       ObservableList<Driver> drivers = DriverHandler.getDrivers();
        for (Driver driver: drivers) {
            driverChoiceBox.getItems().add(driver.getCorporateIDNO());
        }
        ObservableList<Route> routes = RouteHandler.getRoutes();
        for (Route route: routes){
            routeChoiceBox.getItems().add(route.getRouteID());
        }
    }

    public void confirmRouteAssignment(){
    RouteHandler.assignRoute((Integer) routeChoiceBox.getValue(), (Integer) driverChoiceBox.getValue());
    routeChoiceBox.getItems().clear();
    driverChoiceBox.getItems().clear();
    getDriversAndRoutes();
    }

    public void addDeliveryPointStatistics(){
        chart.getData().clear();
        chart.setTitle("Delivery Point Share of orders");
        ObservableList<DeliveryPoint> deliveryPoints = DeliveryPointManager.getDeliveryPoints();
        for (DeliveryPoint deliveryPoint: deliveryPoints) {

            int orderAmount = OrderManager.getOrderByDeliveryPoint(Integer.parseInt(deliveryPoint.getID()));
            pieChartData.add(new PieChart.Data(deliveryPoint.getName()+": "+orderAmount,orderAmount));


        }
        chart.setData(pieChartData);
    }

    public void addDurationStatistics(){
        chart.getData().clear();
        chart.setTitle("Expected vs Real Average Handling Days");
        ObservableList<Item> laundryItems = ItemsManager.getItems();
        for (Item laundryItem: laundryItems) {
            int laundryAmount = ItemsManager.getItemCount(laundryItem.getLaundryItemID());
            int expectedDuration = laundryItem.getHandlingDuration();
            double actualDuration = ItemsManager.getActualAverageHandlingDuration(laundryItem.getLaundryItemID());
            PieChart.Data data = new PieChart.Data(laundryItem.getName()+" Expected: "+expectedDuration+ " Actual: "+actualDuration,actualDuration);
            pieChartData.add(data);
            if (expectedDuration>=actualDuration){
                data.getNode().setStyle("-fx-pie-color: #93C553");
            }
            else {
                data.getNode().setStyle("-fx-pie-color: #FB323C");
            }
        }
        chart.setData(pieChartData);
    }

    public void addLaundryItemStatistics(){
        chart.getData().clear();
        chart.setTitle("Laundry Item Share of orders");
        ObservableList<Item> laundryItems = ItemsManager.getItems();
        for (Item laundryItem: laundryItems) {
            int laundryAmount = ItemsManager.getItemCount(laundryItem.getLaundryItemID());
            pieChartData.add(new PieChart.Data(laundryItem.getName()+": "+laundryAmount,laundryAmount));
        }
        chart.setData(pieChartData);
    }
}
