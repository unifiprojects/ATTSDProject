package com.maurosalani.project.attsd.web;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserWebController {

	@GetMapping("/")
	public String index(Model model, HttpServletResponse response) {
		model.addAttribute("isLogged", false);
		Cookie cookie = new Cookie("login_token", "");
		cookie.setMaxAge(0);
		response.addCookie(cookie);
		return  "index";
	}
}
