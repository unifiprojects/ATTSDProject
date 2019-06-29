package com.maurosalani.project.attsd.model;

import java.util.Date;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Game {

	@Id
	@GeneratedValue
	private Long id;

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
