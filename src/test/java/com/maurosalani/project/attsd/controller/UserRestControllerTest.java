package com.maurosalani.project.attsd.controller;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.sql.Date;

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

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.UpdateAddFollowedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateAddGameLikedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdatePasswordUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateUserFormDTO;
import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.exception_handler.RestControllerExceptionHandler;
import com.maurosalani.project.attsd.model.Game;
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
		applicationContext.registerSingleton("exceptionHandler", RestControllerExceptionHandler.class);

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
	public void testPost_InsertNewUser() throws Exception {
		User newUser = new User(null, "testUsername", "pwd");
		when(userService.insertNewUser(newUser)).
			thenReturn(new User(1L, "testUsername", "pwd"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(new UserDTO(null, "testUsername", "pwd")).
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
	public void testPost_InsertNewUser_PasswordRequired() throws Exception {
		User newUser = new User(null, "testUsername", "");
		when(userService.insertNewUser(newUser)).thenThrow(PasswordRequiredException.class);

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(new UserDTO(null, "testUsername", "")).
		when().
			post("/api/users/new").
		then().
			statusCode(400).
			statusLine(containsString("Password required"));
	}
	
	@Test
	public void testPost_InsertNewUser_UsernameAlreadyExist() throws Exception {
		User newUser = new User(null, "testUsername", "testPassword");
		when(userService.insertNewUser(newUser)).thenThrow(UsernameAlreadyExistingException.class);

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(new UserDTO(null, "testUsername", "testPassword")).
		when().
			post("/api/users/new").
		then().
			statusCode(400).
			statusLine(containsString("Username already exist"));
	}
	
	@Test
	public void testPut_UpdateUser_UserSuccessLogin() throws Exception {
		User userReplacement = new User(null, "testUsername", "new_password");
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "password");
		User userToUpdate = new User(1L, "testUsername", "password");
		UpdateUserFormDTO form = new UpdateUserFormDTO(credentialsDTO, userReplacement);
		
		when(userService.verifyLogin(form.getCredentials())).
			thenReturn(userToUpdate);
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
	public void testPut_UpdateOfUser_UserDoesNotProvideLogin_ShouldGetError() throws Exception {
		User userReplacement = new User(null, "testUsername", "new_password");
		UpdateUserFormDTO form = new UpdateUserFormDTO(null, userReplacement);
		when(userService.verifyLogin(null)).thenThrow(LoginFailedException.class);
		
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
	public void testPut_UpdateAnotherUser_ShouldGetBadRequestError() throws Exception{
		User userReplacement = new User(null, "myUsername", "new_password");
		CredentialsDTO credentialsDTO = new CredentialsDTO("myUsername", "myPassword");
		User userToUpdate = new User(1L, "myUsername", "myPassword");
		UpdateUserFormDTO form = new UpdateUserFormDTO(credentialsDTO, userReplacement);
		
		when(userService.verifyLogin(form.getCredentials())).
			thenReturn(userToUpdate);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			put("/api/users/update/99").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testPatch_UpdatePassword_UserSuccessLogin() throws Exception {
		String newPassword = "newPassword";
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "password");
		User userToUpdate = new User(1L, "testUsername", "password");
		UpdatePasswordUserFormDTO form = new UpdatePasswordUserFormDTO(credentialsDTO, newPassword);
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		when(userService.getUserById(1L)).
			thenReturn(userToUpdate);		
		when(userService.changePassword(userToUpdate, form.getNewPassword())).
			thenReturn(new User(1L, "testUsername", "newPassword"));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/password/1").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("newPassword"));
	}
	
	@Test
	public void testPatch_UpdatePasswordToAnotherUser_ShouldGetBadRequest() throws Exception {
		String newPassword = "newPassword";
		CredentialsDTO credentialsDTO = new CredentialsDTO("myUsername", "myPassword");
		User userToUpdate = new User(1L, "myUsername", "myPassword");
		UpdatePasswordUserFormDTO form = new UpdatePasswordUserFormDTO(credentialsDTO, newPassword);
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/password/99").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testPatch_UpdatePassword_UserDoesNotProvideLogin_ShouldGetError() throws Exception {
		String newPassword = "newPassword";
		UpdatePasswordUserFormDTO form = new UpdatePasswordUserFormDTO(null, newPassword);
		
		when(userService.verifyLogin(null)).thenThrow(LoginFailedException.class);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/password/1").
		then().
			statusCode(401).
			statusLine(containsString("Invalid username or password"));
	}

	@Test
	public void testPatch_AddFollowedUser_UserSuccessLogin() throws Exception {
		User followedToAdd = new User(null, "followed", "pwd");
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "pwd");
		User userToUpdate = new User(1L, "testUsername", "pwd");
		UpdateAddFollowedUserFormDTO form = new UpdateAddFollowedUserFormDTO(credentialsDTO, followedToAdd);
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		when(userService.getUserById(1L)).
			thenReturn(userToUpdate);
		when(userService.addFollowedUser(userToUpdate, form.getFollowedToAdd())).
			thenReturn(new User(1L, "testUsername", "pwd", asList(followedToAdd), null, null));
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addFollowedUser/1").
		then().
			statusCode(200).
			body(
					"id", equalTo(1),
					"username", equalTo("testUsername"),
					"password", equalTo("pwd"),
					"followedUsers", not(equalTo(null)));
	}
	
	@Test
	public void testPatch_AddFollowedUserToAnotherUser_ShouldGetBadRequest() throws Exception {
		User followedToAdd = new User(null, "followed", "pwd");
		CredentialsDTO credentialsDTO = new CredentialsDTO("myUsername", "myPwd");
		User userToUpdate = new User(1L, "myUsername", "myPwd");
		UpdateAddFollowedUserFormDTO form = new UpdateAddFollowedUserFormDTO(credentialsDTO, followedToAdd);
		
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addFollowedUser/99").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testPatch_AddFollowedUser_UserDoesNotProvideLogin_ShouldGetError() throws Exception {
		User followedToAdd = new User(null, "followed", "pwd");
		UpdateAddFollowedUserFormDTO form = new UpdateAddFollowedUserFormDTO(null, followedToAdd);
		
		when(userService.verifyLogin(null)).thenThrow(LoginFailedException.class);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addFollowedUser/1").
		then().
			statusCode(401).
			statusLine(containsString("Invalid username or password"));
	}
	
	@Test
	public void testPatch_AddGameLiked_UserSuccessLogin() throws Exception {
		Game gameLiked = new Game(null, "gameLiked", "description", new Date(1));
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "pwd");
		User userToUpdate = new User(1L, "testUsername", "pwd");
		UpdateAddGameLikedUserFormDTO form = new UpdateAddGameLikedUserFormDTO(credentialsDTO, gameLiked);
		
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		when(userService.getUserById(1L)).
			thenReturn(userToUpdate);
		when(userService.addGame(userToUpdate, form.getGameLiked())).
			thenReturn(new User(1L, "testUsername", "pwd", null, null, asList(gameLiked)));

		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addGame/1").
		then().
			statusCode(200).
			body(
				"id", equalTo(1),
				"username", equalTo("testUsername"),
				"password", equalTo("pwd"),
				"games", not(equalTo(null)));
	}
	
	@Test
	public void testPatch_AddGameLikedToAnotherUser_ShouldGetBadRequest() throws Exception {
		Game gameLiked = new Game(null, "gameLiked", "description", new Date(1));
		CredentialsDTO credentialsDTO = new CredentialsDTO("myUsername", "myPwd");
		User userToUpdate = new User(1L, "myUsername", "myPwd");
		UpdateAddGameLikedUserFormDTO form = new UpdateAddGameLikedUserFormDTO(credentialsDTO, gameLiked);
		
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToUpdate);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addGame/99").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
	
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testPatch_AddGameLiked_UserDoesNotProvideLogin_ShouldGetError() throws Exception {
		Game gameLiked = new Game(null, "gameLiked", "description", new Date(1));
		UpdateAddGameLikedUserFormDTO form = new UpdateAddGameLikedUserFormDTO(null, gameLiked);
		
		when(userService.verifyLogin(null)).thenThrow(LoginFailedException.class);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(form).
		when().
			patch("/api/users/update/addGame/1").
		then().
			statusCode(401).
			statusLine(containsString("Invalid username or password"));
	}
	
	@Test
	public void testDelete_removeExistingUser_UserLoginSuccess() throws Exception{
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "password");
		User userToDelete = new User(1L, "username", "password");
		when(userService.verifyLogin(credentialsDTO)).thenReturn(userToDelete);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(credentialsDTO).
		when().
			delete("/api/users/delete/1").
		then().
			statusCode(204);
		
		verify(userService, times(1)).deleteById(1L);
	}
	
	@Test
	public void testDelete_removeAnotherUser_shouldReturnBadRequest() throws Exception{
		CredentialsDTO credentialsDTO = new CredentialsDTO("myUsername", "myPassword");
		User userToDelete = new User(1L, "myUsername", "myPassword");
		when(userService.verifyLogin(credentialsDTO)).
			thenReturn(userToDelete);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(credentialsDTO).
		when().
			
			delete("/api/users/delete/99").
		then().
			statusCode(400).
			statusLine(containsString("Bad Request"));
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}
	
	@Test
	public void testDelete_LoginFail_shouldReturnUnauthorized() throws Exception {
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "password");
		when(userService.verifyLogin(credentialsDTO)).thenThrow(LoginFailedException.class);
		
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(credentialsDTO).
		when().
			delete("/api/users/delete/1").
		then().
			statusCode(401);
		
		verifyNoMoreInteractions(ignoreStubs(userService));
	}

}
