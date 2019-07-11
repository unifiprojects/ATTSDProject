package com.maurosalani.project.attsd.web;

import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

import com.maurosalani.project.attsd.model.User;

@Controller
public class UserWebController {

	private HashMap<String, User> loggedUsers = new HashMap<>();

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
			Cookie cookie = new Cookie("login_token", "");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
		}

		return "index";
	}

	@GetMapping("/login")
	public String login(Model model, HttpServletResponse response,
			@CookieValue(value = "login_token", required = false) String token) {
		if(isAlreadyLogged(token)) {
			model.addAttribute("errorMessage", "You are already logged! Try to log out from homepage.");
			model.addAttribute("disableInputText", true);
			response.addCookie(new Cookie("login_token", token));
		} 
		return "login";
	}

	private boolean isAlreadyLogged(String token) {
		return loggedUsers.containsKey(token);
	}

	HashMap<String, User> getLoggedUsers() {
		return loggedUsers;
	}

}
