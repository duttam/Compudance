package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.view.util.DateUtil;

public class Schedule implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Position position;
	
	private Employee employee;
	private Event event; //if subevent the enter in the database
	private Tenant tenant;
	private Integer breakHours = 0;
	private Integer breakMinutes = 0;
	private Integer workingHours;
	private Integer workingMinutes;
	private List<Employee> availableEmployees; // mapped to schedule view
	private boolean active;
	private boolean checked=false;
	private String scheduleDate;
	private SignInOut signInOut;
	private Integer eventPositionId;
	private static Map<Integer,String> scheduleStatusMap = new HashMap<Integer, String>();
	private Integer schedulestatus;
	private Date notifiedtime; // time when first notified
	private Date updatetime; //time when this schedule is accepted/rejected or dropped
	private Date expiretime; // time when this schedule is going to expire
	private Date overrideTime;
	private Date offerAcceptTime;
	private EventPosition eventPosition;
	private Map<Object,Object> emailAttributes;
	
	private String eventMonth;

	private BigDecimal totalCost;
	static{
		scheduleStatusMap.put(1,"Notify");
		scheduleStatusMap.put(2,"Accept");
		scheduleStatusMap.put(3,"Reject");
		scheduleStatusMap.put(4,"Expired");
	}
	public Schedule() {
		super();
	}
	public Schedule(boolean generateId,Integer id, Event event, Tenant tenant){
		if(generateId)
			this.id = id;
		this.event = event;
		this.tenant = tenant;
	}
	public BigDecimal getRegPay() {
		BigDecimal regPay = new BigDecimal(0.0);
		EmployeeRate empRate = employee.getEmpRate();
		if(empRate!=null && empRate.getHourlyRate()!=null){
			regPay = getTotalLaborCost(empRate.getHourlyRate());
		}
		return regPay;
	}
	// more than 8 hours
	public BigDecimal getOtPay() {
		BigDecimal otPay = new BigDecimal(0.0);
		EmployeeRate empRate = employee.getEmpRate();
		int otHour = this.getWorkingHours()-8;
		if(tenant.getOvertimeOption().equalsIgnoreCase("dayOT") && otHour>=0){
			
			BigDecimal overtimeRatefactor = new BigDecimal(tenant.getOvertimeRate());
			BigDecimal overtimeRate = empRate.getHourlyRate().multiply(overtimeRatefactor);
			if(otHour>0){
				
				int otMin = this.getWorkingMinutes();
				otPay = overtimeRate.multiply(new BigDecimal(otHour)).add((overtimeRate.divide(new BigDecimal(60),2).multiply(new BigDecimal(otMin))));
			}else{
				int otMin = this.getWorkingMinutes();
				if(otMin>0){
					otPay = overtimeRate.divide(new BigDecimal(60),2).multiply(new BigDecimal(otMin));
				}
			}
		}
		return otPay;
	}
	// more than 12 hours
	public BigDecimal getDtPay() {
		BigDecimal dtPay = new BigDecimal(0.0);
		return dtPay;
	}
	public BigDecimal getTotalPay(){
		BigDecimal totalPay = new BigDecimal(0.0);
		totalPay = this.getRegPay();//.add(this.getOtPay().add(this.getDtPay()));
		return totalPay;
	}
	public BigDecimal getTotalLaborCost(BigDecimal hourlyRate) {
		BigDecimal totalLaborCost = new BigDecimal(0.0);
		workingHours = workingHours==0?0:workingHours;
		workingMinutes = workingMinutes==0?0:workingMinutes;
		BigDecimal shiftHrs = new BigDecimal(workingHours);
		BigDecimal shiftMins = new BigDecimal(workingMinutes);
		
		breakHours = breakHours==0?0:breakHours;
		breakMinutes = breakMinutes==0?0:breakMinutes;
		BigDecimal breakHrs = new BigDecimal(breakHours);
		BigDecimal breakMins = new BigDecimal(breakMinutes);
		if (this.totalCost==null) {
			this.totalCost = hourlyRate.multiply(shiftHrs).add((hourlyRate.divide(new BigDecimal(60),2).multiply(shiftMins)));
			totalLaborCost = hourlyRate.multiply(breakHrs).add((hourlyRate.divide(new BigDecimal(60),2).multiply(breakMins)));
			
        }
		
		return this.totalCost.subtract(totalLaborCost);
		
		
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	
	public Employee getEmployee() {
		return employee;
	}
	public void setEmployee(Employee employee) {
		this.employee = employee;
	}
	public Event getEvent() {
		return event;
	}
	public void setEvent(Event event) {
		this.event = event;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public Integer getWorkingHours() {
		if(tenant!=null && tenant.getBreakOption().equalsIgnoreCase("paid"))
			return workingHours;
		else
			return (workingHours-breakHours);
		
	}
	public void setWorkingHours(Integer workingHours) {
		this.workingHours = workingHours;
	}
	
	public List<Employee> getAvailableEmployees() {
		return availableEmployees;
	}
	public void setAvailableEmployees(List<Employee> availableEmployees) {
		this.availableEmployees = availableEmployees;
	}
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getScheduleDate() {
		return scheduleDate;
	}
	public void setScheduleDate(String scheduleDate) {
		this.scheduleDate = scheduleDate;
	}
	public SignInOut getSignInOut() {
		return signInOut;
	}
	public void setSignInOut(SignInOut signInOut) {
		this.signInOut = signInOut;
	}
	public Integer getEventPositionId() {
		return eventPositionId;
	}
	public void setEventPositionId(Integer eventPositionId) {
		this.eventPositionId = eventPositionId;
	}
	public EventPosition getEventPosition() {
		return eventPosition;
	}
	public void setEventPosition(EventPosition eventPosition) {
		this.eventPosition = eventPosition;
	}
	public Map<Object, Object> getEmailAttributes() {
		return emailAttributes;
	}
	public void setEmailAttributes(Map<Object, Object> emailAttributes) {
		this.emailAttributes = emailAttributes;
	}
	public Integer getWorkingMinutes() {
		if(tenant!=null && tenant.getBreakOption().equalsIgnoreCase("paid"))
			return workingMinutes;
		else
			return (workingMinutes - breakMinutes);
		
	}
	public void setWorkingMinutes(Integer workingMinutes) {
		this.workingMinutes = workingMinutes;
	}
	public String getEventMonth() {
		return eventMonth;
	}
	public void setEventMonth(String eventMonth) {
		this.eventMonth = eventMonth;
	}
	public Integer getBreakHours() {
		return breakHours;
	}
	public void setBreakHours(Integer breakHours) {
		this.breakHours = breakHours;
	}
	public Integer getBreakMinutes() {
		return breakMinutes;
	}
	public void setBreakMinutes(Integer breakMinutes) {
		this.breakMinutes = breakMinutes;
	}
	public Integer getSchedulestatus() {
		return schedulestatus;
	}
	public void setSchedulestatus(Integer schedulestatus) {
		this.schedulestatus = schedulestatus;
	}
	public Date getNotifiedtime() {
		return notifiedtime;
	}
	public void setNotifiedtime(Date notifiedtime) {
		this.notifiedtime = notifiedtime;
	}
	public Date getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}
	public Date getExpiretime() {
		return expiretime;
	}
	public void setExpiretime(Date expiretime) {
		this.expiretime = expiretime;
	}
	public static String getScheduleStatusTitle(Integer key){
		return scheduleStatusMap.get(key);
	}
	
	public Date getOverrideTime() {
		return overrideTime;
	}
	public void setOverrideTime(Date overrideTime) {
		this.overrideTime = overrideTime;
	}
	
	public Date getOfferAcceptTime() {
		return offerAcceptTime;
	}
	public void setOfferAcceptTime(Date offerAcceptTime) {
		this.offerAcceptTime = offerAcceptTime;
	}
	public boolean isScheduleExpired(){
		if(this.getSchedulestatus()!=null && this.getExpiretime()!=null)
			if(this.getSchedulestatus()==1 && DateUtil.compareDateAndTime(this.getExpiretime(), new Date())<=0)
				return true;
		
		return false;
	}
	public boolean isScheduleUpdated(){
		if(this!=null && this.getExpiretime()!=null && this.getUpdatetime()!=null)
			if(DateUtil.compareDateAndTime(this.getUpdatetime(), this.getExpiretime())<0)
				return true;
		return false;
	}
	public String getScheduleStatusTitle(){
		if(this.getSchedulestatus()!=null)
			return this.getScheduleStatusTitle(this.getSchedulestatus());
		else
			return "";
	}
}
