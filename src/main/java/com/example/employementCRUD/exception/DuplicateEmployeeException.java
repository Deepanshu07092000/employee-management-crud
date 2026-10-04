package com.example.employementCRUD.exception;

public class DuplicateEmployeeException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DuplicateEmployeeException(String message) {
		super(message);
	}

}
