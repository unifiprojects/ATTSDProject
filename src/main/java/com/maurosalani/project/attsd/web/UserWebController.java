package com.maurosalani.project.attsd.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserWebController {

	@GetMapping("/")
	public String index(Model model) {
		return  "index";
	}
}
