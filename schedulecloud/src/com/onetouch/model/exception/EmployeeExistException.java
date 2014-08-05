package com.onetouch.model.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;

public class EmployeeExistException extends DataAccessException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public EmployeeExistException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
