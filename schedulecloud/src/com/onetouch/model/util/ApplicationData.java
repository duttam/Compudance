package com.onetouch.model.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.State;


//@ManagedBean(name="applicationData",eager=true)
//@ApplicationScoped
@Repository
@Qualifier("applicationData") 
public class ApplicationData extends BaseDAO{

	 
	 private @Value("${cityxml.directory.location}") String city_xmllocation;
	 private @Value("${policy.directory.location}") String policy_dir;
	 private @Value("${image.directory.location}") String image_dir;
	 private @Value("${image.url}") String image_url;
	 private @Value("${server.url}") String server_url;
	 private @Value("${customLocation.code}") String customLocationCode;
	 
	 private DualHashBidiMap states;
	 
	 
	 @PostConstruct
	 public void init(){
		 states = new DualHashBidiMap();
		 List<State> stateList = (List<State>)getJdbcTemplate().query("select * from states",new RowMapper<State>() {
	            public State mapRow(ResultSet rs, int rowNum) throws SQLException {
	            	State state = new State();
	        		
	        		state.setStateCode(rs.getString("StateCode"));
	        		state.setStateName(rs.getString("StateName"));
	        		
	        		return state;
	            	}
	            });
			
		 for(State state: stateList){
             states.put(state.getStateName(),state.getStateCode());
		 }
		 
		 
		 
	 }
	public String getCity_xmllocation() {
		return city_xmllocation;
	}
	public String getPolicy_dir() {
		return policy_dir;
	}
	public String getImage_dir() {
		return image_dir;
	}
	public DualHashBidiMap getStates() {
		return states;
	}

	public String getStateName(String stateCode){
        return (String)states.inverseBidiMap().get(stateCode);
	}


	public String getStateCode(String stateName){
	        return (String)states.get(stateName);
	}
	public String getImage_url() {
		return image_url;
	}
	

	/*public PhoneNumber createPhoneNumber(String phoneNumber){
    	Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
        Matcher matcher = pattern.matcher(phoneNumber);
        String areaCode="",number1="",number2="";
        if (matcher.matches()) {
        	areaCode = phoneNumber.substring(0, 3);
        	number1   = phoneNumber.substring(4,7);
        	number2   = phoneNumber.substring(8,phoneNumber.length());
        }
        else
        {
	        areaCode = phoneNumber.substring(0, 3);
	        number1   = phoneNumber.substring(3,phoneNumber.length());
	        
        }
        return new PhoneNumber(areaCode, number1+number2);
    }*/
	
	public String getServer_url() {
		return server_url;
	}
	public String getCustomLocationCode() {
		return customLocationCode;
	}
	public void setCustomLocationCode(String customLocationCode) {
		this.customLocationCode = customLocationCode;
	}
	public void setServer_url(String server_url) {
		this.server_url = server_url;
	}
	public static String getPhoneNumber(String phoneNumber){
            if (phoneNumber == null) {
                return null;
            }
            int len = 0;
            if ((phoneNumber != null) && (phoneNumber.trim().length() == 0)) {
                return "";
            }
            len = phoneNumber.trim().length();
            Pattern pattern = Pattern.compile("\\d{3}-\\d{3}-\\d{4}");
            Matcher matcher = pattern.matcher(phoneNumber);
            String areaCode="",number1="",number2="";
            if (matcher.matches() && (len >=8)) {
                    areaCode = phoneNumber.substring(0, 3);
                    number1   = phoneNumber.substring(4,7);
                    number2   = phoneNumber.substring(8,phoneNumber.length());
            }
            else if (len > 4) {
                    areaCode = phoneNumber.substring(0, 3);
                    number1   = phoneNumber.substring(3,phoneNumber.length());

            } else {
                return phoneNumber;
            }
            return areaCode+number1+number2;
    }
		
}
