package com.xhumanity.social.exception;

public class APIException extends RuntimeException
{
	private static final long serialVersionUID = 5069796445984449394L;

	public APIException(final String message)
	{
		super(message);
	}
}
