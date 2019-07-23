package com.maurosalani.project.attsd.exception_handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.maurosalani.project.attsd.exception.BadRequestException;
import com.maurosalani.project.attsd.exception.GameNotFoundException;
import com.maurosalani.project.attsd.exception.LoginFailedException;
import com.maurosalani.project.attsd.exception.PasswordRequiredException;
import com.maurosalani.project.attsd.exception.UserNotFoundException;
import com.maurosalani.project.attsd.exception.UsernameAlreadyExistingException;

@ControllerAdvice("com.maurosalani.project.attsd.controller")
public class RestControllerExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(UserNotFoundException.class)
	public void handleUserNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), "User Not Found");
	}

	@ExceptionHandler(GameNotFoundException.class)
	public void handleGameNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.NOT_FOUND.value(), "Game Not Found");
	}

	@ExceptionHandler(BadRequestException.class)
	public void handeBadRequest(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), "Bad Request");
	}

	@ExceptionHandler(LoginFailedException.class)
	public void handeLoginFailed(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
	}

	@ExceptionHandler(PasswordRequiredException.class)
	public void handePasswordIsRequired(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), "Password required");
	}

	@ExceptionHandler(UsernameAlreadyExistingException.class)
	public void handeUsernameAlreadyExist(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value(), "Username already exist");
	}

}
