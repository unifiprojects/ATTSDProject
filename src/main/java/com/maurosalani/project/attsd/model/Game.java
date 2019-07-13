package com.maurosalani.project.attsd.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class Game implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;

	private String name;
	private String description;
	private Date releaseDate;

	public Game() {

	}

	public Game(Long id, String name, String description, Date releaseDate) {
		this.id = id;
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
		Game other = (Game) obj;
		return Objects.equals(description, other.description) && Objects.equals(id, other.id)
				&& Objects.equals(name, other.name) && Objects.equals(releaseDate, other.releaseDate);
	}

	@Override
	public int hashCode() {
		return Objects.hash(description, id, name, releaseDate);
	}

	@Override
	public String toString() {
		return "Game [id=" + id + ", name=" + name + ", description=" + description + ", releaseDate=" + releaseDate
				+ "]";
	}

}
