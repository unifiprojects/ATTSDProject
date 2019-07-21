package com.maurosalani.project.attsd;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;
import com.maurosalani.project.attsd.service.UserService;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(UserService.class)
public class UserServiceRepositoryIT {

	@Autowired
	private UserService userService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void testServiceCanInsertIntoRepository() throws Exception {
		User saved = userService.insertNewUser(new User(null, "user", "password"));
		userRepository.findById(saved.getId()).isPresent();
	}

}
