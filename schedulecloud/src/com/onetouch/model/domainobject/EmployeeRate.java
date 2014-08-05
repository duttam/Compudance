package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class EmployeeRate implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private Integer employeeId;
	private BigDecimal hourlyRate = new BigDecimal(0.0);
	private BigDecimal overtimeHourlyrate = new BigDecimal(0.0);
	private BigDecimal oldHourlyRate = new BigDecimal(0.0);
	private Date rateStartDate;
	private Date oldRateStartDate;// if ratestartdate<oldratestartdate then dont allow modification, as events may be scheduled @ that rate
	private Date rateEnddate;
	private Tenant tenant;
	
	private BigDecimal regPay = new BigDecimal(0.0);
	private BigDecimal otPay = new BigDecimal(0.0);
	private BigDecimal dtPay = new BigDecimal(0.0);
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public BigDecimal getHourlyRate() {
		if(hourlyRate!=null)
			return hourlyRate.setScale(2);
		else
			return new BigDecimal(0.0);
	}
	public void setHourlyRate(BigDecimal hourlyRate) {
		this.hourlyRate = hourlyRate;
	}
	public Date getRateStartDate() {
		return rateStartDate;
	}
	public void setRateStartDate(Date rateStartDate) {
		this.rateStartDate = rateStartDate;
	}
	public Date getRateEnddate() {
		return rateEnddate;
	}
	public void setRateEnddate(Date rateEnddate) {
		this.rateEnddate = rateEnddate;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public BigDecimal getOldHourlyRate() {
		return oldHourlyRate;
	}
	public void setOldHourlyRate(BigDecimal oldHourlyRate) {
		this.oldHourlyRate = oldHourlyRate;
	}
	public Date getOldRateStartDate() {
		return oldRateStartDate;
	}
	public void setOldRateStartDate(Date oldRateStartDate) {
		this.oldRateStartDate = oldRateStartDate;
	}
	
	
	public void setRegPay(BigDecimal regPay) {
		this.regPay = regPay;
	}
	
	public void setOtPay(BigDecimal otPay) {
		this.otPay = otPay;
	}
	
	public void setDtPay(BigDecimal dtPay) {
		this.dtPay = dtPay;
	}
	public BigDecimal getRegPay() {
		return regPay;
	}
	public BigDecimal getOtPay() {
		return otPay;
	}
	public BigDecimal getDtPay() {
		return dtPay;
	}
	public BigDecimal getOvertimeHourlyrate() {
		return overtimeHourlyrate;
	}
	public void setOvertimeHourlyrate(BigDecimal overtimeHourlyrate) {
		this.overtimeHourlyrate = overtimeHourlyrate;
	}
	
}
