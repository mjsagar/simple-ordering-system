package com.michael.sagar.simpleorderingsystem.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.michael.sagar.simpleorderingsystem.model.Customer;
import com.michael.sagar.simpleorderingsystem.model.Order;
import com.michael.sagar.simpleorderingsystem.model.Product;
import com.michael.sagar.simpleorderingsystem.repository.CustomerRepository;
import com.michael.sagar.simpleorderingsystem.repository.OrderRepository;
import com.michael.sagar.simpleorderingsystem.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
    @RequestMapping("/api/order")
    public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ProductRepository productRepository;

        @GetMapping
        @RequestMapping("/create")
        public ResponseEntity<Order> createOrders(@RequestBody String body) {
            ObjectMapper mapper = new ObjectMapper();
            try{
            Map<String,Object> mapBody = mapper.readValue(body,Map.class);
            Long cusId = Long.valueOf((int)mapBody.get("customer_id"));
            Customer customer = customerRepository.findOne(cusId);
            Product product = productRepository.findByProductName((String)mapBody.get("product_name"));
            Order order = new Order(customer,product,(int)mapBody.get("quantity"));
            orderRepository.saveAndFlush(order);
            return new ResponseEntity(order, HttpStatus.OK);
            }
            catch(Exception e){
                e.printStackTrace();
                return new ResponseEntity("error with original port", HttpStatus.EXPECTATION_FAILED);
            }
        }

    @GetMapping
    @RequestMapping("/get/id/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
       Order order = orderRepository.findOne(id);
       if(order!=null)
        return new ResponseEntity(order, HttpStatus.OK);
       else
        return new ResponseEntity("order with id :"+id+" not found", HttpStatus.NOT_FOUND);
    }

    @GetMapping
    @RequestMapping("/get/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return new ResponseEntity(orders, HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("/update")
    public ResponseEntity<Order> updateOrder(@RequestBody String body) {
        ObjectMapper mapper = new ObjectMapper();
        try{
            Map<String,Object> mapBody = mapper.readValue(body,Map.class);
            Long orderId = Long.valueOf((int)mapBody.get("order_id"));
            int quantity = (int)mapBody.get("quantity");
            Order order = orderRepository.findOne(orderId);
            order.setQuantity(quantity);
            orderRepository.saveAndFlush(order);
            return new ResponseEntity(order, HttpStatus.OK);
        }
        catch(Exception e){
            e.printStackTrace();
            return new ResponseEntity("error updating order", HttpStatus.EXPECTATION_FAILED);
        }
    }

    }
