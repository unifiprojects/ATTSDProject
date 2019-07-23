package com.maurosalani.project.attsd;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.dto.Credentials;
import com.maurosalani.project.attsd.dto.UpdateUserForm;
import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("mysql")
public class UserRestControllerIT {

	@Autowired
	private UserRepository userRepository;
	
	@LocalServerPort
	private int port;

	@Before
	public void setup() {
		RestAssured.port = port;
		userRepository.deleteAll();
		userRepository.flush();
	}
	
	@Test
	public void testNewUser() throws Exception {
		UserDTO userDto = new UserDTO(null, "username", "password");
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(userDto).
				when().
					post("/api/users/new");
		User saved = response.getBody().as(User.class);

		assertThat(userRepository.findById(saved.getId()).get()).isEqualTo(saved);
	}
	
	@Test
	public void testUpdateExistingUser() throws Exception {
		User saved = userRepository.save(new User (null, "testUsername", "testPassword"));
		
		Credentials credentials = new Credentials("testUsername", "testPassword");
		User userReplacement = new User(null, "new_username", "new_password");
		UpdateUserForm form = new UpdateUserForm(credentials, userReplacement);
		
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					put("/api/users/update/" + saved.getId());
		User updated = response.getBody().as(User.class);

		assertThat(userRepository.findById(saved.getId()).get()).isEqualTo(updated);
	}

}
