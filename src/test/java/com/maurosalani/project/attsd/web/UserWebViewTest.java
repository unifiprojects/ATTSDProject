package com.maurosalani.project.attsd.web;

import static com.gargoylesoftware.htmlunit.WebAssert.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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

}
