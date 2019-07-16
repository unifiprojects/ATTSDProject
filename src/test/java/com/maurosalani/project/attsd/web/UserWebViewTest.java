package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.assertFormPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertInputPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertLinkNotPresentWithText;
import static com.gargoylesoftware.htmlunit.WebAssert.assertLinkPresentWithText;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextNotPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTextPresent;
import static com.gargoylesoftware.htmlunit.WebAssert.assertTitleEquals;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebViewTest {

	private static final String LATEST_RELEASES_EXISTING_MESSAGE = "Latest releases!";

	private static final String NO_LATEST_RELEASES_MESSAGE = "No latest releases...";

	private static final String WELCOME_PLEASE_LOGIN = "Welcome! Please Log in or Register";

	private static final String WELCOME_BACK_USER = "Welcome back username";

	@Autowired
	private WebClient webClient;

	@MockBean
	private UserService userService;

	@MockBean
	private GameService gameService;

	@Test
	public void testHomePageTitle() throws Exception {
		HtmlPage page = webClient.getPage("/");
		assertTitleEquals(page, "ATTSD-Project: Social Games");
	}

	@Test
	public void testHomePageWhenUserNotLogged_ShouldContainSearchForm() throws Exception {
		HtmlPage page = webClient.getPage("/");

		assertTitleEquals(page, "ATTSD-Project: Social Games");
		assertTextPresent(page, "ATTSD-Project: Social Games");
		assertTextPresent(page, WELCOME_PLEASE_LOGIN);
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

		String pattern = "dd-mm-yyyy";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		HtmlTable table = page.getHtmlElementById("latestReleases");
		assertThat(removeWindowsCR(table.asText())).isEqualTo("Game1	Description1	"
				+ dateFormat.format(new Date(1)) + "\n" + "Game2	Description2	" + dateFormat.format(new Date(2))
				+ "\n" + "Game3	Description3	" + dateFormat.format(new Date(3)) + "\n" + "Game4	Description4	"
				+ dateFormat.format(new Date(4)));
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
		User user = new User(1L, "username", "pwd");
		when(userService.getUserByUsernameAndPassword("username", "pwd")).thenReturn(user);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", user.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", user.getPassword()));
		HtmlPage page = webClient.getPage(requestSettings);

		assertTextPresent(page, WELCOME_BACK_USER);
		assertLinkPresentWithText(page, "Logout");
		assertTextNotPresent(page, WELCOME_PLEASE_LOGIN);
		assertLinkNotPresentWithText(page, "Log in");
		assertLinkNotPresentWithText(page, "Register");
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
		final HtmlForm loginForm = page.getFormByName("login_form");
		loginForm.getInputByName("username").setValueAttribute("username");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getButtonByName("btn_submit").click();

		verify(userService).getUserByUsernameAndPassword("username", "pwd");
	}

	@Test
	public void testLoginPage_WhenUserAlreadyLogged_ShouldShowMessageAndInputsShouldBeDisabled() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userService.getUserByUsernameAndPassword("username", "pwd")).thenReturn(user);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", user.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", user.getPassword()));
		webClient.getPage(requestSettings);

		HtmlPage page = webClient.getPage("/login");
		final HtmlForm loginForm = page.getFormByName("login_form");
		assertTextPresent(page, "You are already logged! Try to log out from homepage.");
		assertThat(loginForm.getInputByName("username").isDisabled());
		assertThat(loginForm.getInputByName("password").isDisabled());
		assertThat(loginForm.getButtonByName("btn_submit").isDisabled());
	}

	@Test
	public void testLoginPage_WhenUsernameOrPasswordNotCorrect_ShouldShowMessage() throws Exception {
		when(userService.getUserByUsernameAndPassword("username", "pwd")).thenThrow(UserNotFoundException.class);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", "username"));
		requestSettings.getRequestParameters().add(new NameValuePair("password", "pwd"));
		// necessary because 401 would make the test fail, but is the correct status
		// code to return
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		HtmlPage page = webClient.getPage(requestSettings);

		assertTextPresent(page, "Invalid username or password.");
	}

	@Test
	public void testRegistrationPage_ShouldContainRegistrationForm() throws Exception {
		HtmlPage page = webClient.getPage("/registration");

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
		assertLinkPresentWithText(returnedPage, "Homepage");
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

		HtmlTable tableUsers = searchPage.getHtmlElementById("userSearchResults");
		assertThat(removeWindowsCR(tableUsers.asText())).isEqualTo("Users\n" + " user1_nameTest\n" + "user2_nameTest");
		HtmlTable tableGames = searchPage.getHtmlElementById("gameSearchResults");
		assertThat(removeWindowsCR(tableGames.asText())).isEqualTo("Games\n" + " game1_nameTest\n" + "game2_nameTest");

		assertTextNotPresent(page, "No Users");
		assertTextNotPresent(page, "No Games");
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

		assertTextPresent(searchPage, "No Users");
		assertTextPresent(searchPage, "No Games");
	}

	@Test
	public void testProfile_WhenProfileNotFound_ShouldShowProfile404() throws Exception {
		when(userService.getUserByUsername("username_wrong")).thenThrow(UserNotFoundException.class);
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/username_wrong");
		assertTitleEquals(page, "Profile not found");
		assertTextPresent(page, "Profile not found.");
		assertLinkPresentWithText(page, "Homepage");
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

		assertTextPresent(page, "Username: " + user.getUsername());

		HtmlTable tableUsers = page.getHtmlElementById("userFollowed");
		assertThat(removeWindowsCR(tableUsers.asText()))
				.isEqualTo("Users followed\n" + " user1_nameTest\n" + "user2_nameTest");
		HtmlTable tableGames = page.getHtmlElementById("games");
		assertThat(removeWindowsCR(tableGames.asText())).isEqualTo("Games\n" + " game1_nameTest\n" + "game2_nameTest");
		assertTextNotPresent(page, "No Users");
		assertTextNotPresent(page, "No Games");
		assertLinkPresentWithText(page, "user1_nameTest");
		assertLinkPresentWithText(page, "user2_nameTest");
		assertLinkPresentWithText(page, "game1_nameTest");
		assertLinkPresentWithText(page, "game2_nameTest");
		assertLinkPresentWithText(page, "Homepage");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessAnotherProfile_ShouldShowAddToFollowedButton() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "pwdLogged");
		when(userService.getUserByUsernameAndPassword("usernameLogged", "pwdLogged")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);

		User user = new User(2L, "username", "pwd");

		when(userService.getUserByUsername("username")).thenReturn(user);

		HtmlPage page = webClient.getPage("/profile/username");

		assertTextPresent(page, "Username: " + user.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");

		assertLinkPresentWithText(page, "Homepage");

		final HtmlForm addToFollowedForm = page.getFormByName("addToFollowed_form");
		assertThat(addToFollowedForm.getInputByName("followedToAdd").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getButtonByName("btn_add").getDisabledAttribute()).isEqualTo("");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessFollowedProfile_ShouldShowNoButton() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "pwdLogged");
		User userFollowed = new User(1L, "usernameFollowed", "pwdFollowed");

		userLogged.addFollowedUser(userFollowed);

		when(userService.getUserByUsernameAndPassword("usernameLogged", "pwdLogged")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);

		when(userService.getUserByUsername("usernameFollowed")).thenReturn(userFollowed);

		HtmlPage page = webClient.getPage("/profile/usernameFollowed");

		assertTextPresent(page, "Username: " + userFollowed.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");

		assertLinkPresentWithText(page, "Homepage");
		assertTextNotPresent(page, "Add to followed");
	}

	@Test
	public void testProfile_WhenLoggedUserAccessHisProfile_ShouldShowChangePasswordButton() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "pwdLogged");
		when(userService.getUserByUsernameAndPassword("usernameLogged", "pwdLogged")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);

		HtmlPage page = webClient.getPage("/profile/usernameLogged");

		assertTextPresent(page, "Username: " + userLogged.getUsername());
		assertTextPresent(page, "No Users");
		assertTextPresent(page, "No Games");

		assertLinkPresentWithText(page, "Homepage");
		assertTextNotPresent(page, "Add to followed");

		final HtmlForm addToFollowedForm = page.getFormByName("changePassword_form");
		assertThat(addToFollowedForm.getInputByName("oldPassword").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getInputByName("newPassword").getDisabledAttribute()).isEqualTo("");
		assertThat(addToFollowedForm.getButtonByName("btn_change").getDisabledAttribute()).isEqualTo("");
	}

	@Test
	public void testProfile_UserLoggedAndPressAddFollowed_ShouldRedirectToFollowedProfile() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "pwdLogged");

		// Login with userLogged
		when(userService.getUserByUsernameAndPassword("usernameLogged", "pwdLogged")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);

		User user = new User(2L, "username", "pwd");
		User userLoggedWithFollowed = new User(1L, "usernameLogged", "pwdLogged");
		userLoggedWithFollowed.addFollowedUser(user);
		user.addFollowerUser(userLoggedWithFollowed);
		when(userService.getUserByUsername("username")).thenReturn(user);
		when(userService.addFollowedUser(userLogged, user)).thenReturn(userLoggedWithFollowed);

		// Click on btn_add
		HtmlPage page = webClient.getPage("/profile/username");
		final HtmlForm addToFollowedForm = page.getFormByName("addToFollowed_form");
		HtmlPage returnedPage = addToFollowedForm.getButtonByName("btn_add").click();

		assertTextPresent(returnedPage, "Username: " + user.getUsername());
		assertTextPresent(returnedPage, "No Users");
		assertTextPresent(returnedPage, "No Games");
		assertLinkPresentWithText(returnedPage, "Homepage");
		assertTextNotPresent(returnedPage, "Add to followed");
	}
	
	@Test
	public void testProfile_UserLoggedAndPressChangePassword_NewPasswordIsEmpty() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "oldPassword");
	
		// Login with userLogged
		when(userService.getUserByUsernameAndPassword("usernameLogged", "oldPassword")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);

		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
	
		// Click on btn_change
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("oldPassword");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();

		assertTitleEquals(returnedPage, "Password error");
		assertTextPresent(returnedPage, "New password is required.");
		assertLinkPresentWithText(returnedPage, "Homepage");
	}
	
	@Test
    public void testProfile_UserLoggedAndPressChangePassword_OldPasswordNotMatch() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "oldPassword");
		
		// Login with userLogged
		when(userService.getUserByUsernameAndPassword("usernameLogged", "oldPassword")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);
		
		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		
		// Click on btn_change
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		
		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("oldPassword_wrong");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("newPassword");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();

		assertTitleEquals(returnedPage, "Password error");
		assertTextPresent(returnedPage, "Old password do not match.");
		assertLinkPresentWithText(returnedPage, "Homepage");
    }
	
	@Test
    public void testProfile_UserLoggedAndPressChangePassword_Success() throws Exception {
		User userLogged = new User(1L, "usernameLogged", "oldPassword");
		
		// Login with userLogged
		when(userService.getUserByUsernameAndPassword("usernameLogged", "oldPassword")).thenReturn(userLogged);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", userLogged.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", userLogged.getPassword()));
		webClient.getPage(requestSettings);
		
		when(userService.getUserByUsername("usernameLogged")).thenReturn(userLogged);
		
		// Click on btn_change
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		
		HtmlPage page = webClient.getPage("/profile/usernameLogged");
		final HtmlForm changePasswordForm = page.getFormByName("changePassword_form");
		changePasswordForm.getInputByName("oldPassword").setValueAttribute("oldPassword");
		changePasswordForm.getInputByName("newPassword").setValueAttribute("newPassword");
		HtmlPage returnedPage = changePasswordForm.getButtonByName("btn_change").click();
		
		assertTitleEquals(returnedPage, "Password changed");
		assertTextPresent(returnedPage, "Password changed successfully.");
		assertLinkPresentWithText(returnedPage, "Homepage");
    }

	@Before
	/**
	 * Necessary to clear session
	 */
	public void clearSessionOfWebClient() {
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(true);
		webClient.getCookieManager().clearCookies();
	}

	private String removeWindowsCR(String s) {
		return s.replaceAll("\r", "");
	}

}
