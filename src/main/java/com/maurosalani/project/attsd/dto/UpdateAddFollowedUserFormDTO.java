package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.User;

public class UpdateAddFollowedUserFormDTO {

	private CredentialsDTO credentialsDTO;
	private User followedToAdd;

	public UpdateAddFollowedUserFormDTO() {
	}

	public UpdateAddFollowedUserFormDTO(CredentialsDTO credentialsDTO, User followedToAdd) {
		super();
		this.credentialsDTO = credentialsDTO;
		this.followedToAdd = followedToAdd;
	}

	public CredentialsDTO getCredentials() {
		return credentialsDTO;
	}

	public void setCredentials(CredentialsDTO credentialsDTO) {
		this.credentialsDTO = credentialsDTO;
	}

	public User getFollowedToAdd() {
		return followedToAdd;
	}

	public void setFollowedToAdd(User followedToAdd) {
		this.followedToAdd = followedToAdd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentialsDTO == null) ? 0 : credentialsDTO.hashCode());
		result = prime * result + ((followedToAdd == null) ? 0 : followedToAdd.hashCode());
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
		UpdateAddFollowedUserFormDTO other = (UpdateAddFollowedUserFormDTO) obj;
		if (credentialsDTO == null) {
			if (other.credentialsDTO != null)
				return false;
		}
		else if (!credentialsDTO.equals(other.credentialsDTO))
			return false;
		if (followedToAdd == null) {
			if (other.followedToAdd != null)
				return false;
		}
		else if (!followedToAdd.equals(other.followedToAdd))
			return false;
		return true;
	}

}
