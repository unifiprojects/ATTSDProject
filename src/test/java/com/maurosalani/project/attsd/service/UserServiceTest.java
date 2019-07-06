package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserService userService;

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		when(userRepository.findAll()).thenReturn(Collections.emptyList());
		assertThat(userService.getAllUsers()).isEmpty();
	}

	@Test
	public void testFindAllWithExistingUsers() {
		User user1 = new User(1l, "username1", "pwd1");
		User user2 = new User(2l, "username2", "pwd2");
		when(userRepository.findAll()).thenReturn(asList(user1, user2));
		assertThat(userService.getAllUsers()).containsExactly(user1, user2);
	}

	@Test
	public void testGetUserByIdWhenUserDoesNotExist() {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(userService.getUserById(1L)).isNull();
	}

	@Test
	public void testGetUserByIdWithExistingUser() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThat(userService.getUserById(1L)).isEqualTo(user);
	}

	@Test
	public void testGetUserByUsernameWhenUserDoesNotExist() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		assertThat(userService.getUserByUsername("username")).isNull();
	}

	@Test
	public void testGetUserByUsernameWithExistingUser() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
		assertThat(userService.getUserByUsername("username")).isEqualTo(user);
	}

	@Test
	public void testInsertNewUser_setsIdToNull_returnsSavedUser() {
		User toSave = spy(new User(99L, "toSaveUsername", "toSavePwd"));
		User saved = new User(1L, "savedUsername", "savedPwd");

		when(userRepository.save(any(User.class))).thenReturn(saved);

		User result = userService.insertNewUser(toSave);

		assertThat(result).isEqualTo(saved);
		InOrder inOrder = inOrder(toSave, userRepository);
		inOrder.verify(toSave).setId(null);
		inOrder.verify(userRepository).save(toSave);
	}

	@Test
	public void testInsertNewUser_UserIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.insertNewUser(null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_setsIdToArgument_ShouldReturnSavedUser() {
		User replacement = spy(new User(null, "replacement_user", "replacement_pwd"));
		User replaced = new User(1L, "replaced_user", "replaced_user");
		when(userRepository.save(any(User.class))).thenReturn(replaced);

		User result = null;
		try {
			result = userService.updateUserById(1L, replacement);
		} catch (UserNotFoundException e) {
			fail();
		}
		assertThat(result).isEqualTo(replaced);
		InOrder inOrder = inOrder(replacement, userRepository);
		inOrder.verify(replacement).setId(1L);
		inOrder.verify(userRepository).save(replacement);
	}

	@Test
	public void testUpdateUserById_UserIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.updateUserById(1L, null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_IdNotFound_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(null);
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.updateUserById(1L, user));
	}

	@Test
	public void testUpdateUserById_IdIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> userService.updateUserById(null, user));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testDeleteById_IdIsNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.deleteById(null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testDeleteById_IdNotFound_ShouldThrowException() {
		when(userRepository.findById(1L)).thenReturn(null);
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.deleteById(1L));
	}

	@Test
	public void testDeleteById_IdFound() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThatCode(() -> userService.deleteById(1L)).doesNotThrowAnyException();
		verify(userRepository, times(1)).deleteById(1L);
	}

}
