package com.michael.sagar.simpleorderingsystem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderTest {

    @Autowired
    private MockMvc mockMvc;


    // setting up customer
    private Customer customer1;

    // setting up product for the customer to buy
    private Product product1;

    // setting up product for the customer to buy
    private Order order1;

    @Before
    public void setup(){
        customer1 = new Customer("cus1");
        product1 = new Product("brick1", "red brick", 0.95);
    }

    @Test
    public void submit_new_order_return_order_number() {

        //Given - a customer wants to buy any number of bricks
        try {

            //when  - a create order request for a number of bricks is submitted

            //posting to /api/order/create with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(post("/api/order/create")
                    // adding a JSON string to the request
                    .content("{\"customer_id\":" + cus1.getCustomerId() + ",\"product_name\":\""
                            + product1.getProductName() + "\",\"quantity\":150}")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            // then - an order reference is returned and is unique to submission
            mockMvc1.andExpect(jsonPath("$.orderId").value(anyInt()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setupOrder(){
        order1 = new Order(customer1, product1, 150);
    }

    @Test
    public void order_created_requested_by_get_quantity_returned() {

        //Given - A customer has submitted an order for some bricks
        try {
            //when  - an order request is submitted with a valid reference
            setupOrder();
            //posting to /api/order/get with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(get("/api/order/get/id/"+order.getOrderId()));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            // then - an order reference is returned and is unique to submission
            mockMvc1.andExpect(jsonPath("$.quantity").value(order1.getQuantity()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void createTenOrdersWithDifferentCustomers(){

        for (int i = 0; i < 10 ; i++) {
            new Customer("customer"+i);
            new Order(customer1, product1, i*5);
        }
    }

    @Test
    public void many_customer_have_submitted_orders_return_all_orders() {

        createTenOrdersWithDifferentCustomers();
        //Given - Many customer have submitted orders for bricks
        try {
            //when  - a get orders request is cubmitted
            setupOrder();
            //posting to /api/order/get/all to retrive all orders
            ResultActions mockMvc1 = this.mockMvc.perform(get("/api/order/get/all"));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            //then all orders have reference and quantity
            for (int i = 0; i < 10 ; i++) {
                mockMvc1.andExpect(jsonPath("$["+i+"].orderId").value(anyInt()));
                mockMvc1.andExpect(jsonPath("$["+i+"].quantity").value(anyInt()));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

}
