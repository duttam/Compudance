package com.onetouch.model.exception;



import org.springframework.dao.DataAccessException;

public class UserAlreadyFoundException extends DataAccessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public UserAlreadyFoundException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

	
	public UserAlreadyFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
