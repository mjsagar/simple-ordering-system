package com.michael.sagar.simpleorderingsystem.repository;

import com.michael.sagar.simpleorderingsystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

//using JPA as this already has the methods I require
public interface ProductRepository extends JpaRepository<Product, Long> {

    //added this method so the products can be found by productName
    public Product findByProductName(String ProductName);

}
