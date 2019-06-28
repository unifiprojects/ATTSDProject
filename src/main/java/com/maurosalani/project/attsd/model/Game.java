package com.maurosalani.project.attsd.model;

import java.util.Date;
import java.util.Objects;

public class Game {

	private long id;

	private String name;
	private String description;
	private Date releaseDate;

	public Game() {

	}

	public Game(String name, String description, Date releaseDate) {
		this.name = name;
		this.description = description;
		this.releaseDate = releaseDate;
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

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Game object = (Game) obj;
		return Objects.equals(id, object.id) && Objects.equals(name, object.name)
				&& Objects.equals(description, object.description) && Objects.equals(releaseDate, object.releaseDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, description, releaseDate);
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", name=" + name + ", description=" + description + ", releaseDate=" + releaseDate
				+ "]";
	}

}
