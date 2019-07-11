package com.maurosalani.project.attsd.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;

import javax.servlet.http.Cookie;

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
	public void testUserNotLoggedIn_LoginTokenNotFound() throws Exception {
		mvc.perform(get("/")).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", false)).
			andExpect(cookie().value("login_token", "")).
			andExpect(cookie().maxAge("login_token", 0));
	}
	
	@Test
	public void testUserNotLoggedIn_UsernNotLogged() throws Exception {
		mvc.perform(get("/").cookie(new Cookie("login_token", "token"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", false)).
			andExpect(cookie().value("login_token", "")).
			andExpect(cookie().maxAge("login_token", 0));
	}
	
	@Test
	public void testUserLoggedIn() throws Exception {
		HashMap<String, User> loggedUser = new HashMap<String, User>();
		loggedUser.put("token", new User(1L,"usernameTest","passwordTest"));
		userWebController.setLoggedUser(loggedUser);
		
		mvc.perform(get("/").cookie(new Cookie("login_token", "token"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("isLogged", true)).
			andExpect(model().attribute("username", "usernameTest")).
			andExpect(cookie().value("login_token", "token"));
	}


}
