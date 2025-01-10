package com.example.li_pharmacy;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;

import java.sql.*;

public class SQLUtils {
    public static User register(String username, String password, String securityQuestion, String securityAnswer) {
        String sql = "insert into users (username, password, secQuestion, secAnswer) values (?, ?, ?, ?);";
        return runFormSQL(sql, username, password, securityQuestion, securityAnswer, false);
    }
    
    public static User login(String username, String password) {
        String sql = "select * from users where username = ? and password = ? limit 1;";
        return runFormSQL(sql, username, password, null, null, true);
    }
    
    // region Table
    public static ObservableList<Medicine> refreshTable() {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        String sql = "select * from medicine;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            ResultSet result = prepared.executeQuery();
            ObservableList<Medicine> data = FXCollections.observableArrayList();
            
            return getMedicineData(result, data);
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error Refreshing Table", "There was an error running the SQL information to refresh the table.");
            return null;
        }
    }
    
    public static void addItem(String id, String brand, String productName, double price, String type, String status, Date date, String imagePath) {
        Connection connection = connectDB();
        if (connection == null) return;
        
        String sql = "insert into medicine (medicineID, brand, productName, price, type, status, date, imagePath) values (?, ?, ?, ?, ?, ?, ?, ?);";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, id);
            prepared.setString(2, brand);
            prepared.setString(3, productName);
            prepared.setDouble(4, price);
            prepared.setString(5, type);
            prepared.setString(6, status);
            prepared.setDate(7, date);
            prepared.setString(8, imagePath);
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error In addItem", "There was an error running the SQL information to add to the table.");
        }
    }
    
    public static void updateItem(String id, String brand, String productName, String type, String status, double price, Date date, String imagePath) {
        Connection connection = connectDB();
        if (connection == null) return;
        
        String sql = "update medicine set medicineID = ?, brand = ?, productName = ?, type = ?, status = ?, price = ?, date = ?, imagePath = ? where medicineID = ?;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, id);
            prepared.setString(2, brand);
            prepared.setString(3, productName);
            prepared.setString(4, type);
            prepared.setString(5, status);
            prepared.setDouble(6, price);
            prepared.setDate(7, date);
            prepared.setString(8, imagePath);
            prepared.setString(9, id);
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error In updateItem", "There was an error running the SQL information to update the table.");
        }
    }
    
    public static void deleteItem(String id) {
        Connection connection = connectDB();
        if (connection == null) return;
        
        String sql = "delete from medicine where medicineID = ?;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, id);
            prepared.executeUpdate();
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error", "Error in deleteItem", "There was an error deleting information from the table.");
        }
    }
    
    public static ObservableList<Medicine> searchTable(String searchString) {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        String sql = "select * from medicine where medicineID like ? or brand like ? or productName like ? or type like ? or status like ?;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
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
    private static User runFormSQL(String sql, String username, String password, String securityQuestion, String securityAnswer, boolean query) {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
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
                            result.getString("secAnswer"));
            } else {
                prepared.executeUpdate();
                sql = "select * from users where username = ? and password = ? limit 1;";
                return runFormSQL(sql, username, password, null, null, true); // gets the user from the database after creating it, only does recursion once
            }
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error in runFormSQL", "Error Running SQL", "There was an error running the SQL information, or that user doesn't exist.");
        }
        return null;
    }
    
    public static User getUser(String username) {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        String sql = "select * from users where username = ? limit 1;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, username);
            ResultSet result = prepared.executeQuery();
            
            if (result.next())
                return new User(
                        result.getString(2),
                        result.getString(3),
                        result.getString(4),
                        result.getString(5)
                );
        } catch (Exception ignored) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error In getUser", "Error Getting User From Database", "There was an error getting a user from the database, please try again.");
        }
        return null;
    }
    
    public static void setPassword(String username, String password) {
        Connection connection = connectDB();
        if (connection == null) return;
        
        String sql = "update users set password = ? where username = ?;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
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