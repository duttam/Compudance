package com.onetouch.model.mapper.report;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.DetailedAvailReport;
import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;

import com.onetouch.model.domainobject.Tenant;

public class DetailAvailabilityRowMapper implements RowMapper<DetailedAvailReportByEmployee>{
	
		@Override
	public DetailedAvailReportByEmployee mapRow(ResultSet rs, int row) throws SQLException {
			
			DetailedAvailReportByEmployee availabilityRec = new DetailedAvailReportByEmployee();

			SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");


			Integer employeeId = rs.getInt("empId");
			availabilityRec.setEmployeeId(employeeId.toString());
			availabilityRec.setEmployeeFirstname(rs.getString("firstname"));
			availabilityRec.setEmployeeLastname(rs.getString("lastname"));
			availabilityRec.setStatus(rs.getString("status"));
			//availabilityRec.setRating(rs.getInt("rating"));
			availabilityRec.setEventId(rs.getInt("event_id"));
			availabilityRec.setEventName(rs.getString("event_name"));
			availabilityRec.setPosition(rs.getString("position_name"));
			availabilityRec.setLocationCode(rs.getString("loc_code"));
			availabilityRec.setLocationName(rs.getString("loc_name"));
			try {
				if(rs.getString("start_time")!=null)
					availabilityRec.setStartTime(timeformat.parse(rs.getString("start_time")));
				if(rs.getString("end_time")!=null)
					availabilityRec.setEndTime(timeformat.parse(rs.getString("end_time")));
				if(rs.getString("starttime")!=null)
					availabilityRec.setAvailStartTime(timeformat.parse(rs.getString("starttime")));
				if(rs.getString("endtime")!=null)
					availabilityRec.setAvailEndTime(timeformat.parse(rs.getString("endtime")));
				

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
                
           return availabilityRec;     
	}
}
