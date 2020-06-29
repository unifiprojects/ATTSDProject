package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.assertFormNotPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertFormPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertInputPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertLinkNotPresentWithText;
import static com.gargoylesoftware.htmlunit.WebAssert.assertLinkPresentWithText;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextNotPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTitleEquals;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = WebController.class)
public class WebViewTest {

	private static final String LATEST_RELEASES_EXISTING_MESSAGE = "Latest releases!";

	private static final String NO_LATEST_RELEASES_MESSAGE = "No latest releases...";

	private static final String WELCOME_LOGIN = "Log in or Register";

	private static final String WELCOME_BACK_USER = "Welcome back";

	@Autowired
	private WebClient webClient;

	@MockBean
	private UserService userService;

	@MockBean
	private GameService gameService;

	@Before
	/**
	 * Necessary to clear session
	 */
	public void clearSessionOfWebClient() {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getCookieManager().clearCookies();
	}

	@Test
	public void testHomePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/");
		assertTitleEquals(page, "Social Games");
	}

	@Test
	public void testHomePageWhenUserNotLogged_ShouldContainSearchForm() throws Exception {
		HtmlPage page = webClient.getPage("/");

		assertTitleEquals(page, "Social Games");
		assertTextPresent(page, "Social Games");
		assertTextPresent(page, WELCOME_LOGIN);
		assertFormPresent(page, "search_form");
		assertInputPresent(page, "content_search");
	}

	@Test
	public void testHomePageWhenUserNotLogged_ShouldContainLinksToLogin() throws Exception {
		HtmlPage page = webClient.getPage("/");

		assertThat(page.getAnchorByText("Log in").getHrefAttribute()).isEqualTo("/login");
		assertThat(page.getAnchorByText("Register").getHrefAttribute()).isEqualTo("/registration");
	}

	@Test
	public void testHomePage_ShouldContainGamesRecommendedSection() throws Exception {
		Game game1 = new Game(1L, "Game1", "Description1", new Date(1));
		Game game2 = new Game(2L, "Game2", "Description2", new Date(2));
		Game game3 = new Game(3L, "Game3", "Description3", new Date(3));
		Game game4 = new Game(4L, "Game4", "Description4", new Date(4));
		when(gameService.getLatestReleasesGames(anyInt())).thenReturn(asList(game1, game2, game3, game4));
		HtmlPage page = webClient.getPage("/");

		String pattern = "dd-MM-yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		assertThat(page.getElementById("latestReleases").getTextContent()).contains("Game1", "Description1",
				dateFormat.format(new Date(1)));
		assertThat(page.getElementById("latestReleases").getTextContent()).contains("Game2", "Description2",
				dateFormat.format(new Date(2)));
		assertThat(page.getElementById("latestReleases").getTextContent()).contains("Game3", "Description3",
				dateFormat.format(new Date(3)));
		assertThat(page.getElementById("latestReleases").getTextContent()).contains("Game4", "Description4",
				dateFormat.format(new Date(4)));
		assertTextPresent(page, LATEST_RELEASES_EXISTING_MESSAGE);
		assertTextNotPresent(page, NO_LATEST_RELEASES_MESSAGE);
	}

	@Test
	public void testHomePageWithNoLatestReleases() throws Exception {
		HtmlPage page = webClient.getPage("/");

		assertTextPresent(page, NO_LATEST_RELEASES_MESSAGE);
		assertTextNotPresent(page, LATEST_RELEASES_EXISTING_MESSAGE);
	}

	@Test
	public void testHomePage_UserLoginWithSuccess() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		HtmlPage page = webClient.getPage(requestToLogin);

		assertTextPresent(page, WELCOME_BACK_USER);
		assertLinkPresentWithText(page, "Logout");
		assertTextNotPresent(page, WELCOME_LOGIN);
		assertLinkNotPresentWithText(page, "Log in");
		assertLinkNotPresentWithText(page, "Register");
	}

	@Test
	public void testHomePage_LogoutSuccess() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		HtmlPage page = webClient.getPage("/logout");
		assertTitleEquals(page, "Social Games");
		assertTextPresent(page, "Social Games");
		assertTextPresent(page, WELCOME_LOGIN);
		assertFormPresent(page, "search_form");
		assertInputPresent(page, "content_search");
		assertThat(page.getAnchorByText("Log in").getHrefAttribute()).isEqualTo("/login");
		assertThat(page.getAnchorByText("Register").getHrefAttribute()).isEqualTo("/registration");
	}

	@Test
	public void testHomePage_LogoutNoUserLogged() throws Exception {
		HtmlPage page = webClient.getPage("/logout");
		assertTitleEquals(page, "Social Games");
		assertTextPresent(page, "Social Games");
		assertTextPresent(page, WELCOME_LOGIN);
		assertFormPresent(page, "search_form");
		assertInputPresent(page, "content_search");
		assertThat(page.getAnchorByText("Log in").getHrefAttribute()).isEqualTo("/login");
		assertThat(page.getAnchorByText("Register").getHrefAttribute()).isEqualTo("/registration");
	}

	@Test
	public void testLoginPage_ShouldContainLoginForm() throws Exception {
		HtmlPage page = webClient.getPage("/login");

		assertThat(page.getAnchorByText("Go back to homepage").getHrefAttribute()).isEqualTo("/");
		assertFormPresent(page, "login_form");
		final HtmlForm loginForm = page.getFormByName("login_form");
		assertThat(loginForm.getInputByName("username").getDisabledAttribute()).isEqualTo("");
		assertThat(loginForm.getInputByName("password").getDisabledAttribute()).isEqualTo("");
		assertThat(loginForm.getButtonByName("btn_submit").getDisabledAttribute()).isEqualTo("");
		assertTextNotPresent(page, "You are already logged! Try to log out from homepage.");
	}

	@Test
	public void testLoginPage_UserCredentialsAreReceived() throws Exception {
		HtmlPage page = webClient.getPage("/login");
		
		User user = new User(1L,"username", "pwd");
		when(userService.verifyLogin(new CredentialsDTO(user.getUsername(),user.getPassword()))).thenReturn(user);
		when(userService.getUserByUsername("username")).thenReturn(user);
		
		final HtmlForm loginForm = page.getFormByName("login_form");
		loginForm.getInputByName("username").setValueAttribute("username");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getButtonByName("btn_submit").click();

		verify(userService).verifyLogin(new CredentialsDTO("username", "pwd"));
	}

	@Test
	public void testLoginPage_WhenUserAlreadyLogged_ShouldShowMessageAndInputsShouldBeDisabled() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getPage(requestToLogin);

		HtmlPage page = webClient.getPage("/login");
		final HtmlForm loginForm = page.getFormByName("login_form");
		assertTextPresent(page, "You are already logged! Try to log out from homepage.");
		assertTrue(loginForm.getInputByName("username").isDisabled());
		assertTrue(loginForm.getInputByName("password").isDisabled());
		assertTrue(loginForm.getButtonByName("btn_submit").isDisabled());
	}

	@Test
	public void testLoginPage_WhenUsernameOrPasswordNotCorrect_ShouldShowMessage() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		WebRequest requestToLogin = createWebRequestToLogin(credentials, false);
		// necessary because 401 would make the test fail, but is the correct status
		// code to return
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		HtmlPage page = webClient.getPage(requestToLogin);

		assertTextPresent(page, "Invalid username or password.");
	}

	@Test
	public void testRegistrationPage_ShouldContainRegistrationForm() throws Exception {
		HtmlPage page = webClient.getPage("/registration");

		assertTextPresent(page, "Registration");
		assertThat(page.getAnchorByText("Go back to homepage").getHrefAttribute()).isEqualTo("/");
		assertFormPresent(page, "registration_form");
		final HtmlForm registrationForm = page.getFormByName("registration_form");
		assertThat(registrationForm.getInputByName("username").getDisabledAttribute()).isEqualTo("");
		assertThat(registrationForm.getInputByName("password").getDisabledAttribute()).isEqualTo("");
		assertThat(registrationForm.getInputByName("confirmPassword").getDisabledAttribute()).isEqualTo("");
		assertThat(registrationForm.getButtonByName("btn_submit").getDisabledAttribute()).isEqualTo("");

		assertTextNotPresent(page, "You are already logged! Try to log out from homepage.");
	}

	@Test
	public void testRegistration_UserCredentialsAreReceivedCorrectly() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		final HtmlForm registrationForm = page.getFormByName("registration_form");
		registrationForm.getInputByName("username").setValueAttribute("username");
		registrationForm.getInputByName("password").setValueAttribute("pwd");
		registrationForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage returnedPage = registrationForm.getButtonByName("btn_submit").click();

		verify(userService).insertNewUser(new User(null, "username", "pwd"));
		assertTitleEquals(returnedPage, "Registration Success");
		assertTextPresent(returnedPage, "Your registration has been successful!");
		assertTextPresent(returnedPage, "Registration");
		assertLinkPresentWithText(returnedPage, "Go back to homepage");
	}

	@Test
	public void testRegistration_WhenUsernameEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm registrationForm = page.getFormByName("registration_form");
		registrationForm.getInputByName("username").setValueAttribute("");
		registrationForm.getInputByName("password").setValueAttribute("pwd");
		registrationForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage pageAfterRegistration = registrationForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Username is required.");
	}

	@Test
	public void testRegistration_WhenPasswordEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm registrationForm = page.getFormByName("registration_form");
		registrationForm.getInputByName("username").setValueAttribute("usernameTest");
		registrationForm.getInputByName("password").setValueAttribute("");
		registrationForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage pageAfterRegistration = registrationForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Password is required.");
	}

	@Test
	public void testRegistration_WhenConfirmPasswordEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm registrationForm = page.getFormByName("registration_form");
		registrationForm.getInputByName("username").setValueAttribute("usernameTest");
		registrationForm.getInputByName("password").setValueAttribute("pwd");
		registrationForm.getInputByName("confirmPassword").setValueAttribute("");
		HtmlPage pageAfterRegistration = registrationForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Password is required.");
	}

	@Test
	public void testRegistration_WhenPasswordsDoNotMatch_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm registrationForm = page.getFormByName("registration_form");
		registrationForm.getInputByName("username").setValueAttribute("usernameTest");
		registrationForm.getInputByName("password").setValueAttribute("pwd");
		registrationForm.getInputByName("confirmPassword").setValueAttribute("anotherPwd");
		HtmlPage pageAfterRegistration = registrationForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent())
				.contains("Password and Confirm Password must match.");
	}

	@Test
	public void testRegistration_UserAlreadyLogged() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		HtmlPage page = webClient.getPage("/registration");

		assertTextPresent(page, "You are already logged! Try to log out from homepage.");
		assertLinkPresentWithText(page, "Go back to homepage");
		assertTextPresent(page, "Register");
	}

	@Test
	public void testSearchBar_WhenUserAndGameExist_ShouldShowLists() throws Exception {
		String content = "nameToSearch";
		User user1 = new User(1L, "user1_nameTest", "pwd");
		User user2 = new User(2L, "user2_nameTest", "pwd");
		Game game1 = new Game(1L, "game1_nameTest", "description", new Date(1));
		Game game2 = new Game(2L, "game2_nameTest", "description", new Date(1));
		when(userService.getUsersByUsernameLike(content)).thenReturn(asList(user1, user2));
		when(gameService.getGamesByNameLike(content)).thenReturn(asList(game1, game2));

		HtmlPage page = webClient.getPage("/");
		final HtmlForm searchForm = page.getFormByName("search_form");
		searchForm.getInputByName("content_search").setValueAttribute(content);
		HtmlPage searchPage = searchForm.getButtonByName("btn_submit").click();

		assertThat(searchPage.getElementById("userSearchResults").getTextContent()).contains("Users", "user1_nameTest",
				"user2_nameTest");
		assertThat(searchPage.getElementById("gameSearchResults").getTextContent()).contains("Games", "game1_nameTest",
				"game2_nameTest");

		assertTextNotPresent(page, "No Users");
		assertTextNotPresent(page, "No Games");
		assertTextPresent(page, "Search");
		assertLinkPresentWithText(searchPage, "user1_nameTest");
		assertLinkPresentWithText(searchPage, "user2_nameTest");
		assertLinkPresentWithText(searchPage, "game1_nameTest");
		assertLinkPresentWithText(searchPage, "game2_nameTest");
	}

	@Test
	public void testSearchBar_WhenNoResults_ShouldShowMessage() throws Exception {
		when(userService.getUsersByUsernameLike(anyString())).thenReturn(Collections.emptyList());
		when(gameService.getGamesByNameLike(anyString())).thenReturn(Collections.emptyList());

		HtmlPage page = webClient.getPage("/");
		final HtmlForm searchForm = page.getFormByName("search_form");
		searchForm.getInputByName("content_search").setValueAttribute("name_not_existing");
		HtmlPage searchPage = searchForm.getButtonByName("btn_submit").click();

		assertTextPresent(page, "Search");

		assertTextPresent(searchPage, "No Users");
		assertTextPresent(searchPage, "No Games");
	}

	@Test
	public void testSearch_WithEmptyString_ShouldShowErrorMessage() throws Exception {
		HtmlPage page = webClient.getPage("/");
		final HtmlForm searchForm = page.getFormByName("search_form");
		searchForm.getInputByName("content_search").setValueAttribute("  ");
		HtmlPage searchPage = searchForm.getButtonByName("btn_submit").click();

		assertTextPresent(page, "Search");
		assertTextPresent(searchPage, "Error: search field was empty.");
	}

	@Test
	public void testProfile_WhenProfileNotFound_ShouldShowProfile404() throws Exception {
		when(userService.getUserByUsername("username_wrong")).thenThrow(UserNotFoundException.class);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/username_wrong");
		assertTitleEquals(page, "Profile not found");
		assertTextPresent(page, "Profile not found.");
		assertLinkPresentWithText(page, "Go to homepage");
	}

	@Test
	public void testProfile_AddUserWhenNoLoggedUser_ShouldShowUnauthorized401() throws Exception {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/addUser"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("followedToAdd", "followedToAdd"));

		HtmlPage page = webClient.getPage(requestSettings);
		assertTitleEquals(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized Operation. You are not logged in!");
		assertLinkPresentWithText(page, "Go to homepage");
	}

	@Test
	public void testProfile_ProfileFound_ShouldShowCorrectly() throws Exception {
		User user1 = new User(1L, "user1_nameTest", "pwd");
		User user2 = new User(2L, "user2_nameTest", "pwd");
		Game game1 = new Game(1L, "game1_nameTest", "description", new Date(1));
		Game game2 = new Game(2L, "game2_nameTest", "description", new Date(1));

		User user = new User(3L, "username", "pwd");
		user.addFollowedUser(user1);
		user.addFollowedUser(user2);
		user.addGame(game1);
		user.addGame(game2);

		when(userService.getUserByUsername("username")).thenReturn(user);

		HtmlPage page = webClient.getPage("/profile/username");

		assertTextPresent(page, user.getUsername());

		assertThat(page.getElementById("userFollowed").getTextContent()).contains("Users followed", "user1_nameTest",
				"user2_nameTest");
		assertThat(page.getElementById("games").getTextContent()).contains("Games", "game1_nameTest", "game2_nameTest");

		assertTextNotPresent(page, "No Users");
		assertTextNotPresent(page, "No Games");
		assertLinkPresentWithText(page, "user1_nameTest");
		assertLinkPresentWithText(page, "user2_nameTest");
		assertLinkPresentWithText(page, "game1_nameTest");
		assertLinkPresentWithText(page, "game2_nameTest");
		assertLinkPresentWithText(page, "Go back to homepage");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessAnotherProfile_ShouldShowAddToFollowedButton() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("userLogged", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getPage(requestToLogin);

		User user = new User(2L, "someUser", "pwd");
		when(userService.getUserByUsername("someUser")).thenReturn(user);

		HtmlPage page = webClient.getPage("/profile/someUser");

		assertTextPresent(page, user.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");
		assertLinkPresentWithText(page, "Go back to homepage");

		final HtmlForm addToFollowedForm = page.getFormByName("addToFollowed_form");
		assertThat(addToFollowedForm.getInputByName("followedToAdd").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getButtonByName("btn_add").getDisabledAttribute()).isEqualTo("");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessFollowedProfile_ShouldShowNoButton() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("userLogged", "pwd");
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		User userFollowed = new User(2L, "usernameFollowed", "pwdFollowed");
		userLogged.addFollowedUser(userFollowed);

		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		when(userService.verifyLogin(credentials)).thenReturn(userLogged);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getPage(requestToLogin);

		when(userService.getUserByUsername("usernameFollowed")).thenReturn(userFollowed);
		HtmlPage page = webClient.getPage("/profile/usernameFollowed");

		assertTextPresent(page, userFollowed.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");

		assertLinkPresentWithText(page, "Go back to homepage");
		assertTextNotPresent(page, "Add to followed");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessHisPersonalProfile_ShouldShowChangePasswordButton() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("usernameLogged", "pwdLogged");
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		HtmlPage page = webClient.getPage("/profile/usernameLogged");

		assertTextPresent(page, userLogged.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");

		assertLinkPresentWithText(page, "Go back to homepage");
		assertTextNotPresent(page, "Add to followed");

		final HtmlForm addToFollowedForm = page.getFormByName("changePassword_form");
		assertThat(addToFollowedForm.getInputByName("oldPassword").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getInputByName("newPassword").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getButtonByName("btn_change").getDisabledAttribute()).isEqualTo("");
	}

	@Test
	public void testProfile_UserLoggedAndPressAddFollowed_ShouldRedirectToFollowedProfile() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("usernameLogged", "pwdLogged");
		when(userService.getUserByUsername(credentials.getUsername()))
			.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		User userFollowed = new User(2L, "userFollowed", "pwd");
		when(userService.getUserByUsername("userFollowed")).thenReturn(userFollowed);
		
		User userLoggedResult = new User(1L, credentials.getUsername(), credentials.getPassword());

		userLoggedResult.addFollowedUser(userFollowed);
		when(userService.addFollowedUser(userLogged, userFollowed)).thenReturn(userLogged);

		HtmlPage page = webClient.getPage("/profile/userFollowed");
		final HtmlForm addToFollowedForm = page.getFormByName("addToFollowed_form");
		HtmlPage returnedPage = addToFollowedForm.getButtonByName("btn_add").click();

		assertTextPresent(returnedPage, userFollowed.getUsername());
		assertTextPresent(returnedPage, "No Users");
		assertTextPresent(returnedPage, "No Games");
		assertLinkPresentWithText(returnedPage, "Go back to homepage");
		assertTextNotPresent(returnedPage, "Add to followed");
	}

	@Test
	public void testProfile_UserLoggedAndPressChangePassword_NewPasswordIsEmpty() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("usernameLogged", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("pwd");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();

		assertTitleEquals(returnedPage, "Password error");
		assertTextPresent(returnedPage, "Password is required.");
		assertLinkPresentWithText(returnedPage, "Go to homepage");
	}

	@Test
	public void testProfile_UserLoggedAndPressChangePassword_OldPasswordNotMatch() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("usernameLogged", "pwdLogged");
		when(userService.getUserByUsername(credentials.getUsername()))
		.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("oldPassword_wrong");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("newPassword");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();

		assertTitleEquals(returnedPage, "Password error");
		assertTextPresent(returnedPage, "Old password do not match.");
		assertLinkPresentWithText(returnedPage, "Go to homepage");
	}

	@Test
	public void testProfile_ChangePasswordWhenNoLoggedUser_ShouldShowUnauthorized401() throws Exception {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/changePassword"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("oldPassword", "oldPassword"));
		requestSettings.getRequestParameters().add(new NameValuePair("newPassword", "newPassword"));

		HtmlPage page = webClient.getPage(requestSettings);
		assertTitleEquals(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized Operation. You are not logged in!");
		assertLinkPresentWithText(page, "Go to homepage");
	}

	@Test
	public void testProfile_UserLoggedAndPressChangePassword_Success() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("usernameLogged", "pwd");
		User userLogged = new User(1L, credentials.getUsername(), credentials.getPassword());
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(userLogged);
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		User userLoggedResult = new User(1L, credentials.getUsername(), "newPassword");

		when(userService.changePassword(userLogged, "newPassword")).thenReturn(userLoggedResult);
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("pwd");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("newPassword");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();

		assertTitleEquals(returnedPage, "Password changed");
		assertTextPresent(returnedPage, "Password changed successfully.");
		assertLinkPresentWithText(returnedPage, "Go back to homepage");
	}

	@Test
	public void testProfileGame_ProfileGameFound_ShouldShowCorrectly() throws Exception {
		Game game = new Game(1L, "game_nameTest", "description", new Date(1));
		User user1 = new User(1L, "user1_nameTest", "pwd");
		User user2 = new User(2L, "user2_nameTest", "pwd");
		game.addUser(user1);
		game.addUser(user2);

		when(gameService.getGameByName("game_nameTest")).thenReturn(game);
		HtmlPage page = webClient.getPage("/game/game_nameTest");

		assertThat(page.getElementById("usersFans").getTextContent()).contains("user1_nameTest", "user2_nameTest");

		String pattern = "dd-mm-yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		assertTitleEquals(page, game.getName());
		assertThat(page.getElementById("nameOfGame").getTextContent()).isEqualTo(game.getName());
		assertThat(page.getElementById("releaseDate").getTextContent()).isEqualTo(dateFormat.format(new Date(1)));
		assertThat(page.getElementById("description").getTextContent()).isEqualTo(game.getDescription());
		assertTextNotPresent(page, "No users like this game yet...");
		assertLinkPresentWithText(page, "user1_nameTest");
		assertLinkPresentWithText(page, "user2_nameTest");
		assertLinkPresentWithText(page, "Go back to homepage");
	}

	@Test
	public void testProfileGame_LoggedUserShouldSeeLikeButton() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		when(userService.getUserByUsername(credentials.getUsername()))
		.thenReturn(new User(1L, credentials.getUsername(), credentials.getPassword()));
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);

		Game game = new Game(1L, "game_nameTest", "description", new Date(1));
		when(gameService.getGameByName("game_nameTest")).thenReturn(game);

		HtmlPage page = webClient.getPage("/game/game_nameTest");
		assertThat(page.getFormByName("like_form").getButtonByName("btn_like").getDisabledAttribute()).isEqualTo("");
		assertLinkPresentWithText(page, "Go back to homepage");
	}

	@Test
	public void testProfileGame_LoggedUserAlreadyLikeThisGame_ShouldNotSeeButton() throws Exception {
		CredentialsDTO credentials = new CredentialsDTO("username", "pwd");
		User loggedUser = new User(1L, credentials.getUsername(), credentials.getPassword());
		when(userService.getUserByUsername(credentials.getUsername()))
				.thenReturn(loggedUser);
		WebRequest requestToLogin = createWebRequestToLogin(credentials, true);
		webClient.getPage(requestToLogin);
		
		Game game = new Game(1L, "game_nameTest", "description", new Date(1));
		when(gameService.getGameByName("game_nameTest")).thenReturn(game);
		User loggedUserResult = new User(1L, credentials.getUsername(), credentials.getPassword());
		loggedUserResult.addGame(game);
		when(userService.addGame(loggedUser, game)).thenReturn(loggedUserResult);

		HtmlPage page = webClient.getPage("/game/game_nameTest");
		game.addUser(loggedUser);
		when(gameService.getGameByName("game_nameTest")).thenReturn(game);
		HtmlPage pageGameAfterUserPutLike = page.getFormByName("like_form").getButtonByName("btn_like").click();

		assertFormNotPresent(pageGameAfterUserPutLike, "like_form");
		assertLinkPresentWithText(page, "Go back to homepage");
	}

	@Test
	public void testProfileGame_WhenProfileNotFound_ShouldShowGame404() throws Exception {
		when(gameService.getGameByName("name_wrong")).thenThrow(GameNotFoundException.class);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/game/name_wrong");
		assertTitleEquals(page, "Game not found");
		assertTextPresent(page, "Game not found.");
		assertLinkPresentWithText(page, "Go to homepage");
	}

	@Test
	public void testProfileGame_AddGameWhenNoLoggedUser_ShouldShowUnauthorized401() throws Exception {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/addGame"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("gameToAdd", "gameToAdd"));

		HtmlPage page = webClient.getPage(requestSettings);
		assertTitleEquals(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized");
		assertTextPresent(page, "Unauthorized Operation. You are not logged in!");
		assertLinkPresentWithText(page, "Go to homepage");
	}

	private WebRequest createWebRequestToLogin(CredentialsDTO credentials, boolean loginShouldSuccess)
			throws LoginFailedException, FailingHttpStatusCodeException, IOException, UsernameAlreadyExistingException,
			PasswordRequiredException {
		if (loginShouldSuccess)
			when(userService.verifyLogin(credentials))
					.thenReturn(new User(null, credentials.getUsername(), credentials.getPassword()));
		else
			when(userService.verifyLogin(credentials)).thenThrow(LoginFailedException.class);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", credentials.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", credentials.getPassword()));
		return requestSettings;
	}

}
