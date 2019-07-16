package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.User;

public class UpdateUserForm {

	private Credentials credentials;
	private User userToUpdate;

	public UpdateUserForm() {
	}

	public UpdateUserForm(Credentials credentials, User userToUpdate) {
		super();
		this.credentials = credentials;
		this.userToUpdate = userToUpdate;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
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
		result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
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
		UpdateUserForm other = (UpdateUserForm) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (userToUpdate == null) {
			if (other.userToUpdate != null)
				return false;
		} else if (!userToUpdate.equals(other.userToUpdate))
			return false;
		return true;
	}

}
