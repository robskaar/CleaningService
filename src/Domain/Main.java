package Domain;

import Controller.Controller_Application;
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

 public class Main extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        //initial stage setup
        Controller_Application.primaryStage = primaryStage;
        Controller_Application.fxmlLoader = new FXMLLoader(getClass().getResource("../GUI/Costumer/loginScene.fxml"));
        Controller_Application.parent = Controller_Application.fxmlLoader.load();
        Controller_Application.scene = new Scene(Controller_Application.parent);


        // Scene created for Log In
        FXMLLoader logInLoader = new FXMLLoader(getClass().getResource("../GUI/Costumer/loginScene.fxml"));
        Parent logInParent = logInLoader.load();
        Controller_Application.logInScene = new Scene(logInParent, 600, 600);

        // Scene created for Register
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("../GUI/Costumer/registerScene.fxml"));
        Parent registerParent = registerLoader.load();
        Controller_Application.registerScene = new Scene(registerParent, 600, 600);


        // scene created for loggedIn
        FXMLLoader loggedInLoader = new FXMLLoader(getClass().getResource("../GUI/Costumer/loggedInScene.fxml"));
        Parent loggedInParent = loggedInLoader.load();
        Controller_Application.loggedInScene = new Scene(loggedInParent, 600, 600);


        //sets initial theme for the application
        ThemeControl.currentTheme = ThemeControl.DEFAULT;
        Controller_Application.scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());


        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle("Project");
        primaryStage.setScene(Controller_Application.scene);
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
