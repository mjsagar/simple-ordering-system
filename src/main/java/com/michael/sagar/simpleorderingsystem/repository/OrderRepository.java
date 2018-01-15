package com.michael.sagar.simpleorderingsystem.repository;

import com.michael.sagar.simpleorderingsystem.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

//using JPA as this already has the methods I require
public interface OrderRepository extends JpaRepository<Order, Long> {
}