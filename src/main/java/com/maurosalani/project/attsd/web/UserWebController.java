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

	private HashMap<String, User> loggedUser = new HashMap<String, User>();

	@GetMapping("/")
	public String index(Model model, HttpServletResponse response,
			@CookieValue(value = "login_token", required = false) String token) {
		
		model.addAttribute("isLogged", false);
		Cookie cookie = new Cookie("login_token", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return "index";
	}

	void setLoggedUser(HashMap<String, User> loggedUser) {
		this.loggedUser = loggedUser;
	}

}
