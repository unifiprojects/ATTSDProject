package com.maurosalani.project.attsd.dto;

import java.util.Date;

import com.maurosalani.project.attsd.model.Game;

public class GameDTO {

	private Long id;

	private String name;

	private String description;

	private Date releaseDate;

	public GameDTO() {
	}

	public GameDTO(Long id, String name, String description, Date releaseDate) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public Game getGame() {
		Game game = new Game(this.id, this.name, this.description, this.releaseDate);
		return game;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

}
