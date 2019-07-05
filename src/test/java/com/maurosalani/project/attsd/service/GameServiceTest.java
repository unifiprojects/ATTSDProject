package com.maurosalani.project.attsd.service;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
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

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.model.Game;
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

	@Test
	public void testGetGameByIdWithExistingGame() {
		Game game = new Game(1L, "game", "description", new Date());
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		assertThat(gameService.getGameById(1L)).isEqualTo(game);
	}

	@Test
	public void testInsertNewgame_setsIdToNull_returnsSavedGame() {
		Game toSave = spy(new Game(99L, "toSaveGame", "description", new Date(1000)));
		Game saved = new Game(1L, "savedGame", "description", new Date(1000));

		when(gameRepository.save(any(Game.class))).thenReturn(saved);

		Game result = gameService.insertNewGame(toSave);

		assertThat(result).isEqualTo(saved);
		InOrder inOrder = inOrder(toSave, gameRepository);
		inOrder.verify(toSave).setId(null);
		inOrder.verify(gameRepository).save(toSave);
	}

	@Test
	public void testInsertNewGame_GameIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> gameService.insertNewGame(null));
		verifyNoMoreInteractions(gameRepository);
	}

	@Test
	public void testUpdateGameById_setsIdToArgument_ShouldReturnSavedGame() {
		Game replacement = spy(new Game(null, "replacement_game", "description", new Date()));
		Game replaced = new Game(1L, "replaced_game", "description", new Date());
		when(gameRepository.save(any(Game.class))).thenReturn(replaced);

		Game result = null;
		try {
			result = gameService.updateGameById(1L, replacement);
		} catch (GameNotFoundException e) {
			fail();
		}
		assertThat(result).isEqualTo(replaced);
		InOrder inOrder = inOrder(replacement, gameRepository);
		inOrder.verify(replacement).setId(1L);
		inOrder.verify(gameRepository).save(replacement);
	}

	@Test
	public void testUpdateGameById_GameIsNull_ShouldThrowException() {
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> gameService.updateGameById(1L, null));
		verifyNoMoreInteractions(gameRepository);
	}

	@Test
	public void testUpdateGameById_IdNotFound_ShouldThrowException() {
		Game game = new Game(1L, "name", "description", new Date());
		when(gameRepository.findById(1L)).thenReturn(null);
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> gameService.updateGameById(1L, game));
	}

	@Test
	public void testUpdateGameById_IdIsNull_ShouldThrowException() {
		Game game = new Game(1L, "name", "description", new Date());
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> gameService.updateGameById(null, game));
		verifyNoMoreInteractions(gameRepository);
	}
}
