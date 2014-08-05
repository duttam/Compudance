package com.onetouch.model.domainobject;

import java.io.Serializable;

public class TenantSetting implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String logoUrl;
	private String logoutUrl;
	private String theme;
	private String policyFileUrl;
	private String tutorialUrl;
	
	public TenantSetting() {
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getLogoUrl() {
		return logoUrl;
	}
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	public String getLogoutUrl() {
		return logoutUrl;
	}
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}
	public String getTheme() {
		return theme;
	}
	public void setTheme(String theme) {
		this.theme = theme;
	}
	public String getPolicyFileUrl() {
		return policyFileUrl;
	}
	public void setPolicyFileUrl(String policyFileUrl) {
		this.policyFileUrl = policyFileUrl;
	}
	public String getTutorialUrl() {
		return tutorialUrl;
	}
	public void setTutorialUrl(String tutorialUrl) {
		this.tutorialUrl = tutorialUrl;
	}
	
	
	
}
