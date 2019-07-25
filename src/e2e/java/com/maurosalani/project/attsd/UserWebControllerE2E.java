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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
		try (
			Connection conn = DriverManager.getConnection(
				"jdbc:mysql://localhost:3306/attsd_database?allowPublicKeyRetrieval=true&useSSL=false",
				"springuser", "springuser");
			Statement stmt = conn.createStatement();) {
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