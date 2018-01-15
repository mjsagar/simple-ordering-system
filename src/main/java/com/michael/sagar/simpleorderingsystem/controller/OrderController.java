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

import java.util.ArrayList;
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

    /**
     * This method create an order. it is called using /api/order/create
     * It requires JSON to be sent as the body of the request.
     * @param body this is required in the following structure  {"customer_id":1,"product_name":brick,"quantity":150}
     * @return returns an Order object in JOSN format
     */
    @GetMapping
    @RequestMapping("/create")
    public ResponseEntity<Order> createOrders(@RequestBody String body) {
        //creating a mapper to parse the JSON body
        ObjectMapper mapper = new ObjectMapper();
        try {
            //reading the JSON body
            Map<String, Object> mapBody = mapper.readValue(body, Map.class);
            //pulling the customer_id from the JSON body
            Long cusId = Long.valueOf((int) mapBody.get("customer_id"));
            //using JPA to find the customer instance
            Customer customer = customerRepository.findOne(cusId);
            //using JPA to find the customers name by product name
            Product product = productRepository.findByProductName((String) mapBody.get("product_name"));
            //creating a new order for the customer
            Order order = new Order(customer, product, (int) mapBody.get("quantity"));
            //saving the order
            orderRepository.saveAndFlush(order);
            //returning the order as JSON
            return new ResponseEntity(order, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("error with original port", HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * This request requires the id to be in the GET url and returns that Order in JOSON format if found
     * requested by /api/order/get/id/${id of the order}
     * @param id this is the ID of the order required
     * @return return the Order as JOSN
     */
    @GetMapping
    @RequestMapping("/get/id/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        //using JPA to find the order requested
        Order order = orderRepository.findOne(id);
        //if the order has been found then return it as JSON
        if (order != null)
            return new ResponseEntity(order, HttpStatus.OK);
        else
            return new ResponseEntity("order with id :" + id + " not found", HttpStatus.NOT_FOUND);
    }

    /**
     * this will return all orders for all customers
     * requested by /api/order/get/all
     * @return returns a list or Orders in JOSN format
     */
    @GetMapping
    @RequestMapping("/get/all")
    public ResponseEntity<List<Order>> getAllOrders() {
        //using JPA to find all orders
        List<Order> orders = orderRepository.findAll();
        //returning all orders as JSON
        return new ResponseEntity(orders, HttpStatus.OK);
    }

    /**
     * this will update an order, requested by /api/order/update currently this will only update the quantity.
     * If the order has already been dispatched a 400 is returned
     * @param body this is required in the following structure  {"order_id":1,"quantity":150}
     * @return returns a 400 or OK with the Order in JOSN format
     */
    @GetMapping
    @RequestMapping("/update")
    public ResponseEntity<Order> updateOrder(@RequestBody String body) {
        //creating mapper to parse JSON body
        ObjectMapper mapper = new ObjectMapper();
        try {
            //parsing the JSON body
            Map<String, Object> mapBody = mapper.readValue(body, Map.class);
            //extracting the order_id from the JOSN body
            Long orderId = Long.valueOf((int) mapBody.get("order_id"));
            //using JPA to find the Order
            Order order = orderRepository.findOne(orderId);
            //checking if the order has previously been dispatched. If it has then returns BAD REQUEST
            if(order.getDispatched())
                return new ResponseEntity("order already dispatched", HttpStatus.BAD_REQUEST);
            //extracting the quantity from the JSON body
            int quantity = (int) mapBody.get("quantity");
            //setting the new quantity
            order.setQuantity(quantity);
            //saving the order request
            orderRepository.saveAndFlush(order);
            //returning the Order object
            return new ResponseEntity(order, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("error updating order", HttpStatus.EXPECTATION_FAILED);
        }
    }


    /**
     * this will fulful an order by setting dispatched to true
     * requested by /api/order/fulfil
     * @param body the body must be a JOSN array of order ids to fulfil
     * @return returns all the fulfilled orders
     */
    @GetMapping
    @RequestMapping("/fulfil")
    public ResponseEntity<List<Order>> fulfil(@RequestBody String body) {
        //created a JSON mapper to pars the JOSN body
        ObjectMapper mapper = new ObjectMapper();
        try {
            //create a list to hold the dispatched orders
            List<Order> orders = new ArrayList<>();
            //parse the JOSN body in to a list of strings
            List<Integer> listBody = mapper.readValue(body, List.class);
            //looping through all the order ids
            for (Integer orderId : listBody) {
                //using JPA to find the order by id, should really do this using a findAll and then iterate the Orders
                Order order = orderRepository.findOne(Long.valueOf(orderId));
                //set order dispatched to true
                order.setDispatched(true);
                ///save the order
                orderRepository.saveAndFlush(order);
                //add the order to the orders list
                orders.add(order);
            }
            //return all the dispatched orders as JSON
            return new ResponseEntity(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity("error fulfilling order", HttpStatus.EXPECTATION_FAILED);
        }
    }

}
