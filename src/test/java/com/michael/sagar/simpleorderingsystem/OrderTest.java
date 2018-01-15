package com.michael.sagar.simpleorderingsystem;

import com.michael.sagar.simpleorderingsystem.model.Customer;
import com.michael.sagar.simpleorderingsystem.model.Order;
import com.michael.sagar.simpleorderingsystem.model.Product;
import com.michael.sagar.simpleorderingsystem.repository.CustomerRepository;
import com.michael.sagar.simpleorderingsystem.repository.OrderRepository;
import com.michael.sagar.simpleorderingsystem.repository.ProductRepository;
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
import static org.mockito.Matchers.anyLong;
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

    @Autowired
    private ProductRepository productRepositoryTest;

    @Autowired
    private CustomerRepository customerRepositoryTest;

    @Autowired
    private OrderRepository orderRepositoryTest;



    // setting up customer
    private Customer customer1;

    // setting up product for the customer to buy
    private Product product1;

    //Stage 1 tests

    @Before
    public void setup(){
        customer1 = new Customer("cus1");
        customerRepositoryTest.saveAndFlush(customer1);
        product1 = new Product("brick1", "red brick", 0.95);
        productRepositoryTest.saveAndFlush(product1);
    }

    @Test
    public void submit_new_order_return_order_number() {

        //Given - a customer wants to buy any number of bricks
        try {
            //when  - a create order request for a number of bricks is submitted

            //posting to /api/order/create with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(post("/api/order/create")
                    // adding a JSON string to the request
                    .content("{\"customer_id\":" + customer1.getCustomerId() + ",\"product_name\":\""
                            + product1.getProductName() + "\",\"quantity\":150}")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            // then - an order reference is returned and is unique to submission
            mockMvc1.andExpect(jsonPath("$.orderId").value(1));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Test
    public void order_created_requested_by_get_quantity_returned() {

        //Given - A customer has submitted an order for some bricks
        try {
            //when  - an order request is submitted with a valid reference
            Order order1 = order1 = new Order(customer1, product1, 150);
            orderRepositoryTest.saveAndFlush(order1);
            //posting to /api/order/get with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(get("/api/order/get/id/"+order1.getOrderId()));
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
            Customer tempCustomer = customerRepositoryTest.saveAndFlush(new Customer("customer"+i));
            orderRepositoryTest.saveAndFlush(new Order(tempCustomer, product1, i*5));
        }
    }

    @Test
    public void many_customer_have_submitted_orders_return_all_orders() {

        createTenOrdersWithDifferentCustomers();
        //Given - Many customer have submitted orders for bricks
        try {
            //when  - a get orders request is submitted
            //posting to /api/order/get/all to retrive all orders
            ResultActions mockMvc1 = this.mockMvc.perform(get("/api/order/get/all"));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            //then all orders have reference and quantity
            for (int i = 0; i < 10 ; i++) {
                mockMvc1.andExpect(jsonPath("$["+i+"].orderId").value(i+1));
                mockMvc1.andExpect(jsonPath("$["+i+"].quantity").exists());
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    // stage 2 tests

    @Test
    public void update_quantity_in_order_by_id() {

        //Given - A customer has ordered a number of bricks
        try {
            //when  - a update order request for an exsiting order reference and number of bricks is submitted
            Order orderToUpdate = new Order(customer1,product1,100);
            orderRepositoryTest.saveAndFlush(orderToUpdate);

            //posting to /api/order/update with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(post("/api/order/update")
                    // adding a JSON string to the request
                    .content("{\"order_id\":" + orderToUpdate.getOrderId() + ",\"quantity\":150}")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            // then - an orderReference is returned with order reference that is unique to the submission
            mockMvc1.andExpect(jsonPath("$.orderId").value(orderToUpdate.getOrderId()));
            // checking the new quantity is correct
            mockMvc1.andExpect(jsonPath("$.quantity").value(150));

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


    //stage 3
    @Test
    public void fullfil_an_order_by_id() {

        //Given - An order exists
        try {
            //when  - A fulfil order request is submitted for a valid order reference
            Order orderToUpdate = new Order(customer1,product1,100);
            orderRepositoryTest.saveAndFlush(orderToUpdate);

            //posting to /api/order/fulfil with JSON order details
            ResultActions mockMvc1 = this.mockMvc.perform(post("/api/order/fulfil")
                    // adding a JSON string to the request -  deccided to make this a JSON list of ids
                    .content("[{\"order_id\":" + orderToUpdate.getOrderId()+"}]")
                    .contentType(MediaType.APPLICATION_JSON));
            mockMvc1.andDo(print()).andExpect(status().isOk());
            // then - the order is marked as dispatched
            mockMvc1.andExpect(jsonPath("$.orderId").value(orderToUpdate.getOrderId()));
            // checking the new quantity is correct
            mockMvc1.andExpect(jsonPath("$.quantity").value(150));

            Order updatedOrder = orderRepositoryTest.findOne(orderToUpdate.getOrderId());
            assert updatedOrder.getDispatched()==true;

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
