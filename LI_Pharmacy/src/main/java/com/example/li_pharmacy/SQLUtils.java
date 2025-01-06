package com.example.li_pharmacy;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLUtils {
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
    
    public static String[] getSecurityInfo(String username) {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        String sql = "select secQuestion, secAnswer from users where username = ? limit 1;";
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, username);
            ResultSet result = prepared.executeQuery();
            
            if (result.next())
                return new String[] {
                        result.getString(1),
                        result.getString(2)
                };
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
            Utils.errorAlert(Alert.AlertType.ERROR, "Error In getUser", "Error Getting User From Database", "There was an error getting a user from the database, please try again.");
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
}
