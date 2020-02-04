package com.maurosalani.project.attsd.web;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.maurosalani.project.attsd.dto.ChangePasswordFormDTO;
import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.RegistrationFormDTO;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.NewPasswordRequiredException;
import com.maurosalani.project.attsd.exception.OldPasswordErrorException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.PasswordsRegistrationDoNotMatchException;
import com.maurosalani.project.attsd.exception.UnauthorizedOperationException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.exception.UsernameRequiredException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;
import com.maurosalani.push_notification.SubscriptionsHandler;

@Controller
public class WebController {

	private static final String USERNAME = "username";

	private static final String IS_ALREADY_LIKED_FLAG = "isAlreadyLiked";

	private static final String IS_ALREADY_FOLLOWED_FLAG = "isAlreadyFollowed";

	private static final String IS_MY_PROFILE_FLAG = "isMyProfile";

	private static final String IS_LOGGED_FLAG = "isLogged";

	private static final String GAMES_LIST = "gamesList";

	private static final String USERS_LIST = "usersList";

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_FLAG = "disableInput";

	private static final int COUNT_LATEST_RELEASES = 4;

	private final SubscriptionsHandler subscriptionsHandler = SubscriptionsHandler.getInstance(null);

	@Autowired
	private UserService userService;

	@Autowired
	private GameService gameService;

	@GetMapping("/")
	public String index(Model model, HttpSession session) throws UserNotFoundException {
		if (isAlreadyLogged(session)) {
			User user = getLoggedUser(session);
			model.addAttribute(USERNAME, user.getUsername());
			model.addAttribute(IS_LOGGED_FLAG, true);
		} else {
			model.addAttribute(IS_LOGGED_FLAG, false);
		}
		model.addAttribute("latestReleases", gameService.getLatestReleasesGames(COUNT_LATEST_RELEASES));
		return "index";
	}

	@GetMapping("/login")
	public String login(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_FLAG, false);
		}
		model.addAttribute("credentials", new CredentialsDTO());
		return "login";
	}

	@PostMapping(value = "/verifyLogin")
	public String verifyLoginUser(Model model, CredentialsDTO credentials, HttpSession session)
			throws LoginFailedException {
		User result = userService.verifyLogin(credentials);
		session.setAttribute(USERNAME, result.getUsername());
		model.addAttribute(IS_LOGGED_FLAG, true);
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(IS_LOGGED_FLAG, false);
			Logger.getLogger(WebController.class.getName())
					.info("Username: " + (String) session.getAttribute(USERNAME) + " unregister");
			subscriptionsHandler.unsubscribeUser((String) session.getAttribute(USERNAME));
			session.invalidate();
		}
		return "redirect:/";
	}

	@GetMapping("/registration")
	public String registration(Model model, HttpSession session) {
		if (isAlreadyLogged(session)) {
			model.addAttribute(MESSAGE, "You are already logged! Try to log out from homepage.");
			model.addAttribute(DISABLE_INPUT_FLAG, true);
		} else {
			model.addAttribute(MESSAGE, "");
			model.addAttribute(DISABLE_INPUT_FLAG, false);
		}
		model.addAttribute("registrationForm", new RegistrationFormDTO());
		return "registration";
	}

	@PostMapping("/save")
	public String save(Model model, HttpSession session, RegistrationFormDTO form)
			throws UsernameAlreadyExistingException, UsernameRequiredException, PasswordRequiredException,
			PasswordsRegistrationDoNotMatchException {

		form.checkValidity();
		User userSaved = userService.insertNewUser(new User(null, form.getUsername(), form.getPassword()));
		model.addAttribute("user", userSaved);
		return "registrationSuccess";
	}

	@GetMapping("/search")
	public String search(Model model, HttpSession session,
			@RequestParam(value = "content_search", required = true) String content) {
		if (contentIsNotValid(content)) {
			model.addAttribute(MESSAGE, "Error: search field was empty.");
		} else {
			model.addAttribute(MESSAGE, "");
			List<User> usersFound = userService.getUsersByUsernameLike(content);
			List<Game> gamesFound = gameService.getGamesByNameLike(content);
			model.addAttribute(USERS_LIST, usersFound);
			model.addAttribute(GAMES_LIST, gamesFound);
		}
		return "search";
	}

	private boolean contentIsNotValid(String content) {
		return StringUtils.isBlank(content);
	}

	@GetMapping("/profile/{username}")
	public String profile(@PathVariable String username, Model model, HttpSession session)
			throws UserNotFoundException {
		User user = userService.getUserByUsername(username);
		model.addAttribute("user", user);
		if (!isAlreadyLogged(session)) {
			model.addAttribute(IS_LOGGED_FLAG, false);
			model.addAttribute(IS_MY_PROFILE_FLAG, false);
			model.addAttribute(IS_ALREADY_FOLLOWED_FLAG, false);
		} else {
			User loggedUser = getLoggedUser(session);
			boolean isMyProfile = loggedUser.getUsername().equals(user.getUsername());
			boolean isAlreadyFollowed = false;
			if (loggedUser.getFollowedUsers() != null)
				isAlreadyFollowed = loggedUser.getFollowedUsers().contains(user);
			model.addAttribute(IS_LOGGED_FLAG, true);
			model.addAttribute(IS_MY_PROFILE_FLAG, isMyProfile);
			model.addAttribute(IS_ALREADY_FOLLOWED_FLAG, isAlreadyFollowed);
			if (isMyProfile)
				model.addAttribute("changePasswordForm", new ChangePasswordFormDTO());
		}
		return "profile";
	}

	@GetMapping("/game/{name}")
	public String game(@PathVariable String name, HttpSession session, Model model)
			throws GameNotFoundException, UserNotFoundException {
		Game game = gameService.getGameByName(name);
		model.addAttribute("game", game);
		if (!isAlreadyLogged(session)) {
			model.addAttribute(IS_LOGGED_FLAG, false);
			model.addAttribute(IS_ALREADY_LIKED_FLAG, false);
		} else {
			User loggedUser = getLoggedUser(session);
			boolean isAlreadyLiked = false;
			if (game.getUsers() != null)
				isAlreadyLiked = game.getUsers().contains(loggedUser);
			model.addAttribute(IS_LOGGED_FLAG, true);
			model.addAttribute("usernameLogged", loggedUser.getUsername());
			model.addAttribute(IS_ALREADY_LIKED_FLAG, isAlreadyLiked);
		}
		return "game";
	}

	@PostMapping("/addUser")
	public String addFollowedUserToUser(@RequestParam(name = "followedToAdd") String followedToAdd, Model model,
			HttpSession session) throws UserNotFoundException, UnauthorizedOperationException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User loggedUser = getLoggedUser(session);
		User followed = userService.getUserByUsername(followedToAdd);
		User result = userService.addFollowedUser(loggedUser, followed);
		session.setAttribute(USERNAME, result.getUsername());
		Logger.getLogger(WebController.class.getName())
				.info("Username: " + loggedUser.getUsername() + " subscribed to topic: " + followed.getUsername());
		subscriptionsHandler.subscribeToTopic(loggedUser.getUsername(), followed.getUsername());
		return "redirect:/profile/" + followed.getUsername();
	}

	@PostMapping("/addGame")
	public String addGameToUser(@RequestParam(name = "gameToAdd") String gameToAdd, Model model, HttpSession session)
			throws GameNotFoundException, UnauthorizedOperationException, UserNotFoundException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User loggedUser = getLoggedUser(session);
		Game toAdd = gameService.getGameByName(gameToAdd);
		User result = userService.addGame(loggedUser, toAdd);
		session.setAttribute(USERNAME, result.getUsername());
		String message = "Your friend " + loggedUser.getUsername() + " likes " + gameToAdd;
		Logger.getLogger(WebController.class.getName())
				.info("Username: " + loggedUser.getUsername() + " published message: " + message);
		subscriptionsHandler.publishMessageForTopic(message, loggedUser.getUsername());
		return "redirect:/game/" + toAdd.getName();
	}

	@PostMapping("/changePassword")
	public String changePassword(ChangePasswordFormDTO form, Model model, HttpSession session)
			throws UnauthorizedOperationException, OldPasswordErrorException, NewPasswordRequiredException,
			UserNotFoundException, PasswordRequiredException {
		if (!isAlreadyLogged(session)) {
			throw new UnauthorizedOperationException();
		}
		User loggedUser = getLoggedUser(session);
		if (!loggedUser.getPassword().equals(form.getOldPassword())) {
			throw new OldPasswordErrorException();
		}
		if (StringUtils.isWhitespace(form.getNewPassword())) {
			throw new NewPasswordRequiredException();
		}
		User result = userService.changePassword(loggedUser, form.getNewPassword());
		session.setAttribute(USERNAME, result.getUsername());
		return "passwordChanged";
	}

	private boolean isAlreadyLogged(HttpSession session) {
		Optional<String> opt = Optional.ofNullable((String) session.getAttribute(USERNAME));
		return opt.isPresent();
	}

	private User getLoggedUser(HttpSession session) throws UserNotFoundException {
		String loggedUser = (String) session.getAttribute(USERNAME);
		return userService.getUserByUsername(loggedUser);
	}

}
