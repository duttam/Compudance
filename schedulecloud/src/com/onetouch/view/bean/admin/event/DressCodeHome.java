package com.onetouch.view.bean.admin.event;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.StringUtilities;
//import com.onetouch.view.util.StringUtilities;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.DressCode;
import com.onetouch.model.domainobject.DressCodeModel;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.TimeOffRequestModel;


@ManagedBean(name="dressCodeHome")
@ViewScoped
public class DressCodeHome extends BaseBean{
       
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
   
	private CustomUser user;
    private Tenant tenant;
	private DressCode dressCode; 
	private DressCodeModel  dressCodeModel;
	private List<DressCode> dressCodes;
	
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	@PostConstruct
	public void initController() {
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		dressCodes = getEventService().getAllDressCodes(tenant,regionBean.getSelectedRegion());
		
		dressCodeModel = new DressCodeModel(dressCodes);
		
	}
	
	public void addDressCode(ActionEvent actionEvent){
		dressCode = new DressCode();
		dressCode.setTenant(tenant);
		dressCode.setRegion(regionBean.getSelectedRegion());
	}
	public String editDressCode(){
		if(dressCode!=null){
			
			
			RequestContext.getCurrentInstance().execute("dresscodedlg.show();");
		}else{
			FacesUtils.addErrorMessage("","Please select a Dress Codet first");
		}
		return null;
	}
	public String deleteSickTimeoff(){
		
		return "dressCodeHome.jsf?faces-redirect=true";
	}
	
	
	public String saveDressCode() {
		if(dressCode != null) {
			getEventService().saveDressCode(dressCode);
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("","Dress Code Saved");
		return "dressCodeHome.jsf?faces-redirect=true";
		
	}
	
	public String updateDressCode() {
		if(dressCode != null) {
			
			getEventService().updateDressCode(dressCode);
			
		}
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);
		FacesUtils.addInfoMessage("","Dress Code Updated");
		return "dressCodeHome.jsf?faces-redirect=true";

	}
	

	

	public DressCode getDressCode() {
		return dressCode;
	}

	public void setDressCode(DressCode dressCode) {
		this.dressCode = dressCode;
	}

	public DressCodeModel getDressCodeModel() {
		return dressCodeModel;
	}

	public void setDressCodeModel(DressCodeModel dressCodeModel) {
		this.dressCodeModel = dressCodeModel;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	
}
