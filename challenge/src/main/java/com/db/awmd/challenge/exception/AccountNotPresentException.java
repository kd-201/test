package com.db.awmd.challenge.exception;

public class AccountNotPresentException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5623133093175763935L;

	public AccountNotPresentException(String message) {
		super(message);
	}
}
