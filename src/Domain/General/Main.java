package Domain.General;

import Application.general.Controller_Application;
import Domain.Enums.Emulator;
import Services.Resizer.ResizeHelper;
import Services.Themes.ThemeControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

 public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        //initial stage setup
        Controller_Application.primaryStage = primaryStage;

        // Scenes for different log ins
        FXMLLoader logInLoaderCostumer = new FXMLLoader(getClass().getResource("/UI/Costumer/loginSceneCostumer.fxml"));
        Parent logInParentCostumer = logInLoaderCostumer.load();
        Controller_Application.logInSceneCostumer = new Scene(logInParentCostumer, 600, 600);

        FXMLLoader logInLoaderDriver = new FXMLLoader(getClass().getResource("/UI/Driver/loginSceneDriver.fxml"));
        Parent logInParentDriver = logInLoaderDriver.load();
        Controller_Application.logInSceneDriver = new Scene(logInParentDriver, 600, 600);

        FXMLLoader logInLoaderLaundryCentral = new FXMLLoader(getClass().getResource("/UI/LaundryCentral/loginSceneLaundryCentral.fxml"));
        Parent logInParentLaundryCentral = logInLoaderLaundryCentral.load();
        Controller_Application.logInSceneLaundryCentral = new Scene(logInParentLaundryCentral, 600, 600);

        FXMLLoader logInLoaderDeliveryPoint = new FXMLLoader(getClass().getResource("/UI/DeliveryPoint/loginSceneDeliveryPoint.fxml"));
        Parent logInParentDeliveryPoint = logInLoaderDeliveryPoint.load();
        Controller_Application.logInSceneDeliveryPoint = new Scene(logInParentDeliveryPoint, 600, 600);


        // Scene created for Register
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/registerScene.fxml"));
        Parent registerParent = registerLoader.load();
        Controller_Application.registerScene = new Scene(registerParent, 600, 600);


        // scene created for Costumer
        FXMLLoader costumerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/costumer.fxml"));
        Parent costumerParent = costumerLoader.load();
        Controller_Application.costumerScene = new Scene(costumerParent, 600, 600);

        // scene created for Driver
        FXMLLoader driverLoader = new FXMLLoader(getClass().getResource("/UI/Driver/driver.fxml"));
        Parent driverParent = driverLoader.load();
        Controller_Application.driverScene = new Scene(driverParent, 1020, 860);

//        // scene created for DeliveryPoint
//        FXMLLoader deliveryPointLoader = new FXMLLoader(getClass().getResource("/UI/DeliveryPoint/deliveryPoint.fxml"));
//        Parent deliveryPointParent = deliveryPointLoader.load();
//        Controller_Application.deliveryPointScene = new Scene(deliveryPointParent, 600, 600);
//
//        // scene created for loggedIn
//        FXMLLoader laundryLoader = new FXMLLoader(getClass().getResource("/UI/LaundryCentral/laundry.fxml"));
//        Parent laundryParent = laundryLoader.load();
//        Controller_Application.laundryScene = new Scene(laundryParent, 600, 600);


        //sets initial theme for the application
        ThemeControl.currentTheme = ThemeControl.DEFAULT;
        Controller_Application.currentScene = Controller_Application.logInSceneCostumer;
        Controller_Application.logInSceneCostumer.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        Controller_Application.currentEmulator=Emulator.Costumer;


        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Project");
        primaryStage.setScene(Controller_Application.currentScene);
        primaryStage.show();

        // helper class to resize window as setting the primaryStage init style to transparent / undecorated will remove resize options
        ResizeHelper.addResizeListener(primaryStage);
    }


    /**
     * main that starts the program
     *
     * @param args - args to launch
     */
    public static void main(String[] args) {
        launch(args);
    }


}
