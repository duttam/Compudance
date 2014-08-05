package com.onetouch.model.mapper.employee;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.EmployeeRate;

public class EmployeeRateRowMapper implements RowMapper<EmployeeRate>{

	@Override
	public EmployeeRate mapRow(ResultSet rs, int row) throws SQLException {
		EmployeeRate employeeRate = new EmployeeRate();
		employeeRate.setId(rs.getInt("id"));
		employeeRate.setEmployeeId(rs.getInt("emp_id"));
		employeeRate.setHourlyRate(rs.getBigDecimal("rate"));
		employeeRate.setOldHourlyRate(rs.getBigDecimal("rate"));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if(rs.getString("start_date")!=null){
				employeeRate.setRateStartDate(format.parse(rs.getString("start_date")));
				employeeRate.setOldRateStartDate(format.parse(rs.getString("start_date")));
			}
			if(rs.getString("end_date")!=null)
				employeeRate.setRateEnddate(format.parse(rs.getString("end_date")));
			
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return employeeRate;
	}

}
