package com.example.li_pharmacy;

import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLUtils {
    public static User register(String username, String password, String securityQuestion, String securityAnswer) {
        String sql = "insert into users (username, password, secQuestion, secAnswer) values (?, ?, ?, ?);";
        return runFormSQL(sql, username, password, securityQuestion, securityAnswer, false);
    }
    
    public static User login(String username, String password) {
        String sql = "select * from users where username = ? and password = ? limit 1;";
        return runFormSQL(sql, username, password, null, null, true);
    }
    
    private static User runFormSQL(String sql, String username, String password, String securityQuestion, String securityAnswer, boolean query) {
        Connection connection = connectDB();
        if (connection == null) return null;
        
        try (PreparedStatement prepared = connection.prepareStatement(sql)) {
            prepared.setString(1, username);
            prepared.setString(2, password);
            if(securityQuestion != null)
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
                return runFormSQL(sql, username, password, securityAnswer, null, true); // gets the user from the database after creating it, only does recursion once
            }
        } catch (Exception e) {
            Utils.errorAlert(Alert.AlertType.ERROR, "Error in runFormSQL", "Error Running SQL", "There was an error running the SQL information, or that user doesn't exist.");
        }
        return null;
    }
    
    // region Utils
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
            Utils.errorAlert(Alert.AlertType.ERROR, "Error In setPassword", "Error setting a new password", "There was an error setting a new password for this user, please try again.");
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