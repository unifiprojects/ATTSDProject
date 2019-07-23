package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.User;

public class UpdateUserFormDTO {

	private CredentialsDTO credentialsDTO;
	private User userToUpdate;

	public UpdateUserFormDTO() {
	}

	public UpdateUserFormDTO(CredentialsDTO credentialsDTO, User userToUpdate) {
		super();
		this.credentialsDTO = credentialsDTO;
		this.userToUpdate = userToUpdate;
	}

	public CredentialsDTO getCredentials() {
		return credentialsDTO;
	}

	public void setCredentials(CredentialsDTO credentialsDTO) {
		this.credentialsDTO = credentialsDTO;
	}

	public User getUserToUpdate() {
		return userToUpdate;
	}

	public void setUserToUpdate(User userToUpdate) {
		this.userToUpdate = userToUpdate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentialsDTO == null) ? 0 : credentialsDTO.hashCode());
		result = prime * result + ((userToUpdate == null) ? 0 : userToUpdate.hashCode());
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
		UpdateUserFormDTO other = (UpdateUserFormDTO) obj;
		if (credentialsDTO == null) {
			if (other.credentialsDTO != null)
				return false;
		}
		else if (!credentialsDTO.equals(other.credentialsDTO))
			return false;
		if (userToUpdate == null) {
			if (other.userToUpdate != null)
				return false;
		}
		else if (!userToUpdate.equals(other.userToUpdate))
			return false;
		return true;
	}

}
