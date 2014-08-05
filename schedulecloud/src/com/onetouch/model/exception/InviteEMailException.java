package com.onetouch.model.exception;

import org.springframework.mail.MailException;

public class InviteEMailException extends MailException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InviteEMailException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}

}
