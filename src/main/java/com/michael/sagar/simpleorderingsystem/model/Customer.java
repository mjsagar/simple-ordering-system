package com.michael.sagar.simpleorderingsystem.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "sos_customer")
public class Customer {
    @Id
    @Column(name = "customerId")
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long customerId;
    String cusName;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinColumn(name = "orderId")
    @JoinTable(name = "customer_order", joinColumns = {@JoinColumn(name = "customerId")}, inverseJoinColumns = {@JoinColumn(name = "orderId")})
    Set<Order> orders = new HashSet<Order>(0);

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }


    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Customer() {
    }

    public Customer(String cusName) {
        this.cusName = cusName;
    }
}
