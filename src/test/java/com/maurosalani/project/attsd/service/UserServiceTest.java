package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import javax.validation.constraints.AssertFalse;

import org.assertj.core.api.Fail;
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
	public void testFindAllUsersWhenDatabaseIsEmpty() {
		when(userRepository.findAllUsers()).thenReturn(Collections.emptyList());
		assertThat(userService.getAllUsers()).isEmpty();
	}

	@Test
	public void testFindAllUsersWithExistingUsers() {
		User user1 = new User(1l, "username1", "pwd1");
		User user2 = new User(2l, "username2", "pwd2");
		when(userRepository.findAllUsers()).thenReturn(asList(user1, user2));
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
	public void testInsertNewUser_UserIsNull_ShouldReturnNull() {
		User result = userService.insertNewUser(null);
		assertThat(result).isNull();
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
		} catch (Exception e) {
			fail();
		}
		assertThat(result).isEqualTo(replaced);
		InOrder inOrder = inOrder(replacement, userRepository);
		inOrder.verify(replacement).setId(1L);
		inOrder.verify(userRepository).save(replacement);
	}

	@Test
	public void testUpdateUserById_UserIsNull_ShouldReturnNull_ShouldUpdateNothing() {
		User result = null;
		try {
			result = userService.updateUserById(1L, null);
		} catch (Exception e) {
			fail();
		}
		assertThat(result).isEqualTo(null);
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_IdNotFound_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(null);
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.updateUserById(1L, user));
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
