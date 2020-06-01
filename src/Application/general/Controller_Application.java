package Application.general;

import Domain.Enums.Role;
import Domain.Managers.AccountManager;
import Domain.Enums.Emulator;
import Services.Resizer.ResizeHelper;
import Services.Themes.ThemeControl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


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

        if (actionEvent.getSource() == emulateAsCostumer) {
            currentEmulator = Emulator.Costumer;
            fxmlLoginLoader(currentEmulator);
            changeScene(logInSceneCostumer);
        }
        else if (actionEvent.getSource() == emulateAsDeliveryPoint) {
            currentEmulator = Emulator.DeliveryPoint;
            fxmlLoginLoader(currentEmulator);
            changeScene(logInSceneDeliveryPoint);
        }
        else if (actionEvent.getSource() == emulateAsDriver) {
            currentEmulator = Emulator.Driver;
            fxmlLoginLoader(currentEmulator);
            changeScene(logInSceneDriver);
        }
        else if (actionEvent.getSource() == emulateAsLaundryCentral) {

            currentEmulator = Emulator.LaundryCentral;
            fxmlLoginLoader(currentEmulator);
            changeScene(logInSceneLaundryCentral);
        }
        try {
            clearFields((Pane) userName.getParent());

        }
        catch (NullPointerException nex) {
            /* this exception happens when you go from a emulation specific system and want to emulate something else.
             * is non critical and only affect this due to a nulpointer when it cant find the fields changing.
             * it should just clear textfields from login screens, so when on login screen, choosing costumer and type
             * username and password, then emulate as something else it clears the fields.
             */
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
    public static Scene laundryAssistantScene;
    public static Scene driverScene;
    public static Scene laundryManagerScene;

    public void changeThemeDark( ) {
        ThemeControl.currentTheme = ThemeControl.DARK;
        currentScene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        changeScene(currentScene);
    }

    public void changeThemeDefault( ) {
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

    public void closeWindow( ) {
        Platform.exit();
    }

    public void minimizeWindow( ) {
        Stage primaryStage = getPrimaryStage();
        if (primaryStage.isFullScreen()) {
            primaryStage.setFullScreen(false);
            isFullScreen = false;
        }
        else {
            primaryStage.setIconified(true);
        }
    }

    public void maximizeWindow( ) {
        Stage primaryStage = getPrimaryStage();
        primaryStage.setFullScreen(true);
        isFullScreen = true;
    }

    /**
     * gets the stage
     *
     * @return returns it to the calling method - ex. scene changer
     */
    private static Stage getPrimaryStage( ) {
        return primaryStage;
    }

    public void logIn( ) {
        String pass_word = passWord.getText();
        String user_name = userName.getText();

        if(currentEmulator.equals(Emulator.Driver)){
            user_name = "Driver";
            pass_word = "Driver123!";
        }

        if (AccountManager.logIn(user_name, pass_word)) {
            fxmlLoader(currentEmulator, AccountManager.currentRole);
            switch (Controller_Application.currentEmulator) {
                case Driver:
                    currentScene = driverScene;
                    changeScene(driverScene);
                    break;
                case LaundryCentral:
                    currentScene = laundryAssistantScene;
                    if (AccountManager.currentRole.equals(Role.Laundry_Manager)) {
                        changeScene(laundryManagerScene);
                    }
                    else if (AccountManager.currentRole.equals(Role.Laundry_Assistant)) {
                        changeScene(laundryAssistantScene);
                    }

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

    public void fxmlLoader(Emulator emulator, Role role) {
        try {
            switch (emulator) {
                case Driver:
                    FXMLLoader driverLoader = new FXMLLoader(getClass().getResource("/UI/Driver/driver.fxml"));
                    Parent driverParent = driverLoader.load();
                    Controller_Application.driverScene = new Scene(driverParent, 1020, 860, Color.TRANSPARENT);
                    break;
                case Costumer:
                    FXMLLoader costumerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/costumer.fxml"));
                    Parent costumerParent = costumerLoader.load();
                    Controller_Application.costumerScene = new Scene(costumerParent, 600, 600,Color.TRANSPARENT);
                    break;
                case DeliveryPoint:
                    //        FXMLLoader deliveryPointLoader = new FXMLLoader(getClass().getResource("/UI/DeliveryPoint/deliveryPoint.fxml"));
//        Parent deliveryPointParent = deliveryPointLoader.load();
//        Controller_Application.deliveryPointScene = new Scene(deliveryPointParent, 600, 600);
                    break;
                case LaundryCentral:
                    switch (role) {
                        case Laundry_Manager:
                            FXMLLoader laundryManagerLoader = new FXMLLoader(
                                    getClass().getResource("/UI/LaundryCentral/laundryManager.fxml"));
                            Parent laundryManagerParent = laundryManagerLoader.load();
                            Controller_Application.laundryManagerScene = new Scene(laundryManagerParent, 600, 600);
                            break;
                        case Laundry_Assistant:
                            FXMLLoader laundryLoader = new FXMLLoader(
                                    getClass().getResource("/UI/LaundryCentral/laundryAssistant.fxml"));
                            Parent laundryParent = laundryLoader.load();
                            Controller_Application.laundryAssistantScene = new Scene(laundryParent, 600, 600);
                            break;
                    }
                    break;
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void changeScene( ) {
        changeScene(Controller_Application.registerScene);
        clearFields((Pane) userName.getParent());
    }

    public void fxmlLoginLoader(Emulator emulator){
        try{
            switch (emulator){
                case LaundryCentral:
                    FXMLLoader logInLoaderLaundryCentral = new FXMLLoader(getClass().getResource("/UI/LaundryCentral/loginSceneLaundryCentral.fxml"));
                    Parent logInParentLaundryCentral = logInLoaderLaundryCentral.load();
                    Controller_Application.logInSceneLaundryCentral = new Scene(logInParentLaundryCentral, 600, 600);
                    break;
                case DeliveryPoint:
                    FXMLLoader logInLoaderDeliveryPoint = new FXMLLoader(getClass().getResource("/UI/DeliveryPoint/loginSceneDeliveryPoint.fxml"));
                    Parent logInParentDeliveryPoint = logInLoaderDeliveryPoint.load();
                    Controller_Application.logInSceneDeliveryPoint = new Scene(logInParentDeliveryPoint, 600, 600);
                    break;
                case Costumer:
                    FXMLLoader logInLoaderCostumer = new FXMLLoader(getClass().getResource("/UI/Costumer/loginSceneCostumer.fxml"));
                    Parent logInParentCostumer = logInLoaderCostumer.load();
                    Controller_Application.logInSceneCostumer = new Scene(logInParentCostumer, 600, 600);

                    FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/registerScene.fxml"));
                    Parent registerParent = registerLoader.load();
                    Controller_Application.registerScene = new Scene(registerParent, 600, 600);
                    break;
                case Driver:
                    FXMLLoader logInLoaderDriver = new FXMLLoader(getClass().getResource("/UI/Driver/loginSceneDriver.fxml"));
                    Parent logInParentDriver = logInLoaderDriver.load();
                    Controller_Application.logInSceneDriver = new Scene(logInParentDriver, 600, 600);
                    break;
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }


    public void logOff( ) {
        fxmlLoginLoader(currentEmulator);
        switch (currentEmulator) {
            case LaundryCentral:
                changeScene(logInSceneLaundryCentral);
                break;
            case DeliveryPoint:
                changeScene(logInSceneDeliveryPoint);
                break;
            case Costumer:
                changeScene(logInSceneCostumer);
                break;
            case Driver:
                changeScene(logInSceneDriver);
                break;
        }
        AccountManager.logOff();
    }

}
