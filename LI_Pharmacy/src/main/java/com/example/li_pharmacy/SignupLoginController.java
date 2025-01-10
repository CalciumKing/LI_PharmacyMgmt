package com.example.li_pharmacy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
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
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (String item : securityQuestions)
            choiceBox.getItems().add(item);
        choiceBox.setOnAction((event -> securityQuestion = choiceBox.getSelectionModel().getSelectedItem()));
        choiceBox.setValue("Choose Security Question");
    }
    // endregion
    
    // region Form Methods
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
        pageStatus = 2;
        
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
        User user = SQLUtils.getUser(name);
        if (pageStatus == 2 && user != null) {
            securityQuestion = user.getSecurityQuestion();
            securityAnswer = user.getSecurityAnswer();
            securityQuestionLabel.setText(securityQuestion);
            
            securityQuestionPane.setVisible(true);
        } else {
            securityQuestionLabel.setText("Security Question Here");
            securityQuestionPane.setVisible(false);
        }
    }
    
    @FXML
    private void resetPassword() {
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
            
            clearForm();
            Utils.changeScene("dashboard.fxml", user);
            page.getScene().getWindow().hide();
        }
    }
    // endregion
    
    // region Form Validation
    private boolean validForm() {
        String name = username.getText(), newPass = newPasswordField.getText(), signPass = signupPassword.getText(), secAnswer = newSecurityAnswerField.getText();
        User user = SQLUtils.getUser(name);
        
        if (isFormEmpty()) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Fields", "All Fields Must Be Filled In");
            return false;
        } else if (signup.getStyleClass().contains("active"))
            return checkSignupForm(user, signPass);
        else if (login.getStyleClass().contains("active"))
            return !checkLoginForm(user, secAnswer);
        else return Utils.regexValidation(newPass);
    }
    
    private static boolean checkLoginForm(User user, String secAnswer) {
        if (user == null) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Invalid Info", "That User Does Not Exist", "Please enter valid information for a user that does already exists.");
            return true;
        } else if (user.getSecurityAnswer() != null && !user.getSecurityAnswer().equals(secAnswer)) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Passwords Must Match", "Password And Confirm Password Must Match");
            return true;
        }
        return false;
    }
    
    private static boolean checkSignupForm(User user, String signPass) {
        if (user != null) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Invalid Info", "That User Already Exists", "Please enter information for a user that does not already exist.");
            return false;
        } else return Utils.regexValidation(signPass);
    }
    
    private boolean isFormEmpty() {
        if (username.getText().isEmpty())
            return true;
        else if (signup.getStyleClass().contains("active")) {
            return choiceBox.getValue().isEmpty() ||
                    newSecurityAnswerField.getText().isEmpty() ||
                    signupPassword.getText().isEmpty();
        } else if (login.getStyleClass().contains("active")) {
            return loginPassword.getText().isEmpty();
        } else return securityAnswerField.getText().isEmpty() || newPasswordField.getText().isEmpty();
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
    
    @FXML
    private void windowMaximize() {
        if (!alreadyMaximized) {
            Scene scene = page.getScene();
            double initWidth = scene.getWidth();
            double initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(page, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(page, defaultWidth, defaultHeight, true);
        
        alreadyMaximized = !alreadyMaximized;
    }
    // endregion
}