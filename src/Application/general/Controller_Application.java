package Application.general;

import Domain.Managers.AccountManager;
import Domain.Enums.Emulator;
import Services.Resizer.ResizeHelper;
import Services.Themes.ThemeControl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class Controller_Application {
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
    @FXML public Label emulationType;


    @FXML
    private void emulateAs(ActionEvent actionEvent) {
        clearFields((Pane)userName.getParent());
        if (actionEvent.getSource() == emulateAsCostumer) {
            changeScene(logInSceneCostumer);
            currentEmulator = Emulator.Costumer;

        }
        else if (actionEvent.getSource() == emulateAsDeliveryPoint) {
            changeScene(logInSceneDeliveryPoint);
            currentEmulator = Emulator.DeliveryPoint;
        }
        else if (actionEvent.getSource() == emulateAsDriver) {
            changeScene(logInSceneDriver);
            currentEmulator = Emulator.Driver;
        }
        else if (actionEvent.getSource() == emulateAsLaundryCentral) {
            changeScene(logInSceneLaundryCentral);
            currentEmulator = Emulator.LaundryCentral;
        }
}
    // Fields
    private static boolean isFullScreen;
    public static Scene currentScene;
    public static Stage primaryStage;
    public static Emulator currentEmulator = null;

    //Scenes
    public static Scene logInSceneCostumer;
    public static Scene logInSceneLaundryCentral;
    public static Scene logInSceneDeliveryPoint;
    public static Scene logInSceneDriver;
    public static Scene registerScene;
    public static Scene costumerScene;
    public static Scene deliveryPointScene;
    public static Scene laundryScene;
    public static Scene driverScene;

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
        }
        else {
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

    public void logIn() {
        String pass_word = passWord.getText();
        String user_name = userName.getText();

        if (AccountManager.logIn(user_name, pass_word)) {
            switch (Controller_Application.currentEmulator) {
                case Driver:
                    currentScene = driverScene;
                    changeScene(driverScene);
                    break;
                case LaundryCentral:
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

        }
        else {
            userName.setText("Incorrect Credentials");
            passWord.setText("Incorrect Credentials");
        }
    }


    public void changeScene() {
        changeScene(Controller_Application.registerScene);
        clearFields((Pane) userName.getParent());
    }

}
