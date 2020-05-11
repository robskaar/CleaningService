package Domain;

import Domain.ResizeHelper;
import Controller.AppControl;
import Controller.ThemeControl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 18-03-2020
 **/

 public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initial stage setup
        AppControl.primaryStage = primaryStage;
        AppControl.fxmlLoader = new FXMLLoader(getClass().getResource("../GUI/login.fxml"));
        AppControl.parent = AppControl.fxmlLoader.load();
        AppControl.scene = new Scene(AppControl.parent);


        // Scene created for Log In
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("../GUI/login.fxml"));
        Parent logInParent = logInLoader.load();
        AppControl.logInScene = new Scene(logInParent, 600, 600);

        // Scene created for Register
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("../GUI/register.fxml"));
        Parent registerParent = registerLoader.load();
        AppControl.registerScene = new Scene(registerParent, 600, 600);


        // scene created for loggedIn
        FXMLLoader loggedInLoader = new FXMLLoader(getClass().getResource("../GUI/loggedIn.fxml"));
        Parent loggedInParent = loggedInLoader.load();
        AppControl.loggedInScene = new Scene(loggedInParent, 600, 600);


        //sets initial theme for the application
        ThemeControl.currentTheme = ThemeControl.DEFAULT;
        AppControl.scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());


        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Project");
        primaryStage.setScene(AppControl.scene);
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
