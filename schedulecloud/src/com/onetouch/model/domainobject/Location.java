package com.onetouch.model.domainobject;

import java.io.Serializable;
import java.util.List;

public class Location implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private String code;
	private String cacheCode;
	private String address1;
	private String address2;
	private String city;
	private State state=new State("", "");
	private String statecode;
	private String zipcode;
	private String locationType; // location type is fixed or custom
	private String contactName;
	private String contactPhone;
	private String contactCellphone; // optional
	private String contactFax;
	private String contactEmail;
	private String parkingDirection;
	private String timezone;
	private boolean viewGooglemap; // char to boolean mapping, Y/y is true, N/n is false
	private Tenant tenant;
	private Region region;
	private List<Event> allEvents; // attribute to hold all events correspoding to a particular location
	
	public Location(){}
	public Location(Tenant tenant){
		this.tenant = tenant;
	}
	public Location(Integer id) {
	
		this.id = id;
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
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
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
	public String getStatecode() {
		return statecode;
	}
	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getLocationType() {
		return locationType;
	}
	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getContactPhone() {
		return contactPhone;
	}
	public void setContactPhone(String contactPhone) {
		this.contactPhone = contactPhone;
	}
	public String getContactCellphone() {
		return contactCellphone;
	}
	public void setContactCellphone(String contactCellphone) {
		this.contactCellphone = contactCellphone;
	}
	public String getContactFax() {
		return contactFax;
	}
	public void setContactFax(String contactFax) {
		this.contactFax = contactFax;
	}
	public String getContactEmail() {
		return contactEmail;
	}
	public void setContactEmail(String contactEmail) {
		this.contactEmail = contactEmail;
	}
	public String getParkingDirection() {
		return parkingDirection;
	}
	public void setParkingDirection(String parkingDirection) {
		this.parkingDirection = parkingDirection;
	}
	public boolean isViewGooglemap() {
		return viewGooglemap;
	}
	public void setViewGooglemap(boolean viewGooglemap) {
		this.viewGooglemap = viewGooglemap;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	public String getTimezone() {
		return timezone;
	}
	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}
	public String getLocationTitle(){
		if(code!=null && code.length()>0)
			return "["+code+"]";
		
		return "";
				
	}
	
	public Region getRegion() {
		return region;
	}
	public void setRegion(Region region) {
		this.region = region;
	}
	
	public String getCacheCode() {
		return cacheCode;
	}
	public void setCacheCode(String cacheCode) {
		this.cacheCode = cacheCode;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		Location other = (Location) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
