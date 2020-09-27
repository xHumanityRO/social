package com.xhumanity.social.exception;

public class IntegrationException extends RuntimeException {
	private static final long serialVersionUID = 8943655883746560677L;

	public IntegrationException(final String message) {
		super(message);
	}

	public IntegrationException(final Throwable throwable) {
		this(throwable.toString(), throwable);
	}

	public IntegrationException(final String message, final Throwable throwable) {
		super(message, throwable);
		this.setStackTrace(throwable.getStackTrace());
	}
}
