package Application.general;

import Domain.Managers.AccountManager;
import Domain.Enums.Emulator;
import Services.Resizer.ResizeHelper;
import Services.Themes.ThemeControl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

 public class Controller_Application implements Initializable {
    @FXML Button closeBtn;
    @FXML Button maximizeBtn;
    @FXML Button minimizeBtn;
    @FXML private Menu emulatorMenu;
    @FXML private MenuItem emulateAsCostumer;
    @FXML private MenuItem emulateAsDriver;
    @FXML private MenuItem emulateAsDeliveryPoint;
    @FXML private MenuItem emulateAsLaundryCentral;
    @FXML public Button registerButton;
    @FXML private TextField userName;
    @FXML private PasswordField passWord;
    @FXML private Label emulationType;
    // Fields
    private static boolean isFullScreen;
    public static FXMLLoader fxmlLoader;
    public static Parent parent;
    public static Scene currentScene;
    public static Stage primaryStage;
    public static Emulator currentEmulator = null;

    //Scenes
    public static Scene logInScene;
    public static Scene registerScene;
    public static Scene costumerScene;
    public static Scene deliveryPointScene;
    public static Scene laundryScene;
    public static Scene driverScene;
    public static Scene defaultScene;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        passWord.setOnAction(e -> {
//            logIn();
//        });
    }
    public void changeThemeDark() {
        ThemeControl.currentTheme = ThemeControl.DARK;
        currentScene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        changeScene(currentScene);
    }

    public void changeThemeDefault() {
        ThemeControl.currentTheme = ThemeControl.DEFAULT;
        currentScene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        changeScene(currentScene);
    }

    protected void clearFields(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof TextField) {
                // clear the textfield
                ((TextField) node).setText("");
            }
            if (node instanceof DatePicker) {
                // clear the datePickers
                ((DatePicker) node).setValue(null);
            }
        }
    }



    protected static void changeScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(isFullScreen);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        Controller_Application.currentScene = scene;
        ResizeHelper.addResizeListener(primaryStage);
    }

    public void closeWindow() {
        Platform.exit();
    }

    public void minimizeWindow() {
        Stage primaryStage = getPrimaryStage();
        if (primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
            isFullScreen = false;
        } else {
            primaryStage.setIconified(true);
        }
    }

    public void maximizeWindow() {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setFullScreen(true);
        isFullScreen = true;
    }

    /**
     * gets the stage
     *
     * @return returns it to the calling method - ex. scene changer
     */
    private static Stage getPrimaryStage() {
        return primaryStage;
    }

    public void emulateAs(ActionEvent actionEvent) {
        if (actionEvent.getSource() == emulateAsCostumer) {
            Controller_Application.currentEmulator = Emulator.Costumer;
            registerButton.setDisable(false);
            emulationType.setText(" Costumer");
        }
        else {
            if (actionEvent.getSource() == emulateAsDeliveryPoint) {
                Controller_Application.currentEmulator = Emulator.DeliveryPoint;
                emulationType.setText(" Delivery Point");
            }
            else if (actionEvent.getSource() == emulateAsDriver) {
                Controller_Application.currentEmulator = Emulator.Driver;
                emulationType.setText(" Driver");
            }
            else if (actionEvent.getSource() == emulateAsLaundryCentral) {
                Controller_Application.currentEmulator = Emulator.LaundryCentral;
                emulationType.setText(" Laundry Central");
            }
            registerButton.setDisable(true);
        }
    }



    public void logIn() {
        String pass_word = passWord.getText();
        String user_name = userName.getText();

        //if (AccountManager.logIn(user_name, pass_word)) {
            switch (Controller_Application.currentEmulator) {
                case Driver:
                    System.out.println("driver");
                    currentScene = driverScene;
                    changeScene(driverScene);
                    break;
                case LaundryCentral:
                    System.out.println("lcentral");
                    currentScene = laundryScene;
                    changeScene(laundryScene);
                    break;
                case Costumer:
                    currentScene = costumerScene;
                    changeScene(costumerScene);
                    break;
                case DeliveryPoint:
                    currentScene = deliveryPointScene;
                    changeScene(deliveryPointScene);
                    break;
            }
            userName.setText(null);
            passWord.setText(null);

        //}
        /*
        else {
            userName.setText("Incorrect Credentials");
            passWord.setText("Incorrect Credentials");
        }

         */


    }


    public void changeScene() {
        changeScene(Controller_Application.registerScene);
        clearFields((Pane) userName.getParent());
    }
}
