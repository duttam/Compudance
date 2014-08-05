package com.onetouch.model.domainobject;



/**
 * this is a redundant object for phone number display in update family page
 */
public class PhoneNumber {
    
    private String areaCode;
    private String number;
    
    public PhoneNumber(String areaCode, String number) {
		
		this.areaCode = areaCode;
		this.number = number;
	}

	public String getAreaCode(){
        return areaCode;
    }
    
    public void setAreaCode(String areaCode){
        this.areaCode = areaCode;
    }
        
    public String getNumber(){
        return number;
    }
    
    public void setNumber(String number){
        this.number = number;
    }

    public String toString() {
        return areaCode + " " + number;
    }
    
        
    
}
