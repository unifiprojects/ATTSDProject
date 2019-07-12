package com.maurosalani.project.attsd.web;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {

	private static final String MESSAGE_MODEL = "message";

	private static final String DISABLE_INPUT_TEXT_FLAG_MODEL = "disableInputText";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@Test
	public void testStatus2XX() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessIndex_WhenUserNotLoggedIn_AndLoginTokenNotFound() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful())
				.andExpect(model().attributeDoesNotExist("username"));
	}

	@Test
	public void testAccessIndex_UserLoggedIn() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/");

		mvc.perform(requestToPerform).andExpect(status().is2xxSuccessful())
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

		mvc.perform(requestToPerform).andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute(MESSAGE_MODEL, "You are already logged! Try to log out from homepage."))
				.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG_MODEL, true));
	}

	@Test
	public void testAccessLogin_UserIsNotLoggedIn() throws Exception {
		mvc.perform(get("/login")).andExpect(status().is2xxSuccessful()).andExpect(model().attribute(MESSAGE_MODEL, ""))
				.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG_MODEL, false));
	}

	@Test
	public void testVerifyLoginUser_Success() throws Exception {
		User user = new User(1L, "username", "password");
		when(userService.getUserByUsernameAndPassword("username", "password")).thenReturn(user);

		mvc.perform(post("/verifyLogin").param("username", "username").param("password", "password"))
				.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testVerifyLoginUser_FailedWhenUsernameOrPasswordAreIncorrect() throws Exception {
		when(userService.getUserByUsernameAndPassword("wrong_username", "wrong_password")).thenReturn(null);

		mvc.perform(post("/verifyLogin").param("username", "wrong_username").param("password", "wrong_password"))
				.andExpect(view().name("login")).andExpect(status().isUnauthorized())
				.andExpect(model().attribute(MESSAGE_MODEL, "Username or password invalid."))
				.andExpect(model().attributeDoesNotExist("username"));
	}

	@Test
	public void testAccessRegister() throws Exception {
		mvc.perform(get("/register")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessRegister_UserAlreadyLogged() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/register");

		mvc.perform(requestToPerform).andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute(MESSAGE_MODEL, "You are already logged! Try to log out from homepage."))
				.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG_MODEL, true));
	}

	@Test
	public void testAccessRegister_UserIsNotLogged() throws Exception {
		mvc.perform(get("/register")).andExpect(status().is2xxSuccessful())
				.andExpect(model().attribute(MESSAGE_MODEL, ""))
				.andExpect(model().attribute(DISABLE_INPUT_TEXT_FLAG_MODEL, false));
	}

	private MockHttpServletRequestBuilder addUserToSessionAndReturnRequest(User user, String url) {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", user);
		MockHttpServletRequestBuilder requestToPerform = MockMvcRequestBuilders.get(url).session(session);
		return requestToPerform;
	}
}
