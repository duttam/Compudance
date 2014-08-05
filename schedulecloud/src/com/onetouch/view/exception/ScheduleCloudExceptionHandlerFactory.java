package com.onetouch.view.exception;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;

import org.omnifaces.exceptionhandler.FullAjaxExceptionHandlerFactory;

public class ScheduleCloudExceptionHandlerFactory extends ExceptionHandlerFactory {
	   private ExceptionHandlerFactory parent;
	 
	   // this injection handles jsf
	   public ScheduleCloudExceptionHandlerFactory(ExceptionHandlerFactory parent) {
	    this.parent = parent;
	   }
	 
	    @Override
	    public ExceptionHandler getExceptionHandler() {
	 
	        ExceptionHandler handler = new  ScheduleCloudExceptionHandler(parent.getExceptionHandler());
	 
	        return handler;
	    }
	 
	}