package com.maurosalani.project.attsd.web;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebControllerTest {

	private static final String LATEST_RELEASES = "latestReleases";

	private static final String GAMES_LIST = "gamesList";

	private static final String USERS_LIST = "usersList";

	private static final String MESSAGE = "message";

	private static final String DISABLE_INPUT_FLAG = "disableInput";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private UserService userService;

	@MockBean
	private GameService gameService;

	@Test
	public void testAccessIndex_ShouldReturnSuccess() throws Exception {
		mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessIndex_ShouldHaveLatestReleasesAttribute() throws Exception {
		mvc.perform(get("/"))
			.andExpect(model().attributeExists(LATEST_RELEASES));
	}
	
	@Test
	public void testAccessIndex_ShouldShowLatestReleasesGames() throws Exception {
		Game game1 = new Game(1L, "Game1", "Description1", new Date(1));
		Game game2 = new Game(2L, "Game2", "Description2", new Date(2));
		when(gameService.getLatestReleasesGames(anyInt())).thenReturn(asList(game1, game2));
		
		mvc.perform(get("/"))
			.andExpect(model().attribute(LATEST_RELEASES, equalTo(asList(game1, game2))));
	}

	@Test
	public void testAccessIndex_WhenUserNotLoggedIn() throws Exception {
		mvc.perform(get("/"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attributeDoesNotExist("username"));
	}

	@Test
	public void testAccessIndex_UserLoggedIn() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attributeExists("username"));
	}

	@Test
	public void testAccessLogin() throws Exception {
		mvc.perform(get("/login"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testAccessLogin_UserAlreadyLogged_ShouldShowWarningMessage() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/login");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, "You are already logged! Try to log out from homepage."))
			.andExpect(model().attribute(DISABLE_INPUT_FLAG, true));
	}

	@Test
	public void testAccessLogin_UserIsNotLoggedIn() throws Exception {
		mvc.perform(get("/login"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, ""))
			.andExpect(model().attribute(DISABLE_INPUT_FLAG, false));
	}

	@Test
	public void testVerifyLoginUser_Success_ShouldCreateSessionForUserAndRedirectToHome() throws Exception {
		User user = new User(1L, "username", "password");
		when(userService.getUserByUsernameAndPassword("username", "password")).thenReturn(user);

		mvc.perform(post("/verifyLogin")
				.param("username", "username")
				.param("password", "password"))
			.andExpect(request().sessionAttribute("user", user))
			.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testVerifyLoginUser_FailedWhenUsernameOrPasswordAreIncorrect_ShouldBeUnauthorized() throws Exception {
		when(userService.getUserByUsernameAndPassword("wrong_username", "wrong_password"))
				.thenThrow(UserNotFoundException.class);

		mvc.perform(post("/verifyLogin")				
				.param("username", "wrong_username")
				.param("password", "wrong_password"))
			.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
			.andExpect(model().attribute(MESSAGE, "Invalid username or password."))
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("login"));
	}

	@Test
	public void testLogoutUser_UserNotLoggedYet__ShouldVerifyNoSessionIsPresent() throws Exception {
		mvc.perform(get("/logout"))
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testLogoutUser_UserIsLogged_SessionShouldBeRemoved() throws Exception {
		User user = new User(null, "username", "pwd");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/logout");

		mvc.perform(requestToPerform)
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("redirect:/"));
	}

	@Test
	public void testAccessRegistration() throws Exception {
		mvc.perform(get("/registration"))
			.andExpect(status().is2xxSuccessful());
	}

	@Test
	public void testRegistration_UserAlreadyLogged_ShouldShowWarningMessageAndDisableInput() throws Exception {
		User user = new User(1L, "usernameTest", "pwdTest");
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnRequest(user, "/registration");

		mvc.perform(requestToPerform)
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, "You are already logged! Try to log out from homepage."))
			.andExpect(model().attribute(DISABLE_INPUT_FLAG, true));
	}

	@Test
	public void testRegistration_UserIsNotLogged() throws Exception {
		mvc.perform(get("/registration"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute(MESSAGE, ""))
			.andExpect(model().attribute(DISABLE_INPUT_FLAG, false));
	}

	@Test
	public void testSaveAfterRegistration_Success() throws Exception {
		User userToInsert = new User(null, "usernameTest", "pwdTest");
		User userSaved = new User(1L, "usernameTest", "pwdTest");
		when(userService.insertNewUser(userToInsert)).thenReturn(userSaved);

		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password", userToInsert.getPassword())
				.param("confirmPassword",userToInsert.getPassword()))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute("user", userSaved))
			.andExpect(view().name("registrationSuccess"));
	}

	@Test
	public void testSave_UsernameAlreadyUsed_ShouldShowErrorMessageAndStatusConflict() throws Exception {
		User userToInsert = new User(null, "usernameAlreadyExisting", "pwd");
		when(userService.insertNewUser(userToInsert)).thenThrow(UsernameAlreadyExistingException.class);

		mvc.perform(post("/save")
				.param("username", userToInsert.getUsername())
				.param("password",userToInsert.getPassword())
				.param("confirmPassword",userToInsert.getPassword()))
			.andExpect(status().is(HttpStatus.CONFLICT.value()))
			.andExpect(model().attribute(MESSAGE, "Username already existing. Please choose another one."))
			.andExpect(view().name("registration"));
	}

	@Test
	public void testSave_PasswordIsEmpty_ShouldBeBadRequest() throws Exception {

		mvc.perform(post("/save")
				.param("username", "usernameTest")
				.param("password", (String) null)
				.param("confirmPassword", (String) null))
			.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MESSAGE, "Password is required."))
			.andExpect(view().name("registration"));
	}

	@Test
	public void testSave_UsernameIsEmpty_ShouldBeBadRequest() throws Exception {

		mvc.perform(post("/save")
				.param("username", (String) null)
				.param("password", "pwdTest")
				.param("confirmPassword", "pwdTest"))
			.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MESSAGE, "Username is required."))
			.andExpect(view().name("registration"));
	}
	
	@Test
	public void testSave_PasswordsDoNotMatch_ShouldBeBadRequest() throws Exception {
		
		mvc.perform(post("/save")
				.param("username", "usernameTest")
				.param("password", "password")
				.param("confirmPassword", "anotherPassword"))
			.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MESSAGE, "Password and Confirm Password must match."))
			.andExpect(view().name("registration"));
	}
	
	@Test
	public void testSave_PasswordOrUsernameAreInvalid_ShouldBeBadRequest() throws Exception {
		when(userService.insertNewUser(new User(null, "some_unexpected_characters", "some_unexpected_characters"))).thenThrow(DataIntegrityViolationException.class);
		
		mvc.perform(post("/save")
				.param("username", "some_unexpected_characters")
				.param("password", "some_unexpected_characters")
				.param("confirmPassword", "some_unexpected_characters"))
			.andExpect(status().is(HttpStatus.BAD_REQUEST.value()))
			.andExpect(model().attribute(MESSAGE, "Username or password invalid."))
			.andExpect(view().name("registration"));
	}

	@Test
	public void testSearch_ResultListsAreEmpty_ShouldShowMessage() throws Exception {
		String content = "content";
		when(userService.getUsersByUsernameLike(content)).thenReturn(Collections.emptyList());
		when(gameService.getGamesByNameLike(content)).thenReturn(Collections.emptyList());

		mvc.perform(get("/search").param("content_search", content))
			.andExpect(model().attribute(MESSAGE, "No element found."))
			.andExpect(view().name("search"));
	}

	@Test
	public void testSearch_ContentIsEmpty_ShouldShowError() throws Exception {
		mvc.perform(get("/search").param("content_search", ""))
			.andExpect(model().attribute(MESSAGE, "Error: search field was empty."))
			.andExpect(view().name("search"));
	}

	@Test
	public void testSearch_ContentIsOnlyWhitespaces_ShouldShowError() throws Exception {
		mvc.perform(get("/search").param("content_search", "   "))
			.andExpect(model().attribute(MESSAGE, "Error: search field was empty."))
			.andExpect(view().name("search"));
	}

	@Test
	public void testSearch_ContentIsValid_ShouldBeTrimmed() throws Exception {
		String trimmedContent = new String(" someName ").trim();

		mvc.perform(get("/search").param("content_search", " someName "));

		verify(userService).getUsersByUsernameLike(trimmedContent);
		verify(gameService).getGamesByNameLike(trimmedContent);
	}

	@Test
	public void testSearch_ResultListsNotEmpty_ShouldShowUsersAndGames() throws Exception {
		String content = "content";
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");
		when(userService.getUsersByUsernameLike(content)).thenReturn(asList(user1, user2));

		Game game1 = new Game(1L, "name1", "description1", new Date(1000));
		Game game2 = new Game(2L, "name2", "description2", new Date(2000));
		when(gameService.getGamesByNameLike(content)).thenReturn(asList(game1, game2));

		mvc.perform(get("/search").param("content_search", content))
			.andExpect(model().attribute(USERS_LIST, asList(user1, user2)))
			.andExpect(model().attribute(GAMES_LIST, asList(game1, game2)))
			.andExpect(view().name("search"));
	}

	@Test
	public void testSearch_UsersFoundListIsEmpty_ShouldShowOnlyGames() throws Exception {
		String content = "content";
		when(userService.getUsersByUsernameLike(content)).thenReturn(Collections.emptyList());
		Game game1 = new Game(1L, "name1", "description1", new Date(1000));
		Game game2 = new Game(2L, "name2", "description2", new Date(2000));
		when(gameService.getGamesByNameLike(content)).thenReturn(asList(game1, game2));

		mvc.perform(get("/search").param("content_search", content))
			.andExpect(model().attribute(USERS_LIST, Collections.emptyList()))
			.andExpect(model().attribute(GAMES_LIST, asList(game1, game2)))
			.andExpect(view().name("search"));
	}

	@Test
	public void testSearch_GamesFoundListIsEmpty_ShouldShowOnlyUsers() throws Exception {
		String content = "content";
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");
		when(userService.getUsersByUsernameLike(content)).thenReturn(asList(user1, user2));

		when(gameService.getGamesByNameLike(content)).thenReturn(Collections.emptyList());

		mvc.perform(get("/search").param("content_search", content))
			.andExpect(model().attribute(USERS_LIST, asList(user1, user2)))
			.andExpect(model().attribute(GAMES_LIST, Collections.emptyList()))
			.andExpect(view().name("search"));
	}

	@Test
	public void testProfile_UserFound() throws Exception {
		User user = new User(1L, "usernameTest", "password");
		when(userService.getUserByUsername("usernameTest")).thenReturn(user);

		mvc.perform(get("/profile/usernameTest"))
			.andExpect(model().attribute("user", user))
			.andExpect(view().name("profile"));
	}

	@Test
	public void testProfile_ProfileNotFound_ShouldRedirectToPage404() throws Exception {
		when(userService.getUserByUsername("wrong_username")).thenThrow(UserNotFoundException.class);

		mvc.perform(get("/profile/wrong_username"))
			.andExpect(status().isNotFound())
			.andExpect(model().attribute(MESSAGE, "Profile not found."))
			.andExpect(request().sessionAttribute("user", equalTo(null)))
			.andExpect(view().name("profile404"));
	}

	@Test
	public void testGame_GameFound() throws Exception {
		Game game = new Game(1L, "gamenameTest", "gamedescription", new Date(1000));
		when(gameService.getGameByName("gamenameTest")).thenReturn(game);

		mvc.perform(get("/game/gamenameTest"))
			.andExpect(model().attribute("game", game))
			.andExpect(view().name("game"));
	}

	@Test
	public void testGame_GameNotFound_ShouldRedirectToPage404() throws Exception {
		when(gameService.getGameByName("wrong_name")).thenThrow(GameNotFoundException.class);

		mvc.perform(get("/game/wrong_name"))
			.andExpect(status().isNotFound())
			.andExpect(model().attribute(MESSAGE, "Game not found."))
			.andExpect(request().sessionAttribute("game", equalTo(null)))
			.andExpect(view().name("game404"));
	}
	
	@Test
	public void testAddFollowedUser_FollowedAddedSuccessfully_ShouldRedirectToProfileOfFollowed() throws Exception {
		User user = new User(1L, "username", "password");
		User followedToAdd = new User(2L, "followedToAdd", "password");
		user.addFollowedUser(followedToAdd);
		followedToAdd.addFollowerUser(user);
		
		when(userService.getUserByUsername("followedToAdd")).thenReturn(followedToAdd);
		when(userService.addFollowedUser(user, followedToAdd)).thenReturn(user);
		
		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnPutRequest(user, "/addUser");
		mvc.perform(requestToPerform.param("followedToAdd", followedToAdd.getUsername()))
			.andExpect(status().is3xxRedirection())
			.andExpect(view().name("redirect:/profile/" + followedToAdd.getUsername()));
	}
	
	@Test
	public void testAddFollowed_UserIsNotLogged_ShouldBeUnauthorized() throws Exception {
		mvc.perform(put("/addUser")
				.param("followedToAdd", "userNotExisting"))
			.andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
			.andExpect(model().attribute(MESSAGE, "Unauthorized Operation. You are not logged in!"))
			.andExpect(view().name("unauthorized401"));
	}
	
	@Test
	public void testAddFollowedUser_FollowedUserDoesNotExist_StatusShouldBeNotFound() throws Exception {
		User user = new User(1L,"username", "password");
		when(userService.getUserByUsername("wrong_username")).thenThrow(UserNotFoundException.class);

		MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnPutRequest(user, "/addUser");
		mvc.perform(requestToPerform
				.param("followedToAdd", "wrong_username"))
			.andExpect(status().isNotFound())
			.andExpect(model().attribute(MESSAGE, "Profile not found."))
			.andExpect(view().name("profile404"));
	}
	
	@Test
	public void testAddGameToUser_GameAddedSuccessfully() throws Exception {
	    User user = new User(1L,"username", "password");
	    Game toAdd = new Game(2L,"nameToAdd", "descriptionToAdd", new Date(1000));
	    User userReturned = new User(1L,"username", "password");
	    userReturned.addGame(toAdd);
	    toAdd.addUser(userReturned);
	    
	    when(gameService.getGameByName("nameToAdd")).thenReturn(toAdd);
	    when(userService.addGame(user, toAdd)).thenReturn(userReturned);
	    
	    MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnPutRequest(user, "/addGame");
	    mvc.perform(requestToPerform.param("nameToAdd", toAdd.getName()))
	        .andExpect(status().is3xxRedirection())
	        .andExpect(view().name("redirect:/game/" + toAdd.getName()));
	}
	
	@Test
	public void testAddGameToUser_UserIsNotLogged() throws Exception {
	    mvc.perform(put("/addGame").param("nameToAdd", "nameToAdd"))
	        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()))
	        .andExpect(model().attribute(MESSAGE, "Unauthorized Operation. You are not logged in!"))
	        .andExpect(view().name("unauthorized401"));
	}
	
	 @Test
	 public void testAddGameToUser_NameInBodyNotFound() throws Exception {
	    User user = new User(1L,"username", "password");
	    when(gameService.getGameByName("wrong_name")).thenThrow(GameNotFoundException.class);

	    MockHttpServletRequestBuilder requestToPerform = addUserToSessionAndReturnPutRequest(user, "/addGame");
	    mvc.perform(requestToPerform.param("nameToAdd", "wrong_name"))
	        .andExpect(status().isNotFound())
	        .andExpect(model().attribute(MESSAGE, "Game not found."))
	        .andExpect(view().name("game404"));
	}
	
	private MockHttpServletRequestBuilder addUserToSessionAndReturnPutRequest(User user, String url) {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", user);
		MockHttpServletRequestBuilder requestToPerform = MockMvcRequestBuilders.put(url).session(session);
		return requestToPerform;
	}
	
	private MockHttpServletRequestBuilder addUserToSessionAndReturnRequest(User user, String url) {
		MockHttpSession session = new MockHttpSession();
		session.setAttribute("user", user);
		MockHttpServletRequestBuilder requestToPerform = MockMvcRequestBuilders.get(url).session(session);
		return requestToPerform;
	}
}
