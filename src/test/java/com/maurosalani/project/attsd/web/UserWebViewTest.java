package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyInt;
import static java.util.Arrays.asList;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.service.GameService;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserWebController.class)
public class UserWebViewTest {

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
	public void testHomePageWhenUserNotLogged_ShouldContainSearchFormAndLoginLink() throws Exception {
		HtmlPage page = webClient.getPage("/");
		
		assertTitleEquals(page, "ATTSD-Project: Social Games");
		assertTextPresent(page, "ATTSD-Project: Social Games");
		assertTextPresent(page, "Welcome! Please Log in or Register");
		assertFormPresent(page, "search_form");
		assertInputPresent(page, "search_bar");
		assertThat(page.getAnchorByText("Log in").getHrefAttribute()
				).isEqualTo("/login");
		assertThat(page.getAnchorByText("Register").getHrefAttribute()
				).isEqualTo("/registration");
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
		
		HtmlTable table = page.getHtmlElementById("latest_releases");
		assertThat(removeWindowsCR(table.asText()))
			.isEqualTo( 
				"Game1	Description1	" + dateFormat.format(new Date(1)) + "\n" + 
				"Game2	Description2	" + dateFormat.format(new Date(2)) + "\n" + 
				"Game3	Description3	" + dateFormat.format(new Date(3)) + "\n" + 
				"Game4	Description4	" + dateFormat.format(new Date(4)));
	}
	
	private String removeWindowsCR(String s) {
		return s.replaceAll("\r", "");
	}

}
