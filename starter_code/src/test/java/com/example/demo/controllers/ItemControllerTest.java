package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepo = mock(ItemRepository.class);

    private static final String BEEF = "Beef";

    private static final String PORK = "Pork";

    private static final String MUTTON = "Mutton";

    @Before
    public void setUp(){
        itemController = new ItemController();
        TestUtils.injectObjects(itemController,"itemRepository",itemRepo);
    }

    @Test
    public void getAllItems(){
        ArrayList<Item> items = new ArrayList<>();
        Item beef = new Item(1L, "Beef", new BigDecimal(1.7), "meat from cows");
        items.add(beef);
        Item pork = new Item(2L, "Pork", new BigDecimal(1.1), "meat from pigs");
        items.add(pork);
        Item mutton = new Item(3L, "Mutton", new BigDecimal(1.6), "meat from sheep");
        items.add(mutton);
        Item beefHighQuality = new Item(4L, "Beef", new BigDecimal(2.7), "high quality beef from Australia");
        items.add(beefHighQuality);

        when(itemRepo.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());
        assertEquals(items, response.getBody());
    }

    @Test
    public void findItemsHappyPath() {
        ArrayList<Item> items = new ArrayList<>();
        Item beef = new Item(1L, "Beef", new BigDecimal(1.7), "meat from cows");
        items.add(beef);
        Item pork = new Item(2L, "Pork", new BigDecimal(1.1), "meat from pigs");
        items.add(pork);
        Item mutton = new Item(3L, "Mutton", new BigDecimal(1.6), "meat from sheep");
        items.add(mutton);
        Item beefHighQuality = new Item(4L, "Beef", new BigDecimal(2.7), "high quality beef from Australia");
        items.add(beefHighQuality);

        // find by name
        when(itemRepo.findByName("Beef")).thenReturn(Arrays.asList(beef, beefHighQuality));
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName("Beef");
        assertNotNull(responseByName);
        assertEquals(200, responseByName.getStatusCodeValue());
        assertEquals(Arrays.asList(beef, beefHighQuality), responseByName.getBody());


        // find by id
        when(itemRepo.findById(3L)).thenReturn(Optional.of(mutton));
        ResponseEntity<Item> responseID = itemController.getItemById(3L);
        assertNotNull(responseID);
        assertEquals(200, responseID.getStatusCodeValue());
        assertEquals(mutton, responseID.getBody());

    }

    @Test
    public void findItemsUnhappyPath() {
        // find by name
        ResponseEntity<List<Item>> responseByName = itemController.getItemsByName("Chicken");
        assertNotNull(responseByName);
        assertEquals(404, responseByName.getStatusCodeValue());
        assertNull(responseByName.getBody());

        // find by id
        ResponseEntity<Item> responseID = itemController.getItemById(7L);
        assertNotNull(responseID);
        assertEquals(404, responseID.getStatusCodeValue());
        assertNull(responseID.getBody());

    }
}
