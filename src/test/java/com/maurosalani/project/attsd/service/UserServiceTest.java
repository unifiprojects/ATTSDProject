package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.ignoreStubs;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;

import com.maurosalani.project.attsd.dto.Credentials;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;
import com.maurosalani.project.attsd.repository.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private GameRepository gameRepository;

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
	public void testGetUserByIdWhenUserDoesNotExist_ShouldThrowException() throws Exception {
		when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.getUserById(1L));
	}

	@Test
	public void testGetUserByIdWithExistingUser() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThat(userService.getUserById(1L)).isEqualTo(user);
	}

	@Test
	public void testGetUserByIdWithIdNull() throws Exception {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.getUserById(null));
	}

	@Test
	public void testGetUserByUsernameWhenUserDoesNotExist() {
		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.getUserByUsername("username"));
	}

	@Test
	public void testGetUserByUsernameWithUsernameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.getUserByUsername(null));
	}

	@Test
	public void testGetUserByUsername_WithExistingUser() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));
		assertThat(userService.getUserByUsername("username")).isEqualTo(user);
	}

	@Test
	public void testGetUserByUsernameAndPassword_WhenUserDoesNotExist() {
		when(userRepository.findByUsernameAndPassword(anyString(), anyString())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.getUserByUsernameAndPassword("username", "password"));
	}

	@Test
	public void testGetUserByUsernameAndPassword_WithUsernameOrPasswordNull() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.getUserByUsernameAndPassword("username", null));
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.getUserByUsernameAndPassword(null, "password"));
	}

	@Test
	public void testGetUserByUsernameAndPassworWithExistingUser() throws Exception {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findByUsernameAndPassword("username", "password")).thenReturn(Optional.of(user));
		assertThat(userService.getUserByUsernameAndPassword("username", "password")).isEqualTo(user);
	}

	@Test
	public void testGetUsersByUsernameLikeWhenUserDoesNotExist() {
		when(userRepository.findByUsernameLike(anyString())).thenReturn(Collections.emptyList());
		assertThat(userService.getUsersByUsernameLike("username")).isEmpty();
	}

	@Test
	public void testGetUsersByUsernameLikeWithExistingUsers() {
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");
		when(userRepository.findByUsernameLike("username")).thenReturn(asList(user1, user2));
		assertThat(userService.getUsersByUsernameLike("username")).containsExactly(user1, user2);
	}

	@Test
	public void testGetUsersByUsernameLikeWithUsernameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.getUsersByUsernameLike(null));
	}

	@Test
	public void testInsertNewUser_ShouldSetIdToNullAndReturnSavedUser() throws Exception {
		User toSave = spy(new User(99L, "toSaveUsername", "toSavePwd"));
		User saved = new User(1L, "savedUsername", "savedPwd");

		when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
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
	public void testInsertNewUser_PasswordIsEmpty_ShouldThrowException() {
		User userToInsert = new User(null, "username", "");
		when(userRepository.findByUsername("username")).thenReturn(Optional.ofNullable(any(User.class)));
		
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.insertNewUser(userToInsert));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}
	
	@Test
	public void testInsertNewUser_PasswordIsNull_ShouldThrowException() {
		User userToInsert = new User(null, "username", null);
		when(userRepository.findByUsername("username")).thenReturn(Optional.ofNullable(any(User.class)));
		
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.insertNewUser(userToInsert));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}
	
	@Test
	public void testInsertNewUser_UsernameIsNull_ShouldThrowException() {
		User userToInsert = new User(null, null, "pwd");
		when(userRepository.findByUsername(null)).thenReturn(Optional.empty());
		when(userRepository.save(userToInsert)).thenThrow(DataIntegrityViolationException.class);

		assertThatExceptionOfType(DataIntegrityViolationException.class)
			.isThrownBy(() -> userService.insertNewUser(userToInsert))
			.withMessage("Username or password are invalid.");
	}

	@Test
	public void testInsertNewUser_UsernameAlreadyExists_ShouldThrowException() {
		User userToInsert = new User(null, "usernameAlreadyExisting", "pwd");
		User userAlreadyExisting = new User(1L, "usernameAlreadyExisting", "anotherPwd");
		when(userRepository.findByUsername("usernameAlreadyExisting")).thenReturn(Optional.of(userAlreadyExisting));

		assertThatExceptionOfType(UsernameAlreadyExistingException.class)
			.isThrownBy(() -> userService.insertNewUser(userToInsert)).withMessage("Username already existing.");
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testUpdateUserById_setsIdToArgument_ShouldReturnSavedUser() throws Exception {
		User replacement = spy(new User(null, "replacement_user", "replacement_pwd"));
		User replaced = new User(1L, "replaced_user", "replaced_user");
		when(userRepository.save(any(User.class))).thenReturn(replaced);
		when(userRepository.findById(1L)).thenReturn(Optional.of(replaced));

		User result = userService.updateUserById(1L, replacement);

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
		User replacement = new User(1L, "replacement", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.updateUserById(replacement.getId(), replacement));
	}

	@Test
	public void testUpdateUserById_IdIsNull_ShouldThrowException() {
		User replacement = new User(1L, "replacement", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.updateUserById(null, replacement));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testUpdateUserById_PasswordIsNull_ShouldThrowException() {
		User replacement = new User(1L, "replacement", null);
		when(userRepository.findById(1L)).thenReturn(Optional.of(replacement));
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.updateUserById(1L, replacement));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}
	
	@Test
	public void testUpdateUserById_PasswordIsEmpty_ShouldThrowException() {
		User replacement = new User(1L, "replacement", "");
		when(userRepository.findById(1L)).thenReturn(Optional.of(replacement));
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.updateUserById(1L, replacement));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testDeleteById_IdIsNull() {
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.deleteById(null));
		verifyNoMoreInteractions(userRepository);
	}

	@Test
	public void testDeleteById_IdNotFound_ShouldThrowException() {
		when(userRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.deleteById(1L));
		verifyNoMoreInteractions(ignoreStubs(userRepository));
	}

	@Test
	public void testDeleteById_IdFound() {
		User user = new User(1L, "username", "pwd");
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));
		assertThatCode(() -> userService.deleteById(1L)).doesNotThrowAnyException();
		verify(userRepository, times(1)).deleteById(1L);
	}

	@Test
	public void testAddUserToFollowedUsersList_UserIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.addFollowedUser(null, user));
	}

	@Test
	public void testAddUserToFollowedUsersList_ToAddUserIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.addFollowedUser(user, null));
	}

	@Test
	public void testAddUserToFollowedUsersList_UserNotExist_ShouldThrowException() {
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");

		when(userRepository.findById(user1.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.addFollowedUser(user1, user2));
	}

	@Test
	public void testAddUserToFollowedUsersList_FollowedNotExist_ShouldThrowException() {
		User user1 = new User(1L, "username1", "pwd1");
		User user2 = new User(2L, "username2", "pwd2");

		when(userRepository.findById(user1.getId())).thenReturn(Optional.of(user1));
		when(userRepository.findById(user2.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.addFollowedUser(user1, user2));
	}

	@Test
	public void testAddUserToFollowedUsersList_ShouldReturnModifiedUser() throws Exception {
		User user1 = spy(new User(1L, "username", "pwd"));
		User user2 = spy(new User(2L, "username", "pwd"));
		User resulted = new User(1L, "username", "pwd");
		resulted.addFollowedUser(user2);
		when(userRepository.save(any(User.class))).thenReturn(resulted);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
		when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

		User saved = userService.addFollowedUser(user1, user2);
		assertThat(saved).isEqualTo(resulted);
		InOrder inOrder = inOrder(user1, user2, userRepository);
		inOrder.verify(userRepository).findById(1L);
		inOrder.verify(userRepository).findById(2L);
		inOrder.verify(user1).addFollowedUser(user2);
		inOrder.verify(user2).addFollowerUser(user1);
		inOrder.verify(userRepository).save(user1);
	}

	@Test
	public void testAddGameToGamesList_UserIsNull_ShouldThrowException() {
		Game game = new Game(1L, "game name", "game description", new Date(1000));
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.addGame(null, game));
	}

	@Test
	public void testAddGameToGamesList_GameIsNull_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> userService.addGame(user, null));
	}
	
	@Test
	public void testAddGameToGamesList_UserNotFound_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		Game game = new Game(2L, "game name", "game description", new Date(1000));
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(() -> userService.addGame(user, game));
	}
	
	@Test
	public void testAddGameToGamesList_GameNotFound_ShouldThrowException() {
		User user = new User(1L, "username", "pwd");
		Game game = new Game(2L, "game name", "game description", new Date(1000));
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(gameRepository.findById(game.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> userService.addGame(user, game));
	}

	@Test
	public void testAddGameToGamesList_ShouldReturnModifiedUser() throws Exception {
		User user = spy(new User(1L, "username", "pwd"));
		Game game = spy(new Game(2L, "game name", "game description", new Date(1000)));
		User resulted = new User(1L, "username", "pwd");
		resulted.addGame(game);
		when(userRepository.save(any(User.class))).thenReturn(resulted);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
		
		User saved = userService.addGame(user, game);
		assertThat(saved).isEqualTo(resulted);
		InOrder inOrder = inOrder(user, game, userRepository);
		inOrder.verify(user).addGame(game);
		inOrder.verify(game).addUser(user);
		inOrder.verify(userRepository).save(user);
	}
	
	@Test
	public void testChangePassword_ShouldReturnModifiedUser() throws Exception {
		User user = spy(new User(1L, "username", "pwd"));
		String newPassword = "newPwd";
		User resulted = new User(1L, "username", "newPwd");
		when(userRepository.save(any(User.class))).thenReturn(resulted);
		when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
		
		User saved = userService.changePassword(user, newPassword);
		assertThat(saved).isEqualTo(resulted);
		InOrder inOrder = inOrder(user, userRepository);
		inOrder.verify(user).setPassword(newPassword);
		inOrder.verify(userRepository).save(user);
	}
	
	@Test
	public void testChangePassword_UserIsNull_ShouldThrowException() throws Exception {
		String newPassword = "newPwd";
		assertThatExceptionOfType(IllegalArgumentException.class)
			.isThrownBy(() -> userService.changePassword(null, newPassword));
	}
	
	@Test
	public void testChangePassword_PasswordIsNull_ShouldThrowException() throws Exception {
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.changePassword(user, null));
	}
	
	@Test
	public void testChangePassword_PasswordIsEmpty_ShouldThrowException() throws Exception {
		String newPassword = "";
		User user = new User(1L, "username", "pwd");
		assertThatExceptionOfType(PasswordRequiredException.class)
			.isThrownBy(() -> userService.changePassword(user, newPassword));
	}
	
	@Test
	public void testChangePassword_UserNotFound_ShouldThrowException() throws Exception {
		User user = new User(1L, "username", "pwd");
		String newPassword = "newPasssword";
		when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
		assertThatExceptionOfType(UserNotFoundException.class)
			.isThrownBy(() -> userService.changePassword(user, newPassword));
	}
	
	@Test
	public void testVerifyLogin_UserSucceed() throws Exception {
		User userToLog = new User(1L,"username","password");
		Credentials credentials = new Credentials("username", "password"); 
		when(userRepository.findByUsernameAndPassword("username", "password"))
			.thenReturn(Optional.of(userToLog));
		User userLogged = userService.verifyLogin(credentials);
		assertThat(userLogged).isEqualTo(userToLog);
	}	
}
