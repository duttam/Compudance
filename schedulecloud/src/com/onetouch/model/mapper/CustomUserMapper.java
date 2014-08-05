package com.onetouch.model.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;


public class CustomUserMapper implements RowMapper<UserDetails> {

	@Override
	public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
		
        String username = rs.getString(1);
        String password = rs.getString(2);
        boolean enabled = rs.getBoolean(3);
        Integer emp_id = rs.getInt("emp_id");
        //Integer tenant_id = rs.getInt("tenant_id");
        //String logoutUrl = rs.getString("logoutUrl");
        Tenant tenant = new Tenant();
		
		tenant.setId(rs.getInt("id"));
		tenant.setCode(rs.getString("code"));
		tenant.setName(rs.getString("name"));
		tenant.setAddress1(rs.getString("address1"));
		tenant.setAddress2(rs.getString("address2"));
		tenant.setCity(rs.getString("city"));
		String statename = rs.getString("state");
		String statecode = rs.getString("statecode");
		tenant.setState(new State(statecode, statename));
		tenant.setZipcode(rs.getString("zipcode"));
		tenant.setCompanyEmail(rs.getString("email"));
		tenant.setBusinessPhone(rs.getString("business_phone"));
		tenant.setFax(rs.getString("fax"));
		tenant.setEmailSender(rs.getString("email_sender"));
		tenant.setLogoUrl(rs.getString("logoUrl"));
		tenant.setLogoutUrl(rs.getString("logoutUrl"));
		tenant.setTheme(rs.getString("theme"));
		if(rs.getBoolean("break_paid"))
			tenant.setBreakOption("paid");
		else
			tenant.setBreakOption("unpaid");
		tenant.setActive(rs.getBoolean("active"));
		tenant.setOvertimeOption(rs.getString("overtime"));
		tenant.setOvertimeRate(rs.getDouble("overtime_rate"));
		tenant.setSortByRankAndHiredate(rs.getBoolean("sortby_rankhiredate"));
		tenant.setPolicyNotes(rs.getString("policyNotes"));
		tenant.setShowOffer(rs.getBoolean("showOffer"));
		tenant.setShowMove(rs.getBoolean("showMove"));
		tenant.setShowNotifiedEmployeeOnPDF(rs.getBoolean("showNotifiedEmployeeOnPDF"));
        return new CustomUser(username, password, enabled, true, true, true, AuthorityUtils.NO_AUTHORITIES,tenant.getId(),emp_id,tenant);
    }
}