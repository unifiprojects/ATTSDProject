package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
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
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
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
	public void testLoginPage_ShouldContainTextFields() throws Exception {
		HtmlPage page = webClient.getPage("/login");
		
		assertThat(page.getAnchorByText("Go back to homepage").getHrefAttribute()).isEqualTo("/");
		assertFormPresent(page, "login_form");
		assertInputPresent(page, "input_username");
		assertInputPresent(page, "input_password");
		assertTextNotPresent(page, "You are already logged! Try to log out from homepage.");
	}

	@Before
	/**
	 * Necessary to clear session
	 */
	public void clearSessionOfWebClient() {
		webClient.getCookieManager().clearCookies();
	}

	private String removeWindowsCR(String s) {
		return s.replaceAll("\r", "");
	}

}
