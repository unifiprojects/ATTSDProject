package com.maurosalani.project.attsd;

import static org.junit.Assert.*;

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
import org.openqa.selenium.WebDriver;
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

public class myunittest {

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
	public void testProfile_my() throws JSONException {
		Game game = new Game(null, "Monster Hunter: World",
				"Il Nuovo Mondo ti attende! Monster Hunter: World, il capitolo più recente di questa epica saga, offre un'esperienza ancora più ricca e immersiva. Dai la caccia a una schiera di mostri in un nuovo mondo pieno di emozionanti sorprese.",
				new Date(1533765600000L));
		User userLogged = new User(null, "Mauro", "irrefragabile");

		Game game1 = new Game(null, "Overcooked 2",
				"Overcooked ritorna con una nuova esperienza di cucina caotica! Immergiti di nuovo nel Regno delle Cipolle e crea la tua squadra di chef nella versione classica in cooperativa oppure online fino a quattro giocatori.",
				new Date(1533592800000L));
		User userLogged1 = new User(null, "Gaetano", "irrefragabile");

		postGame(game.getName(), game.getDescription(), game.getReleaseDate());
		postUser(userLogged.getUsername(), userLogged.getPassword());
		postGame(game1.getName(), game1.getDescription(), game1.getReleaseDate());
		postUser(userLogged1.getUsername(), userLogged1.getPassword());

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
		body.put("releaseDate", date);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		ResponseEntity<String> answer = new RestTemplate().postForEntity(baseUrl + "/api/games/new", entity,
				String.class);

		return new JSONObject(answer.getBody()).get("name").toString();
	}

}
