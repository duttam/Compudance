package com.onetouch.view.listener;

import java.io.IOException;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
 
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;

import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.filter.TenantAwareHttpServletRequest;
import com.onetouch.view.util.FacesUtils;

 
public class LoginErrorPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = -1216620620302322995L;
 
    /*@Override
    public void afterPhase(final PhaseEvent event)
    {
       		
        
    }*/
    @Override
    public void afterPhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String originalURL = (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
        String loginURL = request.getContextPath() + "/ui/login/login.jsf";

        if (context.getPartialViewContext().isAjaxRequest()
            && originalURL != null
            && loginURL.equals(request.getRequestURI()))
        {
            try {
                context.getExternalContext().redirect(originalURL);
            } catch (IOException e) {
                throw new FacesException(e);
            }
        }
    }
    @Override
    public void beforePhase(final PhaseEvent event)
    {
        Exception e = (Exception) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
                AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
 
        if (e instanceof BadCredentialsException)
        {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
                    AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, null);
            
    		
            FacesUtils.addErrorMessage("Username or password not valid.");
        }
        if (e instanceof DisabledException)
        {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(
                    AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, null);
            FacesUtils.addErrorMessage("User Disabled, contact Schedule-Cloud.");
        }
    }
 
    @Override
    public PhaseId getPhaseId()
    {
        return PhaseId.RESTORE_VIEW;//RENDER_RESPONSE;
    }
 
}