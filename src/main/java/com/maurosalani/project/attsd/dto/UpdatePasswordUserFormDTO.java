package com.maurosalani.project.attsd.dto;

public class UpdatePasswordUserFormDTO {

	private CredentialsDTO credentialsDTO;
	private String newPassword;

	public UpdatePasswordUserFormDTO() {
	}

	public UpdatePasswordUserFormDTO(CredentialsDTO credentialsDTO, String newPassword) {
		super();
		this.credentialsDTO = credentialsDTO;
		this.newPassword = newPassword;
	}

	public CredentialsDTO getCredentials() {
		return credentialsDTO;
	}

	public void setCredentials(CredentialsDTO credentialsDTO) {
		this.credentialsDTO = credentialsDTO;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((credentialsDTO == null) ? 0 : credentialsDTO.hashCode());
		result = prime * result + ((newPassword == null) ? 0 : newPassword.hashCode());
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
		UpdatePasswordUserFormDTO other = (UpdatePasswordUserFormDTO) obj;
		if (credentialsDTO == null) {
			if (other.credentialsDTO != null)
				return false;
		}
		else if (!credentialsDTO.equals(other.credentialsDTO))
			return false;
		if (newPassword == null) {
			if (other.newPassword != null)
				return false;
		}
		else if (!newPassword.equals(other.newPassword))
			return false;
		return true;
	}

}
