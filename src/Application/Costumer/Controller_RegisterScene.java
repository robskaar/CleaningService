package Application.Costumer;

import Application.General.Controller_Application;
import Domain.Enums.Regex;
import Services.Handlers.AccountHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    @FXML
    TextField corporateID;
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
        passWord.setTooltip(passwordTip);
        passwordTip.setShowDelay(null);
        passWord.textProperty().addListener(((observableValue, oldVal, newVal) -> {
            Boolean matchingPassWordCriteria = Regex.PASSWORD_REGEX.matches(passWord.getText());
            if (matchingPassWordCriteria) {
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

    public void register( ) {

        Boolean phoneMatcher = Regex.PHONE_REGEX.matches(phoneNumber.getText());
        Boolean emailMatcher = Regex.EMAIL_REGEX.matches(emailAddress.getText());
        Boolean dateOfBirthMatcher = Regex.DATE_OF_BIRTH_REGEX.matches(dateOfBirth.getEditor().getText());
        if (dateOfBirth.getValue() == null) {
            LocalDate tempDate = LocalDate.parse(dateOfBirth.getEditor().getText(), formatter);
            dateOfBirth.setValue(tempDate);
        }
        if (pwdCriteriasMet && dateOfBirthMatcher && userName.getText() != null
            && firstName.getText() != null && lastName.getText() != null
            && emailMatcher && phoneMatcher) {
            switch (Controller_Application.currentEmulator) {
                case Costumer:
                    AccountHandler.registerCustomer(userName.getText(), passWord.getText(), firstName.getText(),
                                                    lastName.getText(),
                                                    emailAddress.getText(), phoneNumber.getText(),
                                                    dateOfBirth.getValue(),
                                                    0); //0 is a false bit and 1 is a true in mssql
                    break;
                case Driver:
                    AccountHandler.registerDriver(userName.getText(), passWord.getText(), firstName.getText(),
                                                  lastName.getText(),
                                                  emailAddress.getText(), phoneNumber.getText(), dateOfBirth.getValue(),
                                                  Integer.parseInt(corporateID.getText()));
                    break;
            }

            changeToLoginScene(); // changes into login scene after successful registration
        }
        else {
            if (!pwdCriteriasMet) {
                passWord.setStyle("-fx-border-color: red");
                passWord.requestFocus();
            }
            else {
                passWord.setStyle("-fx-border-color: transparent");
            }
            if (!dateOfBirthMatcher) {
                dateOfBirth.setStyle("-fx-border-color: red");
                dateOfBirth.requestFocus();
            }
            else {
                dateOfBirth.setStyle("-fx-border-color: transparent");
            }
            if (!emailMatcher){
                emailAddress.setStyle("-fx-border-color: red");
                emailAddress.requestFocus();
                emailAddress.setTooltip(new Tooltip("Invalid email provided"));
            }else{
                emailAddress.getTooltip().hide();
                emailAddress.setStyle("-fx-border-color: transparent");
            }
            if (!phoneMatcher){
                phoneNumber.setStyle("-fx-border-color: red");
                phoneNumber.requestFocus();
                phoneNumber.setTooltip(new Tooltip("Incorrect Phone Number, 8 digits only 0-9"));
            }else{
                phoneNumber.getTooltip().hide();
                phoneNumber.setStyle("-fx-border-color: transparent");
            }

        }
    }


    public void changeToLoginScene( ) {

        switch (Controller_Application.currentEmulator) {
            case Costumer:
                changeScene(Controller_Application.logInSceneCostumer);
                break;
            case Driver:
                changeScene(Controller_Application.logInSceneDriver);
                break;
        }
        clearFields((Pane) userName.getParent());
    }
}
