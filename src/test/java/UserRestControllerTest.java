import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.controller.UserRestController;
import com.maurosalani.project.attsd.service.UserService;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

	@InjectMocks
	private UserRestController userRestController;

	@Mock
	private UserService userService;

	@Before
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(userRestController);
	}

	@Test
	public void testFindAllUsersWithEmptyDatabase() throws Exception {
		when(userService.getAllUsers()).thenReturn(Collections.emptyList());
		
		given().
		when().
			get("/api/users").
		then().
			statusCode(200).
			assertThat().body(is("[]"));
	}

}
