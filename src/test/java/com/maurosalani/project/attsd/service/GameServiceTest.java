package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.model.Game;
import com.maurosalani.project.attsd.model.User;
import com.maurosalani.project.attsd.repository.GameRepository;

@RunWith(MockitoJUnitRunner.class)
public class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@InjectMocks
	private GameService gameService;

	@Test
	public void testFindAllWhenDatabaseIsEmpty() {
		when(gameRepository.findAll()).thenReturn(Collections.emptyList());
		assertThat(gameService.getAllGames()).isEmpty();
	}

	@Test
	public void testFindAllWithExistingGames() {
		Game game1 = new Game(1L, "game1", "description1", new Date());
		Game game2 = new Game(2L, "game2", "description2", new Date());
		when(gameRepository.findAll()).thenReturn(asList(game1, game2));
		assertThat(gameService.getAllGames()).containsExactly(game1, game2);
	}
	
	@Test
	public void testGetGameByIdWhenGameDoesNotExist() {
		when(gameRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(gameService.getGameById(1L)).isNull();
	}
}
