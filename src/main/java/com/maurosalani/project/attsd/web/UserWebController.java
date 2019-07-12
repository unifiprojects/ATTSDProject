package com.maurosalani.project.attsd.web;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@Controller
public class UserWebController {

	private HashMap<String, User> loggedUsers = new HashMap<>();

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index(Model model, HttpServletResponse response,
			@CookieValue(value = "login_token", required = false) String token) {
		User user = loggedUsers.get(token);
		if (user != null) {
			response.addCookie(new Cookie("login_token", token));
			model.addAttribute("isLogged", true);
			model.addAttribute("username", user.getUsername());
		} else {
			model.addAttribute("isLogged", false);
			response.addCookie(emptyLoginCookie());
		}
		return "index";
	}

	private Cookie emptyLoginCookie() {
		Cookie cookie = new Cookie("login_token", "");
		cookie.setMaxAge(0);
		return cookie;
	}

	@GetMapping("/login")
	public String login(Model model, HttpServletResponse response,
			@CookieValue(value = "login_token", required = false) String token) {
		if (isAlreadyLogged(token)) {
			model.addAttribute("message", "You are already logged! Try to log out from homepage.");
			model.addAttribute("disableInputText", true);
			response.addCookie(new Cookie("login_token", token));
		} else {
			model.addAttribute("message", "");
			model.addAttribute("disableInputText", false);
		}
		return "login";
	}

	@GetMapping("/register")
	public String register(Model model, HttpServletResponse response,
			@CookieValue(value = "login_token", required = false) String token) {
		if (isAlreadyLogged(token)) {
			model.addAttribute("message", "You are logged! Try to log out from homepage.");
			model.addAttribute("disableInputText", true);
			response.addCookie(new Cookie("login_token", token));
		} else {
			model.addAttribute("message", "");
			model.addAttribute("disableInputText", false);
		}
		return "register";
	}

	@PostMapping("/verifyLogin")
	public String logUser(Model model, HttpServletResponse response, String username, String password) {
		User user = userService.getUserByUsernameAndPassword(username, password);
		if(user == null) {
			model.addAttribute("message", "Username or password invalid.");
			return "login";
		}else {
			response.addCookie(new Cookie("login_token", generateToken()));
			loggedUsers.put("token", user);
			return "redirect:/";
		}	
	}

	String generateToken() {
		return RandomStringUtils.randomAlphanumeric(20);
	}

	private boolean isAlreadyLogged(String token) {
		return loggedUsers.containsKey(token);
	}

	HashMap<String, User> getLoggedUsers() {
		return loggedUsers;
	}

}
