package com.maurosalani.project.attsd.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.User;

@DataJpaTest
@RunWith(SpringRunner.class)
public class UserRepositoryTest {

	@Autowired
	private UserRepository repository;

	@Autowired
	private TestEntityManager entitymanager;

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

}
