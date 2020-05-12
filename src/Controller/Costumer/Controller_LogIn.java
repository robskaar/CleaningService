package Controller.Costumer;


import Controller.Controller_Application;
import Domain.Account;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class Controller_LogIn extends Controller_Application implements Initializable {

    @FXML
    private TextField userName;
    @FXML
    private PasswordField passWord;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    passWord.setOnAction( e-> {
        logIn();
    });
    }

    public void logIn() {
        String pass_word = passWord.getText();
        String user_name = userName.getText();

        if (Account.logIn(user_name, pass_word)) {
            scene = loggedInScene;
            changeScene(loggedInScene);
            userName.setText(null);
            passWord.setText(null);
        }
        else {
            userName.setText("Incorrect Credentials");
            passWord.setText("Incorrect Credentials");
        }
    }

    @Override
    public void changeScene() {
        changeScene(Controller_Application.registerScene);
        clearFields((Pane) userName.getParent());
    }
}

