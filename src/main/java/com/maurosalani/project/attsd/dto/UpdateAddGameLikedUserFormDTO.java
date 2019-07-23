package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.Game;

public class UpdateAddGameLikedUserFormDTO {

	private CredentialsDTO credentialsDTO;
	private Game gameLiked;

	public UpdateAddGameLikedUserFormDTO() {
		super();
	}

	public UpdateAddGameLikedUserFormDTO(CredentialsDTO credentialsDTO, Game gameLiked) {
		super();
		this.credentialsDTO = credentialsDTO;
		this.gameLiked = gameLiked;
	}

	public CredentialsDTO getCredentials() {
		return credentialsDTO;
	}

	public void setCredentials(CredentialsDTO credentialsDTO) {
		this.credentialsDTO = credentialsDTO;
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
		result = prime * result + ((credentialsDTO == null) ? 0 : credentialsDTO.hashCode());
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
		UpdateAddGameLikedUserFormDTO other = (UpdateAddGameLikedUserFormDTO) obj;
		if (credentialsDTO == null) {
			if (other.credentialsDTO != null)
				return false;
		}
		else if (!credentialsDTO.equals(other.credentialsDTO))
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
