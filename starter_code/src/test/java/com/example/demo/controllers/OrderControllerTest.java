package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private UserRepository userRepo = mock(UserRepository.class);
    private OrderRepository orderRepo = mock(OrderRepository.class);
    @Before
    public void setUp(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController,"userRepository",userRepo);
        TestUtils.injectObjects(orderController,"orderRepository",orderRepo);
    }

    @Test
    public void submitHappyPath() {

        Cart cart = new Cart();
        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD, cart);
        ArrayList<Item> items = new ArrayList<>();
        BigDecimal total = new BigDecimal(0);
        Item beef = new Item(1L, "Beef", new BigDecimal(1.7), "meat from cows");
        items.add(beef);
        total.add(beef.getPrice());
        Item pork = new Item(2L, "Pork", new BigDecimal(1.1), "meat from pigs");
        items.add(pork);
        total.add(pork.getPrice());
        Item mutton = new Item(3L, "Mutton", new BigDecimal(1.6), "meat from sheep");
        items.add(mutton);
        total.add(mutton.getPrice());
        Item beefHighQuality = new Item(4L, "Beef", new BigDecimal(2.7), "high quality beef from Australia");
        items.add(beefHighQuality);
        total.add(beefHighQuality.getPrice());
        cart.setItems(items);
        cart.setTotal(total);
        cart.setUser(userTest);

        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);

        ResponseEntity<UserOrder> response = orderController.submit(TestUtils.USER_NAME);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrder = response.getBody();
        assertEquals(userOrder.getItems(), items);
        assertEquals(userOrder.getUser().getUsername(), userTest.getUsername());
        assertEquals(userOrder.getTotal(), total);
    }

    @Test
    public void submitUnhappyPath() {
        // user missing
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit(TestUtils.USER_NAME);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());
    }

    @Test
    public void getOrdersForUserHappyPath() {
        Cart cart = new Cart();
        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD, cart);
        ArrayList<Item> items = new ArrayList<>();
        BigDecimal total = new BigDecimal(0);
        Item beef = new Item(1L, "Beef", new BigDecimal(1.7), "meat from cows");
        items.add(beef);
        total.add(beef.getPrice());
        Item pork = new Item(2L, "Pork", new BigDecimal(1.1), "meat from pigs");
        items.add(pork);
        total.add(pork.getPrice());
        Item mutton = new Item(3L, "Mutton", new BigDecimal(1.6), "meat from sheep");
        items.add(mutton);
        total.add(mutton.getPrice());
        Item beefHighQuality = new Item(4L, "Beef", new BigDecimal(2.7), "high quality beef from Australia");
        items.add(beefHighQuality);
        total.add(beefHighQuality.getPrice());
        cart.setItems(items);
        cart.setTotal(total);
        cart.setUser(userTest);
        UserOrder userOrder = UserOrder.createFromCart(cart);
        List<UserOrder> userOrderList = Arrays.asList(userOrder);

        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);
        when(orderRepo.findByUser(userTest)).thenReturn(userOrderList);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.USER_NAME);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        List<UserOrder> userOrderListResponse = response.getBody();
        assertEquals(userOrderListResponse.size(), 1);
        UserOrder userOrderResponse = userOrderListResponse.get(0);
        assertEquals(userOrderResponse.getItems(), items);
        assertEquals(userOrderResponse.getUser().getUsername(), userTest.getUsername());
        assertEquals(userOrderResponse.getTotal(), total);

    }

    @Test
    public void getOrdersForUserUnhappyPath() {
        // user missing
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(null);
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(TestUtils.USER_NAME);
        assertNotNull(response);
        assertEquals(404,response.getStatusCodeValue());

    }
}
