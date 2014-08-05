package com.onetouch.view.bean.admin.tools;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;

@ManagedBean(name="emailGroupHome")
@ViewScoped
public class EmailGroupHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private EmailGroup emailGroup;
	private List<EmailGroup> emailGroups;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	private Tenant tenant;
	@PostConstruct
	public void init(){
		tenant = tenantContext.getTenant();
		emailGroups = new ArrayList<EmailGroup>();
		emailGroup = new EmailGroup(tenant);
	}
	public List<EmailGroup> getEmailGroups() {
		return emailGroups;
	}
	public void setEmailGroups(List<EmailGroup> emailGroups) {
		this.emailGroups = emailGroups;
	}
	public EmailGroup getEmailGroup() {
		return emailGroup;
	}
	public void setEmailGroup(EmailGroup emailGroup) {
		this.emailGroup = emailGroup;
	}
	public void addEmailGroup(ActionEvent actionEvent){
		emailGroup.setCompanyId(tenant.getId());
		emailGroup.setRegionId(regionBean.getSelectedRegion().getId());
		getEmployeeService().addEmailGroup(emailGroup);
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	
}
