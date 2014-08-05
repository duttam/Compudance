package com.onetouch.model.mapper.position;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.Position;

    public class PositionRowMapper implements RowMapper<Position> {

	@Override
	public Position mapRow(ResultSet rs, int row) throws SQLException {
		Position position = new Position();
		
		position.setId(rs.getInt("id"));
		position.setName(rs.getString("name"));
		position.setDescription(rs.getString("description"));
		position.setNotes(rs.getString("notes"));
		position.setTenant(new Tenant(rs.getInt("company_id")));
		position.setViewReports(rs.getBoolean("view_report"));
		return position;
	}

    }