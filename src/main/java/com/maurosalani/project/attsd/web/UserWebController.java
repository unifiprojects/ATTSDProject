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
		response.addCookie(new Cookie("login_token", ""));
		return  "index";
	}
}
