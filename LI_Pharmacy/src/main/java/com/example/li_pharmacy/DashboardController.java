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
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class DashboardController implements Initializable {
    
    // region Variables
    @FXML
    private TextField id_field, brand_field, product_field, price_field, searchBar, scanner_id_field, username_field, email_field, grade_field;
    
    @FXML
    private Button dashBoard_btn, medicine_btn;
    @FXML
    private AnchorPane dashboard, databasePage, scannerPage, userPage, welcomePage;
    
    @FXML
    private ImageView medicine_img, scanner_img, user_img;
    private String imagePath = "";
    
    @FXML
    private Label welcomeText, med_cost_label, cart_total_label;
    
    @FXML
    private ChoiceBox<String> typeBox, statusBox;
    @FXML
    private ComboBox<String> purchaseTypeBox, purchaseBrandBox, purchaseNameBox, purchasePriceBox;
    private final String[] types = {"Pain Relievers", "Antibiotics", "Cardiovascular", "Metabolic", "Respiratory"}, statuses = {"Available", "Not Available"};
    private final HashMap<Medicine, Double> cart = new HashMap<>();
    private double cartPrice = 0.00;
    
    @FXML
    private TableView<Medicine> medicine_table, purchase_table;
    @FXML
    private TableColumn<Medicine, String> id_col, brand_col, product_col, type_col, status_col, purchase_id, purchase_type, purchase_brand, purchase_product_name;
    @FXML
    private TableColumn<Medicine, Double> price_col, purchase_price;
    @FXML
    private TableColumn<Medicine, Date> date_col;
    @FXML
    private ObservableList<Medicine> items, cartItems;
    @FXML
    private Spinner<Integer> spinner;
    
    private double defaultWidth, defaultHeight;
    private boolean alreadyMaximized = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initCols();
        initDropdowns();
        initResultDropdowns();
        
        items = SQLUtils.refreshTable();
        cartItems = items;
        medicine_table.setItems(items);
        purchase_table.setItems(cartItems);
        clearMedicineForm();
        
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
    }
    
    private void initResultDropdowns() {
        purchaseTypeBox.setItems(SQLUtils.getOptions("type"));
        purchaseBrandBox.setItems(SQLUtils.getOptions("brand"));
        purchaseNameBox.setItems(SQLUtils.getOptions("productName"));
        purchasePriceBox.setItems(SQLUtils.getOptions("price"));
        
//        purchaseTypeBox.getSelectionModel().clearSelection();
//        purchaseTypeBox.setPromptText("2");
//        ListCell<String> cell = new ListCell<>();
//        cell.setText("3");
//        purchaseTypeBox.setButtonCell(cell);
        
        System.out.println("Text: " + purchaseTypeBox.getPromptText());
        System.out.println("Value: " + purchaseTypeBox.getValue());
        
//        purchaseBrandBox.setValue(purchaseBrandBox.getPromptText());
//        purchaseNameBox.setValue(purchaseNameBox.getPromptText());
//        purchasePriceBox.setValue(purchasePriceBox.getPromptText());
    }
    
    private void initCols() {
        id_col.setCellValueFactory(new PropertyValueFactory<>("id"));
        brand_col.setCellValueFactory(new PropertyValueFactory<>("brand"));
        product_col.setCellValueFactory(new PropertyValueFactory<>("productName"));
        price_col.setCellValueFactory(new PropertyValueFactory<>("price"));
        type_col.setCellValueFactory(new PropertyValueFactory<>("type"));
        status_col.setCellValueFactory(new PropertyValueFactory<>("status"));
        date_col.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        purchase_id.setCellValueFactory(new PropertyValueFactory<>("id"));
        purchase_type.setCellValueFactory(new PropertyValueFactory<>("type"));
        purchase_brand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        purchase_product_name.setCellValueFactory(new PropertyValueFactory<>("productName"));
        purchase_price.setCellValueFactory(new PropertyValueFactory<>("price"));
    }
    
    private void initDropdowns() {
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
    private void showPage(ActionEvent event) {
        welcomePage.setVisible(false);
        databasePage.setVisible(false);
//        scannerPage.setVisible(false);
//        userPage.setVisible(false);
        
        Button button = (Button) event.getSource();
        AnchorPane pageToShow = null;
        
        if(button.equals(dashBoard_btn))
            pageToShow = welcomePage;
        else if(button.equals(medicine_btn))
            pageToShow = databasePage;
        
        if(pageToShow != null)
            pageToShow.setVisible(true);
    }
    
    @FXML
    private void logOut() {
        Utils.changeScene("signup-login.fxml", null);
        welcomeText.getScene().getWindow().hide();
    }
    // endregion
    
    // region Dashboard Page (First Page)
    public void welcomeName(User user) {
        welcomeText.setText("Welcome, " + user.getUsername());
    }
    // endregion
    
    // region Medicine Table (Second Page)
    @FXML
    private void addItem() {
        if (medicineFormInvalid()) return;
        
        double price = Utils.safeParseDouble(price_field.getText());
        if (price == -1.0) return;
        
        String id = id_field.getText(), brand = brand_field.getText(), product = product_field.getText(), type = typeBox.getValue(), status = statusBox.getValue();
        Date sqlDate = new Date(new java.util.Date().getTime());
        Medicine medicine = new Medicine(id, brand, product, type, status, price, sqlDate, imagePath);
        SQLUtils.addItem(medicine);
        
        items = SQLUtils.refreshTable();
        medicine_table.setItems(items);
        clearMedicineForm();
    }
    
    @FXML
    private void updateItem() {
        if (medicineFormInvalid()) return;
        
        double price = Utils.safeParseDouble(price_field.getText());
        if (price == -1.0) return;
        
        String id = id_field.getText(), brand = brand_field.getText(), product = product_field.getText(), type = typeBox.getValue(), status = statusBox.getValue();
        Date sqlDate = new Date(new java.util.Date().getTime());
        Medicine medicine = new Medicine(id, brand, product, type, status, price, sqlDate, imagePath);
        SQLUtils.updateItem(medicine);
        
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
        initDropdowns();
        
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
        
        Utils.createImage(imagePath, medicine_img);
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
        items = (searchText.isEmpty()) ? SQLUtils.refreshTable() : SQLUtils.searchTable(searchText);
        medicine_table.setItems(items);
    }
    // endregion
    
    // region Purchase Table (Third Page)
    @FXML
    private void addToCart() {
        System.out.println("add to cart");
        Medicine item = SQLUtils.getMedicine(new Medicine(
                purchaseBrandBox.getValue(),
                purchaseNameBox.getValue(),
                purchaseTypeBox.getValue(),
                Utils.safeParseDouble(purchasePriceBox.getValue())
        ));
        if(item == null) return;
        System.out.println("working");
        
        cart.put(item, item.getPrice() * spinner.getValue());
        cartPrice += item.getPrice() * spinner.getValue();
        cart_total_label.setText("$" + cartPrice);
        
//        clearMedicine();
    }
    
    @FXML
    private void removeFromCart() {
        System.out.println("remove from cart");
        Medicine item = SQLUtils.getMedicine(new Medicine(
                purchaseBrandBox.getValue(),
                purchaseNameBox.getValue(),
                purchaseTypeBox.getValue(),
                Utils.safeParseDouble(purchasePriceBox.getValue())
        ));
        if(item == null) return;
        System.out.println("working");
        
        cart.remove(item);
        cartPrice -= item.getPrice() * spinner.getValue();
        cart_total_label.setText("$" + cartPrice);
        
//        clearMedicine();
    }
    
    @FXML
    private void clearCart() {
        System.out.println("clear cart");
//        clearMedicine();
        initResultDropdowns();
        
        cart_total_label.setText("$0.00");
        cartPrice = 0.00;
        cart.clear();
        System.out.println("finished");
    }
    
    private void clearMedicine() {
        
//        purchaseTypeBox
        purchaseBrandBox.setValue("Brand Search:");
        purchaseNameBox.setValue("Name Search:");
        purchasePriceBox.setValue("Price Search:");
        med_cost_label.setText("$0.00");
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
    }
    
    @FXML
    private void searchMeds() {
        String type = purchaseTypeBox.getValue();
        String brand = purchaseBrandBox.getValue();
        String productName = purchaseNameBox.getValue();
        String price = purchasePriceBox.getValue();
        
        cartItems = filterMedicineTable(items, type, brand, productName, price);
        purchase_table.setItems(cartItems);
    }
    
    private ObservableList<Medicine> filterMedicineTable(ObservableList<Medicine> tableItems,
                                                               String type, String brand,
                                                               String productName, String price) {
        Predicate<Medicine> filter = i -> {
            boolean matches = true;
            
            if (type != null && !type.equals("Type Search:"))
                matches &= i.getType().equals(type);
            
            if (brand != null && !brand.equals("Brand Search:"))
                matches &= i.getBrand().equals(brand);
            
            if (productName != null && !productName.equals("Name Search:"))
                matches &= i.getProductName().equals(productName);
            
            if (price != null && !price.equals("Price Search:"))
                matches &= i.getPrice() == Utils.safeParseDouble(price);
            
            return matches;
        };
        
        return tableItems.filtered(filter);
    }
    
    
    @FXML
    private void purchaseSelectedItem() {
        Medicine item = purchase_table.getSelectionModel().getSelectedItem();
        if (item == null) return;
        System.out.println("item selected");
        
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        
        purchaseTypeBox.setValue(item.getType());
        purchaseBrandBox.setValue(item.getBrand());
        purchaseNameBox.setValue(item.getProductName());
        purchasePriceBox.setValue(String.valueOf(item.getPrice()));
        med_cost_label.setText("$" + (spinner.getValue() * item.getPrice()));
    }
    
    @FXML
    private void changeCost() {
        double price = Utils.safeParseDouble(purchasePriceBox.getValue());
        if(price != -1.0)
            med_cost_label.setText("$" + (spinner.getValue() * price));
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
        if (!alreadyMaximized) {
            Scene scene = dashboard.getScene();
            
            double initWidth = scene.getWidth();
            double initHeight = scene.getHeight();
            
            defaultWidth = (defaultWidth == 0) ? scene.getWidth() : defaultWidth;
            defaultHeight = (defaultHeight == 0) ? scene.getHeight() : defaultHeight;
            
            Utils.windowMaximize(dashboard, initWidth, initHeight, false);
        } else
            Utils.windowMaximize(dashboard, defaultWidth, defaultHeight, true);
        
        alreadyMaximized = !alreadyMaximized;
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