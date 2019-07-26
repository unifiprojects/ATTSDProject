package com.maurosalani.project.attsd;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;

import io.github.bonigarcia.wdm.WebDriverManager;

public class UserWebControllerE2E {

	private static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	private static String baseUrl = "http://localhost:" + port;
	private WebDriver driver;

	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

	@Before
	public void setup() {
		baseUrl = "http://localhost:" + port;
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--no-sandbox");
		driver = new ChromeDriver(options);
		try (Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/attsd_database?allowPublicKeyRetrieval=true&useSSL=false", "springuser",
				"springuser"); Statement stmt = conn.createStatement();) {
			String strDelete = "delete from followers_relation";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from user_game_relation";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from user";
			stmt.executeUpdate(strDelete);
			strDelete = "delete from game";
			stmt.executeUpdate(strDelete);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	@After
	public void teardown() {
		driver.quit();
	}

	@Test
	public void testHomePage() {
		driver.get(baseUrl);

		driver.findElement(By.cssSelector("a[href*='/login"));
		driver.findElement(By.cssSelector("a[href*='/registration"));
	}

	@Test
	public void testHomePage_ShouldContainsLatestReleaseGames() throws JSONException {
		String name1 = postGame("Game Name 1", "Game Description 1", new Date(1000));
		String name2 = postGame("Game Name 2", "Game Description 2", new Date(1000));

		driver.get(baseUrl);
		assertThat(driver.findElement(By.id("latestReleases")).getText()).contains(name1, "Game Description 1");
		assertThat(driver.findElement(By.id("latestReleases")).getText()).contains(name2, "Game Description 2");
	}

	@Test
	public void testRegistrationSuccess_ShouldGoToSuccessPage() throws JSONException {
		User toRegister = new User(null, "usernameRegistered", "passwordRegistered");

		driver.get(baseUrl);
		driver.findElement(By.linkText("Register")).click();

		final WebElement usernameField = driver.findElement(By.name("username"));
		usernameField.clear();
		usernameField.sendKeys(toRegister.getUsername());
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys(toRegister.getPassword());
		final WebElement confirmPassword = driver.findElement(By.name("confirmPassword"));
		confirmPassword.clear();
		confirmPassword.sendKeys(toRegister.getPassword());
		driver.findElement(By.name("btn_submit")).click();

		assertThat(driver.getPageSource()).contains("Your registration has been successful!");
		driver.findElement(By.linkText("Homepage"));
	}

	@Test
	public void testLoginSuccess() throws JSONException {
		User userToLog = new User(null, "usernameLogged", "password");
		postUser(userToLog.getUsername(), userToLog.getPassword());
		logUser(userToLog);

		assertThat(driver.getPageSource()).contains("Welcome back");
		driver.findElement(By.linkText(userToLog.getUsername()));
		driver.findElement(By.linkText("Logout"));
	}

	@Test
	public void testSearch_ShouldShowUsersAndGames() throws JSONException {
		String user1 = postUser("username 1", "password 1");
		String user2 = postUser("username 2", "password 2");
		String game1 = postGame("name 1", "description 1", new Date(1000));
		String game2 = postGame("name 2", "description 2", new Date(1000));

		searchContent("name");

		assertThat(driver.findElement(By.id("userSearchResults")).getText()).contains(user1);
		assertThat(driver.findElement(By.id("userSearchResults")).getText()).contains(user2);
		assertThat(driver.findElement(By.id("gameSearchResults")).getText()).contains(game1);
		assertThat(driver.findElement(By.id("gameSearchResults")).getText()).contains(game2);
	}

	@Test
	public void testProfile_LoggedUserAccessAnotherProfile() throws JSONException {
		User userLogged = new User(null, "usernameLogged", "password");
		User userToShow = new User(null, "usernameToShow", "password");
		postUser(userLogged.getUsername(), userLogged.getPassword());
		postUser(userToShow.getUsername(), userToShow.getPassword());
		logUser(userLogged);
		searchContent(userToShow.getUsername());
		driver.findElement(By.linkText(userToShow.getUsername())).click();

		assertThat(driver.getPageSource()).contains(userToShow.getUsername());
		assertThat(driver.findElement(By.id("userFollowed")).getText()).contains("No Users");
		assertThat(driver.findElement(By.id("games")).getText()).contains("No Games");
		assertThat(driver.findElement(By.name("btn_add")).getText()).contains("Follow");
	}

	@Test
	public void testProfile_LoggedUserChangeHisPassword() throws JSONException {
		User userLogged = new User(null, "usernameLogged", "password");
		postUser(userLogged.getUsername(), userLogged.getPassword());
		logUser(userLogged);
		driver.findElement(By.linkText(userLogged.getUsername())).click();

		final WebElement oldPassword = driver.findElement(By.name("oldPassword"));
		oldPassword.clear();
		oldPassword.sendKeys(userLogged.getPassword());
		final WebElement newPassword = driver.findElement(By.name("newPassword"));
		newPassword.clear();
		newPassword.sendKeys("newPassword");
		driver.findElement(By.name("btn_change")).click();

		assertThat(driver.getPageSource()).contains("Password changed successfully.");
		driver.findElement(By.linkText("Homepage"));
	}

	@Test
	public void testGameProfile_ShowGameProfile() throws JSONException {
		Game game = new Game(null, "name 1", "description 1", new Date(1000));
		User userLogged = new User(null, "usernameLogged", "password");

		postGame(game.getName(),game.getDescription(), game.getReleaseDate());
		postUser(userLogged.getUsername(), userLogged.getPassword());
		logUser(userLogged);
		searchContent("name");
		driver.findElement(By.linkText(game.getName())).click();

		assertThat(driver.getPageSource()).contains("No users like this game yet...");
		assertThat(driver.findElement(By.id("description")).getText()).contains("description 1");
		assertThat(driver.findElement(By.name("btn_like")).getText()).contains("Like");
	}

	private void logUser(User userToLog) {
		driver.get(baseUrl);
		driver.findElement(By.linkText("Log in")).click();

		final WebElement usernameField = driver.findElement(By.name("username"));
		usernameField.clear();
		usernameField.sendKeys(userToLog.getUsername());
		final WebElement password = driver.findElement(By.name("password"));
		password.clear();
		password.sendKeys(userToLog.getPassword());

		driver.findElement(By.name("btn_submit")).click();
	}

	private void searchContent(String content) {
		driver.get(baseUrl);
		final WebElement usernameField = driver.findElement(By.name("content_search"));
		usernameField.clear();
		usernameField.sendKeys(content);
		driver.findElement(By.name("btn_submit")).click();
	}

	private String postUser(String username, String password) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("username", username);
		body.put("password", password);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		ResponseEntity<String> answer = new RestTemplate().postForEntity(baseUrl + "/api/users/new", entity,
				String.class);

		return new JSONObject(answer.getBody()).get("username").toString();
	}

	private String postGame(String name, String description, Date date) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("name", name);
		body.put("description", description);
		body.put("date", date);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		ResponseEntity<String> answer = new RestTemplate().postForEntity(baseUrl + "/api/games/new", entity,
				String.class);

		return new JSONObject(answer.getBody()).get("name").toString();
	}

}