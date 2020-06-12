package Application.LaundryCentral;

import Domain.DeliveryPoint.DeliveryPoint;
import Domain.Driver.Driver;
import Domain.Enums.Role;
import Domain.LaundryItems.LaundryItem;
import Domain.Order.Order;
import Domain.Route.Route;
import Foundation.Database.DB;
import Services.Handlers.*;
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
    @FXML private ChoiceBox<Integer> driverChoiceBox;
    @FXML private ChoiceBox<Integer> routeChoiceBox;

    private Order viewingOrder;
    private int buttonWidth = 50;
    private int buttonHeight = 30;
    private int inboundOrderID = 2;
    private int ongoingOrderID = 3;
    private int outboundOrderID = 4;
    private Order activeOngoingOrder;
    private LaundryItem itemEditing;
    private ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private Label labelName = new Label("Item Name: ");
    private Label labelDuration = new Label("Handling Duration: ");
    private Label labelPrice = new Label("Price: ");
    private TextField itemName = new TextField();
    private TextField itemDuration = new TextField();
    private TextField itemPrice = new TextField();

    /**
     * initialize the LaundryManager fxml/controller
     *
     * @param url            - standard param of init
     * @param resourceBundle - standard param of init
     */
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
            OrderHandler.updateOrderDB(activeOngoingOrder);
            initOngoingList();
        });
        statisticsPane.setCenter(chart);
        chart.setMinSize(600, 600);
    }

    /**
     * handles what pane to show and which to hide depending on actionEvent from a button
     *
     * @param event
     */
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

    /**
     * shows the main menu for the manager
     */
    public void showManagerMenu( ) {
        primaryStage.setHeight(600);
        primaryStage.setWidth(600);
        initChoicePane.setVisible(true);
        statisticsPane.setVisible(false);
        managementPane.setVisible(false);
        assistantTaskPane.setVisible(false);
    }

    /**
     * get laundry items and display them as buttons.
     * also adds action events when the button clicked will be able to edit that item
     */
    public void getItems( ) {
        servicesVbox.setVisible(true);
        assignDriverVbox.setVisible(false);
        currentItemsBox.getChildren().clear();
        editItemsPane.getChildren().clear();
        confirmChanges.setVisible(false);
        ObservableList<LaundryItem> items = ItemsHandler.getItems();
        for (LaundryItem item : items) {
            Button itemButton = new Button(item.getName());
            itemButton.prefHeight(30);
            itemButton.setMinWidth(100);
            currentItemsBox.getChildren().add(itemButton);

            itemButton.setOnAction(e -> {
                confirmChanges.setOnAction(actionEvent -> {
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
        newItemButton.setOnAction(e -> {
            confirmChanges.setOnAction(actionEvent -> {
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

    /**
     * used to add item type to the DB
     */
    public void addItem( ) {
        double newPrice = Double.parseDouble(itemPrice.getText());
        int newDuration = Integer.parseInt(itemDuration.getText());
        String newName = itemName.getText();
        ItemsHandler.addNewItem(newName, newPrice, newDuration);
        getItems();
    }

    /**
     * used to update items in the DB
     */
    private void updateItems( ) {
        double newPrice = Double.parseDouble(itemPrice.getText());
        int newDuration = Integer.parseInt(itemDuration.getText());
        String newName = itemName.getText();
        int itemID = itemEditing.getLaundryItemID();
        ItemsHandler.updateItem(itemID, newName, newPrice, newDuration);
        getItems();
    }

    /**
     * gets unassigned drivers and unassigned routes, puts them in Choice boxes that can be picked
     */
    public void getDriversAndRoutes( ) {
        routeChoiceBox.getItems().clear();
        driverChoiceBox.getItems().clear();
        assignDriverVbox.setVisible(true);
        servicesVbox.setVisible(false);
        ObservableList<Driver> drivers = DriverHandler.getDrivers();
        for (Driver driver : drivers) {
            driverChoiceBox.getItems().add(driver.getCorporateIDNO());
        }
        ObservableList<Route> routes = RouteHandler.getRoutes();
        for (Route route : routes) {
            routeChoiceBox.getItems().add(route.getRouteID());
        }
    }

    /*
    confirms the choiceboxes and assigns the route to the driver in DB.
    will then renew the pane and the assigned route + driver is no longer found in choiceboxes
     */
    public void confirmRouteAssignment( ) {
        RouteHandler.assignRoute(routeChoiceBox.getValue(), driverChoiceBox.getValue());
        routeChoiceBox.getItems().clear();
        driverChoiceBox.getItems().clear();
        getDriversAndRoutes();
    }

    /*
    adds statistics for share of delivery point orders to the chart
     */
    public void addDeliveryPointStatistics( ) {
        chart.getData().clear();
        chart.setTitle("Delivery Point Share of orders");
        ObservableList<DeliveryPoint> deliveryPoints = DeliveryPointHandler.getDeliveryPoints();
        for (DeliveryPoint deliveryPoint : deliveryPoints) {
            int orderAmount = OrderHandler.getOrderByDeliveryPoint(Integer.parseInt(deliveryPoint.getID()));
            pieChartData.add(new PieChart.Data(deliveryPoint.getName() + ": " + orderAmount, orderAmount));
        }
        chart.setData(pieChartData);
    }

    /*
    adds duration statistics to the chart,
    this will display avg. actual handling duration in days for each type of laundry, versus the expected time.
    if above, it is displayed red, if below or equal it is green. to indicate the manager of handling time issues
     */
    public void addDurationStatistics( ) {
        chart.getData().clear();
        chart.setTitle("Expected vs Real Average Handling Days");
        ObservableList<LaundryItem> laundryItems = ItemsHandler.getItems();
        for (LaundryItem laundryItem : laundryItems) {
            int laundryAmount = ItemsHandler.getItemCount(laundryItem.getLaundryItemID());
            int expectedDuration = laundryItem.getHandlingDuration();
            double actualDuration = ItemsHandler.getActualAverageHandlingDuration(laundryItem.getLaundryItemID());
            PieChart.Data data = new PieChart.Data(
                    laundryItem.getName() + " Expected: " + expectedDuration + " Actual: " + actualDuration,
                    actualDuration);
            pieChartData.add(data);
            if (expectedDuration >= actualDuration) {
                data.getNode().setStyle("-fx-pie-color: #93C553");
            }
            else {
                data.getNode().setStyle("-fx-pie-color: #FB323C");
            }
        }
        chart.setData(pieChartData);
    }

    /*
    adds laundry item share on orders to the chart.
     */
    public void addLaundryItemStatistics( ) {
        chart.getData().clear();
        chart.setTitle("Laundry Item Share of orders");
        ObservableList<LaundryItem> laundryItems = ItemsHandler.getItems();
        for (LaundryItem laundryItem : laundryItems) {
            int laundryAmount = ItemsHandler.getItemCount(laundryItem.getLaundryItemID());
            pieChartData.add(new PieChart.Data(laundryItem.getName() + ": " + laundryAmount, laundryAmount));
        }
        chart.setData(pieChartData);
    }

    /**
     * will display a chart of age distribution of the registered customers.
     */
    public void addCustomerAgeStatistics( ) {
        chart.getData().clear();
        chart.setTitle("Age distribution of registered customers");
        int age0To15 = 0;
        int age16To30 = 0;
        int age31To45 = 0;
        int age46To60 = 0;
        int age61To100 = 0;
        ObservableList<Integer> customerIDs = CustomerHandler.getCostumerID();
        ArrayList<Integer> customerAges = new ArrayList<>();
        for (Integer customerID : customerIDs) {
            int customerAge = CustomerHandler.getAge(customerID);
            customerAges.add(customerAge);
        }
        for (Integer age : customerAges) {
            if (age > 0 && age < 16) {
                age0To15++;
            }
            else if (age > 15 && age < 31) {
                age16To30++;
            }
            else if (age > 31 && age < 46) {
                age31To45++;
            }
            else if (age > 45 && age < 61) {
                age46To60++;
            }
            else if (age > 60 && age < 101) {
                age61To100++;
            }
        }
        pieChartData.add(new PieChart.Data("Age 0 to 15: " + age0To15, age0To15));
        pieChartData.add(new PieChart.Data("Age 16 to 30: " + age16To30, age16To30));
        pieChartData.add(new PieChart.Data("Age 31 to 45: " + age31To45, age31To45));
        pieChartData.add(new PieChart.Data("Age 46 to 60: " + age46To60, age46To60));
        pieChartData.add(new PieChart.Data("Age 61 to 100: " + age61To100, age61To100));
    }
}
