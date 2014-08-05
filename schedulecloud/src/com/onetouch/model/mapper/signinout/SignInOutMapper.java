package com.onetouch.model.mapper.signinout;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;

import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.TimeOffRequest;

    public class SignInOutMapper implements RowMapper<SignInOut> {

	@Override
	public SignInOut mapRow(ResultSet rs, int row) throws SQLException {
		SignInOut signInOut = new SignInOut();		
		SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
		try {
			
			signInOut.setStartTime(format.parse(rs.getString("starttime")));
			signInOut.setEndTime(format.parse(rs.getString("endtime")));
			
			/*try {
				signInOut.setBreakStartTime(format.parse(rs.getString("breakstart")));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				signInOut.setBreakEndTime(format.parse(rs.getString("breakend")));
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return signInOut;
	}

    }