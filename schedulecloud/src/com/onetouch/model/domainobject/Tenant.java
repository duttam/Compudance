package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

public class Tenant implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String address1;
	private String address2;
	private String city;
	private State state = new State();
	private String zipcode;
	private String companyEmail;
	private String businessPhone;
	private String fax;
	private String code;//tenant code
	private String logoUrl;
	private String logoutUrl;
	private String theme;
	private String username;
	private String password;
	private boolean active;
	private String contactname;
	private Date enrollmentDate;
	private Employee adminEmployee;
	private String emailSender;
	private String emailSenderPrefix;
	private String serverUsername;
	private String serverPassword;
	private String breakOption = "unpaid";
	private String overtimeOption  = "none";
	private Double overtimeRate = new Double(1);
	private Integer emailExpLmt = 259200000;
	private boolean sortByRankAndHiredate;
	private String policyNotes;
	private boolean showOffer;
	private boolean showMove;
	private boolean showNotifiedEmployeeOnPDF;
	public Tenant(){
		enrollmentDate = new Date();
		
	}
	public Tenant(Integer id) {

		this.id = id;
	}
	
		
	public Tenant(Integer id, String code) {
		
		this.id = id;
		this.code = code;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCompanyEmail() {
		return companyEmail;
	}
	public void setCompanyEmail(String companyEmail) {
		this.companyEmail = companyEmail;
	}
	public String getBusinessPhone() {
		return businessPhone;
	}
	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}
	public String getFax() {
		return fax;
	}
	public void setFax(String fax) {
		this.fax = fax;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
		
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tenant other = (Tenant) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getContactname() {
		return contactname;
	}
	public void setContactname(String contactname) {
		this.contactname = contactname;
	}
	public Date getEnrollmentDate() {
		return enrollmentDate;
	}
	public void setEnrollmentDate(Date enrollmentDate) {
		this.enrollmentDate = enrollmentDate;
	}
	public Employee getAdminEmployee() {
		return adminEmployee;
	}
	public void setAdminEmployee(Employee adminEmployee) {
		this.adminEmployee = adminEmployee;
	}
	
	public String getEmailSender() {
		return emailSender;
	}
	public void setEmailSender(String emailSender) {
		this.emailSender = emailSender;
	}
	public String getServerUsername() {
		return serverUsername;
	}
	public void setServerUsername(String serverUsername) {
		this.serverUsername = serverUsername;
	}
	public String getServerPassword() {
		return serverPassword;
	}
	public void setServerPassword(String serverPassword) {
		this.serverPassword = serverPassword;
	}
	public String getBreakOption() {
		return breakOption;
	}
	public void setBreakOption(String breakOption) {
		this.breakOption = breakOption;
	}
	public String getOvertimeOption() {
		return overtimeOption;
	}
	public void setOvertimeOption(String overtimeOption) {
		this.overtimeOption = overtimeOption;
	}
	public Double getOvertimeRate() {
		return overtimeRate;
	}
	public void setOvertimeRate(Double overtimeRate) {
		this.overtimeRate = overtimeRate;
	}
	public Integer getEmailExpLmt() {
		return emailExpLmt;
	}
	public void setEmailExpLmt(Integer emailExpLmt) {
		this.emailExpLmt = emailExpLmt;
	}
	public String getEmailSenderPrefix() {
		return emailSenderPrefix;
	}
	public void setEmailSenderPrefix(String emailSenderPrefix) {
		this.emailSenderPrefix = emailSenderPrefix;
	}
	public boolean isSortByRankAndHiredate() {
		return sortByRankAndHiredate;
	}
	public void setSortByRankAndHiredate(boolean sortByRankAndHiredate) {
		this.sortByRankAndHiredate = sortByRankAndHiredate;
	}
	public String getPolicyNotes() {
		return policyNotes;
	}
	public void setPolicyNotes(String policyNotes) {
		this.policyNotes = policyNotes;
	}
	public boolean isShowOffer() {
		return showOffer;
	}
	public void setShowOffer(boolean showOffer) {
		this.showOffer = showOffer;
	}
	public boolean isShowMove() {
		return showMove;
	}
	public void setShowMove(boolean showMove) {
		this.showMove = showMove;
	}
	public boolean isShowNotifiedEmployeeOnPDF() {
		return showNotifiedEmployeeOnPDF;
	}
	public void setShowNotifiedEmployeeOnPDF(boolean showNotifiedEmployeeOnPDF) {
		this.showNotifiedEmployeeOnPDF = showNotifiedEmployeeOnPDF;
	}
	
		
}
