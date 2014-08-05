package com.onetouch.model.dao;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.util.Assert;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.mapper.CustomUserMapper;
import com.onetouch.model.mapper.tenant.TenantRowMapper;
import com.onetouch.view.context.TenantContextUtil;



public class CustomJdbcUserDetailsManager extends JdbcUserDetailsManager{
	
	
	private String groupAuthoritiesByUsernameQuery;
	private String createUserSql = "insert into users (username, password, enabled,tenant_id, emp_id) values (?,?,?,?,?)";
	private String insertGroupMemberSql = "insert into group_members (group_id, username,tenant_id) values (?,?,?)";
	private String updateGroupMemberSql = "update group_members set group_id = ? where username = ? and tenant_id = ?";
	private String updatePolicySql = "update Users set Users.accept_policy = ? where Users.username = ?";
	private PasswordEncoder shaPasswordEncoder;
	private SaltSource saltSource;
	public final String oldPasswordCompareQuery = "select password from users where username = ? and tenant_id = ? and enabled = 1";
	private String deleteByEmpIdQuery;
	protected List<UserDetails> loadUsersByUsername(String username) {
		
		List<UserDetails> users = null;
		try{
			users = getJdbcTemplate().query(this.getUsersByUsernameQuery(), new Object[] {username}, new CustomUserMapper());
		}catch (Exception e) {
			e.printStackTrace();
		}
		//Assert.isTrue(users.size()==1,"one user only");
		
		/*for(UserDetails user : users){
			CustomUser customUser = (CustomUser)user;
			Tenant tenant = customUser.getTenant();//loadTenantByUsername(customUser.getTenantId());
			customUser.setTenant(tenant);
		}*/
		
		return users;
		
    }
	@Override
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
            List<GrantedAuthority> combinedAuthorities) {
        String returnUsername = userFromUserQuery.getUsername();
        CustomUser user = (CustomUser)userFromUserQuery;
        Tenant tenant = new Tenant();
        BeanUtils.copyProperties(user.getTenant(),tenant);
        //return new CustomUser(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),true, true, true, combinedAuthorities,user.getTenantId(),user.getEmp_id());
        return new CustomUser(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(),true, true, true, combinedAuthorities,user.getTenantId(),user.getEmp_id(),tenant);
        
    }
	
	/*protected List<GrantedAuthority> loadGroupAuthorities(String username) {
		Tenant tenant = TenantContextUtil.getTenant();
		Assert.notNull(tenant, "Tenant cant be null here");
		
        
        return getJdbcTemplate().query(this.groupAuthoritiesByUsernameQuery, new Object[] {username,tenant.getId()}, new RowMapper<GrantedAuthority>() {
            public GrantedAuthority mapRow(ResultSet rs, int rowNum) throws SQLException {
                 String roleName = getRolePrefix() + rs.getString(3);

                return new SimpleGrantedAuthority(roleName);
            }
        });
    }*/
	@Override
	public void createUser(final UserDetails user) {
		final CustomUser customUser = (CustomUser)user;
		validateUserDetails(customUser);
		Object salt = null;
		if (this.saltSource != null) {
            salt = this.saltSource.getSalt(customUser);
        }
		final String encryptedPassword = shaPasswordEncoder.encodePassword(user.getPassword(),salt);
        getJdbcTemplate().update(createUserSql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1, user.getUsername());
                ps.setString(2, encryptedPassword);
                ps.setBoolean(3, user.isEnabled());
                ps.setInt(4,customUser.getTenantId());
                ps.setInt(5,customUser.getEmp_id());
            }

        });

        
    }
	
	public void addUserToGroup(UserDetails user) {
		logger.warn("sql :"+DEF_INSERT_GROUP_MEMBER_SQL+" UserDetails "+user);
		CustomUser customUser = (CustomUser)user;
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
		for(GrantedAuthority authority : authorities ){
			if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_ADMIN")){
				final int id = findGroupId("ROLE_ADMIN");
				getJdbcTemplate().update(insertGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_MANAGER")){
				final int id = findGroupId("ROLE_MANAGER");
				getJdbcTemplate().update(insertGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SALES")){
				final int id = findGroupId("ROLE_SALES");
				getJdbcTemplate().update(insertGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SUPERVISOR")){
				final int id = findGroupId("ROLE_SUPERVISOR");
				getJdbcTemplate().update(insertGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SECURED")){
				final int id = findGroupId("ROLE_USER");
				getJdbcTemplate().update(insertGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else{}
		}
        
    }
	public void updateUserGroup(UserDetails user) {
		logger.warn("sql :"+DEF_INSERT_GROUP_MEMBER_SQL+" UserDetails "+user);
		CustomUser customUser = (CustomUser)user;
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
		for(GrantedAuthority authority : authorities ){
			if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_ADMIN")){
				final int id = findGroupId("ROLE_ADMIN");
				getJdbcTemplate().update(updateGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_MANAGER")){
				final int id = findGroupId("ROLE_MANAGER");
				getJdbcTemplate().update(updateGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SALES")){
				final int id = findGroupId("ROLE_SALES");
				getJdbcTemplate().update(updateGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SUPERVISOR")){
				final int id = findGroupId("ROLE_SUPERVISOR");
				getJdbcTemplate().update(updateGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SECURED")){
				final int id = findGroupId("ROLE_USER");
				getJdbcTemplate().update(updateGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else{}
		}
        
    }
	public void updateUserPolicy(final String username, Integer tenantId,final String acceptPolicy){
		getJdbcTemplate().update(updatePolicySql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
            		ps.setString(1, acceptPolicy);
                    ps.setString(2, username);
                    
            }
		});
	}
	private int findGroupId(String group) {
        return getJdbcTemplate().queryForInt(DEF_FIND_GROUP_ID_SQL, group);
    }
	
	private void validateUserDetails(CustomUserDetail user) {
		
        Assert.hasText(user.getUsername(), "Username may not be empty or null");
        Assert.hasText(user.getTenantId().toString(),"Tenat id may not be empty or null");
        validateAuthorities(user.getAuthorities());
    }
	
	private void validateAuthorities(Collection<? extends GrantedAuthority> authorities){
		Assert.notNull(authorities, "Authorities list must not be null");

        for (GrantedAuthority authority : authorities) {
            Assert.notNull(authority, "Authorities list contains a null entry");
            Assert.hasText(authority.getAuthority(), "getAuthority() method must return a non-empty string");
        }
	}
	protected Tenant loadTenantByUsername(Integer companyId){
		List<Tenant> tenants =  getJdbcTemplate().query("select * from company where id = ?",new Object[]{companyId} ,new TenantRowMapper());
		
		return tenants.get(0);
	}
	public String getEncodedPassword(String username,Integer companyId){
		
		return (String)getJdbcTemplate().queryForObject(oldPasswordCompareQuery, new Object[] {username,companyId}, String.class  );
		
	}
	
	public String getGroupAuthoritiesByUsernameQuery() {
		return groupAuthoritiesByUsernameQuery;
	}

	public void setGroupAuthoritiesByUsernameQuery(
			String groupAuthoritiesByUsernameQuery) {
		this.groupAuthoritiesByUsernameQuery = groupAuthoritiesByUsernameQuery;
	}

	public SaltSource getSaltSource() {
		return saltSource;
	}

	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	public PasswordEncoder getShaPasswordEncoder() {
		return shaPasswordEncoder;
	}

	public void setShaPasswordEncoder(PasswordEncoder shaPasswordEncoder) {
		this.shaPasswordEncoder = shaPasswordEncoder;
	}
	public List<UserDetails> findAllUsers(final Integer companyId) {
		List<UserDetails> users = getJdbcTemplate().query("select username,password,enabled,emp_id,company.* from users,company where users.tenant_id = company.id and users.tenant_id = ?", new Object[] {companyId}, new CustomUserMapper());
		
		return users;
	}
	public void deleteUserByEmpId(final Integer companyId, final Integer employeeId) {
		// TODO Auto-generated method stub
		getJdbcTemplate().update(this.getDeleteByEmpIdQuery(), new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
               ps.setInt(1, companyId);
               ps.setInt(2, employeeId);
               
            }
        });
		
	}
	public String getDeleteByEmpIdQuery() {
		return deleteByEmpIdQuery;
	}
	public void setDeleteByEmpIdQuery(String deleteByEmpIdQuery) {
		this.deleteByEmpIdQuery = deleteByEmpIdQuery;
	}
	public void resetTempPassword(String username, String tempPassword) {
		// TODO Auto-generated method stub
		
		getJdbcTemplate().update(JdbcUserDetailsManager.DEF_CHANGE_PASSWORD_SQL, tempPassword, username);
	}
	public String findUsernameByEmployeeId(Employee employee) {
		// TODO Auto-generated method stub
		List<String> usernames= getJdbcTemplate().queryForList("select username from users where emp_id = ? and tenant_id = ? and enabled = true",new Object[]{employee.getId(),employee.getTenant().getId()},String.class);
		if (usernames.isEmpty()){
	        return null;
	    } else {
	        return usernames.get(0);
	    }
	}
	public void deleteUserFromGroup(UserDetails user) {
		// TODO Auto-generated method stub
		CustomUser customUser = (CustomUser)user;
		
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(user.getAuthorities());
		String deleteGroupMemberSql = "delete from group_members where group_id = ? and username = ? and tenant_id =? ";
		for(GrantedAuthority authority : authorities ){
			if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_ADMIN")){
				final int id = findGroupId("ROLE_ADMIN");
				
				getJdbcTemplate().update(deleteGroupMemberSql , new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_MANAGER")){
				final int id = findGroupId("ROLE_MANAGER");
				getJdbcTemplate().update(deleteGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SUPERVISOR")){
				final int id = findGroupId("ROLE_SUPERVISOR");
				getJdbcTemplate().update(deleteGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else if(((SimpleGrantedAuthority)authority).getAuthority().equalsIgnoreCase("ROLE_ACCESS_SECURED")){
				final int id = findGroupId("ROLE_USER");
				getJdbcTemplate().update(deleteGroupMemberSql, new Object[] {id,user.getUsername(),customUser.getTenantId()});
			}
			else{}
		}
	}

	
	
    
}
