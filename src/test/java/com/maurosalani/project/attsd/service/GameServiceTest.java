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
import java.sql.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;

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
		Game game1 = new Game(1L, "game1", "description1", new Date(0));
		Game game2 = new Game(2L, "game2", "description2", new Date(0));
		when(gameRepository.findAll()).thenReturn(asList(game1, game2));
		assertThat(gameService.getAllGames()).containsExactly(game1, game2);
	}

	@Test
	public void testGetGameByIdWhenGameDoesNotExist_ShouldThrowException() {
		when(gameRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> gameService.getGameById(1L));
	}

	@Test
	public void testGetGameByIdWithExistingGame() throws Exception {
		Game game = new Game(1L, "game", "description", new Date(0));
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		assertThat(gameService.getGameById(1L)).isEqualTo(game);
	}

	@Test
	public void testGetGameByIdWithIdNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> gameService.getGameById(null));
	}

	@Test
	public void testGetGameByNameWhenGameDoesNotExist_ShouldThrowException() {
		when(gameRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> gameService.getGameByName("game"));
	}

	@Test
	public void testGetGameByNameWithExistingGame() throws Exception {
		Game game = new Game(1L, "game", "description", new Date(0));
		when(gameRepository.findByName("game")).thenReturn(Optional.of(game));
		assertThat(gameService.getGameByName("game")).isEqualTo(game);
	}

	@Test
	public void testGetGameByNameWithNameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> gameService.getGameByName(null));
	}

	@Test
	public void testGetGamesByNameLikeWhenGameDoesNotExist() {
		when(gameRepository.findByNameLike(anyString())).thenReturn(null);
		assertThat(gameService.getGamesByNameLike("name")).isNull();
	}

	@Test
	public void testGetGamesByNameLikeWithExistingGames() {
		Game game1 = new Game(1L, "game1", "description1", new Date(0));
		Game game2 = new Game(2L, "game2", "description2", new Date(0));
		when(gameRepository.findByNameLike("%name%")).thenReturn(asList(game1, game2));
		assertThat(gameService.getGamesByNameLike("name")).containsExactly(game1, game2);
	}

	@Test
	public void testGetGamesByNameLike_verifyNameIsTrimmedAndWithWildcards() {
		String content = " someName ";
		gameService.getGamesByNameLike(content);
		
		verify(gameRepository).findByNameLike("%someName%");
	}
	
	@Test
	public void testGetGamesByNameLikeWithNameNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> gameService.getGamesByNameLike(null));
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
	public void testUpdateGameById_setsIdToArgument_ShouldReturnSavedGame()
			throws IllegalArgumentException, GameNotFoundException {
		Game replacement = spy(new Game(null, "replacement_game", "description", new Date(0)));
		Game replaced = new Game(1L, "replaced_game", "description", new Date(0));
		when(gameRepository.save(any(Game.class))).thenReturn(replaced);
		when(gameRepository.findById(1L)).thenReturn(Optional.of(replaced));

		Game result = gameService.updateGameById(1L, replacement);

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
		Game game = new Game(1L, "name", "description", new Date(0));
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> gameService.updateGameById(1L, game));
		verifyNoMoreInteractions(ignoreStubs(gameRepository));
	}

	@Test
	public void testUpdateGameById_IdIsNull_ShouldThrowException() {
		Game game = new Game(1L, "name", "description", new Date(0));
		assertThatExceptionOfType(IllegalArgumentException.class)
				.isThrownBy(() -> gameService.updateGameById(null, game));
		verifyNoMoreInteractions(gameRepository);
	}

	@Test
	public void testDeleteById_IdIsNull() {
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> gameService.deleteById(null));
		verifyNoMoreInteractions(gameRepository);
	}

	@Test
	public void testDeleteById_IdNotFound_ShouldThrowException() {
		when(gameRepository.findById(1L)).thenReturn(Optional.empty());
		assertThatExceptionOfType(GameNotFoundException.class).isThrownBy(() -> gameService.deleteById(1L));
		verifyNoMoreInteractions(ignoreStubs(gameRepository));
	}

	@Test
	public void testDeleteById_IdFound() {
		Game game = new Game(1L, "name", "description", new Date(0));
		when(gameRepository.findById(1L)).thenReturn(Optional.of(game));
		assertThatCode(() -> gameService.deleteById(1L)).doesNotThrowAnyException();
		verify(gameRepository, times(1)).deleteById(1L);
	}
	
	@Test
	public void testFindTop3LatestReleaseGames() {
		Game game1 = new Game(1L, "game1", "description1", new Date(100));
		Game game2 = new Game(2L, "game2", "description2", new Date(200));
		Game game3 = new Game(3L, "game3", "description3", new Date(300));
		when(gameRepository.findFirstNOrderByReleaseDate(PageRequest.of(0,3))).thenReturn(asList(game3, game2, game1));
		
		List<Game> latest3Release = gameService.getLatestReleasesGames(3);
		assertThat(latest3Release.size()).isEqualTo(3);
		assertThat(latest3Release).containsExactly(game3, game2, game1);
	}
}
