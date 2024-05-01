package com.apiecommerce.tokoto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizationException extends RuntimeException {
	public UnauthorizationException(String message) {
		super(message);
	}
}
