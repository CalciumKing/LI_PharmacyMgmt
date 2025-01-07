package com.example.li_pharmacy;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Optional;
import java.util.regex.Pattern;

public class Utils {
    private static double xOffset;
    private static double yOffset;
    
    // region Alert Methods
    public static void errorAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        alert.showAndWait();
    }
    
    private static Alert createAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        return alert;
    }
    
    public static Optional<ButtonType> confirmAlert(Alert.AlertType type, String title, String headerText, String contentText) {
        Alert alert = createAlert(type, title, headerText, contentText);
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yes, no);
        return alert.showAndWait();
    }
    // endregion Alert Methods
    
    // region Random Utils
    public static void changeScene(String sceneName, User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(sceneName));
            Parent root = fxmlLoader.load();
            
            if (user != null) {
                DashboardController dashboardController = fxmlLoader.getController();
                dashboardController.welcomeName(user);
            }
            
            Stage stage = new Stage();
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.setTitle("Pharmacy Application");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ignored) {
            errorAlert(Alert.AlertType.ERROR, "Scene Error", "Error Changing Scene", "There was an error changing scenes, please try again");
        }
    }
    
    public static boolean regexValidation(String password) {
        // region Regex Characters
        // . any single character
        // * 0 or more occurrences of the preceding element
        // + 1 or more occurrence of the preceding element
        // [] match any character inside brackets
        // ^ start of a string
        // $ end of a string
        // \ escape character
        // ?=* positive look ahead assertion
        // ?! negative look ahead assertion
        // .{8, } at least 8 characters
        // \\d shortcut for 0-9
        //endregion
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[/~`!@#$%^&*()_+{};:',<.>? =]).{8,}$";
        
        if (password != null && !Pattern.compile(passwordRegex).matcher(password).matches()) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Password", "Please Enter A Valid Password That Contains At Least 8 Characters, 1 Uppercase, 1 Lowercase, 1 Number, and 1 Special Character");
            return false;
        }
        
        return true;
    }
    // endregion
    
    // region Window Settings
    public static void windowMinimize(ActionEvent event) {
        ((Stage) ((Button) event.getSource()).getScene().getWindow()).setIconified(true);
    }
    
    public static void windowClose() {
        System.exit(0);
    }
    
    public static void windowClick(MouseEvent event) {
        xOffset = event.getScreenX();
        yOffset = event.getScreenY();
    }
    
    public static void windowDrag(MouseEvent event, AnchorPane pane) {
        Stage stage = (Stage) pane.getScene().getWindow();
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);
    }
    // endregion
}