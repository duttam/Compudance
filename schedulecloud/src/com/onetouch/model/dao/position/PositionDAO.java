package com.onetouch.model.dao.position;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.mapper.event.EventRowMapper;
import com.onetouch.model.mapper.position.PositionRowMapper;


@Repository
@Qualifier("positionDAO")
public class PositionDAO extends BaseDAO{

	private final String createPositionSql = "insert into position (name,description,notes,company_id,view_report) values (?,?,?,?,?)";
	private final String updatePositionSql = "update position set name=?,description=?,notes=?, view_report=? where id=? and company_id=?";
	private final String selectPositionNumberSql = "SELECT p.id,p.description,p.notes,p.event_type,p.name,ep.number FROM position p, event_position ep where p.id = ep.position_id and ep.event_id = ?";
	
	
	public List<Position> findAll(final Integer companyId){
		return getJdbcTemplate().query("select * from position where position.company_id = ? order by displayOrder asc ",new Object[]{companyId}, new PositionRowMapper());
	}
	
	public Integer save(final Position position){
		getJdbcTemplate().update(createPositionSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, position.getName());
				ps.setString(2,position.getDescription());
				ps.setString(3,position.getNotes());
				ps.setInt(4,position.getTenant().getId());
				ps.setBoolean(5, position.isViewReports());
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void update(final Position position){
		getJdbcTemplate().update(updatePositionSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, position.getName());
				ps.setString(2,position.getDescription());
				ps.setString(3,position.getNotes());
				ps.setBoolean(4,position.isViewReports());
				ps.setInt(5,position.getId());
				ps.setInt(6,position.getTenant().getId());
				
			}
		});
	}

	public Position find(final Integer id, final Integer companyId) {
		List<Position> positions = getJdbcTemplate().query("select * from position where position.id = ? and position.company_id = ? ",new Object[]{id,companyId}, new PositionRowMapper());
		
		return (positions.size()>0) ? positions.get(0) : null;
	}

	public List<Position> findByEvent(final Event selectedEvent) {
		
		return getJdbcTemplate().query(selectPositionNumberSql,new Object[]{selectedEvent.getId()}, new RowMapper<Position>() {

			@Override
			public Position mapRow(ResultSet rs, int row) throws SQLException {
				Position position = new Position();
				
				position.setId(rs.getInt("p.id"));
				position.setName(rs.getString("p.name"));
				position.setDescription(rs.getString("p.description"));
				position.setNotes(rs.getString("p.notes"));
				position.setReqdNumber(rs.getInt("ep.number"));
				
				return position;
			}
			
		}
		);
	}

	public Position findById(Integer pstnId) {
		List<Position> positions = getJdbcTemplate().query("select * from position where position.id = ? ",new Object[]{pstnId}, new PositionRowMapper());
		
		return positions.get(0);
	}

	public List<Position> findPositionsByEvent(Integer eventId) {
		
		return getJdbcTemplate().query("select distinct(p.id), p.name as name, p.notes as notes, p.description as description, p.company_id as company_id,p.view_report from shift_position sp,position p where sp.position_id = p.id and sp.event_id = ?",new Object[]{eventId}, new PositionRowMapper());
	}
	public Integer findReqdEmployeeNumberByPosition(final Integer eventId,final Integer shiftId,final Integer positionId){
		List<Integer> reqdEmpNumbers = getJdbcTemplate().queryForList("select number from shift_position where event_id=? and shift_id=? and position_id=?",Integer.class,new Object[]{eventId,shiftId,positionId});
		if(reqdEmpNumbers!=null && reqdEmpNumbers.size()==0)
			return 0;
		else
			return reqdEmpNumbers.get(0);
	}

	public void updateDisplayOrder(final List<Position> positionList) {
		getJdbcTemplate().batchUpdate("update position set position.displayOrder = ? where position.id = ?", new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Position p = positionList.get(i);
				ps.setInt(1,p.getDisplayOrder());
				ps.setInt(2, p.getId());
			}
			
			@Override
			public int getBatchSize() {
				return positionList.size();
			}
		});
	}
	public List<Position> findByEmployee(final Integer employeeId ,final Integer companyId){
		return getJdbcTemplate().query("SELECT pos.id,pos.name,pos.description,pos.notes,pos.company_id,pos.view_report FROM employee_position empos, Position pos where empos.position_id = pos.id and empos.employee_id = ? and empos.company_id = ? ",new Object[]{employeeId,companyId}, new PositionRowMapper());
	}
	
	public void saveBatchPositions(final List<Position> positions){
		getJdbcTemplate().batchUpdate(createPositionSql, new BatchPreparedStatementSetter() {
			 
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Position position = positions.get(i);
				ps.setString(1, position.getName());
				ps.setString(2,position.getDescription());
				ps.setString(3,position.getNotes());
				ps.setInt(4,position.getTenant().getId());
			}
		 
			@Override
			public int getBatchSize() {
				return positions.size();
			}
		  });
	}

	public Integer find(String positionName, Integer tenantId) {
		// TODO Auto-generated method stub
		return getJdbcTemplate().queryForObject("select max(id) from position where name = ? and company_id = ?", new Object[]{positionName,tenantId},Integer.class);
	}
}
