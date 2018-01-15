package com.michael.sagar.simpleorderingsystem.model;

import javax.persistence.*;

@Entity
@Table(name = "sos_product")
public class Product {

    @Id
    @Column(name = "productId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long productId;

    String productName;

    String productDescription;

    double unitPrice;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    //we want the product name to be unique so a product can be found by it
    @Column(unique=true)
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Product() {
    }

    public Product(String productName, String productDescription, double unitPrice) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.unitPrice = unitPrice;
    }

}


