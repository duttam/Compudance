package com.onetouch.model.mapper.location;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Location;


public class LocationRowMapper implements RowMapper<Location> {

	@Override
	public Location mapRow(ResultSet rs, int row) throws SQLException {
		
		Location location = new Location();
		
		location.setId(rs.getInt("id"));
		location.setName(rs.getString("name"));
		location.setCode(rs.getString("code"));
		location.setCacheCode(rs.getString("code"));
		location.setAddress1(rs.getString("address1"));
		location.setAddress2(rs.getString("address2"));
		location.setCity(rs.getString("city"));
		State state = new State();
		state.setStateName(rs.getString("state"));
		state.setStateCode(rs.getString("statecode"));
		location.setState(state);
		location.setStatecode(rs.getString("statecode"));
		location.setZipcode(rs.getString("zipcode"));
		location.setLocationType(rs.getString("location_type"));
		location.setContactName(rs.getString("contact_name"));
		location.setContactPhone(rs.getString("primary_phone"));
		location.setContactCellphone(rs.getString("alternate_phone"));
		location.setContactFax(rs.getString("fax"));
		location.setContactEmail(rs.getString("email"));
		location.setParkingDirection(rs.getString("parking_direction"));
		location.setTimezone(rs.getString("timezone"));
		location.setTenant(new Tenant(rs.getInt("company_id"))); // just set the company id, if required retrieve the additional attributes from cache
		location.setRegion(new Region(rs.getInt("region_id")));
		return location;
	}

	}