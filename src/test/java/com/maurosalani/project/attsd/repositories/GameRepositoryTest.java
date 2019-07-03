package com.maurosalani.project.attsd.repositories;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
public class GameRepositoryTest {

	@Autowired
	private GameRepository repository;

	@Autowired
	private TestEntityManager entityManager;

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
