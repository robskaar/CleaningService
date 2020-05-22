package Application.LaundryCentral;

import Application.general.Controller_Application;
import Domain.Enums.Role;
import Foundation.Database.DB;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @Author Robert Skaar
 * @Project newProject  -  https://github.com/robskaar
 * @Date 11-05-2020
 **/

public class Controller_LaundryManager extends Controller_LaundryAssistant implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        DB.setDBPropertiesPath(Role.Laundry_Manager);
    }

}
