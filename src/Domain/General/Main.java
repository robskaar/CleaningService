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
import javafx.util.Callback;


/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

 public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Callback<Class<?>, Object> controllerFactory = new Callback<>() {
            Controller_Application a = new Controller_Application();

            @Override
            public Object call(Class<?> type) {
                if (type == Controller_Application.class) {
                    return a;
                }
                return null;
            }
        };

        //initial stage setup
        Controller_Application.primaryStage = primaryStage;

        // Scene created for Log In
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("/UI/General/loginScene.fxml"));
        FXMLLoader defaultLoader = new FXMLLoader(getClass().getResource("/UI/General/defaultScene.fxml"));
        defaultLoader.setControllerFactory(controllerFactory);
        logInLoader.setControllerFactory(controllerFactory);
        Parent logInParent = logInLoader.load();
        Controller_Application.logInScene = new Scene(logInParent, 600, 600);



        // Scene created for Register
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/registerScene.fxml"));
        Parent registerParent = registerLoader.load();
        Controller_Application.registerScene = new Scene(registerParent, 600, 600);


        // scene created for loggedIn
        FXMLLoader costumerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/costumer.fxml"));
        Parent costumerParent = costumerLoader.load();
        Controller_Application.costumerScene = new Scene(costumerParent, 600, 600);

        // scene created for loggedIn
        FXMLLoader driverLoader = new FXMLLoader(getClass().getResource("/UI/Driver/driver.fxml"));
        Parent driverParent = driverLoader.load();
        Controller_Application.driverScene = new Scene(driverParent, 1020, 860);

//        // scene created for loggedIn
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
        Controller_Application.logInScene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        Controller_Application.currentEmulator=Emulator.Costumer;


        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Project");
        primaryStage.setScene(Controller_Application.logInScene);
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
