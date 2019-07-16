package com.maurosalani.project.attsd.dto;

import org.apache.commons.lang3.StringUtils;

import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.PasswordsRegistrationDoNotMatchException;
import com.maurosalani.project.attsd.exception.UsernameRequiredException;

public class RegistrationForm {

	private String username;
	private String password;
	private String confirmPassword;

	public RegistrationForm(String username, String password, String confirmPassword) {
		super();
		this.username = username;
		this.password = password;
		this.confirmPassword = confirmPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public boolean isValid()
			throws UsernameRequiredException, PasswordRequiredException, PasswordsRegistrationDoNotMatchException {
		if (username == null || StringUtils.isWhitespace(username)) {
			throw new UsernameRequiredException();
		}
		if (password == null || confirmPassword == null || StringUtils.isWhitespace(password)
				|| StringUtils.isWhitespace(confirmPassword)) {
			throw new PasswordRequiredException();
		}
		if (!password.equals(confirmPassword)) {
			throw new PasswordsRegistrationDoNotMatchException();
		}
		return true;
	}

}
