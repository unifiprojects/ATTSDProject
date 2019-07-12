package com.maurosalani.project.attsd.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.servlet.http.Cookie;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.maurosalani.project.attsd.model.User;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private UserWebController userWebController;
	
	@Test
	public void testStatus2XX() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testAccessIndex_WhenUserNotLoggedIn_AndLoginTokenNotFound() throws Exception {
		mvc.perform(get("/")).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", false)).
			andExpect(cookie().value("login_token", "")).
			andExpect(cookie().maxAge("login_token", 0));
	}
	
	@Test
	public void testAccessIndex_UserNotLoggedIn_AndLoginTokenIsFake() throws Exception {
		mvc.perform(get("/").cookie(new Cookie("login_token", "tokenNotExisting"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", false)).
			andExpect(cookie().value("login_token", "")).
			andExpect(cookie().maxAge("login_token", 0));
	}
	
	@Test
	public void testAccessIndex_UserLoggedIn() throws Exception {
		userWebController.getLoggedUsers().put("token", new User(1L, "usernameTest", "passwordTest"));
		
		mvc.perform(get("/").cookie(new Cookie("login_token", "token"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", true)).
			andExpect(model().attribute("username", "usernameTest")).
			andExpect(cookie().value("login_token", "token"));
	}

	@Test
	public void testAccessLogin() throws Exception {
		mvc.perform(get("/login")).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testAccessLogin_UserAlreadyLogged() throws Exception {
		userWebController.getLoggedUsers().put("token", new User(1L, "usernameTest", "passwordTest"));
		
		mvc.perform(get("/login").cookie(new Cookie("login_token", "token"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("errorMessage", "You are already logged! Try to log out from homepage.")).
			andExpect(model().attribute("disableInputText", true)).
			andExpect(cookie().value("login_token", "token"));
	}	
	
	@Test
	public void testAccessLogin_UserIsNotLoggedIn() throws Exception {
		mvc.perform(get("/login")).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("errorMessage", "")).
			andExpect(model().attribute("disableInputText", false)).
			andExpect(cookie().doesNotExist("login_token"));
	}
	
	@Test
	public void testAccessRegister() throws Exception {
		mvc.perform(get("/register")).andExpect(status().is2xxSuccessful());
	}
	
	@After
	public void resetLoggedUsers() throws Exception {
		userWebController.getLoggedUsers().clear();
	}
	
}
