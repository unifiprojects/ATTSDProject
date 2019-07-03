package com.maurosalani.project.attsd.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.maurosalani.project.attsd.model.Game;

@DataJpaTest
@RunWith(SpringRunner.class)
public class GameRepositoryTest {

	@Autowired
	private GameRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void testFindAllWithEmptyDatabase() {
		List<Game> users = repository.findAll();
		assertThat(users).isEmpty();
	}

}
