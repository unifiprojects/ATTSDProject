package com.maurosalani.project.attsd;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.UpdateAddFollowedUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdatePasswordUserFormDTO;
import com.maurosalani.project.attsd.dto.UpdateUserFormDTO;
import com.maurosalani.project.attsd.dto.UserDTO;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

import io.restassured.RestAssured;
import io.restassured.response.Response;

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
	public void testUpdate_WithExistingUser() throws Exception {
		User saved = userRepository.save(new User (null, "testUsername", "testPassword"));
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "testPassword");
		User userReplacement = new User(null, "new_username", "new_password");
		UpdateUserFormDTO form = new UpdateUserFormDTO(credentialsDTO, userReplacement);
		
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					put("/api/users/update/" + saved.getId());
		User updated = response.getBody().as(User.class);

		assertThat(userRepository.findById(saved.getId()).get()).isEqualTo(updated);
	}
	
	@Test
	public void testUpdate_ChangedPasswordOfExistingUser() throws Exception {
		User saved = userRepository.save(new User (null, "testUsername", "testPassword"));
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "testPassword");
		UpdatePasswordUserFormDTO form = new UpdatePasswordUserFormDTO(credentialsDTO, "new_password");
		
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					patch("/api/users/update/password/" + saved.getId());
		User updated = response.getBody().as(User.class);

		assertThat(userRepository.findById(saved.getId()).get()).isEqualTo(updated);
	}
	
	@Test
	public void testUpdate_AddFollowedToExistingUser() throws Exception {
		User userToUpdate = userRepository.save(new User(null, "testUsername", "testPassword"));
		User followedToAdd = userRepository.save(new User(null, "followed", "password"));
		CredentialsDTO credentialsDTO = new CredentialsDTO("testUsername", "testPassword");
		UpdateAddFollowedUserFormDTO form = new UpdateAddFollowedUserFormDTO(credentialsDTO, followedToAdd);
		
		Response response =
				given().
					contentType(MediaType.APPLICATION_JSON_VALUE).
					body(form).
				when().
					patch("/api/users/update/addFollowedUser/" + userToUpdate.getId());
		User updated = response.getBody().as(User.class);
		
		assertThat(userRepository.findById(userToUpdate.getId()).get()).isEqualTo(updated);
	}
	
	@Test
	public void testDeleteUser() throws Exception {
		User userToDelete = userRepository.save(new User(null, "toDelete", "pwd"));
		CredentialsDTO credentialsDTO = new CredentialsDTO("toDelete", "pwd");
				
		given().
			contentType(MediaType.APPLICATION_JSON_VALUE).
			body(credentialsDTO).
		when().
			delete("/api/users/delete/" + userToDelete.getId()).
		then().
			statusCode(HttpStatus.NO_CONTENT.value());
		
		assertThat(userRepository.findById(userToDelete.getId())).isEmpty();
	}

}
