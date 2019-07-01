package com.maurosalani.project.attsd.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.User;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entitymanager;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFindAllWithExistingUser() {
		User user = new User(null, "test", "pwd", null, null, null);
		User saved = entitymanager.persistFlushFind(user);
		Collection<User> users = repository.findAll();

		assertThat(users).containsExactly(saved);
	}

	@Test
	public void testFindByUsername() {
		User user = new User(null, "test", "pwd", null, null, null);
		User saved = entitymanager.persistFlushFind(user);
		User userFound = repository.findByUsername("test");

		assertThat(userFound).isEqualTo(saved);
	}

	@Test
	public void testUsernameAndPasswordAreMandatoryWhenUserIsSaved() {
		User userNoUsername = new User(null, null, "pwd", null, null, null);
		User userNoPassword = new User(null, "username", null, null, null, null);
		User userNoUsernameAndPassword = new User(null, null, "pwd", null, null, null);

		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsername));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoPassword));
		assertThatExceptionOfType(DataIntegrityViolationException.class)
				.isThrownBy(() -> repository.saveAndFlush(userNoUsernameAndPassword));
	}

	@Test
	public void testPersistenceOfFollowedList() {
		List<User> followed = new LinkedList<User>();
		followed.add(new User(null, "one", "pwd", null, null, null));
		followed.add(new User(null, "two", "pwd", null, null, null));
		User user = new User(null, "test", "pwd", followed, null, null);
		User saved = entitymanager.persistFlushFind(user);
		
		assertThat(followed).isEqualTo(saved.getFollowedUsers());
	}
	
	 @Test
	  public void testPersistenceOfFollowerList() {
	    List<User> followed = new LinkedList<User>();
	    followed.add(new User(null, "one", "pwd", null, null, null));
	    followed.add(new User(null, "two", "pwd", null, null, null));
	    User user = new User(null, "test", "pwd", followed, null, null);
	    User saved = entitymanager.persistFlushFind(user);
	    
	    assertThat(saved.getFollowedUsers().get(0).getFollowerUsers()).containsExactly(saved);
	  }

}
