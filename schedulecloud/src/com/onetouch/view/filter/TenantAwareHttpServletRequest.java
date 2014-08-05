package com.onetouch.view.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * A wrapper for the HttpServletRequest that includes the tenant id in the context path. Note that we do not cache
 * the servletPath or the contextPath as this can lead to problem with forwarding requests.
 * 
 * 
 * 
 */
public class TenantAwareHttpServletRequest extends HttpServletRequestWrapper {
	private final String tenantId;

	public TenantAwareHttpServletRequest(HttpServletRequest request, String tenantId) {
		super(request);
		this.tenantId = tenantId;
	}

	@Override
	public String getServletPath() {
		String servletPath = super.getServletPath();
		int start = servletPath.indexOf(tenantId);
		if (start < 0) {
			return servletPath;
		}
		int end = start + tenantId.length();
		return servletPath.substring(end);
	}

	@Override
	public String getContextPath() {
		return super.getContextPath() + "/" + tenantId;
	}

	public String getTenantId() {
		return tenantId;
	}
	
	
}