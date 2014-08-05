package com.onetouch.view.filter;



import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.RegexRequestMatcher;
import org.springframework.security.web.util.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import com.onetouch.model.domainobject.Tenant;

import com.onetouch.model.service.tenant.ITenantService;

import com.onetouch.view.context.TenantContext;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.FacesUtils;



/**
 * Obtains the tenant id as the first path after the context root and contains a / after it. If the tenant is found, it
 * will then replace the {@link HttpServletRequest} with {@link TenantAwareHttpServletRequest}. A few examples where
 * /context is always the context root:
 * 
 * <ul>
 * <li>/context/ - null tenant</li>
 * <li>/context/a - null tenant because there is no trailing slash afterwards. This allows for URL's with no tenant that
 * do not have a folder. For example, a logout URL.</li>
 * <li>/context/a/ - tenant id of a</li>
 * <li>/context/tenant/b/c/def.png - tenant id of tenant</li>
 * </ul>
 * 
 * 
 * 
 * 
 */
@Component
public class TenantFilter  extends OncePerRequestFilter {
	
	RequestMatcher ignoreMatcher = new RegexRequestMatcher("^/(ui|template|images|css|resources).*", null);
	
	@Autowired
	private TenantContext tenantContext;
	
	@Autowired
	private ITenantService tenantService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (ignoreMatcher.matches(request)) {
			filterChain.doFilter(request, response);
			return;
		}
		String tenantName = getTenantId(request);// get the tenant name from url
		
		if (tenantName == null) {
			filterChain.doFilter(request, response);
			
			return;
		}
		
		if(tenantName.equalsIgnoreCase("rest")){
			filterChain.doFilter(request, response);
			
			return;
		}
			
		
		try {
			// if its first request
			if(tenantName!=null  ){
				Tenant tenant = tenantContext.getTenant();
				if( tenant == null ) {
					tenant = tenantService.getTenant(tenantName,false);// get tenant , loaded during application startup
					 if ( tenant == null ){
						 tenant = tenantService.getTenant(tenantName,true); // reload tenants
						 //if still null, there is no tenat added recently
						 if ( tenant == null ){
							 String contextRoot = request.getSession().getServletContext().getContextPath();
						 
							 response.sendRedirect(contextRoot+"/ui/error/error.html");
						 }
						 //redirect to error page throw new TenantException( "Unknown tenant: " + tenantName );
					 }
					 
					 tenantContext.setTenant(tenant);
				}
				else {
					 // tenant context already initialized
					 // check whether it is as expected
					 if ( !tenant.getCode().equals( tenantName ) ){
						 String contextRoot = request.getSession().getServletContext().getContextPath();
						 response.sendRedirect(contextRoot+"/j_spring_security_logout");
						 //redirect to error page throw new TenantException( "Unexpected tenant [session=" + sessionTenantId + ", url=" + tenantName + "]" );
					 }
				}
					 
				TenantContextUtil.setTenant(tenant);
				filterChain.doFilter(new TenantAwareHttpServletRequest(request, tenantName), response);
			}
		}
		finally {
			TenantContextUtil.clearTenant();
		}
	}

	

	private String getTenantId(HttpServletRequest request) {
		/*String tenantId = TenantContextUtil.getTenantId();
		if (tenantId != null) {
			return tenantId;
		}*/

		String requestUrl = currentUrl(request);

		if ("".equals(requestUrl) || "/".equals(requestUrl)) {
			return null;
		}
		StringTokenizer tokens = new StringTokenizer(requestUrl, "/");
		if (tokens.hasMoreTokens()) {
			String result = tokens.nextToken();
			if (tokens.hasMoreTokens() || requestUrl.endsWith("/")) {
				if(result.equalsIgnoreCase("javax.faces.resource"))
					return null;
				return result;
			}
		}
		return null;
	}

	private String currentUrl(HttpServletRequest request) {
		StringBuilder url = new StringBuilder();
		url.append(request.getServletPath());

		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			url.append(pathInfo);
		}
		return url.toString();
	}

	
}
