package Application.DeliveryPoint;

import Application.general.Controller_Application;
import Domain.LaundryItems.LaundryItem;
import Domain.Managers.AccountManager;
import Domain.Managers.CustomerHandler;
import Domain.Managers.ItemsManager;
import Domain.Managers.OrderManager;
import Domain.Order.Order;
import Domain.Order.OrderItem;
import UI.Costumer.ItemBox;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Controller_DeliveryPoint extends Controller_Application implements Initializable {
    @FXML
    private VBox startPane;

    @FXML
    private Label orderMsg;

    @FXML
    private TextField phoneNumberSearch;

    @FXML
    private VBox orderPane;

    @FXML
    private VBox tempCustomerPane;

    @FXML
    private Label firstNameLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneNumberLabel;

    @FXML
    private VBox orderListPane;

    @FXML
    private VBox orderItemsPane;

    @FXML
    private VBox laundryItemsPane;

    @FXML
    private TextField firstNameTF;

    @FXML
    private TextField lastNameTF;

    @FXML
    private TextField emailTF;

    @FXML
    private TextField phoneNumberTF;
    @FXML
    private VBox registerCustomer;
    @FXML
    private Label errorMessage;


    private ArrayList<LaundryItem> removedItems = new ArrayList<>();
    private ObservableList<LaundryItem> addedLaundryItems = FXCollections.observableArrayList();
    private Order currentOrder;
    private int currentCostumerID;
    private boolean isEditing =false;
    private final int READY_FOR_TRANSIT_ORDER_STATUS = 1;
    private final int READY_FOR_PICKUP_ORDER_STATUS = 6;
    private final int COMPLETED_ORDER_STATUS = 7;
    private final String NEW_ORDER = "New Order";
    private final String NO_ORDER = "No Orders ready";
    private final String PHONE_REGEX = "^[0-9]{8}$";
    private final String EMAIL_REGEX ="(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPane.toFront();
    }

    public void searchOnPhoneNumber() {
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Matcher matcher = pattern.matcher(phoneNumberSearch.getText());
        if (matcher.matches()) {
            phoneNumberSearch.setStyle("-fx-border-color: green");

            try {
                currentCostumerID = CustomerHandler.getCustomerIDByPhoneNumber(Integer.parseInt(phoneNumberSearch.getText()));
                if (currentCostumerID == 0) {
                    throw new InputMismatchException();
                }
                setOrderListPane();
                phoneNumberSearch.setStyle("-fx-border-color: Transparent");
                showItems();
                phoneNumberSearch.clear();
            } catch (InputMismatchException e) {
                e.printStackTrace();
                wrongNumber();
            }
        } else {
            wrongNumber();
        }
    }

    public void showCustomerRegister() {
        registerCustomer.toFront();
    }

    public void makeNewCustomer() {

        if (isEditing==false && checkIfFilledOut()) {
            currentCostumerID = CustomerHandler.register(firstNameTF.getText(), lastNameTF.getText(),
                    emailTF.getText(), phoneNumberTF.getText());
            setCustomerLabels();
            orderPane.toFront();
            tempCustomerPane.toFront();
            showItems();
        } else if (isEditing && checkIfFilledOut()) {
            CustomerHandler.updateCostumer(firstNameTF.getText(), lastNameTF.getText(),
                    emailTF.getText(), phoneNumberTF.getText(),currentCostumerID);
            setCustomerLabels();
            orderPane.toFront();
            tempCustomerPane.toFront();
            showItems();
        } else {
            showInvisibleLabel(errorMessage);
            clearCustomerTextfields();
        }
    }
    public void editCustomer(){
        registerCustomer.toFront();
        isEditing = true;
    }
    public void confirmOrder() {
        if (hasMultipleDeliveryDays()) {
            splitOrder();
        } else {
            if (!addedLaundryItems.isEmpty() && currentOrder.getID() == 0) {
                OrderManager.createOrder(currentOrder.getCustomerID(), READY_FOR_TRANSIT_ORDER_STATUS, currentOrder.getOrderItems(), currentOrder.getStartDate(), AccountManager.currentDeliveryPointID);
            } else {
                OrderManager.createOrderItems(addedLaundryItems, currentOrder.getID());
            }
            if (!removedItems.isEmpty()) {
                for (LaundryItem laundryItem : removedItems
                ) {
                    OrderManager.deleteOrderItems(laundryItem.getOrderItemID());
                }
            }
        }
        clearPanes();
        startPane.toFront();
    }


    public void addNewOrder() {

        if (canMakeNewItem()) {
            Label label = new Label(NEW_ORDER);
            currentOrder = new Order(
                    LocalDateTime.now(),
                    READY_FOR_TRANSIT_ORDER_STATUS,
                    AccountManager.currentDeliveryPointID,
                    currentCostumerID
            );
            orderListPane.getChildren().add(label);
        }
    }
    public void returnToStart(){
        startPane.toFront();
    }

    private void setOrderListPane() {
        ArrayList<Order> orders = new ArrayList<>();
        orders = OrderManager.getCustomerOrders(Integer.parseInt(phoneNumberSearch.getText()));
        currentCostumerID = CustomerHandler.getCustomerIDByPhoneNumber(Integer.parseInt(phoneNumberSearch.getText()));
        orderPane.toFront();
        for (Order order : orders
        ) {
            Label label = new Label(((String.valueOf(order.getID()))));
            label.setOnMouseClicked(mouseEvent -> {
                orderItemsPane.getChildren().clear();
                addOrderItems(order.getID());
                currentOrder = order;
            });
            if (order.getStatusID() == READY_FOR_PICKUP_ORDER_STATUS) {
                HBox hBox = new HBox();
                hBox.setSpacing(20);
                Button button = new Button("Finish order");
                button.setOnMouseClicked(mouseEvent -> {
                    order.setStatus(COMPLETED_ORDER_STATUS);
                    order.setEndDate(LocalDateTime.now());
                    OrderManager.updateOrderDB(order);
                    orderListPane.getChildren().remove(hBox);
                    orderItemsPane.getChildren().clear();
                });
                hBox.getChildren().addAll(label, button);
                orderListPane.getChildren().add(hBox);
            } else if (order.getStatusID() == 8) {
                orderListPane.getChildren().add(label);
            }

        }
        if (orderListPane.getChildren().isEmpty()) {
            Label noOrders = new Label(NO_ORDER);
            orderListPane.getChildren().add(noOrders);
        }
    }

    private void showItems() {
        for (LaundryItem laundryItem : ItemsManager.getItems()
        ) {
            ItemBox itemBox = new ItemBox(laundryItem);
            itemBox.setAddButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                if (currentOrder == null) {
                    addNewOrder();
                }
                addedLaundryItems.add(laundryItem);
                addOrderItemFromLaundryItem(laundryItem);
                updateOrderPaneU(laundryItem);
            });
            laundryItemsPane.getChildren().add(itemBox);
        }
    }

    private void addOrderItems(int orderID) {
        for (LaundryItem laundryItem : ItemsManager.getorderLaundryItems(orderID)
        ) {
            ItemBox itemBox = new ItemBox(laundryItem);
            itemBox.setRemoveButton();
            itemBox.getButton().setOnMouseClicked(mouseEvent -> {
                removedItems.add(laundryItem);
                orderItemsPane.getChildren().remove(itemBox);

            });
            orderItemsPane.getChildren().add(itemBox);
        }
    }

    private void updateOrderPaneU(LaundryItem laundryItem) {

        ItemBox itemBox = new ItemBox(laundryItem);
        itemBox.setRemoveButton();
        itemBox.getButton().setOnMouseClicked(mouseEvent -> {
            addedLaundryItems.remove(laundryItem);
            orderItemsPane.getChildren().remove(itemBox);
        });
        orderItemsPane.getChildren().add(itemBox);
    }

    private void clearPanes() {
        orderListPane.getChildren().clear();
        orderItemsPane.getChildren().clear();
        laundryItemsPane.getChildren().clear();
    }

    private boolean hasMultipleDeliveryDays() {
        final int DAY_OF_WEEK = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        boolean alreadyHasDeliveryDay = false;
        boolean[] deliveryDays = new boolean[4];
        int deliveryDay;
        for (OrderItem item : currentOrder.getOrderItems()
        ) {
            deliveryDay = DAY_OF_WEEK + item.getLaundryItem().getHandlingDuration();

            if (deliveryDay <= 4) {
                deliveryDays[0] = true;
                item.setDeliveryDay(4);
            } else if (deliveryDay <= 9) {
                deliveryDays[1] = true;
                item.setDeliveryDay(9);
            } else if (deliveryDay <= 11) {
                deliveryDays[2] = true;
                item.setDeliveryDay(11);
            } else if (deliveryDay <= 16) {
                deliveryDays[3] = true;
                item.setDeliveryDay(16);
            } else {
                System.out.println("More than 2 weeks delivery");
            }

        }
        for (Boolean hasDeliveryDay : deliveryDays
        ) {
            if (hasDeliveryDay) {
                if (alreadyHasDeliveryDay) {

                    return true;
                } else {
                    alreadyHasDeliveryDay = true;
                }
            }
        }
        return false;
    }

    private void splitOrder() {
        ArrayList<OrderItem> orderItems;
        for (int i = 0; i < 4; i++) {
            orderItems = new ArrayList<>();
            for (OrderItem orderItem : currentOrder.getOrderItems()
            ) {
                switch (i) {
                    case 0:
                        if (orderItem.getDeliveryDay() == 4) {
                            orderItems.add(orderItem);
                        }
                        break;
                    case 1:
                        if (orderItem.getDeliveryDay() == 9) {
                            orderItems.add(orderItem);
                        }
                        break;
                    case 2:
                        if (orderItem.getDeliveryDay() == 11) {
                            orderItems.add(orderItem);
                        }
                        break;
                    case 3:
                        if (orderItem.getDeliveryDay() == 16) {
                            orderItems.add(orderItem);
                        }
                        break;
                }

            }
            if (!orderItems.isEmpty()) {
                OrderManager.createOrder(currentOrder.getCustomerID(), READY_FOR_TRANSIT_ORDER_STATUS, orderItems, currentOrder.getStartDate(), AccountManager.currentDeliveryPointID);
                showInvisibleLabel(orderMsg);
            }
        }
        if (currentOrder.getID() != 0) {
            OrderManager.deleteOrder(currentOrder.getID());
        }
    }

    private void addOrderItemFromLaundryItem(LaundryItem laundryItem) {
        currentOrder.getOrderItems().add(new OrderItem(false, currentOrder.getID(), laundryItem.getLaundryItemID()));
    }

    private void wrongNumber() {
        phoneNumberSearch.clear();
        phoneNumberSearch.setStyle("-fx-border-color: red");
        phoneNumberSearch.setPromptText("Wrong PhoneNumber");
    }

    private boolean canMakeNewItem() {
        if (orderListPane.getChildren().isEmpty()) {
            return true;
        } else if (orderListPane.getChildren().get(orderListPane.getChildren().size() - 1).toString().contains(NEW_ORDER)) {
            return false;
        } else {
            if (orderListPane.getChildren().get(0).toString().contains(NO_ORDER)) {
                orderListPane.getChildren().clear();
            }
            return true;
        }
    }

    private void showInvisibleLabel(Label label) {
        final Timeline timer = new Timeline();
        KeyFrame showMsg = new KeyFrame(Duration.seconds(0), actionEvent -> {
            label.setVisible(true);
        });
        KeyFrame hideMsg = new KeyFrame(Duration.seconds(4), actionEvent -> {
            label.setVisible(false);
        });
        timer.getKeyFrames().addAll(showMsg, hideMsg);
        timer.play();
    }

    private void setCustomerLabels() {
        firstNameLabel.setText("First name: " + firstNameTF.getText());
        lastNameLabel.setText("Last name: " + lastNameTF.getText());
        emailLabel.setText("Email: " + emailTF.getText());
        phoneNumberLabel.setText("Phone number: " + phoneNumberTF.getText());
    }

    private void clearCustomerTextfields() {
        firstNameTF.clear();
        lastNameTF.clear();
        emailTF.clear();
        phoneNumberTF.clear();
    }

    private boolean checkIfFilledOut(){
        Pattern pattern = Pattern.compile(PHONE_REGEX);
        Pattern emailPattern = Pattern.compile(EMAIL_REGEX);
        Matcher phoneMatcher = pattern.matcher(phoneNumberTF.getText());
        Matcher emailMatcher = emailPattern.matcher(emailTF.getText());

        if (emailMatcher.matches() && phoneMatcher.matches() && firstNameTF.getText() != null && lastNameTF.getText() != null
                && emailTF.getText() != null && phoneNumberTF.getText() != null){
            return true;
        } else {
            return false;
        }
    }

}
