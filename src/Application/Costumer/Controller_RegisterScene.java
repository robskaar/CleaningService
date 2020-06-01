package Application.Costumer;

import Application.general.Controller_Application;
import Domain.Managers.AccountManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller_RegisterScene extends Controller_Application implements Initializable {

    @FXML
    TextField userName;
    @FXML
    TextField passWord;
    @FXML
    TextField firstName;
    @FXML
    TextField lastName;
    @FXML
    TextField emailAddress;
    @FXML
    TextField phoneNumber;
    @FXML
    DatePicker dateOfBirth;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private boolean pwdCriteriasMet = false;
    private Tooltip passwordTip = new Tooltip(
            "Minimum eight characters, need one uppercase, one lowercase letter, one number and a special character");
    private Tooltip dateTip = new Tooltip(
            "Input like dd/mm/yyyy");

    /**
     * used to add listeners
     *
     * @param url            - default of init method
     * @param resourceBundle - default of init method
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Pattern passComplexity = Pattern.compile("((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})");
        passWord.setTooltip(passwordTip);
        passwordTip.setShowDelay(null);
        passWord.textProperty().addListener(((observableValue, oldVal, newVal) -> {
            Matcher matching = passComplexity.matcher(passWord.getText());
            if (matching.matches()) {
                passWord.setStyle("-fx-border-color: green");
                pwdCriteriasMet = true;
            }
            else {
                passWord.setStyle("-fx-border-color: red");
                pwdCriteriasMet = false;
            }

        }));

        /*
        this is used due to convertion errors, when a user input their date of birth themselves
        the datepicker will not pick it up for the registration, unless assigning it to the value.
         */
        dateOfBirth.setTooltip(dateTip); // lets user know what date format to use
        dateOfBirth.setPromptText("DoB: dd/mm/yyyy");


    }

    public void register() {
        String dateRegex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(dateOfBirth.getEditor().getText());
        if (dateOfBirth.getValue() == null) {
            LocalDate tempDate = LocalDate.parse(dateOfBirth.getEditor().getText(), formatter);
            dateOfBirth.setValue(tempDate);
        }
        if (pwdCriteriasMet && matcher.matches() && userName.getText() != null
            && firstName.getText() != null && lastName.getText() != null
            && emailAddress.getText() != null && phoneNumber.getText() != null) {
            AccountManager.register(userName.getText(), passWord.getText(), firstName.getText(), lastName.getText(),
                                    emailAddress.getText(), phoneNumber.getText(), dateOfBirth.getValue(),
                                    0); //0 is a false bit and 1 is a true in mssql
            changeScene(); // changes into login scene after successful registration
        }
        else {
            if (!pwdCriteriasMet) {
                passWord.setStyle("-fx-border-color: red");
                passWord.requestFocus();
            }
            else {
                passWord.setStyle("-fx-border-color: transparent");
            }
            if (!matcher.matches()) {
                dateOfBirth.setStyle("-fx-border-color: red");
                dateOfBirth.requestFocus();
            }
            else {
                dateOfBirth.setStyle("-fx-border-color: transparent");
            }
        }
    }


    public void changeScene() {
        changeScene(Controller_Application.logInSceneCostumer);
        clearFields((Pane) userName.getParent());
    }
}
