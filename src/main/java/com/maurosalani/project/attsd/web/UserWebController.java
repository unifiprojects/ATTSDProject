package com.maurosalani.project.attsd.web;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@Controller
public class UserWebController {

	private static final String GAMES_LIST = "gamesList";

	private static final String USERS_LIST = "usersList";

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_TEXT_FLAG = "disableInputText";

	@Autowired
	private UserService userService;

	@Autowired
	private GameService gameService;

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
	public String verifyLoginUser(Model model, String username, String password, HttpSession session)
			throws UserNotFoundException {
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
	public String save(Model model, HttpSession session, User user) throws UsernameAlreadyExistingException {
		User userSaved = userService.insertNewUser(user);
		model.addAttribute("user", userSaved);
		return "registrationSuccess";
	}

	@GetMapping("/search")
	public String search(Model model, HttpSession session, String content) {
		if (content == "" || content == null) {
			model.addAttribute(MESSAGE, "Empty field for search.");
		} else {
			List<User> usersFound = userService.getUsersByUsernameLike(content);
			List<Game> gamesFound = gameService.getGamesByNameLike(content);
			if (usersFound.isEmpty() && gamesFound.isEmpty()) {
				model.addAttribute(MESSAGE, "No element found.");
			} else {
				model.addAttribute(USERS_LIST, usersFound);
				model.addAttribute(GAMES_LIST, gamesFound);
			}
		}
		return "search";
	}

	private boolean isAlreadyLogged(HttpSession session) {
		Optional<User> opt = Optional.ofNullable((User) session.getAttribute("user"));
		return opt.isPresent();
	}

}
