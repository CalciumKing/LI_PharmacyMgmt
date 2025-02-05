module com.example.li_pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires io;
    
    
    opens com.example.li_pharmacy to javafx.fxml;
    exports com.example.li_pharmacy;
}