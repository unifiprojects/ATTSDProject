package com.maurosalani.project.attsd.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import com.maurosalani.project.attsd.controller.UserRestController;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

	@InjectMocks
	private UserRestController userRestController;

	@Mock
	private UserService userService;

	@Before
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(userRestController);
	}

	@Test
	public void testFindAllUsersWithEmptyDatabase() {
		when(userService.getAllUsers()).thenReturn(Collections.emptyList());
		
		given().
		when().
			get("/api/users").
		then().
			statusCode(200).
			assertThat().body(is(equalTo("[]")));
	}
	
	@Test
	public void testFindAllUsersWithExistingUsers() {
		User user1 = new User(1L, "user1", "pwd1"); 
		User user2 = new User(2L, "user2", "pwd2"); 
		when(userService.getAllUsers()).thenReturn(asList(user1, user2));
		
		given().
		when().
			get("/api/users").
		then().
		statusCode(200).
		contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
		assertThat().body(
				"id[0]", equalTo(1),
				"username[0]", equalTo("user1"),
				"password[0]", equalTo("pwd1"),
				"id[1]", equalTo(2),
				"username[1]", equalTo("user2"),
				"password[1]", equalTo("pwd2"));
		
	}
	
	@Test
	public void testFindUserByIdWhenNotFound() {
		when(userService.getUserById(anyLong())).thenReturn(null);
		
		given().
		when().
			get("/api/users/1").
		then().
			statusCode(200).
			assertThat().body(equalTo(""));	
		}

}
