package com.example.li_pharmacy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class SignupLoginController {
    //region Variables
    @FXML
    private AnchorPane page, resetPane1, resetPane2, loginPane, signupPane;
    private String[] securityQuestions = new String[]{
            "What was the name of your favorite childhood pet?",
            "What year was your grandmother born?",
            "What month was your first child born?",
            "What breed of cat do you like the most?",
            "What was the name of your school physical education teacher?",
            "In which area of the city is your place of work located?"
    };
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private Label formText;
    @FXML
    private Button login, signup, forgotPassword, formButton, resetPasswordButton;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    // endregion
    
    @FXML
    private void changeForm() {
        ObservableList<String> shortLogin = login.getStyleClass(), shortSignUp = signup.getStyleClass();
        if (shortLogin.contains("active")) { // switching to signup
            formText.setText("Signup Form");
            shortLogin.remove("active");
            shortLogin.add("notActive");
            shortSignUp.remove("notActive");
            shortSignUp.add("active");
            
            confirmPassword.setVisible(true);
            formButton.setText("Sign Up");
            forgotPassword.setVisible(false);
            
        } else { // switching to login
            formText.setText("Login Form");
            formButton.setText("Login");
            shortSignUp.remove("active");
            if (!shortSignUp.contains("notActive"))
                shortSignUp.add("notActive");
            shortLogin.remove("notActive");
            shortLogin.add("active");
            
            confirmPassword.setVisible(false);
            formButton.setText("Login");
            password.setPromptText("Password:");
            forgotPassword.setVisible(true);
            resetPasswordButton.setVisible(false);
            formButton.setVisible(true);
        }
        
        clearForm();
    }
    
    private void clearForm() {
        username.clear();
        password.clear();
        confirmPassword.clear();
    }
    
    @FXML
    void forgotPassword(ActionEvent event) {
    
    }
    
    @FXML
    void formSubmit(ActionEvent event) {
    
    }
    
    @FXML
    void resetPassword(ActionEvent event) {
    
    }
    
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