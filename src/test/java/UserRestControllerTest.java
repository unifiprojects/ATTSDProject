import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.controller.UserRestController;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

@RunWith(MockitoJUnitRunner.class)
public class UserRestControllerTest {

	@InjectMocks
	private UserRestController userRestController;

	@Before
	public void setup() {
		RestAssuredMockMvc.standaloneSetup(userRestController);
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
