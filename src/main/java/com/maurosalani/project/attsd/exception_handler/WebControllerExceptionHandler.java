package com.maurosalani.project.attsd.exception_handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.maurosalani.project.attsd.dto.CredentialsDTO;
import com.maurosalani.project.attsd.dto.RegistrationForm;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.NewPasswordRequiredException;
import com.maurosalani.project.attsd.exception.OldPasswordErrorException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.PasswordsRegistrationDoNotMatchException;
import com.maurosalani.project.attsd.exception.UnauthorizedOperationException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;
import com.maurosalani.project.attsd.exception.UsernameRequiredException;

@ControllerAdvice("com.maurosalani.project.attsd.web")
public class WebControllerExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String REGISTRATION_VIEW = "registration";
	private static final String REGISTRATION_FORM = "registrationForm";
	private static final String MESSAGE = "message";

	@ExceptionHandler(UsernameAlreadyExistingException.class)
	public String handleUsernameAlreadyExisting(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.CONFLICT.value());
		model.addAttribute(MESSAGE, "Username already existing. Please choose another one.");
		model.addAttribute(REGISTRATION_FORM, new RegistrationForm());
		return REGISTRATION_VIEW;
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public String handleDataIntegrityViolation(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Username or password invalid.");
		model.addAttribute(REGISTRATION_FORM, new RegistrationForm());
		return REGISTRATION_VIEW;
	}

	@ExceptionHandler(UserNotFoundException.class)
	public String handleUserNotFound(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE, "Profile not found.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "profile404";
	}

	@ExceptionHandler(LoginFailedException.class)
	public String handleLoginFailed(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE, "Invalid username or password.");
		model.addAttribute("credentials", new CredentialsDTO());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return "login";
	}

	@ExceptionHandler(GameNotFoundException.class)
	public String handleGameNotFound(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE, "Game not found.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "game404";
	}

	@ExceptionHandler(UnauthorizedOperationException.class)
	public String handleUnauthorizedOperation(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE, "Unauthorized Operation. You are not logged in!");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return "unauthorized401";
	}

	@ExceptionHandler(PasswordsRegistrationDoNotMatchException.class)
	public String handleRegistrationPasswordNotValid(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Password and Confirm Password must match.");
		model.addAttribute(REGISTRATION_FORM, new RegistrationForm());
		return REGISTRATION_VIEW;
	}

	@ExceptionHandler(PasswordRequiredException.class)
	public String handlePasswordRequired(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Password is required.");
		model.addAttribute(REGISTRATION_FORM, new RegistrationForm());
		return REGISTRATION_VIEW;
	}

	@ExceptionHandler(UsernameRequiredException.class)
	public String handleUsernameRequired(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Username is required.");
		model.addAttribute(REGISTRATION_FORM, new RegistrationForm());
		return REGISTRATION_VIEW;
	}

	@ExceptionHandler(OldPasswordErrorException.class)
	public String handleOldPasswordException(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Old password do not match.");
		return "passwordError";
	}

	@ExceptionHandler(NewPasswordRequiredException.class)
	public String handleNewPasswordException(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE, "Password is required.");
		return "passwordError";
	}

}
