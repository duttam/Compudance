package com.onetouch.view.exception;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.el.ELException;
import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

import org.apache.log4j.Logger;
import org.omnifaces.exceptionhandler.FullAjaxExceptionHandler;
import org.springframework.dao.DataAccessException;

import com.onetouch.model.exception.UserAlreadyFoundException;
import com.onetouch.view.util.FacesUtils;
import com.sun.faces.mgbean.ManagedBeanCreationException;

public class ScheduleCloudExceptionHandler extends FullAjaxExceptionHandler {
	static final Logger logger = Logger.getLogger(ScheduleCloudExceptionHandler.class.getName());
	private ExceptionHandler wrapped;
	public ScheduleCloudExceptionHandler(ExceptionHandler wrapped) {
		super(wrapped);
		this.wrapped = wrapped;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return this.wrapped;
	}
	@Override
	protected void logException(FacesContext context, Throwable exception, String location, String message, Object... parameters){
		logger.warn(exception.getStackTrace());

	}

}