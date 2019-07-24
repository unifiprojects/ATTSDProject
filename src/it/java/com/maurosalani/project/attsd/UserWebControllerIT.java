package com.maurosalani.project.attsd;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserWebControllerIT {

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private UserRepository userRepository;

	@LocalServerPort
	private int port;

	private WebDriver driver;

	private String baseUrl;

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port;
		driver = new HtmlUnitDriver();
		userRepository.deleteAll();
		userRepository.flush();
		gameRepository.deleteAll();
		gameRepository.flush();
	}
	
	@Test
	public void testHomePage_UserShouldSeeLoginAndLatestReleasesGames() {
		Game gameLatestRelease1 = new Game(null, "game1", "description1", new Date(1000));
		Game gameLatestRelease2 = new Game(null, "game2", "description2", new Date(1001));
		gameRepository.save(gameLatestRelease1);
		gameRepository.save(gameLatestRelease2);
		driver.get(baseUrl);
		
		driver.findElement(By.linkText("Log in"));
		driver.findElement(By.linkText("Register"));
		assertThat(driver.findElement(By.id("latestReleases")).getText()).
			contains("game1", "description1", "game2","description2");
	}

	@Test
	public void testHomePage_UserLogWithSuccess_ShouldSeeLogOutInHomepage() {
		User userRegistered = userRepository.save(new User(null, "username", "password"));
		driver.get(baseUrl + "/login");
		driver.findElement(By.name("username")).sendKeys(userRegistered.getUsername());
		driver.findElement(By.name("password")).sendKeys(userRegistered.getPassword());
		driver.findElement(By.name("btn_submit")).click();
		
		assertThat(driver.getPageSource()).contains("Welcome back");
		driver.findElement(By.linkText("Logout"));
	}
	
	@Test
	public void testRegistration_UserRegisterWithSuccess() {
		User userToRegister = new User(null, "username", "password");
		driver.get(baseUrl + "/registration");
		driver.findElement(By.name("username")).sendKeys(userToRegister.getUsername());
		driver.findElement(By.name("password")).sendKeys(userToRegister.getPassword());
		driver.findElement(By.name("confirmPassword")).sendKeys(userToRegister.getPassword());
		driver.findElement(By.name("btn_submit")).click();
		
		assertThat(userRepository.findByUsername(userToRegister.getUsername())).isPresent();
		assertThat(driver.getPageSource()).contains("Your registration has been successful!");
	}
	
	@Test
	public void testSearchUsersAndGames_ShouldSeeAllTheComplyingResults() {
		User user1 = new User(null, "someName1", "password");
		User user2 = new User(null, "someName2", "password");
		User userNotComplying = new User(null, "notComplying", "password");
		Game game1 = new Game(null, "someName1", "description", null);
		Game game2 = new Game(null, "someName2", "description", null);
		Game gameNotComplying = new Game(null, "notComplying", "description", null);
		userRepository.save(user1);
		userRepository.save(user2);
		userRepository.save(userNotComplying);
		gameRepository.save(game1);
		gameRepository.save(game2);
		gameRepository.save(gameNotComplying);

		driver.get(baseUrl);
		driver.findElement(By.name("content_search")).sendKeys("Name");
		driver.findElement(By.name("btn_submit")).click();
		
		assertThat(driver.getPageSource()).doesNotContain("No Users", "No Games");
		driver.findElement(By.cssSelector
			("a[href*='/profile/" + user1.getUsername() + "']"));
		driver.findElement(By.cssSelector
			("a[href*='/profile/" + user2.getUsername() + "']"));
		driver.findElement(By.cssSelector
				("a[href*='/game/" + game1.getName() + "']"));
		driver.findElement(By.cssSelector
				("a[href*='/game/" + game2.getName() + "']"));
	}
	
	@Test
	public void testShowProfile_ShouldSeeUsernameAndLists() {
		User user = new User(null, "UsernameTest", "PasswordTest");
		User user1 = new User(null, "UsernameTest1", "PasswordTest1");
		User user2 = new User(null, "UsernameTest2", "PasswordTest2");
		user.addFollowedUser(user1);
		user.addFollowedUser(user2);
		user1.addFollowedUser(user);
		user2.addFollowedUser(user);
		Game game1 = new Game(null, "Name1", "description", null);
		Game game2 = new Game(null, "Name2", "description", null);
		user.addGame(game1);
		user.addGame(game2);
		game1.addUser(user);
		game2.addUser(user);
		userRepository.save(user1);
		userRepository.save(user2);
		gameRepository.save(game1);
		gameRepository.save(game2);
		userRepository.save(user);
		
		driver.get(baseUrl + "/profile/" + user.getUsername());
		
		assertThat(driver.getPageSource()).contains(user.getUsername());
		assertThat(driver.findElement(By.id("userFollowed")).getText()).
			doesNotContain("No Users");
		assertThat(driver.findElement(By.id("games")).getText()).
			doesNotContain("No Games");
		driver.findElement(By.cssSelector
				("a[href*='/profile/" + user1.getUsername() + "']"));
			driver.findElement(By.cssSelector
				("a[href*='/profile/" + user2.getUsername() + "']"));
			driver.findElement(By.cssSelector
					("a[href*='/game/" + game1.getName() + "']"));
			driver.findElement(By.cssSelector
					("a[href*='/game/" + game2.getName() + "']"));
	}
	
	
}
