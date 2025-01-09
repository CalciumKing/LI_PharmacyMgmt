package com.example.li_pharmacy;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // region Variables
    @FXML
    private TextField id_field, brand_field, product_field, price_field, searchBar;
    
    @FXML
    private AnchorPane dashboard, databasePage, scannerPage, userPage, welcomePage;
    
    @FXML
    private ImageView medicine_img;
    private String imagePath = "";
    
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
    private final String[] types = new String[]{"Pain Relievers", "Antibiotics", "Cardiovascular", "Metabolic", "Respiratory"};
    private final String[] statuses = new String[]{"Available", "Not Available"};
    
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
    
    private double defaultWidth;
    private double defaultHeight;
    private boolean alreadyMaximized = false;
    
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
        typeBox.setValue("Type");
        
        for (String status : statuses)
            statusBox.getItems().add(status);
        statusBox.setValue("Status");
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
    
    @FXML
    private void logOut() {
        Utils.changeScene("signup-login.fxml", null);
        welcomeText.getScene().getWindow().hide();
    }
    // endregion
    
    // region Dashboard Page (First Page)
    public void welcomeName(User user) {
        username = user.getUsername();
        welcomeText.setText("Welcome, " + username);
    }
    // endregion
    
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
        
        java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
        SQLUtils.addItem(id, brand, product, price, type, status, sqlDate, imagePath);
        
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
        
        java.sql.Date sqlDate = new java.sql.Date(new Date().getTime());
        SQLUtils.updateItem(id, brand, product, type, status, price, sqlDate, imagePath);
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
        clearMedicineForm();
    }
    
    @FXML
    private void deleteItem() {
        if (id_field.getText().isEmpty()) return;
        
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
                typeBox.getValue().isEmpty() || statusBox.getValue().isEmpty() ||
                typeBox.getValue().equals("Type") || statusBox.getValue().equals("Status") ||
                imagePath.isEmpty()) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Fields", "All Fields Must Be Filled In");
            return true;
        }
        return false;
    }
    
    @FXML
    private void clearMedicineForm() {
        id_field.clear();
        brand_field.clear();
        product_field.clear();
        price_field.clear();
        searchBar.clear();
        
        typeBox.setValue("Type");
        statusBox.setValue("Status");
        
        imagePath = "";
        Utils.createImage("bin\\Images\\defaultImage.jpg", medicine_img);
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
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
        imagePath = item.getImagePath();
        
        Utils.createImage(item.getImagePath(), medicine_img);
    }
    
    @FXML
    private void chooseImage() {
        try {
            FileChooser open = new FileChooser();
            open.setTitle("Open Image File");
            open.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Image File", "*.jpg", "*.jpeg", "*.png"));
            File saveFolder = new File("bin\\Images");
            open.setInitialDirectory(saveFolder);
            
            File file = open.showOpenDialog(medicine_img.getScene().getWindow());
            
            if (file != null) {
                if (!saveFolder.exists() && !saveFolder.mkdirs())
                    return;
                
                File saveFile = new File(saveFolder, file.getName());
                Files.copy(file.toPath(), saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                Image image = new Image(file.toURI().toString(), 125, 165, false, true);
                medicine_img.setImage(image);
                imagePath = saveFile.getPath();
            }
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.INFORMATION, "Form Validation", "Invalid Fields", "All Fields Must Be Filled In");
        }
    }
    
    @FXML
    private void searchMedicine() {
        String searchText = searchBar.getText();
        items = (searchText.isEmpty()) ? SQLUtils.refreshTable(): SQLUtils.searchTable(searchText);
        medicine_table.setItems(items);
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
        Utils.windowDrag(event, dashboard);
    }
    
    @FXML
    private void windowMaximize() {
        if(!alreadyMaximized) {
            Scene scene = dashboard.getScene();
            double initWidth = scene.getWidth();
            double initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(dashboard, initWidth, initHeight, false);
            
            alreadyMaximized = true;
        } else {
            Utils.windowMaximize(dashboard, defaultWidth, defaultHeight, true);
            alreadyMaximized = false;
        }
    }
    // endregion
    
    @FXML
    private void addUser(ActionEvent event) {
    
    }
    
    @FXML
    private void clearScannerForm(ActionEvent event) {
    
    }
    
    @FXML
    private void clearUsersForm(ActionEvent event) {
    
    }
    
    @FXML
    private void deleteUser(ActionEvent event) {
    
    }
    
    @FXML
    private void finalizeScannerForm(ActionEvent event) {
    
    }
    
    @FXML
    private void loadItem(KeyEvent event) {
    }
    
    @FXML
    private void loadUser(KeyEvent event) {
    
    }
    
    @FXML
    private void selectedScanner(MouseEvent event) {
    
    }
    
    @FXML
    private void selectedUser(MouseEvent event) {
    
    }
    
    @FXML
    private void submitScanner(ActionEvent event) {
    
    }
    
    @FXML
    private void updateUser(ActionEvent event) {
    
    }
}