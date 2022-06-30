package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private ItemRepository itemRepo = mock(ItemRepository.class);
    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController,"userRepository",userRepo);
        TestUtils.injectObjects(cartController,"cartRepository",cartRepo);
        TestUtils.injectObjects(cartController,"itemRepository",itemRepo);
    }



    @Test
    public void addAndRemoveHappyPath() {
        //add
        Cart cart = new Cart();
        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD, cart);
        ModifyCartRequest addCartRequest = new ModifyCartRequest();
        addCartRequest.setUsername(TestUtils.USER_NAME);
        addCartRequest.setItemId(1L);
        addCartRequest.setQuantity(2);
        Item beef = new Item(1L,"Beef", new BigDecimal(1.7), "meat from cows");

        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(beef));

        ResponseEntity<Cart> responseAdd = cartController.addTocart(addCartRequest);
        assertNotNull(responseAdd);
        assertEquals(200, responseAdd.getStatusCodeValue());
        Cart cartAdd = responseAdd.getBody();
        assertEquals(cartAdd.getItems(), Arrays.asList(beef, beef));

        // remove
        ModifyCartRequest removeFromCartRequest = new ModifyCartRequest();
        removeFromCartRequest.setUsername(TestUtils.USER_NAME);
        removeFromCartRequest.setItemId(1L);
        removeFromCartRequest.setQuantity(1);

        ResponseEntity<Cart> responseRemove = cartController.removeFromcart(removeFromCartRequest);
        assertNotNull(responseRemove);
        assertEquals(200, responseRemove.getStatusCodeValue());
        Cart cartRemove = responseRemove.getBody();
        assertEquals(cartRemove.getItems(), Arrays.asList(beef));
    }

    @Test
    public void addUnhappyPath() {
        // user missing
        ModifyCartRequest addCartRequest = new ModifyCartRequest();
        addCartRequest.setUsername(TestUtils.USER_NAME);
        addCartRequest.setItemId(1L);
        addCartRequest.setQuantity(2);
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserMissing = cartController.addTocart(addCartRequest);
        assertNotNull(responseUserMissing);
        assertEquals(404, responseUserMissing.getStatusCodeValue());

        // item missing
        Cart cart = new Cart();
        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD, cart);
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemMissing = cartController.addTocart(addCartRequest);
        assertNotNull(responseItemMissing);
        assertEquals(404, responseItemMissing.getStatusCodeValue());
    }

    @Test
    public void removeUnhappyPath(){
        // remove
        ModifyCartRequest removeFromCartRequest = new ModifyCartRequest();
        removeFromCartRequest.setUsername(TestUtils.USER_NAME);
        removeFromCartRequest.setItemId(1L);
        removeFromCartRequest.setQuantity(1);
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(null);
        ResponseEntity<Cart> responseUserMissing = cartController.removeFromcart(removeFromCartRequest);
        assertNotNull(responseUserMissing);
        assertEquals(404, responseUserMissing.getStatusCodeValue());

        // item missing
        Cart cart = new Cart();
        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD, cart);
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Cart> responseItemMissing = cartController.removeFromcart(removeFromCartRequest);
        assertNotNull(responseItemMissing);
        assertEquals(404, responseItemMissing.getStatusCodeValue());
    }
}
