package com.maurosalani.project.attsd.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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

}
