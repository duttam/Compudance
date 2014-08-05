package com.onetouch.model.mapper.tenant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantSetting;

public class TenantWithSettingExtractor implements ResultSetExtractor<List<Tenant>>{

	@Override
	public List<Tenant> extractData(ResultSet rs) throws SQLException,
			DataAccessException {
		
		Map<Integer,Tenant> tenantMap = new HashMap<Integer, Tenant>();
		Tenant tenant = null;
		while(rs.next()){
			Integer id = rs.getInt("id");
			tenant = tenantMap.get(id);
			if(tenant==null){
				tenant = new Tenant();
				tenant.setId(id);
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
				tenantMap.put(id,tenant);
			}
			Integer tenantSettingId = rs.getInt("tenant_setting_id");
			if(tenantSettingId>0){
				TenantSetting tenantSetting = new TenantSetting();
				tenantSetting.setId(tenantSettingId);
				tenantSetting.setLogoUrl(rs.getString("logoUrl"));
				tenantSetting.setLogoutUrl(rs.getString("logoutUrl"));
				tenantSetting.setPolicyFileUrl(rs.getString("policyFileUrl"));
				tenantSetting.setTheme(rs.getString("theme"));
				tenantSetting.setTutorialUrl(rs.getString("tutorialUrl"));
				//tenant.setTenantSetting(tenantSetting);
			}
		}
		return new ArrayList<Tenant>(tenantMap.values());
	}

}
