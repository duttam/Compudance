package com.onetouch.model.mapper.tenant;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;

public class TenantRowMapper implements RowMapper<Tenant>{

	@Override
	public Tenant mapRow(ResultSet rs, int row) throws SQLException {
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
		String emailSender = rs.getString("email_sender");
		tenant.setEmailSender(emailSender);
		if(emailSender!=null && emailSender.length()>0){
			String[] emails = emailSender.split("\\@");
			String emailSenderPrefix = emails[0];
			tenant.setEmailSenderPrefix(emailSenderPrefix);
		}
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
		return tenant;
	}

}
