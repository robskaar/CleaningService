package Application.Costumer;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Domain.LaundryItems.Item;
import Domain.Managers.AccountManager;
import Domain.Managers.ItemsManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Foundation.Database.DB;
import UI.Costumer.ItemBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class Controller_Costumer extends Controller_Application implements Initializable {
    @FXML
    private HBox toolBar;

    @FXML
    private MenuButton settingsBtn;

    @FXML
    private MenuItem defaultThemeBtn;

    @FXML
    private MenuItem darkThemeBtn;

    @FXML
    private Button closeBtn;

    @FXML
    private Button minimizeBtn;

    @FXML
    private Button maximizeBtn;

    @FXML
    private VBox costumerMenu;

    @FXML
    private Label nameLabel;

    @FXML
    private VBox itemView;

    @FXML
    private VBox itemVbox;

    @FXML
    private Button addOrderButton;

    @FXML
    private VBox orderHistoryPane;

    @FXML
    private VBox onGoingOrders;

    @FXML
    private VBox onGoingOrderIDPane;

    @FXML
    private VBox onGoingStatusPane;

    @FXML
    private VBox previousOrders;

    @FXML
    private VBox previousOrderOrderID;

    @FXML
    private VBox previousOrderStatus;

    @FXML
    private VBox confirmOrderPane;

    @FXML
    private VBox orderVBox;

    @Override
    public void changeScene() {

    }

    private ObservableList<Item> addedItems = FXCollections.observableArrayList();
    private IntegerBinding listSize = Bindings.size(addedItems);
    private BooleanBinding listPopulated = listSize.greaterThan(0);
    private ArrayList<ItemBox> itemBoxes = new ArrayList<>();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        costumerMenu.toFront();
        addOrderButton.disableProperty().bind(listPopulated.not());
        addOrderButton.textProperty().bind(Bindings.convert(listSize).concat(" Items"));

    }

    public void showItems() {
        itemView.toFront();
        System.out.println(itemBoxes.size());

        if (itemBoxes.size() == 0) {
            for (Item item : ItemsManager.getItems()
            ) {
                System.out.println(item.getName());
                ItemBox itemBox = new ItemBox(item);
                itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                    addedItems.add(item);

                });
                itemBoxes.add(itemBox);

            }


        }
    }

    public void showCart() {
        confirmOrderPane.toFront();
        for (Item item : addedItems
        ) {
            ItemBox itemBox = new ItemBox(item);
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                addedItems.remove(item);
                orderVBox.getChildren().remove(itemBox);
            });
            orderVBox.getChildren().add(itemBox);
        }
    }

    public void logOff() {
        addedItems.clear();
        super.logOff();
    }

    public void goBack() {
        orderVBox.getChildren().clear();
        costumerMenu.toFront();
    }

    public void showOrderHistory() {
        orderHistoryPane.toFront();
        clearOrderHistory();
        for (Order order : OrderManager.getCustomerOrders(AccountManager.getCurrentUser())
        ) {
            Label orderID = new Label(String.valueOf(order.getID()));
            Label status = new Label(order.getStatus());

            switch (order.getStatusID()) {
                case 7:
                    previousOrderStatus.getChildren().add(status);
                    previousOrderOrderID.getChildren().add(orderID);
                    break;
                case 8:
                    status.setOnMouseClicked(mouseEvent -> {
                        //TODO make Edit order Menu
                    });
                    onGoingOrderIDPane.getChildren().add(orderID);
                    onGoingStatusPane.getChildren().add(status);
                    break;
                default:
                    onGoingOrderIDPane.getChildren().add(orderID);
                    onGoingStatusPane.getChildren().add(status);
                    break;
            }
        }
    }

    private void clearOrderHistory() {
        previousOrderOrderID.getChildren().clear();
        previousOrderStatus.getChildren().clear();
        onGoingStatusPane.getChildren().clear();
        onGoingOrderIDPane.getChildren().clear();
    }
}
