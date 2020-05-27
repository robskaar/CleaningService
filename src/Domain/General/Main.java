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
        FXMLLoader logInLoaderCostumer = new FXMLLoader(getClass().getResource("/UI/Costumer/loginSceneCostumer.fxml"));
        Parent logInParentCostumer = logInLoaderCostumer.load();
        Controller_Application.logInSceneCostumer = new Scene(logInParentCostumer, 600, 600);
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/UI/Costumer/registerScene.fxml"));
        Parent registerParent = registerLoader.load();
        Controller_Application.registerScene = new Scene(registerParent, 600, 600);

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
