package Application.Costumer;

import Application.general.Controller_Application;
import Domain.LaundryItems.LaundryItems;
import Domain.Managers.AccountManager;
import Domain.Managers.ItemsManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import UI.Costumer.ItemBox;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.SQLException;
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
    @FXML
    private Button deleteButton;

    @FXML
    private Button confirmOrders;


    @Override
    public void changeScene() {

    }

    private ObservableList<LaundryItems> addedLaundryItems = FXCollections.observableArrayList();
    private ArrayList<LaundryItems> removedLaundryItems = new ArrayList<>();
    private IntegerBinding listSize = Bindings.size(addedLaundryItems);
    private BooleanBinding listPopulated = listSize.greaterThan(0);
    private ArrayList<ItemBox> itemBoxes = new ArrayList<>();
    private static final int pendingStatusID = 8;


    public void initialize(URL url, ResourceBundle resourceBundle) {
        costumerMenu.toFront();
        addOrderButton.disableProperty().bind(listPopulated.not());
        addOrderButton.textProperty().bind(Bindings.convert(listSize).concat(" Items"));

    }

    public void showItems() {
        itemView.toFront();

        if (itemBoxes.size() == 0) {
            for (LaundryItems laundryItems : ItemsManager.getItems()
            ) {
                ItemBox itemBox = new ItemBox(laundryItems);
                itemBox.setAddButton();
                itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                    addedLaundryItems.add(laundryItems);

                });
                itemBoxes.add(itemBox);

            }
            itemVbox.getChildren().addAll(itemBoxes);

        }
    }

    public void showOrderItems(int orderID) {
        ;
        confirmOrderPane.toFront();
        orderVBox.getChildren().clear();
        deleteButton.setOnMouseClicked(mouseEvent -> {
            OrderManager.deleteOrder(orderID);
            goBack();
        });

        confirmOrders.setOnMouseClicked(mouseEvent -> {
            deleteOrderItems();
            goBack();
        });

        for (LaundryItems laundryItems : ItemsManager.getorderLaundryItems(orderID)
        ) {
            ItemBox itemBox = new ItemBox(laundryItems);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                removedLaundryItems.add(laundryItems);
                orderVBox.getChildren().remove(itemBox);

            });
            orderVBox.getChildren().add(itemBox);

        }
    }

    public void showCart() {
        confirmOrderPane.toFront();
        deleteButton.setOnMouseClicked(mouseEvent -> {
            addedLaundryItems.clear();
            goBack();
        });
        confirmOrders.setOnMouseClicked(mouseEvent -> {
            try {
                createOrders();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        for (LaundryItems laundryItems : addedLaundryItems
        ) {
            ItemBox itemBox = new ItemBox(laundryItems);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                addedLaundryItems.remove(laundryItems);
                orderVBox.getChildren().remove(itemBox);
            });
            orderVBox.getChildren().add(itemBox);
        }
    }

    public void logOff() {
        addedLaundryItems.clear();
        super.logOff();
    }

    public void goBack() {
        orderVBox.getChildren().clear();
        costumerMenu.toFront();
    }

    public void createOrders() throws SQLException {
        //8 is the StatusID in our database for premade orders
        OrderManager.createOrder(AccountManager.currentCostumerID, pendingStatusID, addedLaundryItems);
        System.out.println("Order added with " + addedLaundryItems.size() + " Items");
        addedLaundryItems.clear();
        goBack();
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
                case pendingStatusID:
                    status.setOnMouseClicked(mouseEvent -> {
                        showOrderItems(order.getID());
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

    private void deleteOrderItems() {
        for (LaundryItems laundryItems : removedLaundryItems
        ) {
            OrderManager.deleteOrderItems(laundryItems.getOrderItemID());
        }
    }
}
