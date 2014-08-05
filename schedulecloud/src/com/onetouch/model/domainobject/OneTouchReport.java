package com.onetouch.model.domainobject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class OneTouchReport {

	public String month;
	public String eventDate;
	private Date reportDate;
	public List<Event> eventList;
	public List<Schedule> scheduleList;
	public Integer empSchTotalMinutes;
	public Integer empSchTotalHours;
	public Integer empSchTotalPay;
	private Position position;
	//private List<DetailedAvailReport> detailedAvailReports;
	private List<DetailedAvailReportByEmployee>  detailedAvailReports;
	public OneTouchReport() {
		
	}
	public OneTouchReport(String month, List<Event> eventList) {
		
		this.month = month;
		this.eventList = eventList;
	}
	
	
	public OneTouchReport(String month) {
		
		this.month = month;
	}
	public OneTouchReport(List<Event> eventList) {
		this.eventList = eventList;
	}
	public Integer getTotalActualHrs(){
		int totalActHrs = 0;
		for(Schedule schedule : scheduleList){
			totalActHrs = totalActHrs+schedule.getWorkingHours();
		}
		
		return totalActHrs;
		
	}
	public Integer getTotalActualMinutes(){
		int totalActMins = 0;
		for(Schedule schedule : scheduleList){
			totalActMins = totalActMins+schedule.getWorkingMinutes();
		}
		return totalActMins;
	}
	public BigDecimal getTotalActualPay(){
		BigDecimal totalActualPay = new BigDecimal(0.0);
		for(Schedule schedule : scheduleList){
			totalActualPay = totalActualPay.add(schedule.getTotalPay());
		}
		
		return totalActualPay;
	}
	public BigDecimal getTotalEmpSchRegPay(){
		BigDecimal totalRegPay = new BigDecimal(0.0);
		for(Schedule schedule : scheduleList){
			totalRegPay = totalRegPay.add(schedule.getRegPay());
		}
		
		return totalRegPay;
	}
	
	public BigDecimal getTotalEmpSchOTPay(){
		BigDecimal totalOTPay = new BigDecimal(0.0);
		for(Schedule schedule : scheduleList){
			totalOTPay = totalOTPay.add(schedule.getOtPay());
		}
		
		return totalOTPay;
	}
	public BigDecimal eventDetailTotalForecastLabor(){
		BigDecimal totalForecastLabor = new BigDecimal(0.0);
		for(Event event : eventList){
			totalForecastLabor = totalForecastLabor.add(event.getTotalForecastPay());
		}
		
		return totalForecastLabor;
	}
	public BigDecimal eventDetailTotalActualLabor(){
		BigDecimal totalActualLabor = new BigDecimal(0.0);
		for(Event event : eventList){
			totalActualLabor = totalActualLabor.add(event.getTotalActualPay());
		}
		
		return totalActualLabor;
	}
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public List<Event> getEventList() {
		return eventList;
	}
	public void setEventList(List<Event> eventList) {
		this.eventList = eventList;
	}
	public List<Schedule> getScheduleList() {
		return scheduleList;
	}
	public void setScheduleList(List<Schedule> scheduleList) {
		this.scheduleList = scheduleList;
	}
	public Integer getEmpSchTotalMinutes() {
		return empSchTotalMinutes;
	}
	public void setEmpSchTotalMinutes(Integer empSchTotalMinutes) {
		this.empSchTotalMinutes = empSchTotalMinutes;
	}
	public Integer getEmpSchTotalHours() {
		return empSchTotalHours;
	}
	public void setEmpSchTotalHours(Integer empSchTotalHours) {
		this.empSchTotalHours = empSchTotalHours;
	}
	public Integer getEmpSchTotalPay() {
		return empSchTotalPay;
	}
	public void setEmpSchTotalPay(Integer empSchTotalPay) {
		this.empSchTotalPay = empSchTotalPay;
	}
	
	public BigDecimal getTotalMonthlyForecastcost(){
		BigDecimal totalMonthlyForecast = new BigDecimal(0);
		for(Event event : eventList){
			totalMonthlyForecast = totalMonthlyForecast.add(event.getTotalForecastLaborCost());
		}
		return totalMonthlyForecast;
	}
	
	public List<DetailedAvailReportByEmployee> getDetailedAvailReports() {
		return detailedAvailReports;
	}
	public void setDetailedAvailReports(
			List<DetailedAvailReportByEmployee> detailedAvailReports) {
		this.detailedAvailReports = detailedAvailReports;
	}
	public BigDecimal getTotalMonthlyActaulcost(){
		BigDecimal totalMonthlyActual = new BigDecimal(0);
		for(Event event : eventList){
			totalMonthlyActual = totalMonthlyActual.add(event.getTotalActualLaborCost());
		}
		return totalMonthlyActual;
	}
	
	public BigDecimal getTotalMonthlyOverUnder(){
		BigDecimal totalMonthlyOverUnder = new BigDecimal(0);
		for(Event event : eventList){
			totalMonthlyOverUnder = totalMonthlyOverUnder.add(event.getOverUnder());
		}
		return totalMonthlyOverUnder;
	}
	public String getEventDate() {
		return eventDate;
	}
	public void setEventDate(String eventDate) {
		this.eventDate = eventDate;
	}
	
	public Date getReportDate() {
		return reportDate;
	}
	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}
	
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Integer getTotalGuestCnt() {
		Integer totalGuestCnt =0;
		
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList)
				totalGuestCnt = totalGuestCnt+event.getGuestCount();
		}
		return totalGuestCnt;
	}
	
	public Integer getTotalReqd() {
		Integer totalReqd = 0;
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList)
				totalReqd = totalReqd+event.getPositionRequiredCount();
		}
		return totalReqd;
	}
	
	public Integer getTotalStaffed() {
		Integer totalStaffed = 0;
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList)
				totalStaffed = totalStaffed+event.getPositionStaffedCount();
		}
		return totalStaffed;
	}
	
	public Integer getTotalPendingNotification(){
		Integer totalPending = 0;
		if(eventList!=null && eventList.size()>0){
			for(Event event : eventList)
				totalPending = totalPending+event.getPendingNotificationCount();
		}
		return totalPending;
	}

	
}
