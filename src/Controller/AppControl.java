package Controller;

import Domain.ResizeHelper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

abstract public class AppControl {
    @FXML
    Button closeBtn;
    @FXML
    Button maximizeBtn;
    @FXML
    Button minimizeBtn;


    // Fields
    private static boolean isFullScreen;
    public static FXMLLoader fxmlLoader;
    public static Parent parent;
    public static Scene scene;
    public static Stage primaryStage;

    //Scenes
    public static Scene logInScene;
    public static Scene registerScene;
    public static Scene loggedInScene;


    public void changeThemeDark() {
        ThemeControl.currentTheme = ThemeControl.DARK;
        scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        changeScene(scene);
    }

    public void changeThemeDefault() {
        ThemeControl.currentTheme = ThemeControl.DEFAULT;
        scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        changeScene(scene);
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

    /**
     * should call changeScene(Scene scene) and param the scene to change into
     * is done this way since we sometimes want to clear field or whatever after/before a scene change, this should be put in the overwritten method here as well.
     * making this abstract will force subclasses to implement this method, since all panes
     * need to be able to change to another pane, we need this.
     */
    abstract public void changeScene();

    protected static void changeScene(Scene scene) {
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(isFullScreen);
        scene.getStylesheets().clear();
        scene.getStylesheets().add(ThemeControl.currentTheme.getTheme());
        AppControl.scene = scene;
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


}
