package com.michael.sagar.simpleorderingsystem.repository;

import com.michael.sagar.simpleorderingsystem.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

//using JPA as this already has the methods I require
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
