package com.maurosalani.project.attsd.dto;

import com.maurosalani.project.attsd.model.User;

public class UpdateAddFollowedUserForm {

	private Credentials credentials;
	private User followedToAdd;

	public UpdateAddFollowedUserForm() {
	}

	public UpdateAddFollowedUserForm(Credentials credentials, User followedToAdd) {
		super();
		this.credentials = credentials;
		this.followedToAdd = followedToAdd;
	}

	public Credentials getCredentials() {
		return credentials;
	}

	public void setCredentials(Credentials credentials) {
		this.credentials = credentials;
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
		result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
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
		UpdateAddFollowedUserForm other = (UpdateAddFollowedUserForm) obj;
		if (credentials == null) {
			if (other.credentials != null)
				return false;
		} else if (!credentials.equals(other.credentials))
			return false;
		if (followedToAdd == null) {
			if (other.followedToAdd != null)
				return false;
		} else if (!followedToAdd.equals(other.followedToAdd))
			return false;
		return true;
	}

}
