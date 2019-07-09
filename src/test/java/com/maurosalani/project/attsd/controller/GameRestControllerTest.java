package com.maurosalani.project.attsd.controller;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.maurosalani.project.attsd.service.GameService;

@RunWith(MockitoJUnitRunner.class)
public class GameRestControllerTest {

	@InjectMocks
	private GameRestController gameRestController;

	@Mock
	private GameService gameService;

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
