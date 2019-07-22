package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.Game;

public class UpdateAddGameLikedUserForm {

	private Credentials credentials;
	private Game gameLiked;

	public UpdateAddGameLikedUserForm() {
		super();
	}

	public UpdateAddGameLikedUserForm(Credentials credentials, Game gameLiked) {
		super();
		this.credentials = credentials;
		this.gameLiked = gameLiked;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
	}

	public Game getGameLiked() {
		return gameLiked;
	}

	public void setGameLiked(Game gameLiked) {
		this.gameLiked = gameLiked;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
		result = prime * result + ((gameLiked == null) ? 0 : gameLiked.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UpdateAddGameLikedUserForm other = (UpdateAddGameLikedUserForm) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		}
		else if (!credentials.equals(other.credentials))
			return false;
		if (gameLiked == null) {
			if (other.gameLiked != null)
				return false;
		}
		else if (!gameLiked.equals(other.gameLiked))
			return false;
		return true;
	}

}
