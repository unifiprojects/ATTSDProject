package com.maurosalani.project.attsd.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.http.Cookie;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {
	
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private UserWebController userWebController;
	
	@MockBean
	private UserService userService;
	
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
			andExpect(model().attribute("message", "You are already logged! Try to log out from homepage.")).
			andExpect(model().attribute("disableInputText", true)).
			andExpect(cookie().value("login_token", "token"));
	}	
	
	@Test
	public void testAccessLogin_UserIsNotLoggedIn() throws Exception {
		mvc.perform(get("/login")).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("message", "")).
			andExpect(model().attribute("disableInputText", false)).
			andExpect(cookie().doesNotExist("login_token"));
	}
	
	@Test
	public void testAccessRegister() throws Exception {
		mvc.perform(get("/register")).andExpect(status().is2xxSuccessful());
	}
	
	@Test
	public void testAccessRegister_UserAlreadyLogged() throws Exception {
		userWebController.getLoggedUsers().put("token", new User(1L, "usernameTest", "passwordTest"));
		
		mvc.perform(get("/register").cookie(new Cookie("login_token", "token"))).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("message", "You are logged! Try to log out from homepage.")).
			andExpect(model().attribute("disableInputText", true)).
			andExpect(cookie().value("login_token", "token"));	
	}
	
	@Test
	public void testAccessRegister_UserIsNotLogged() throws Exception {		
		mvc.perform(get("/register")).
			andExpect(status().is2xxSuccessful()).
			andExpect(model().attribute("message", "")).
			andExpect(model().attribute("disableInputText", false)).
			andExpect(cookie().doesNotExist("login_token"));
	}
	
	@Test
	public void testLoginUser_Success() throws Exception {
		User user = new User (1L, "username", "password");
		when(userService.getUserByUsernameAndPassword("username", "password")).
			thenReturn(user);
		
		mvc.perform(post("/verifyLogin").
				param("username", "username").
				param("password", "password")).
			andExpect(cookie().exists("login_token")).
			andExpect(cookie().value("login_token", CoreMatchers.not(CoreMatchers.equalTo("")))).
			andExpect(view().name("redirect:/"));
	
		assertThat(userWebController.getLoggedUsers()).hasSize(1);
	}
	
	@Test
	public void testLoginUser_Failed() throws Exception {
	    when(userService.getUserByUsernameAndPassword("username", "password")).
	      thenReturn(null);
	    
	    mvc.perform(post("/verifyLogin").
	        param("username", "username").
	        param("password", "password")).
			andExpect(status().is2xxSuccessful()).
		  	andExpect(model().attribute("message", "Username or password invalid.")).
		  	andExpect(cookie().doesNotExist("login_token"));
	    
		assertThat(userWebController.getLoggedUsers()).hasSize(0);
	}	
	
	@Test
	public void testRegisterUser_Success() throws Exception {
	    User user = new User (null , "usernameTest", "passwordTest");
	    User userReturned = new User (1L , "usernameTest", "passwordTest");
	    when(userService.insertNewUser(user)).
	        thenReturn(userReturned);
	    
	    mvc.perform(post("/verifyRegister").
		        param("username", "usernameTest").
		        param("password", "passwordTest")).
	      	andExpect(cookie().doesNotExist("login_token")).
	      	andExpect(model().attribute("message", user.getUsername() + " you have successfully registered")).
			andExpect(model().attribute("disableInputText", false)).
			andExpect(status().is2xxSuccessful());
	}
	
	@After
	public void resetLoggedUsers() throws Exception {
		userWebController.getLoggedUsers().clear();
	}
	
}
