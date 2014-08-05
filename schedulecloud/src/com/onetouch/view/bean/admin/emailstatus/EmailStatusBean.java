package com.onetouch.view.bean.admin.emailstatus;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.CellEditEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.EmailStatusModel;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.util.FacesUtils;

@ManagedBean
@ViewScoped
public class EmailStatusBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<EmailStatus> allEmails;
	private Tenant tenant;
	private CustomUser user;
	private EmailStatusModel emailStatusModel;
	private EmailStatus[] selectEmailStatus;
	private EmailStatus selectedEmailStatus;
	private String emailContentFile;
	private List<EmailStatus> filteredEmailStatus;
	private SelectItem[] statusOptions;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@PostConstruct
	public void init(){
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		allEmails = getEmailStatusService().getAllEmails(tenant,regionBean.getSelectedRegion());
		emailStatusModel = new EmailStatusModel(allEmails);
		statusOptions = this.createFilterStatusOptions(new String[]{"pending","success","fail","resend"});
	}
	public String viewEmailContent(){
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/emailcontent");
		File htmlDir = new File(path+"/"+tenant.getCode());
		if (!htmlDir.exists()) {
			if (htmlDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		String websiteUrl = getApplicationData().getServer_url();
		String contextRoot = FacesUtils.getServletContext().getContextPath().substring(1);
		String fileName = selectedEmailStatus.getId()+".html";
		
		String htmlContent = selectedEmailStatus.getContent();
		File htmlContentFile = new File(htmlDir+"/"+fileName);
		try {
			FileUtils.writeStringToFile(htmlContentFile, htmlContent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		emailContentFile = websiteUrl+contextRoot+"/emailcontent/"+tenant.getCode()+"/"+fileName;
		return null;
	}
	public void onToEmailEdit(EmailStatus cellEmailStatus) {
		getEmailStatusService().updateEmailStatus(cellEmailStatus,true,false);
		FacesUtils.addInfoMessage("Email updated successfully");
    }  
	public String resendEmails(){
		for(EmailStatus resendEmailStatus : selectEmailStatus){
			getEmailStatusService().updateEmailStatus(resendEmailStatus,false,true);
		}
		return "emailStatus.jsf?faces-redirect=true";
	}
	private SelectItem[] createFilterStatusOptions(String[] data)  {  
        SelectItem[] options = new SelectItem[data.length + 1];  
  
        options[0] = new SelectItem("", "All Status");  
        for(int i = 0; i < data.length; i++) {  
            options[i + 1] = new SelectItem(data[i], data[i]);  
        }  
  
        return options;  
    } 
	public EmailStatusModel getEmailStatusModel() {
		return emailStatusModel;
	}
	public void setEmailStatusModel(EmailStatusModel emailStatusModel) {
		this.emailStatusModel = emailStatusModel;
	}
	public EmailStatus[] getSelectEmailStatus() {
		return selectEmailStatus;
	}
	public void setSelectEmailStatus(EmailStatus[] selectEmailStatus) {
		this.selectEmailStatus = selectEmailStatus;
	}
	public EmailStatus getSelectedEmailStatus() {
		return selectedEmailStatus;
	}
	public void setSelectedEmailStatus(EmailStatus selectedEmailStatus) {
		this.selectedEmailStatus = selectedEmailStatus;
	}
	public String getEmailContentFile() {
		return emailContentFile;
	}
	public void setEmailContentFile(String emailContentFile) {
		this.emailContentFile = emailContentFile;
	}
	public List<EmailStatus> getFilteredEmailStatus() {
		return filteredEmailStatus;
	}
	public void setFilteredEmailStatus(List<EmailStatus> filteredEmailStatus) {
		this.filteredEmailStatus = filteredEmailStatus;
	}
	public SelectItem[] getStatusOptions() {
		return statusOptions;
	}
	public void setStatusOptions(SelectItem[] statusOptions) {
		this.statusOptions = statusOptions;
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	
}
