package com.example.li_pharmacy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class SQLUtils {
    public static User register(String username, String password, String securityQuestion, String securityAnswer) {
        String sql = "insert into users (username, password, secQuestion, secAnswer) values (?, ?, ?, ?);";
        return runFormSQL(sql, new User(username, password, securityQuestion, securityAnswer), false);
    }
    
    public static User login(String username, String password) {
        String sql = "select * from users where username = ? and password = ? limit 1;";
        return runFormSQL(sql, new User(username, password), true);
    }
    
    // region Table
    public static ObservableList<Medicine> refreshTable() {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from medicine;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            ResultSet result = prepared.executeQuery();
            ObservableList<Medicine> data = FXCollections.observableArrayList();
            
            return getMedicineData(result, data);
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error Refreshing Table", "There was an error running the SQL information to refresh the table.");
            return null;
        }
    }
    
    public static void addItem(Medicine medicine) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "insert into medicine (medicineID, brand, productName, price, type, status, date, imagePath) values (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            prepared.setString(1, medicine.getId());
            prepared.setString(2, medicine.getBrand());
            prepared.setString(3, medicine.getProductName());
            prepared.setDouble(4, medicine.getPrice());
            prepared.setString(5, medicine.getType());
            prepared.setString(6, medicine.getStatus());
            prepared.setDate(7, medicine.getDate());
            prepared.setString(8, medicine.getImagePath());
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error In addItem", "There was an error running the SQL information to add to the table.");
        }
    }
    
    public static void updateItem(Medicine medicine) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "update medicine set medicineID = ?, brand = ?, productName = ?, type = ?, status = ?, price = ?, date = ?, imagePath = ? where medicineID = ?;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            prepared.setString(1, medicine.getId());
            prepared.setString(2, medicine.getBrand());
            prepared.setString(3, medicine.getProductName());
            prepared.setString(4, medicine.getType());
            prepared.setString(5, medicine.getStatus());
            prepared.setDouble(6, medicine.getPrice());
            prepared.setDate(7, medicine.getDate());
            prepared.setString(8, medicine.getImagePath());
            prepared.setString(9, medicine.getId());
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error In updateItem", "There was an error running the SQL information to update the table.");
        }
    }
    
    public static void deleteItem(String id) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "delete from medicine where medicineID = ?;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            prepared.setString(1, id);
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error in deleteItem", "There was an error deleting information from the table.");
        }
    }
    
    public static ObservableList<Medicine> searchTable(String searchString) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from medicine where medicineID like ? or brand like ? or productName like ? or type like ? or status like ?;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            for (int i = 1; i <= 4; i++)
                prepared.setString(i, "%" + searchString + "%");
            prepared.setString(5, searchString + "%");
            ResultSet result = prepared.executeQuery();
            ObservableList<Medicine> data = FXCollections.observableArrayList();
            
            return getMedicineData(result, data);
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error Searching Table", "There was an error running the SQL information to refresh the table.");
            return null;
        }
    }
    // endregion
    
    // region Utils
    private static User runFormSQL(String sql, User user, boolean query) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            PreparedStatement prepared = connection.prepareStatement(sql);
            String username = user.getUsername(), password = user.getPassword(), securityQuestion = user.getSecurityQuestion(), securityAnswer = user.getSecurityAnswer();
            prepared.setString(1, username);
            prepared.setString(2, password);
            if (securityQuestion != null)
                prepared.setString(3, securityQuestion);
            if (securityAnswer != null)
                prepared.setString(4, securityAnswer);
            
            if (query) {
                ResultSet result = prepared.executeQuery();
                
                if (result.next())
                    return new User(
                            result.getString("username"),
                            result.getString("password"),
                            result.getString("secQuestion"),
                            result.getString("secAnswer")
                    );
            } else {
                prepared.executeUpdate();
                sql = "select * from users where username = ? and password = ? limit 1;";
                return runFormSQL(sql, new User(username, password), true); // gets the user from the database after creating it, only does recursion once
            }
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error in runFormSQL", "Error Running SQL", "There was an error running the SQL information, or that user doesn't exist.");
        }
        return null;
    }
    
    public static User getUser(String username) {
        try (Connection connection = connectDB()) {
            if (connection == null) return null;
            
            String sql = "select * from users where username = ? limit 1;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            prepared.setString(1, username);
            ResultSet result = prepared.executeQuery();
            
            if (result.next())
                return new User(
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("secQuestion"),
                        result.getString("secAnswer")
                );
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error In getUser", "Error Getting User From Database", "There was an error getting a user from the database, please try again.");
        }
        return null;
    }
    
    public static void setPassword(String username, String password) {
        try (Connection connection = connectDB()) {
            if (connection == null) return;
            
            String sql = "update users set password = ? where username = ?;";
            PreparedStatement prepared = connection.prepareStatement(sql);
            
            prepared.setString(1, password);
            prepared.setString(2, username);
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error In setPassword", "Error setting a new password", "There was an error setting a new password for this user, please try again.");
        }
    }
    
    private static ObservableList<Medicine> getMedicineData(ResultSet result, ObservableList<Medicine> data) {
        try {
            while (result.next())
                data.add(new Medicine(
                        result.getString("medicineID"),
                        result.getString("brand"),
                        result.getString("productName"),
                        result.getString("type"),
                        result.getString("status"),
                        result.getDouble("price"),
                        result.getDate("date"),
                        result.getString("imagePath")));
            return data;
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Medicine Data Error", "Error Compiling Medicine Data", "Database could not be connected to, please try again.");
            return null;
        }
    }
    
    private static Connection connectDB() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/pharmacy", "root", "password");
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Connection Error", "Error Connecting To Auto Database", "Database could not be connected to, please try again.");
            return null;
        }
    }
    // endregion
}