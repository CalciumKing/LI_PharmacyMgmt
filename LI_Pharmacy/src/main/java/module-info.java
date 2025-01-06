module com.example.li_pharmacy {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    
    
    opens com.example.li_pharmacy to javafx.fxml;
    exports com.example.li_pharmacy;
}