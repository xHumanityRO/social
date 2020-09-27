package com.xhumanity.social.exception;

public class DatabaseException extends RuntimeException {
	private static final long serialVersionUID = -2960230623456700302L;

	public DatabaseException(final String message) {
		super(message);
	}

	public DatabaseException(final String message, final Throwable throwable) {
		super(message, throwable);
		this.setStackTrace(throwable.getStackTrace());
	}

	public DatabaseException(final Throwable throwable) {
		super(throwable);
		this.setStackTrace(throwable.getStackTrace());
	}
}
