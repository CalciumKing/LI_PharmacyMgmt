package com.example.li_pharmacy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class SignupLoginController implements Initializable {
    //region Variables
    private int pageStatus = 0;
    @FXML
    private AnchorPane page, forgotPasswordPane, securityQuestionPane, loginPane, signupPane;
    private final String[] securityQuestions = new String[]{
            "What was the name of your favorite childhood pet?",
            "What year was your grandmother born?",
            "What month was your first child born?",
            "What breed of cat do you like the most?",
            "What was the name of your school physical education teacher?",
            "In which area of the city is your place of work located?"
    };
    private String securityQuestion;
    private String securityAnswer;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Label securityQuestionLabel, formText;
    @FXML
    private Button login, signup;
    @FXML
    private TextField username, securityAnswerField, newPasswordField, newSecurityAnswerField;
    @FXML
    private PasswordField loginPassword, signupPassword;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (String item : securityQuestions)
            choiceBox.getItems().add(item);
        choiceBox.setOnAction((event -> securityQuestion = choiceBox.getSelectionModel().getSelectedItem()));
        choiceBox.setValue("Choose Security Question");
    }
    // endregion
    
    @FXML
    private void changeForm() {
        ObservableList<String> shortLogin = login.getStyleClass(), shortSignUp = signup.getStyleClass();
        if (shortLogin.contains("active")) { // switching to signup
            loginPane.setVisible(false);
            signupPane.setVisible(true);
            forgotPasswordPane.setVisible(false);
            securityQuestionPane.setVisible(false);
            pageStatus = 1;
            
            shortLogin.remove("active");
            shortSignUp.remove("notActive");
            shortLogin.add("notActive");
            shortSignUp.add("active");
            
            formText.setText("Signup Form:");
            
        } else { // switching to login
            loginPane.setVisible(true);
            signupPane.setVisible(false);
            forgotPasswordPane.setVisible(false);
            securityQuestionPane.setVisible(false);
            pageStatus = 0;
            
            shortLogin.add("active");
            shortSignUp.add("notActive");
            shortLogin.remove("notActive");
            shortSignUp.remove("active");
            
            formText.setText("Login Form:");
        }
        
        clearForm();
    }
    
    private void clearForm() {
        username.clear();
        loginPassword.clear();
        securityAnswerField.clear();
        newPasswordField.clear();
        securityQuestionLabel.setText("Security Question Here");
        signupPassword.clear();
        securityQuestion = "";
        securityAnswer = "";
        newSecurityAnswerField.clear();
        choiceBox.setValue("Choose Security Question");
    }
    
    @FXML
    private void forgotPassword() {
        loginPane.setVisible(false);
        forgotPasswordPane.setVisible(true);
        pageStatus = 3;
        
        formText.setText("Forgot Password Form:");
        
        ObservableList<String> shortLogin = login.getStyleClass();
        if (shortLogin.contains("active") && !shortLogin.contains("notActive")) {
            shortLogin.remove("active");
            shortLogin.add("notActive");
        }
    }
    
    @FXML
    private void checkValidUser() {
        String name = username.getText();
        if (pageStatus == 3 && SQLUtils.getUser(name) != null) {
            String[] securityInfo = SQLUtils.getSecurityInfo(name);
            if (securityInfo == null) return;
            
            securityQuestion = securityInfo[0];
            securityAnswer = securityInfo[1];
            securityQuestionLabel.setText(securityQuestion);
            
            securityQuestionPane.setVisible(true);
        } else {
            securityQuestionLabel.setText("Security Question Here");
            securityQuestionPane.setVisible(false);
        }
    }
    
    @FXML
    private void resetPassword() {
        System.out.println(securityAnswerField.getText());
        System.out.println(securityAnswer);
        System.out.println();
        if (!securityAnswerField.getText().equals(securityAnswer)) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Non-Matching Fields", "The Security Question Field Does Not Match The Security Questions Answer.");
            return;
        }
        
        if (validForm()) {
            SQLUtils.setPassword(username.getText(), newPasswordField.getText());
            
            securityQuestionPane.setVisible(false);
            forgotPasswordPane.setVisible(false);
            loginPane.setVisible(true);
            
            ObservableList<String> shortLogin = login.getStyleClass();
            shortLogin.add("active");
            shortLogin.remove("notActive");
            
            clearForm();
        }
    }
    
    @FXML
    private void login() {
        User user = SQLUtils.login(username.getText(), loginPassword.getText());
        
        if (user == null) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Null User", "That User Does Not Exist", "Please enter information for a user that does already exist.");
            return;
        }
        
        clearForm();
        Utils.changeScene("dashboard.fxml", user);
        page.getScene().getWindow().hide();
    }
    
    @FXML
    private void createUser() {
        if (validForm()) {
            User user = SQLUtils.register(username.getText(), signupPassword.getText(), choiceBox.getValue(), newSecurityAnswerField.getText());
            if (user == null) {
                Utils.errorAlert(Alert.AlertType.ERROR, "Null User", "That User Already Exists", "Please enter information for a user that does not already exist.");
                return;
            }
            
            clearForm();
            Utils.changeScene("dashboard.fxml", user);
            page.getScene().getWindow().hide();
        }
    }
    
    // region Form Validation
    private boolean validForm() {
        String name = username.getText(), pass = loginPassword.getText(), newPass = newPasswordField.getText(), secQuestion = choiceBox.getValue(), secAnswer = newSecurityAnswerField.getText();
        User user = SQLUtils.getUser(name);
        
        if (isFormEmpty()) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Fields", "All Fields Must Be Filled In");
            return false;
        } else if (signup.getStyleClass().contains("active")) {
            if (user != null) {
                Utils.errorAlert(Alert.AlertType.ERROR, "Invalid Info", "That User Already Exists", "Please enter information for a user that does not already exist.");
                return false;
            } else return Utils.regexValidation(pass);
        } else if (login.getStyleClass().contains("active")) {
            if (user == null) {
                Utils.errorAlert(Alert.AlertType.ERROR, "Invalid Info", "That User Does Not Exist", "Please enter valid information for a user that does already exists.");
                return false;
            } else if (user.getSecurityAnswer() != null && !user.getSecurityAnswer().equals(secAnswer)) {
                Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Passwords Must Match", "Password And Confirm Password Must Match");
                return false;
            }
        } else return Utils.regexValidation(newPass);
        
        return true;
    }
    
    private boolean isFormEmpty() {
        if (username.getText().isEmpty())
            return false;
        else if (signup.getStyleClass().contains("active") &&
                (choiceBox.getValue().isEmpty() ||
                        newSecurityAnswerField.getText().isEmpty() ||
                        signupPassword.getText().isEmpty()))
            return false;
        else if (login.getStyleClass().contains("active") &&
                loginPassword.getText().isEmpty())
            return false;
        else return !securityAnswerField.getText().isEmpty() && !newPasswordField.getText().isEmpty();
    }
    // endregion
    
    // region Window Settings
    @FXML
    private void windowMinimize(ActionEvent event) {
        Utils.windowMinimize(event);
    }
    
    @FXML
    private void windowClose() {
        Utils.windowClose();
    }
    
    @FXML
    private void windowClick(MouseEvent event) {
        Utils.windowClick(event);
    }
    
    @FXML
    private void windowDrag(MouseEvent event) {
        Utils.windowDrag(event, page);
    }
    // endregion
}