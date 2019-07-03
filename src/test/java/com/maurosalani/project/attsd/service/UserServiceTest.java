package com.maurosalani.project.attsd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static java.util.Arrays.asList;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	public void testFindAllUsersWhenDatabaseIsEmpty() {
		when(userRepository.findAllUsers()).thenReturn(Collections.emptyList());
		assertThat(userService.getAllUsers()).isEmpty();
	}

	@Test
	public void testFindAllUsersWhitExistingUsers() {
		User user1 = new User(1l, "username1", "pwd1");
		User user2 = new User(2l, "username2", "pwd2");
		when(userRepository.findAllUsers()).thenReturn(asList(user1, user2));
		assertThat(userService.getAllUsers()).containsExactly(user1, user2);
	}

}
