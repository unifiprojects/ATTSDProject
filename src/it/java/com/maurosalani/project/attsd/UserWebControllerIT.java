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
		
		assertThat(driver.findElement(By.linkText("Log in"))).isNotNull();
		assertThat(driver.findElement(By.linkText("Register"))).isNotNull();
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
		assertThat(driver.findElement(By.linkText("Logout"))).isNotNull();
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
	
	
}
