package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * This class is created according to the instructions found in video
 * https://learn.udacity.com/nanodegrees/nd035/parts/cd0629/lessons/20a8cdf0-d88c-42c9-bc1e-b1559a9e80da/concepts/42ab6175-983a-490d-be61-42cc2a7b658f
 */
public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController,"userRepository",userRepo);
        TestUtils.injectObjects(userController,"cartRepository",cartRepo);
        TestUtils.injectObjects(userController,"bCryptPasswordEncoder",encoder);
    }
    public CreateUserRequest getCreateUserRequest(){
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername(TestUtils.USER_NAME);
        r.setPassword(TestUtils.PASSWORD);
        r.setConfirmPassword(TestUtils.PASSWORD);
        return r;
    }

    @Test
    public void create_user_happy_path() throws Exception{
        when(encoder.encode(TestUtils.PASSWORD)).thenReturn("thisIsHashed");

        final ResponseEntity<User> responseCreate = userController.createUser(getCreateUserRequest());

        assertNotNull(responseCreate);
        assertEquals(200, responseCreate.getStatusCodeValue());

        User u = responseCreate.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals(TestUtils.USER_NAME, u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());
    }

    @Test
    public void createUserBadRequest() throws Exception{
        CreateUserRequest r = getCreateUserRequest();
        r.setConfirmPassword("nothing");
        final ResponseEntity<User> responseBad = userController.createUser(r);
        assertNotNull(responseBad);
        assertEquals(400, responseBad.getStatusCodeValue());
    }

    @Test
    public void findUserHappyPath() {

        User userTest = new User(1L, TestUtils.USER_NAME, TestUtils.PASSWORD);
        when(userRepo.findByUsername(TestUtils.USER_NAME)).thenReturn(userTest);

        final ResponseEntity<User> responseByName = userController.findByUserName(TestUtils.USER_NAME);
        User u = responseByName.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals(TestUtils.USER_NAME, u.getUsername());

        when(userRepo.findById(1L)).thenReturn(Optional.of(userTest));

        final ResponseEntity<User> responseByID = userController.findById(userTest.getId());
        u = responseByID.getBody();
        assertNotNull(u);
        assertEquals(1L, u.getId());
        assertEquals(TestUtils.USER_NAME, u.getUsername());
    }

    @Test
    public void findUserUnhappyPath() {
        final ResponseEntity<User> responseByName = userController.findByUserName("notAName");
        assertNull(responseByName.getBody());
        final ResponseEntity<User> responseByID = userController.findById(2L);
        assertNull(responseByID.getBody());
    }
}