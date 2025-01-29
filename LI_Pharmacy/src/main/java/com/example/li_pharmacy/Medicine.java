package com.example.li_pharmacy;

import java.sql.Date;

public class Medicine {
    private String id, brand, productName, type, status, imagePath;
    private double price;
    private Date date;
    
    public Medicine(String brand, String productName, String type, double price) {
        this.brand = brand;
        this.productName = productName;
        this.type = type;
        this.price = price;
    }
    
    public Medicine(String id, String brand, String productName, String type,
                    String status, double price, Date date, String imagePath) {
        this.id = id;
        this.brand = brand;
        this.productName = productName;
        this.type = type;
        this.status = status;
        this.price = price;
        this.date = date;
        this.imagePath = imagePath;
    }
    
    // region Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getImagePath() {
        return imagePath;
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    // endregion
}