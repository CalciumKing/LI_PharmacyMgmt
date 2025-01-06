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
    private Label securityQuestionLabel;
    @FXML
    private Button login, signup;
    @FXML
    private TextField username, securityAnswerField, newPasswordField;
    @FXML
    private PasswordField loginPassword, signupPassword;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        for (String item : securityQuestions)
            choiceBox.getItems().add(item);
        choiceBox.setOnAction((event -> securityQuestion = choiceBox.getSelectionModel().getSelectedItem()));
    }
    // endregion
    
    @FXML
    private void changeForm() {
        System.out.println("Working");
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
            
        } else { // switching to login
            loginPane.setVisible(false);
            signupPane.setVisible(true);
            forgotPasswordPane.setVisible(false);
            securityQuestionPane.setVisible(false);
            pageStatus = 0;
            
            shortLogin.add("active");
            shortSignUp.add("notActive");
            shortLogin.remove("notActive");
            shortSignUp.remove("active");
            
//            shortSignUp.remove("active");
//            if (!shortSignUp.contains("notActive"))
//                shortSignUp.add("notActive");
//            shortLogin.remove("notActive");
//            shortLogin.add("active");
        }
        
        clearForm();
    }
    
    private void clearForm() {
        username.clear();
        loginPassword.clear();
        securityAnswerField.clear();
        securityQuestionLabel.setText("Security Question Here");
        signupPassword.clear();
        securityQuestion = "";
        securityAnswer = "";
    }
    
    @FXML
    private void forgotPassword() {
        loginPane.setVisible(false);
        forgotPasswordPane.setVisible(true);
        pageStatus = 3;
        
        ObservableList<String> shortLogin = login.getStyleClass();
        if (shortLogin.contains("active") && !shortLogin.contains("notActive")) {
            shortLogin.remove("active");
            shortLogin.add("notActive");
        }
    }
    
    @FXML
    private void checkValidUser() {
        String name = username.getText();
        if(pageStatus == 3 && SQLUtils.getUser(name) != null) {
            String[] securityInfo = SQLUtils.getSecurityInfo(name);
            if(securityInfo == null) return;
            
            securityQuestion = securityInfo[0];
            securityAnswer = securityInfo[1];
            securityQuestionLabel.setText(securityQuestion);
            
            securityQuestionPane.setVisible(true);
        }
    }
    
    @FXML
    private void resetPassword() {
        if(securityAnswerField.getText().equals(securityAnswer)) {
            SQLUtils.setPassword(username.getText(), newPasswordField.getText());
            
            securityQuestionPane.setVisible(false);
            forgotPasswordPane.setVisible(false);
            loginPane.setVisible(true);
            
            ObservableList<String> shortLogin = login.getStyleClass();
            shortLogin.add("active");
            shortLogin.remove("notActive");
        }
    }
    
    @FXML
    private void login() {
    
    }
    
    @FXML
    private void createUser() {
    
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