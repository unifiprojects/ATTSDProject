package com.maurosalani.project.attsd.web;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.UserService;

@Controller
public class UserWebController {

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_TEXT_FLAG = "disableInputText";

	@Autowired
	private UserService userService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			User user = (User) session.getAttribute("user");
			model.addAttribute("username", user.getUsername());
		}
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_TEXT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_TEXT_FLAG, false);
		}
		return "login";
	}

	@PostMapping("/verifyLogin")
	public String verifyLoginUser(Model model, HttpServletResponse response, String username, String password,
			HttpSession session) throws UserNotFoundException {
		User user = userService.getUserByUsernameAndPassword(username, password);
		session.setAttribute("user", user);
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (isAlreadyLogged(session))
			session.invalidate();
		return "redirect:/";
	}

	@GetMapping("/registration")
	public String registration(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_TEXT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_TEXT_FLAG, false);
		}
		return "registration";
	}

	@PostMapping("/save")
	public String save(Model model, HttpServletResponse response, HttpSession session, User user) {
		User userSaved = userService.insertNewUser(user);
		model.addAttribute("user", userSaved);
		return "registrationSuccess";
	}

	private boolean isAlreadyLogged(HttpSession session) {
		if (session == null || session.getAttribute("user") == null)
			return false;
		else
			return true;
	}

}
