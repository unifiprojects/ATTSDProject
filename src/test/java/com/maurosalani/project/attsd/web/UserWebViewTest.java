package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
		assertInputPresent(page, "search_bar");
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
		assertThat(loginForm.getInputByName("username").getDisabledAttribute()).isEqualTo("disabled");
		assertThat(loginForm.getInputByName("password").getDisabledAttribute()).isEqualTo("disabled");
		assertThat(loginForm.getButtonByName("btn_submit").getDisabledAttribute()).isEqualTo("disabled");
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
	public void testRegistrationPage_WhenUserAldreadyLogged_ShouldShowMessageAndInputsShouldBeDisabled() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userService.getUserByUsernameAndPassword("username", "pwd")).thenReturn(user);
		WebRequest requestSettings = new WebRequest(new URL("http://localhost/verifyLogin"), HttpMethod.POST);
		requestSettings.setRequestParameters(new ArrayList<>());
		requestSettings.getRequestParameters().add(new NameValuePair("username", user.getUsername()));
		requestSettings.getRequestParameters().add(new NameValuePair("password", user.getPassword()));
		webClient.getPage(requestSettings);

		HtmlPage page = webClient.getPage("/registration");

		assertTextPresent(page, "You are already logged! Try to log out from homepage.");

		final HtmlForm registrationForm = page.getFormByName("registration_form");
		assertThat(registrationForm.getInputByName("username").getDisabledAttribute()).isEqualTo("disabled");
		assertThat(registrationForm.getInputByName("password").getDisabledAttribute()).isEqualTo("disabled");
		assertThat(registrationForm.getInputByName("confirmPassword").getDisabledAttribute()).isEqualTo("disabled");
		assertThat(registrationForm.getButtonByName("btn_submit").getDisabledAttribute()).isEqualTo("disabled");
	}

	@Test
	public void testRegistration_UserCredentialsAreReceivedCorrectly() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		final HtmlForm loginForm = page.getFormByName("registration_form");
		loginForm.getInputByName("username").setValueAttribute("username");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage returnedPage = loginForm.getButtonByName("btn_submit").click();

		verify(userService).insertNewUser(new User(null, "username", "pwd"));
		assertTitleEquals(returnedPage, "Registration Success");
		assertTextPresent(returnedPage, "Your registration has been successful!");
		assertLinkPresentWithText(returnedPage, "Homepage");
	}

	@Test
	public void testRegistration_WhenUsernameEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm loginForm = page.getFormByName("registration_form");
		loginForm.getInputByName("username").setValueAttribute("");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage pageAfterRegistration = loginForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Username is required.");
	}

	@Test
	public void testRegistration_WhenPasswordEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm loginForm = page.getFormByName("registration_form");
		loginForm.getInputByName("username").setValueAttribute("usernameTest");
		loginForm.getInputByName("password").setValueAttribute("");
		loginForm.getInputByName("confirmPassword").setValueAttribute("pwd");
		HtmlPage pageAfterRegistration = loginForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Password is required.");
	}

	@Test
	public void testRegistration_WhenConfirmPasswordEmpty_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm loginForm = page.getFormByName("registration_form");
		loginForm.getInputByName("username").setValueAttribute("usernameTest");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getInputByName("confirmPassword").setValueAttribute("");
		HtmlPage pageAfterRegistration = loginForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent()).contains("Password is required.");
	}

	@Test
	public void testRegistration_WhenPasswordsDoNotMatch_ShouldShowMessage() throws Exception {
		HtmlPage page = webClient.getPage("/registration");
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);

		final HtmlForm loginForm = page.getFormByName("registration_form");
		loginForm.getInputByName("username").setValueAttribute("usernameTest");
		loginForm.getInputByName("password").setValueAttribute("pwd");
		loginForm.getInputByName("confirmPassword").setValueAttribute("anotherPwd");
		HtmlPage pageAfterRegistration = loginForm.getButtonByName("btn_submit").click();

		assertTitleEquals(pageAfterRegistration, "Registration");
		assertThat(pageAfterRegistration.getBody().getTextContent())
				.contains("Password and Confirm Password must match.");
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
