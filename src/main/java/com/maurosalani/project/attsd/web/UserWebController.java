package com.maurosalani.project.attsd.web;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

import org.apache.commons.lang3.StringUtils;

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
			throws LoginFailedException {
		User user;
		try {
			user = userService.getUserByUsernameAndPassword(username, password);
		} catch (UserNotFoundException e) {
			throw new LoginFailedException();
		}
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
	public String search(Model model, HttpSession session,
			@RequestParam(value = "content_search", required = true) String content) {
		if (contentIsNotValid(content)) {
			model.addAttribute(MESSAGE, "Error: search field was empty.");
		} else {
			String trimmedContent = content.trim();
			List<User> usersFound = userService.getUsersByUsernameLike(trimmedContent);
			List<Game> gamesFound = gameService.getGamesByNameLike(trimmedContent);
			if (usersFound.isEmpty() && gamesFound.isEmpty()) {
				model.addAttribute(MESSAGE, "No element found.");
			} else {
				model.addAttribute(USERS_LIST, usersFound);
				model.addAttribute(GAMES_LIST, gamesFound);
			}
		}
		return "search";
	}

	private boolean contentIsNotValid(String content) {
		return StringUtils.isBlank(content);
	}

	@GetMapping("/profile/{username}")
	public String profile(@PathVariable String username, Model model) throws UserNotFoundException {
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		return "profile";
	}

	@GetMapping("/game/{name}")
	public String game(@PathVariable String name, Model model) throws GameNotFoundException {
		Game game = gameService.getGameByName(name);
		model.addAttribute("game", game);
		return "game";
	}

	private boolean isAlreadyLogged(HttpSession session) {
		Optional<User> opt = Optional.ofNullable((User) session.getAttribute("user"));
		return opt.isPresent();
	}

}
