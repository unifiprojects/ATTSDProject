package com.maurosalani.project.attsd.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.maurosalani.project.attsd.exception.BadRequestException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception_handler.GlobalExceptionHandler;
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
		HandlerExceptionResolver handlerExceptionResolver = initGlobalExceptionHandlerResolvers();

		RestAssuredMockMvc.standaloneSetup(MockMvcBuilders.standaloneSetup(userRestController)
				.setHandlerExceptionResolvers(handlerExceptionResolver));
	}

	/**
	 * Necessary to register the exception handler for these unit tests
	 * 
	 * @return
	 */
	private HandlerExceptionResolver initGlobalExceptionHandlerResolvers() {
		StaticApplicationContext applicationContext = new StaticApplicationContext();
		applicationContext.registerSingleton("exceptionHandler", GlobalExceptionHandler.class);

		WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		return webMvcConfigurationSupport.handlerExceptionResolver();
	}

	@Test
	public void testFindAllUsersWithEmptyDatabase() {
		when(userService.getAllUsers()).thenReturn(Collections.emptyList());

		given().
		when().
			get("/api/users").
		then().
			statusCode(200).
			assertThat().
				body(is(equalTo("[]")));
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
			assertThat().
			body("id[0]", equalTo(1), 
				"username[0]", equalTo("user1"), 
				"password[0]", equalTo("pwd1"),
				"id[1]", equalTo(2), 
				"username[1]", equalTo("user2"), 
				"password[1]", equalTo("pwd2"));

	}

	@Test
	public void testFindUserByIdWhenNotFound() throws UserNotFoundException {
		when(userService.getUserById(anyLong())).thenThrow(UserNotFoundException.class);

		given().
		when().
			get("/api/users/id/1").
		then().	
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testFindUserByIdWithExistingUser() throws UserNotFoundException {
		when(userService.getUserById(anyLong())).thenReturn(new User(1L, "username", "pwd"));
		
		given().
		when().
			get("/api/users/id/1").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"username", equalTo("username"), 
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testFindUserByUsernameWhenNotFound() throws UserNotFoundException {
		when(userService.getUserByUsername(anyString())).thenThrow(UserNotFoundException.class);
		
		given().
		when().
			get("/api/users/username/testName").
		then().	
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testFindUserByUsernameWithExistingUser() throws UserNotFoundException {
		when(userService.getUserByUsername(anyString())).thenReturn(new User(1L, "testName", "pwd"));
		
		given().
		when().
			get("/api/users/username/testName").
		then().	
			statusCode(200).
			body("id", equalTo(1), 
				"username", equalTo("testName"), 
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWithNoMatches()  {
		when(userService.getUsersByUsernameLike("testUsername")).thenReturn(Collections.emptyList());
		
		given().
		when().
			get("/api/users/usernamelike/testUsername").
		then().
			statusCode(200).
			contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
		assertThat().
			body(is(equalTo("[]")));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWithExistingUsers()  {
	    User user1 = new User(1L, "testUsername1", "pwd1");
	    User user2 = new User(2L, "testUsername2", "pwd2");
	    
	    when(userService.getUsersByUsernameLike("testUsername")).thenReturn(asList(user1,user2));
	    
	    given().
	    when().
	      	get("/api/users/usernamelike/testUsername").
	    then().
	      	statusCode(200).
	      	contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
	    assertThat().
	      	body(
		        "id[0]", equalTo(1),
		        "username[0]", equalTo("testUsername1"),
		        "password[0]", equalTo("pwd1"),
		        "id[1]", equalTo(2),
		        "username[1]", equalTo("testUsername2"),
		        "password[1]", equalTo("pwd2"));
	}
	
	@Test
	public void testGetUserByIdWithEmptyId()  {		
		given().
		when().
			get("/api/users/id").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testGetUserByUsernameWithEmptyUsername()  {		
		given().
		when().
			get("/api/users/username").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testGetUsersByUsernameLikeWithEmptyUsername()  {		
		given().
		when().
			get("/api/users/usernamelike").
		then().	
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testPost_InsertNewUser() {
		User requestBodyUser = new User(null, "testUsername", "pwd");
		when(userService.insertNewUser(requestBodyUser)).
			thenReturn(new User(1L, "testUsername", "pwd"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(requestBodyUser).
		when().
			post("/api/users/new").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("pwd"));
	}
	
	@Test
	public void testPut_UpdatePasswordOfUser_UserSuccessLogin() throws UserNotFoundException, BadRequestException  {
		User userReplacement = new User(null, "testUsername", "new_password");
		UpdateUserForm form = new UpdateUserForm("testUsername", "password", userReplacement);
		when(userService.verifyLogin(form.getUsername(), form.getPassword())).
			thenReturn(true);
		when(userService.updateUserById(1L, userReplacement)).
			thenReturn(new User(1L, "testUsername", "new_password"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			put("/api/users/update/1").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("new_password"));
	}
	
	@Test
	public void testPut_UpdatePasswordOfUser_UserDoesNotProvideLogin_ShouldGetError() {
		User userReplacement = new User(null, "testUsername", "new_password");
		UpdateUserForm form = new UpdateUserForm(null, null, userReplacement);
		when(userService.verifyLogin(form.getUsername(), form.getPassword())).thenReturn(false);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			put("/api/users/update/1").
		then().
			statusCode(401);
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testPut_UpdateOfUser_WithEmptyIdInUrl()  {
		User userReplacement = new User(null, "testUsername", "new_password");
		UpdateUserForm form = new UpdateUserForm("testUsername", "password", userReplacement);

		given().
			contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).
			body(form).
		when().
			put("/api/users/update").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testPut_UpdateOfUser_IdNotFound() throws UserNotFoundException, BadRequestException  {
		User userReplacement = new User(null, "testUsername", "new_password");
		UpdateUserForm form = new UpdateUserForm("testUsername", "password", userReplacement);
		when(userService.verifyLogin(form.getUsername(), form.getPassword())).thenReturn(true);
		doThrow(UserNotFoundException.class).when(userService).updateUserById(1L, form.getUserToUpdate());
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			put("/api/users/update/1").
		then().
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testPut_UpdateOfAnotherUser_ShouldGetBadRequestError() throws UserNotFoundException, BadRequestException  {
		User userReplacement = new User(null, "anotherUsername", "new_password");
		UpdateUserForm form = new UpdateUserForm("myUsername", "password", userReplacement);
		when(userService.verifyLogin(form.getUsername(), form.getPassword())).thenReturn(true);
		doThrow(BadRequestException.class).when(userService).updateUserById(1L, form.getUserToUpdate());
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			put("/api/users/update/1").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testDelete_removeExistingUser_UserLoginSuccess() throws UserNotFoundException, BadRequestException {
		User userCredentials = new User(null, "username", "password");
		when(userService.verifyLogin(userCredentials.getUsername(), userCredentials.getPassword())).thenReturn(true);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(userCredentials).
		when().
			delete("/api/users/delete/1").
		then().
			statusCode(204);
		
		verify(userService, times(1)).deleteUserById(1L, userCredentials);
	}
	
	@Test
	public void testDelete_removeNotExistingUser_shouldReturn404() throws UserNotFoundException, BadRequestException {
		User userCredentials = new User(null, "username", "password");
		when(userService.verifyLogin(userCredentials.getUsername(), userCredentials.getPassword())).thenReturn(true);
		doThrow(UserNotFoundException.class).when(userService).deleteUserById(99L, userCredentials);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(userCredentials).
		when().
			delete("/api/users/delete/99").
		then().
			statusCode(404).
			statusLine(containsString("User Not Found"));
	}
	
	@Test
	public void testDelete_removeAnotherUser_shouldReturnBadRequest() throws UserNotFoundException, BadRequestException {
		User userCredentials = new User(null, "username", "password");
		when(userService.verifyLogin(userCredentials.getUsername(), userCredentials.getPassword())).thenReturn(true);
		doThrow(BadRequestException.class).when(userService).deleteUserById(1L, userCredentials);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(userCredentials).
		when().
			delete("/api/users/delete/1").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
	}
	
	@Test
	public void testDelete_LoginFail_shouldReturnUnauthorized() throws UserNotFoundException, BadRequestException {
		User userCredentials = new User(null, null, null);
		when(userService.verifyLogin(userCredentials.getUsername(), userCredentials.getPassword())).thenReturn(false);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(userCredentials).
		when().
			delete("/api/users/delete/1").
		then().
			statusCode(401);
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testDelete_WithEmptyIdInUrl()  {
		User userCredentials = new User(null, "username", "password");
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(userCredentials).
		when().
			delete("/api/users/delete").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
		
		verifyNoMoreInteractions(userService);
	}
	

}
