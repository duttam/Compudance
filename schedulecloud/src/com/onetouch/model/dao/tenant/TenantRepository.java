package com.onetouch.model.dao.tenant;



import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.annotation.PostConstruct;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import com.onetouch.model.dao.BaseDAO;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;
import com.onetouch.model.mapper.document.DocumentRowMapper;
import com.onetouch.model.mapper.employee.EmployeePositionExtractor;
import com.onetouch.model.mapper.tenant.TenantRowMapper;



@Repository
@Qualifier("tenantRepository")
public class TenantRepository extends BaseDAO{

	private List<Tenant> tenants = null;
	/*private String selectTenantWithDetail = "select c.id,c.name,c.address1,c.address2,c.city,c.state,c.statecode,c.zipcode,c.email,c.business_phone," +
											"c.fax,c.code, cs.id as tenant_setting_id, cs.logoUrl,cs.logoutUrl,cs.theme,cs.policyFileUrl,cs.tutorialUrl " +
											"from company c , companysetting cs " +
											"where c.id = cs.company_id";*/
	private String createTenantSql = "insert into company (name,enrolldate,contact,address1,address2,city,state,statecode,zipcode,email,business_phone,fax,email_sender,logourl," +
									 "theme,break_paid,active,overtime,overtime_rate,code,sortby_rankhiredate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@PostConstruct
	public void initTenantRepository(){
		try{
		tenants = getJdbcTemplate().query("select * from company", new TenantRowMapper());
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	public List<Tenant> getTenants(boolean refresh){
		
		if(refresh){
			tenants = getJdbcTemplate().query("select * from company", new TenantRowMapper());
		}
		return tenants;
	}
	public int save(final Tenant tenant) {
		getJdbcTemplate().update(createTenantSql, new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
				
				ps.setString(1, tenant.getName());
				ps.setString(2,dateformat.format(tenant.getEnrollmentDate()));
				ps.setString(3, tenant.getContactname());
				ps.setString(4,tenant.getAddress1());
				ps.setString(5,tenant.getAddress2());
				ps.setString(6,tenant.getCity());
				ps.setString(7,tenant.getState().getStateName());
				ps.setString(8, tenant.getState().getStateCode());
				ps.setString(9,tenant.getZipcode());
				ps.setString(10,tenant.getCompanyEmail());
				ps.setString(11,tenant.getBusinessPhone());
				ps.setString(12,tenant.getFax());
				ps.setString(13,tenant.getEmailSender());
				ps.setString(14,tenant.getLogoUrl());
				ps.setString(15,tenant.getTheme());
				if(tenant.getBreakOption().equalsIgnoreCase("paid"))
					ps.setBoolean(16,true);
				else
					ps.setBoolean(16,false);
				ps.setBoolean(17,true);// active
				
				ps.setString(18,tenant.getOvertimeOption());
				if(tenant.getOvertimeOption().equalsIgnoreCase("none"))
					ps.setDouble(19, new Double(0));
				else
					ps.setDouble(19, tenant.getOvertimeRate());
				ps.setString(20,tenant.getCode());
				ps.setBoolean(21,tenant.isSortByRankAndHiredate());
			}
		});
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public Integer saveDocument(final TenantDocument tenantDocument){
		LobHandler lobHandler = new DefaultLobHandler();
		getJdbcTemplate().execute("insert into companydocument(documentname,documenttype,documentbytes,notes,company_id) values (?,?,?,?,?)", new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
	    public void setValues(PreparedStatement ps, LobCreator lc) throws SQLException {
	    	ps.setString(1, tenantDocument.getDocumentName());
    		ps.setString(2, tenantDocument.getDocumentType());
    		lc.setBlobAsBytes(ps, 3,tenantDocument.getDocumentBytes());
    		ps.setString(4, tenantDocument.getDocumentNote());
	        ps.setInt(5, tenantDocument.getTenant().getId());
	        }
		});
		
		return getJdbcTemplate().queryForInt( "select last_insert_id()");
	}
	
	public List<TenantDocument> getDocuments(final Integer companyId){
		List<TenantDocument> tenantDocuments = getJdbcTemplate().query("select * from companydocument where company_id = ?",new Object[]{companyId} ,new DocumentRowMapper());
		
		return tenantDocuments;
	}
	public Tenant getTenant(Integer companyId){
		List<Tenant> tenants =  getJdbcTemplate().query("select * from company where id = ?",new Object[]{companyId} ,new TenantRowMapper());
		
		return tenants.get(0);
		
	
	}
	public void updateDocument(final TenantDocument document) {
		
		getJdbcTemplate().update("update companydocument set notes = ? where id = ? and company_id = ?", new PreparedStatementSetter() {
					
					@Override
					public void setValues(PreparedStatement ps) throws SQLException {
						ps.setString(1, document.getDocumentNote());
						ps.setInt(2, document.getId());
						ps.setInt(3, document.getTenant().getId());
					}
				});
	}
	public List<Tenant> findAll() {
		List<Tenant> tenants =  getJdbcTemplate().query("select * from company order by name asc" ,new TenantRowMapper());
		
		return tenants;
	}
	public void deleteDocument(final TenantDocument deleteDocument) {
		Integer companyId =  deleteDocument.getTenant().getId();
		Integer documentId = deleteDocument.getId();
		getJdbcTemplate().update("DELETE from companydocument where id = ? AND company_id = ?",new Object[]{documentId,companyId });
	}
	public void update(final Tenant tenant) {
		getJdbcTemplate().update("update company set name = ? ,address1 = ?,address2 = ?,city = ?,state = ?,statecode = ?,zipcode = ?,email = ?,business_phone = ?," +
								 "fax = ?,email_sender = ?, break_paid = ?,overtime = ?,overtime_rate = ?, code = ?,sortby_rankhiredate=? where id = ?", new PreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, tenant.getName());
				ps.setString(2,tenant.getAddress1());
				ps.setString(3,tenant.getAddress2());
				ps.setString(4,tenant.getCity());
				ps.setString(5,tenant.getState().getStateName());
				ps.setString(6, tenant.getState().getStateCode());
				ps.setString(7,tenant.getZipcode());
				ps.setString(8,tenant.getCompanyEmail());
				ps.setString(9,tenant.getBusinessPhone());
				ps.setString(10,tenant.getFax());
				ps.setString(11,tenant.getEmailSender());
				if(tenant.getBreakOption().equalsIgnoreCase("paid"))
					ps.setBoolean(12,true);
				else
					ps.setBoolean(12,false);
				ps.setString(13,tenant.getOvertimeOption());
				if(tenant.getOvertimeOption().equalsIgnoreCase("none"))
					ps.setDouble(14, new Double(0));
				else
					ps.setDouble(14, tenant.getOvertimeRate());
				ps.setString(15,tenant.getCode());
				ps.setBoolean(16,tenant.isSortByRankAndHiredate());
				ps.setInt(17,tenant.getId());
			}
		});
	}
	public void disableCompany(Tenant company) {
		Integer companyId = company.getId();
		getJdbcTemplate().update("update company set active = false where id = ? ",new Object[]{companyId});
	}
	public void savePolicyNotes(final Tenant tenant) {
		getJdbcTemplate().update("update company set policyNotes = ? where id = ?", new PreparedStatementSetter() {

		@Override
		public void setValues(PreparedStatement ps) throws SQLException {
			ps.setString(1, tenant.getPolicyNotes());
			ps.setInt(2,tenant.getId());
		}
		});
	}
}
