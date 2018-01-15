package com.michael.sagar.simpleorderingsystem.model;

import javax.persistence.*;

@Entity
@Table(name = "sos_order")
public class Order {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long orderId;
    Boolean dispatched;

    @ManyToOne
    @JoinColumn(name = "productId")
    Product product;

    @ManyToOne
    @JoinColumn(name = "customerId")
    Customer customer;

    int quantity;

    double subtotal;


    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderNo) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.subtotal = this.product.unitPrice * quantity;
        this.quantity = quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public Boolean getDispatched() {
        return dispatched;
    }

    public void setDispatched(Boolean dispatched) {
        this.dispatched = dispatched;
    }

    public Order() {
    }

    public Order(Customer customer, Product product, int quantity) {
        this.customer = customer;
        this.product = product;
        this.quantity = quantity;
        this.subtotal = product.unitPrice * quantity;
        this.dispatched = false;
    }

}
