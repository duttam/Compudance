package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.util.Date;

public class EmailStatus implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String fromEmail;
	private String toEmail;
	private String subject;
	private String operation;
	private Date createTime;
	private Date updateTime;
	private String content;
	private Integer scheduleId;
	private Integer employeeId;
	private Integer regionId;
	private Integer companyId;
	private String status;
	private Integer noRetry;
	
	public EmailStatus(String fromEmail, String toEmail,String subject, String operation,
			String content,Date createTime, Integer regionId, Integer companyId,Integer employeeId,String status) {
		
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.subject = subject;
		this.operation = operation;
		this.createTime = createTime;
		this.content = content;
		this.regionId = regionId;
		this.companyId = companyId;
		this.employeeId = employeeId;
		this.status = status;
	}
	
	public EmailStatus(String fromEmail, String toEmail, String subject,
			String operation,  String content, Date createTime,
			Integer scheduleId, Integer employeeId, Integer regionId,
			Integer companyId, String status) {
		
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.subject = subject;
		this.operation = operation;
		this.content = content;
		this.createTime = createTime;
		this.scheduleId = scheduleId;
		this.employeeId = employeeId;
		this.regionId = regionId;
		this.companyId = companyId;
		this.status = status;
	}

	public EmailStatus(Integer id, String status) {
		
		this.id = id;
		this.status = status;
	}

	public EmailStatus() {
		// TODO Auto-generated constructor stub
	}

	public EmailStatus(String fromEmail, String toEmail, String subject,String operation, String content, Date createTime, Integer regionId,Integer companyId, String status) {
		this.fromEmail = fromEmail;
		this.toEmail = toEmail;
		this.subject = subject;
		this.operation = operation;
		this.content = content;
		this.createTime = createTime;
		this.regionId = regionId;
		this.companyId = companyId;
		this.status = status;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getRegionId() {
		return regionId;
	}
	public void setRegionId(Integer regionId) {
		this.regionId = regionId;
	}
	public Integer getCompanyId() {
		return companyId;
	}
	public void setCompanyId(Integer companyId) {
		this.companyId = companyId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public Integer getScheduleId() {
		return scheduleId;
	}
	public void setScheduleId(Integer scheduleId) {
		this.scheduleId = scheduleId;
	}
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getNoRetry() {
		return noRetry;
	}
	public void setNoRetry(Integer noRetry) {
		this.noRetry = noRetry;
	}
	
	

}
