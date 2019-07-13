package com.maurosalani.project.attsd.exception_handler;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.maurosalani.project.attsd.exception.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private static final String MESSAGE_MODEL = "message";

	@ExceptionHandler(UserNotFoundException.class)
	public String handleUserNotFound(Model model, HttpServletResponse response) throws IOException {
		model.addAttribute(MESSAGE_MODEL, "Username or password invalid.");
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return "login";
	}

}
