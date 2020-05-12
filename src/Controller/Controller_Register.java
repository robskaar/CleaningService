package Controller;

import Domain.Account;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Controller_Register extends Controller_Application implements Initializable {

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
    private Date costumDate;
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
        dateOfBirth.setPromptText("dd/mm/yyyy");
        dateOfBirth.getEditor().textProperty().addListener((observableValue, oldVal, newVal) -> {
            try {
                LocalDate date = LocalDate.parse(dateOfBirth.getEditor().getText());
                dateOfBirth.setValue(date);
            }
            catch (DateTimeParseException e) {
                        /*we get formatting error in compiler, but the datepicker field and SQL is actually smart enough
                        to get the correct date anyhow.
                        this happens if you enter mm/dd/yyyy instead of the format above
                        * */
            }
        });


    }

    public void register() {
        String dateRegex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
        Pattern pattern = Pattern.compile(dateRegex);
        Matcher matcher = pattern.matcher(dateOfBirth.getEditor().getText());
        if (pwdCriteriasMet && matcher.matches() && userName.getText() != null
            && firstName.getText() != null && lastName.getText() != null
            && emailAddress.getText() != null && phoneNumber.getText() != null) {

            formatter.parse(dateOfBirth.getEditor().getCharacters());
            Account.register(userName.getText(), passWord.getText(), firstName.getText(), lastName.getText(),
                             emailAddress.getText(), phoneNumber.getText(), dateOfBirth.getValue());
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

    @Override
    public void changeScene() {
        changeScene(Controller_Application.logInScene);
        clearFields((Pane) userName.getParent());
    }
}
