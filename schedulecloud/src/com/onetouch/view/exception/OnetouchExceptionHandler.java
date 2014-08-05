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
import org.springframework.dao.DataAccessException;

import com.onetouch.model.exception.UserAlreadyFoundException;
import com.onetouch.view.util.FacesUtils;
import com.sun.faces.mgbean.ManagedBeanCreationException;

public class OnetouchExceptionHandler extends ExceptionHandlerWrapper {
	private static final Logger log = Logger.getLogger(OnetouchExceptionHandler.class.getCanonicalName());
	private ExceptionHandler wrapped;

	OnetouchExceptionHandler(ExceptionHandler exception) {
		this.wrapped = exception;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}
	
	/*@Override
	  public void handle() throws FacesException {
	    Iterator<ExceptionQueuedEvent> iterator = getUnhandledExceptionQueuedEvents().iterator();
	    
	    while (iterator.hasNext()) {
	      ExceptionQueuedEvent event = (ExceptionQueuedEvent) iterator.next();
	      ExceptionQueuedEventContext context = (ExceptionQueuedEventContext)event.getSource();
	 
	      Throwable throwable = context.getException();
	      
	      FacesContext fc = FacesContext.getCurrentInstance();
	      String message = "";
	      try {
	          Flash flash = fc.getExternalContext().getFlash();
	          if (throwable instanceof UserAlreadyFoundException)
	              message = ((UserAlreadyFoundException)throwable).getMessage();
	             
	          flash.put("errorDetails", message);
	          fc.getExternalContext().getFlash().setKeepMessages(true);
	          log.error(throwable.getClass().getSimpleName()+" error message : "+ throwable.getMessage()+" "+throwable.getStackTrace()+" ");
	          FacesUtils.redirectToPage("ui/error/error");
	          
	          fc.renderResponse();
	          fc.responseComplete();
	      } finally {
	          iterator.remove();
	      }
	    }
	    
	    // Let the parent handle the rest
	    getWrapped().handle();
	  }*/
	
	@Override
	public void handle() throws FacesException {


		for (final Iterator<ExceptionQueuedEvent> it = getUnhandledExceptionQueuedEvents().iterator(); it.hasNext();) {
			Throwable t = it.next().getContext().getException();
			while ((t instanceof FacesException || t instanceof ELException)
					&& t.getCause() != null) {
				t = t.getCause();
			}

			
				final FacesContext facesContext = FacesContext.getCurrentInstance();
				final ExternalContext externalContext = facesContext.getExternalContext();
				
				try {
					t.printStackTrace();
					log.error(t.getClass().getSimpleName()+" error message : "+ t.getMessage()+" "+t.getStackTrace()+" ");
					String message="";
					Flash flash = facesContext.getExternalContext().getFlash();
					//message = t.getMessage(); // beware, don't leak internal info!
					if (t instanceof UserAlreadyFoundException)
			              message = ((UserAlreadyFoundException)t).getMessage();
			          
					flash.put("errorDetails", message);
					facesContext.getExternalContext().getFlash().setKeepMessages(true);
					FacesUtils.redirectToPage("ui/error/error");
					facesContext.renderResponse();
					facesContext.responseComplete();
				} finally {
					it.remove();
				}
			}

		getWrapped().handle();
	}
	
	
}