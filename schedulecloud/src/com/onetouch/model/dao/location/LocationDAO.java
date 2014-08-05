package com.onetouch.model.dao.location;





import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.location.LocationRowMapper;


@Repository
@Qualifier("locationDAO")
public class LocationDAO extends BaseDAO{

	private final String createLocationSql = "insert into location(name,address1,address2,city,state,statecode,zipcode,location_type,contact_name,primary_phone,alternate_phone,fax,email,parking_direction,timezone,company_id,code,region_id) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String updateLocationSql = "update location set name=?,address1=?,address2=?,city=?,state=?,statecode=?,zipcode=?,location_type=?,contact_name=?,primary_phone=?,alternate_phone=?,fax=?,email=?,parking_direction=?,timezone=?,code=? where id=? and company_id=? and region_id = ?";
	public List<Location> findAll(final Integer companyId,final String locationType,Integer regionId, CustomUser customUser){
		if(locationType.equalsIgnoreCase("All")){
			if(customUser.isSalesPerson())
				return getJdbcTemplate().query("select temp.* from( select l.* from salesperson_loc sl,location l where sl.loc_id = l.id and sl.emp_id = ? and l.region_id = ? and l.company_id = ? union SELECT * FROM location l1 where l1.company_id = ? and l1.region_id = ? and l1.location_type = 'custom' )  temp order by temp.name asc ",new Object[]{customUser.getEmp_id(), regionId,companyId,companyId, regionId}, new LocationRowMapper());
			else
				return getJdbcTemplate().query("select * from location where location.company_id = ? and region_id = ? order by name asc",new Object[]{companyId,regionId}, new LocationRowMapper());
		}
		else{
			if(customUser.isSalesPerson()){
				if(locationType.equalsIgnoreCase("standard"))
					return getJdbcTemplate().query("select l.* from salesperson_loc sl,location l where sl.loc_id = l.id and sl.emp_id = ? and l.region_id = ? and l.company_id = ? and l.location_type = ? order by name asc",new Object[]{customUser.getEmp_id(),regionId, companyId,locationType}, new LocationRowMapper());
				else
					return getJdbcTemplate().query("select l.* from location l where l.region_id = ? and l.company_id = ? and l.location_type = ? order by name asc",new Object[]{regionId, companyId,locationType}, new LocationRowMapper());
			}
			else
				return getJdbcTemplate().query("select * from location where location.company_id = ? and location.location_type = ? and region_id = ? order by name asc",new Object[]{companyId,locationType,regionId}, new LocationRowMapper());
		}
	}
	
	public List<Region> findAllRegions(final Integer companyId){
		
		return getJdbcTemplate().query("select * from region where company_id = ? ",new Object[]{companyId}, new RowMapper<Region>() {

			@Override
			public Region mapRow(ResultSet rs, int row) throws SQLException {
				Region region = new Region(new Tenant(companyId));
				region.setId(rs.getInt("id"));
				region.setName(rs.getString("name"));
				region.setDescription(rs.getString("description"));
				return region;
			}
		});
		
	}
	public Integer save(final Location location){
		getJdbcTemplate().update(createLocationSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, location.getName());
				ps.setString(2,location.getAddress1());
				ps.setString(3,location.getAddress2());
				ps.setString(4,location.getCity());
				ps.setString(5,location.getState().getStateName());
				ps.setString(6,location.getState().getStateCode());
				ps.setString(7,location.getZipcode());
				ps.setString(8,location.getLocationType());
				ps.setString(9,location.getContactName());
				ps.setString(10,location.getContactPhone());
				ps.setString(11,location.getContactCellphone());
				ps.setString(12, location.getContactFax());
				ps.setString(13,location.getContactEmail());
				ps.setString(14,location.getParkingDirection());
				ps.setString(15,location.getTimezone());
				ps.setInt(16,location.getTenant().getId());
				ps.setString(17, location.getCode());
				ps.setInt(18, location.getRegion().getId());
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public void update(final Location location){
		getJdbcTemplate().update(updateLocationSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, location.getName());
				ps.setString(2,location.getAddress1());
				ps.setString(3,location.getAddress2());
				ps.setString(4,location.getCity());
				ps.setString(5,location.getState().getStateName());
				ps.setString(6,location.getState().getStateCode());
				ps.setString(7,location.getZipcode());
				ps.setString(8,location.getLocationType());
				ps.setString(9,location.getContactName());
				ps.setString(10,location.getContactPhone());
				ps.setString(11,location.getContactCellphone());
				ps.setString(12, location.getContactFax());
				ps.setString(13,location.getContactEmail());
				ps.setString(14,location.getParkingDirection());
				ps.setString(15,location.getTimezone());
				ps.setString(16,location.getCode());
				ps.setInt(17,location.getId());
				ps.setInt(18,location.getTenant().getId());
				ps.setInt(19,location.getRegion().getId());
			}
		});
	}

	public Location findById(Integer id) {
		
		List<Location> locations = getJdbcTemplate().query("select * from location where location.id = ? ",new Object[]{id}, new LocationRowMapper());
		return (locations!=null)?locations.get(0):null;
	}
	
	public Integer saveRegion(final Region region){
		getJdbcTemplate().update("insert into region(name,description,company_id) values (?,?,?)", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				
				ps.setString(1,region.getName());
				ps.setString(2,region.getDescription());
				ps.setInt(3,region.getTenant().getId());
			}
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}

	
	public void deleteById(Integer locationId, Integer regionId, Integer companyId) {
		getJdbcTemplate().update("delete from location where id = ? and region_id = ? and company_id = ?",new Object[]{locationId,regionId,companyId})	;
	}
}
