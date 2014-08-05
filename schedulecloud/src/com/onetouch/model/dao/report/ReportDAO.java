package com.onetouch.model.dao.report;

import java.util.List;
import java.util.Date;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.DetailedAvailReport;

import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.report.DetailAvailabilityRowMapper;
import com.onetouch.model.mapper.report.DetailedAvailabilityExtractor;
import com.onetouch.view.util.DateUtil;

@Repository
@Qualifier("reportDAO")
public class ReportDAO extends BaseDAO {

    private final String  detailedAvailabilityByEmployeeDateRange =
    " SELECT  sch.employee_id, emp.firstname, emp.lastname, emp.rating, ss.status , evtpos.event_id, evt.name event_name, " +
    " pos.name position_name, loc.code loc_code, loc.name loc_name, evtpos.start_date, " +
    " evtpos.start_time, evtpos.end_time " +

    " FROM schedule_cloud.schedule sch, " +
        " event_position evtpos, " +
        " employee emp, " +
        " event evt, " +
        " location loc, " +
	" position pos, " +
    " schedulestatus ss " +
        
        " where sch.event_pos_id = evtpos.id  " +
        "  and evtpos.position_id = ?" +
        "  and sch.employee_id = emp.id " +
	"  and evtpos.event_id = evt.id " +
        "  and evt.location_id = loc.id " +
        "  and evtpos.position_id = pos.id " +
        "  and sch.status = ss.id " +
	" and (event_startdate BETWEEN DATE(?) and DATE(?)) " +
            
    " order by evtpos.company_id, evtpos.region_id, evtpos.start_date ";
    
    private final String query = "select * from ( SELECT  sch.employee_id, emp.firstname, emp.lastname, emp.rating, ss.status , evtpos.event_id, evt.name event_name, pos.name position_name, loc.code loc_code, loc.name loc_name, evtpos.start_date, evtpos.start_time, evtpos.end_time FROM schedule_cloud.schedule sch, event_position evtpos,  employee emp,  event evt,  location loc, 	position pos,  schedulestatus ss  where sch.event_pos_id = evtpos.id  and evtpos.position_id = ?  and sch.employee_id = emp.id and evtpos.event_id = evt.id  and evt.location_id = loc.id  and evtpos.position_id = pos.id  and sch.status = ss.id and (event_startdate BETWEEN DATE(?) and DATE(?)) order by evtpos.company_id, evtpos.region_id, evtpos.start_date ) as temp, availability avl where avl.employee_id = temp.employee_id and avl.avail_date = temp.start_date";
        /**/
        public List<DetailedAvailReport> findAvailabilityByPositionAndDate(final Position position, final Date startDate, final Date endDate, final Region region){
            //System.out.println(" date to Str =" + DateUtil.getDateAsStr(startDate)  +  " " + position.getId());
	    return getJdbcTemplate().query(query,new Object[]{position.getId(), DateUtil.getDateAsStr(startDate), DateUtil.getDateAsStr(endDate)}, new DetailedAvailabilityExtractor());
            //return getJdbcTemplate().query(detailedAvailabilityByEmployeeDateRange,new Object[]{}, new DetailedAvailabilityExtractor());
        }
		public List<DetailedAvailReportByEmployee> findAvailabilityByPositionAndDate(
				Position selectedPosition, Date reportDate,
				Region selectedRegion, Tenant tenant) {
			if(selectedRegion==null){
				return getJdbcTemplate().query("select temp1.avlId, temp1.empId, date_format(temp1.starttime,'%l:%i %p') as starttime,date_format(temp1.endtime,'%l:%i %p') as endtime, " +
						"temp1.avail_date,temp1.firstname,temp1.lastname, temp2.SchId, temp2.employee_id, temp2.status , temp2.event_id, temp2.event_name, temp2.position_name, temp2.loc_code, " +
						"temp2.loc_name, temp2.start_date, date_format(temp2.start_time,'%l:%i %p') as start_time, date_format(temp2.end_time,'%l:%i %p') as end_time from " +
						
						"(select distinct avl.id as avlId, e.id as empId, avl.starttime, avl.endtime, avl.avail_date, e.firstname as firstname, e.lastname as lastname from employee e, employee_position ep, availability avl where e.id = ep.employee_id and ep.position_id = ? and avl.employee_id = e.id and e.company_id = ? and avl.avail_date = DATE(?)) " +
						"as temp1 " +
						"left join (SELECT  sch.id as SchId, sch.employee_id, ss.status , evtpos.event_id, evt.name event_name, pos.name position_name, loc.code loc_code, loc.name loc_name, evtpos.start_date, evtpos.start_time, evtpos.end_time FROM schedule_cloud.schedule sch, event_position evtpos,  employee emp,  event evt,  location loc, 	position pos,  schedulestatus ss where sch.event_pos_id = evtpos.id  and evtpos.position_id = ?  and sch.employee_id = emp.id and evtpos.event_id = evt.id  and evt.location_id = loc.id  and evtpos.position_id = pos.id  and sch.status = ss.id and event_startdate = DATE(?) and evtpos.company_id = ? ) " +
						"as temp2 on temp1.empId = temp2.employee_id order by lastname,firstname",
						new Object[]{selectedPosition.getId(),tenant.getId(), DateUtil.getDateAsStr(reportDate),selectedPosition.getId(), DateUtil.getDateAsStr(reportDate),tenant.getId()}, new DetailAvailabilityRowMapper());
			}
			
			else{
				return getJdbcTemplate().query("select temp1.avlId, temp1.empId, date_format(temp1.starttime,'%l:%i %p') as starttime,date_format(temp1.endtime,'%l:%i %p') as endtime, " +
						"temp1.avail_date,temp1.firstname,temp1.lastname, temp2.SchId, temp2.employee_id, temp2.status , temp2.event_id, temp2.event_name, temp2.position_name, temp2.loc_code, " +
						"temp2.loc_name, temp2.start_date, date_format(temp2.start_time,'%l:%i %p') as start_time, date_format(temp2.end_time,'%l:%i %p') as end_time from " +
						
						"(select distinct avl.id as avlId, e.id as empId, avl.starttime, avl.endtime, avl.avail_date, e.firstname as firstname, e.lastname as lastname, e.rating as rating " +
						"from employee e, employee_position ep, availability avl " +
						"where e.id = ep.employee_id and ep.position_id = ? and avl.employee_id = e.id and e.company_id = ? and avl.avail_date = DATE(?) and e.region_id = ? and e.status='active' ) " +
						"as temp1 " +
						"left join (SELECT  sch.id as SchId, sch.employee_id, ss.status , evtpos.event_id, evt.name event_name, pos.name position_name, loc.code loc_code, loc.name loc_name, evtpos.start_date, evtpos.start_time, evtpos.end_time  " +
						"FROM schedule_cloud.schedule sch, event_position evtpos,  employee emp,  event evt,  location loc, 	position pos,  schedulestatus ss  " +
						"where sch.event_pos_id = evtpos.id  and evtpos.position_id = ?  and sch.employee_id = emp.id and evtpos.event_id = evt.id  and evt.location_id = loc.id  " +
						"and evtpos.position_id = pos.id  and sch.status = ss.id  and event_startdate = DATE(?) and evtpos.company_id = ? and evtpos.region_id = ? and emp.status='active') " +
						"as temp2 on temp1.empId = temp2.employee_id order by lastname,firstname",
						new Object[]{selectedPosition.getId(),tenant.getId(), DateUtil.getDateAsStr(reportDate),selectedRegion.getId(),selectedPosition.getId(), DateUtil.getDateAsStr(reportDate),tenant.getId(),selectedRegion.getId()}, new DetailAvailabilityRowMapper());
			}
		}
		public List<DetailedAvailReportByEmployee> findEmployeeStatusByPositionAndDate(
				Position selectedPosition, Date reportDate,
				Region selectedRegion, Tenant tenant) {
			if(selectedRegion==null){
				return getJdbcTemplate().query("select temp1.avlId, temp1.empId, date_format(temp1.starttime,'%l:%i %p') as starttime,date_format(temp1.endtime,'%l:%i %p') as endtime, " +
						"temp1.avail_date,temp1.firstname,temp1.lastname, temp2.SchId, temp2.employee_id, temp2.status , temp2.event_id, temp2.event_name, temp2.position_name, temp2.loc_code, " +
						"temp2.loc_name, temp2.start_date, date_format(temp2.start_time,'%l:%i %p') as start_time, date_format(temp2.end_time,'%l:%i %p') as end_time from " +
						
						"(select distinct avl.id as avlId, e.id as empId, avl.starttime, avl.endtime, avl.avail_date, e.firstname as firstname, e.lastname as lastname " +
						"from employee e, employee_position ep, availability avl " +
						"where e.id = ep.employee_id " +
						"and ep.position_id = ? " +
						"and avl.employee_id = e.id " +
						"and e.company_id = ? " +
						"and avl.avail_date = DATE(?)" +
						") as temp1 left join " +
						"(SELECT  sch.id as SchId, sch.employee_id, ss.status , evtpos.event_id, evt.name event_name, pos.name position_name, loc.code loc_code, loc.name loc_name, " +
						"evtpos.start_date, evtpos.start_time, evtpos.end_time " +
						"FROM schedule_cloud.schedule sch, event_position evtpos,  employee emp,  event evt,  location loc, 	position pos,  schedulestatus ss " +
						"where sch.event_pos_id = evtpos.id  " +
						//"and evtpos.position_id = ?  " +
						"and sch.employee_id = emp.id " +
						"and evtpos.event_id = evt.id  " +
						"and evt.location_id = loc.id  " +
						"and evtpos.position_id = pos.id  and sch.status = ss.id " +
						"and event_startdate = DATE(?) and evtpos.company_id = ? " +
						") as temp2 " +
						"on temp1.empId = temp2.employee_id order by lastname,firstname",
						new Object[]{selectedPosition.getId(),
						tenant.getId(), 
						DateUtil.getDateAsStr(reportDate), 
						//selectedPosition.getId(), 
						DateUtil.getDateAsStr(reportDate),
						tenant.getId()}, 
						new DetailAvailabilityRowMapper());
			}
			
			else{
				return getJdbcTemplate().query("select temp1.avlId, temp1.empId, date_format(temp1.starttime,'%l:%i %p') as starttime,date_format(temp1.endtime,'%l:%i %p') as endtime, " +
						"temp1.avail_date,temp1.firstname,temp1.lastname, temp2.SchId, temp2.employee_id, temp2.status , temp2.event_id, temp2.event_name, temp2.position_name, temp2.loc_code, " +
						"temp2.loc_name, temp2.start_date, date_format(temp2.start_time,'%l:%i %p') as start_time, date_format(temp2.end_time,'%l:%i %p') as end_time from " +
						
						"(select distinct avl.id as avlId, e.id as empId, avl.starttime, avl.endtime, avl.avail_date, e.firstname as firstname, e.lastname as lastname, e.rating as rating " +
						"from employee e, employee_position ep, availability avl " +
						"where e.id = ep.employee_id " +
						"and ep.position_id = ? " +
						"and avl.employee_id = e.id " +
						"and e.company_id = ? " +
						"and avl.avail_date = DATE(?) " +
						"and e.region_id = ?" +
						") as temp1 left join " +
						"(SELECT  sch.id as SchId, sch.employee_id, ss.status , evtpos.event_id, evt.name event_name, pos.name position_name, loc.code loc_code, loc.name loc_name, evtpos.start_date, " +
						"evtpos.start_time, evtpos.end_time  " +
						"FROM schedule_cloud.schedule sch, event_position evtpos,  employee emp,  event evt,  location loc, 	position pos,  schedulestatus ss  " +
						"where sch.event_pos_id = evtpos.id  " +
						//"and evtpos.position_id = ?  " +
						"and sch.employee_id = emp.id " +
						"and evtpos.event_id = evt.id  " +
						"and evt.location_id = loc.id  " +
						"and evtpos.position_id = pos.id  " +
						"and sch.status = ss.id  " +
						"and event_startdate = DATE(?) " +
						"and evtpos.company_id = ? " +
						"and evtpos.region_id = ? " +
						") as temp2 " +
						"on temp1.empId = temp2.employee_id order by lastname,firstname",
						new Object[]{selectedPosition.getId(),
						tenant.getId(), 
						DateUtil.getDateAsStr(reportDate),
						selectedRegion.getId(),
						//selectedPosition.getId(), 
						DateUtil.getDateAsStr(reportDate),
						tenant.getId(),
						selectedRegion.getId()}, 
						new DetailAvailabilityRowMapper());
			}
		}
         
}