package com.example.li_pharmacy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // region Variables
    @FXML
    private TextField id_field, brand_field, product_field, price_field;
    
    @FXML
    private AnchorPane dashboard, databasePage, scannerPage, userPage, welcomePage;
    
    @FXML
    private ImageView item_img;
    
    @FXML
    private TextField scanner_id_field;
    
    @FXML
    private ImageView scanner_img;
    
    @FXML
    private ImageView user_img;
    
    @FXML
    private Button users_page_btn;
    
    @FXML
    private Label welcomeText;
    String username = "";
    
    @FXML
    private ChoiceBox<String> typeBox, statusBox;
    private final String[] types = new String[] {"Pain Relievers", "Antibiotics", "Cardiovascular", "Metabolic", "Respiratory"};
    private final String[] statuses = new String[] {"Available", "Not Available"};
    @FXML
    private TableView<Medicine> medicine_table;
    @FXML
    private TableColumn<Medicine, String> id_col, brand_col, product_col, type_col, status_col;
    @FXML
    private TableColumn<Medicine, Double> price_col;
    @FXML
    private TableColumn<Medicine, Date> date_col;
    @FXML
    ObservableList<Medicine> items;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        brand_col.setCellValueFactory(new PropertyValueFactory<>("brand"));
        product_col.setCellValueFactory(new PropertyValueFactory<>("productName"));
        price_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
        clearMedicineForm();
        
        for (String type : types)
            typeBox.getItems().add(type);
//        typeBox.setOnAction((event -> securityQuestion = typeBox.getSelectionModel().getSelectedItem()));
        typeBox.setValue("Choose Medicine Type");
        
        for (String status : statuses)
            statusBox.getItems().add(status);
//        statusBox.setOnAction((event -> securityQuestion = statusBox.getSelectionModel().getSelectedItem()));
        statusBox.setValue("Choose Medicine Status");
    }
    
    // endregion
    
    // region Side NavBar
    @FXML
    private void welcomePage() {
        welcomePage.setVisible(true);
        databasePage.setVisible(false);
        scannerPage.setVisible(false);
        userPage.setVisible(false);
    }
    
    @FXML
    private void databasePage() {
        welcomePage.setVisible(false);
        databasePage.setVisible(true);
        scannerPage.setVisible(false);
        userPage.setVisible(false);
    }
    
    @FXML
    private void scannerPage() {
        welcomePage.setVisible(false);
        databasePage.setVisible(false);
        scannerPage.setVisible(true);
        userPage.setVisible(false);
    }
    
    @FXML
    private void usersPage() {
        welcomePage.setVisible(false);
        databasePage.setVisible(false);
        scannerPage.setVisible(false);
        userPage.setVisible(true);
    }
    // endregion
    
    public void welcomeName(User user) {
        username = user.getUsername();
        welcomeText.setText("Welcome, " + username);
    }
    
    // region Medicine Table (Second Page)
    @FXML
    private void addItem() {
        if (medicineFormInvalid()) return;
        
        String id = id_field.getText();
        String brand = brand_field.getText();
        String product = product_field.getText();
        double price = Double.parseDouble(price_field.getText());
        String type = typeBox.getValue();
        String status = statusBox.getValue();
        
        SQLUtils.addItem(id, brand, product, price, type, status, null);
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
        clearMedicineForm();
    }
    
    @FXML
    private void updateItem() {
        if (medicineFormInvalid()) return;
        
        String id = id_field.getText();
        String brand = brand_field.getText();
        String product = product_field.getText();
        double price = Double.parseDouble(price_field.getText());
        String type = typeBox.getValue();
        String status = statusBox.getValue();
        
        SQLUtils.updateItem(id, brand, product, type, status, price, null);
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
        clearMedicineForm();
    }
    
    @FXML
    private void deleteItem() {
        if (medicineFormInvalid()) return;
        
        Optional<ButtonType> optionSelected = Utils.confirmAlert(Alert.AlertType.CONFIRMATION, "Form Validation", "Delete This Item", "Confirming, would you like to delete this piece of data?");
        if (optionSelected.isPresent() && optionSelected.get().getText().equals("Yes")) {
            SQLUtils.deleteItem(id_field.getText());
            
            items = SQLUtils.refreshTable();
            medicine_table.setItems(items);
            clearMedicineForm();
        }
    }
    
    private boolean medicineFormInvalid() {
        if (id_field.getText().isEmpty() || brand_field.getText().isEmpty() ||
                product_field.getText().isEmpty() || price_field.getText().isEmpty() ||
                typeBox.getValue().isEmpty() || statusBox.getValue().isEmpty()) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Fields", "All Fields Must Be Filled In");
            return true;
        }
        return false;
    }
    
    @FXML
    void clearMedicineForm() {
        id_field.clear();
        brand_field.clear();
        product_field.clear();
        price_field.clear();
        
        typeBox.setValue("Choose Medicine Type");
        statusBox.setValue("Choose Medicine Status");
    }
    
    @FXML
    private void selected() {
        Medicine item = medicine_table.getSelectionModel().getSelectedItem();
        if (item == null) return;
        
        id_field.setText(item.getId());
        brand_field.setText(item.getBrand());
        product_field.setText(item.getProductName());
        price_field.setText(String.valueOf(item.getPrice()));
        typeBox.setValue(item.getType());
        statusBox.setValue(item.getStatus());
    }
    // endregion
    
    @FXML
    void addUser(ActionEvent event) {
    
    }
    @FXML
    void clearScannerForm(ActionEvent event) {
    
    }
    
    @FXML
    void clearUsersForm(ActionEvent event) {
    
    }
    
    @FXML
    void deleteUser(ActionEvent event) {
    
    }
    
    @FXML
    void finalizeScannerForm(ActionEvent event) {
    
    }
    
    @FXML
    void loadItem(KeyEvent event) {
    
    }
    
    @FXML
    void loadUser(KeyEvent event) {
    
    }
    
    @FXML
    private void logOut() {
        Utils.changeScene("signup-login.fxml", null);
        welcomeText.getScene().getWindow().hide();
    }
    
    @FXML
    void selectedScanner(MouseEvent event) {
    
    }
    
    @FXML
    void selectedUser(MouseEvent event) {
    
    }
    
    @FXML
    void submitScanner(ActionEvent event) {
    
    }
    
    @FXML
    void updateUser(ActionEvent event) {
    
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
        Utils.windowDrag(event, dashboard);
    }
    // endregion
}