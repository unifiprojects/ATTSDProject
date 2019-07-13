package com.maurosalani.project.attsd.web;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_TEXT_FLAG = "disableInputText";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;
	
	@MockBean
	private GameService gameService;

	@Test
	public void testStatus2XX() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessIndex_WhenUserNotLoggedIn() throws Exception {
		mvc.perform(get("/"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attributeDoesNotExist("username"));
	}

	@Test
	public void testAccessIndex_UserLoggedIn() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attributeExists("username"));
	}

	@Test
	public void testAccessLogin() throws Exception {
		mvc.perform(get("/login")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessLogin_UserAlreadyLogged() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/login");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, "You are already logged! Try to log out from homepage."))
			.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG, true));
	}

	@Test
	public void testAccessLogin_UserIsNotLoggedIn() throws Exception {
		mvc.perform(get("/login"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, ""))
			.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG, false));
	}

	@Test
	public void testVerifyLoginUser_Success() throws Exception {
		User user = new User(1L, "username", "password");
		when(userService.getUserByUsernameAndPassword("username", "password")).thenReturn(user);

		mvc.perform(post("/verifyLogin")
				.param("username", "username")
				.param("password", "password"))			
			.andExpect(request().sessionAttribute("user", user))
			.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testVerifyLoginUser_FailedWhenUsernameOrPasswordAreIncorrect() throws Exception {
		when(userService.getUserByUsernameAndPassword("wrong_username", "wrong_password")).thenThrow(UserNotFoundException.class);

		mvc.perform(post("/verifyLogin")
				.param("username", "wrong_username")
				.param("password", "wrong_password"))
			.andExpect(status().isNotFound())
			.andExpect(model().attribute(MESSAGE, "Username or password invalid."))
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("login"));
	}

	@Test
	public void testLogoutUser_SessionDoesNotExist() throws Exception {
		mvc.perform(get("/logout"))
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("redirect:/"));
	}
	
	@Test
	public void testLogoutUser_SessionExists() throws Exception {
		User user = new User(null, "username", "pwd");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/logout");
		
		mvc.perform(requestToPerform)
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testAccessRegistration() throws Exception {
		mvc.perform(get("/registration")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testRegistration_UserAlreadyLogged() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/registration");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, "You are already logged! Try to log out from homepage."))
			.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG, true));
	}

	@Test
	public void testRegistration_UserIsNotLogged() throws Exception {
		mvc.perform(get("/registration"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, ""))
			.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG, false));
	}

	@Test
	public void testSave_SuccessAfterRegistration() throws Exception {		
		User userToInsert = new User(null, "usernameTest", "pwdTest");
		User userSaved = new User(1L, "usernameTest", "pwdTest");
		when(userService.insertNewUser(userToInsert)).thenReturn(userSaved);
		
		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password", userToInsert.getPassword()))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute("user", userSaved))
			.andExpect(view().name("registrationSuccess"));
	}
	
	@Test
	public void testSave_UsernameAlreadyUsed() throws Exception {		
		User userToInsert = new User(null, "usernameAlreadyExisting", "pwd");
		when(userService.insertNewUser(userToInsert)).thenThrow(UsernameAlreadyExistingException.class);
		
		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password", userToInsert.getPassword()))
			.andExpect(status().is(HttpStatus.CONFLICT.value()))
			.andExpect(model().attribute(MESSAGE, "Username already existing. Please choose another one."))
			.andExpect(view().name("registration"));
	}
	
	@Test
	public void testSave_PasswordIsEmpty() throws Exception {		
		User userToInsert = new User(null, "usernameTest", null);
		when(userService.insertNewUser(userToInsert)).thenThrow(DataIntegrityViolationException.class);
		
		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password", userToInsert.getPassword()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
		.andExpect(model().attribute(MESSAGE, "Username or password invalid."))
		.andExpect(view().name("registration"));
	}
	
	@Test
	public void testSave_UsernameIsEmpty() throws Exception {		
		User userToInsert = new User(null, null, "pwdTest");
		when(userService.insertNewUser(userToInsert)).thenThrow(DataIntegrityViolationException.class);
		
		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password", userToInsert.getPassword()))
		.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
		.andExpect(model().attribute(MESSAGE, "Username or password invalid."))
		.andExpect(view().name("registration"));
	}
	
	@Test
	public void testSearch_ResultListsAreEmpty() throws Exception {		
		String content = "content";
		when(userService.getUsersByUsernameLike(content)).thenReturn(Collections.emptyList());
		when(gameService.getGamesByNameLike(content)).thenReturn(Collections.emptyList());
		
		mvc.perform(get("/search")
				.param("content", content))
			.andExpect(model().attribute(MESSAGE, "No element found."))
			.andExpect(view().name("search"));
	}
	
	@Test
	public void testSearch_ContentIsEmpty() throws Exception {		
		mvc.perform(get("/search")
				.param("content", ""))
			.andExpect(model().attribute(MESSAGE, "Empty field for search."))
			.andExpect(view().name("search"));
	}
	
	@Test
	public void testSearch_ContentIsNull() throws Exception {		
		mvc.perform(get("/search"))
			.andExpect(model().attribute(MESSAGE, "Empty field for search."))
			.andExpect(view().name("search"));
	}

	private MockHttpServletRequestBuilder addUserToSessionAndReturnRequest(User user, String url) {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", user);
		MockHttpServletRequestBuilder requestToPerform = MockMvcRequestBuilders.get(url).session(session);
		return requestToPerform;
	}
}
