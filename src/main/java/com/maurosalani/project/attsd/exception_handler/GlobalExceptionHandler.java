package com.maurosalani.project.attsd.exception_handler;

import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.UnauthorizedOperationException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String MESSAGE_MODEL = "message";

	@ExceptionHandler(UsernameAlreadyExistingException.class)
	public String handleUsernameAlreadyExisting(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.CONFLICT.value());
		model.addAttribute(MESSAGE_MODEL, "Username already existing. Please choose another one.");
		return "registration";
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public String handleDataIntegrityViolation(Model model, HttpServletResponse response) {
		response.setStatus(HttpStatus.BAD_REQUEST.value());
		model.addAttribute(MESSAGE_MODEL, "Username or password invalid.");
		return "registration";
	}

	@ExceptionHandler(UserNotFoundException.class)
	public String handleUserNotFound(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE_MODEL, "Profile not found.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "profile404";
	}

	@ExceptionHandler(LoginFailedException.class)
	public String handleLoginFailed(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE_MODEL, "Username or password invalid.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "login";
	}
	
	@ExceptionHandler(GameNotFoundException.class)
	public String handleGameNotFound(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE_MODEL, "Game not found.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "game404";
	}
	
	@ExceptionHandler(UnauthorizedOperationException.class)
	public String handleUnauthorizedOperation(Model model, HttpServletResponse response) {
		model.addAttribute(MESSAGE_MODEL, "Unauthorized Operation. You are not logged in!");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		return "unauthorized401";
	}

}
