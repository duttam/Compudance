package com.onetouch.view.bean.menu;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.util.FacesUtils;

//@ManagedBean(name="subMenuBean")
//@SessionScoped
public class SubMenuBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Integer activeMenuIndex;
	private String menuid = "position";
	private String includeViewPage = "";
	public SubMenuBean(){
		activeMenuIndex = 0;
	}
	
	@PostConstruct
	public void init(){
		activeMenuIndex = 0;
	}
	public void onTabChange(TabChangeEvent tabChangeEvent){
		if(tabChangeEvent.getTab().getId().equalsIgnoreCase( "positionTab" ) ){
			menuid = "position";
		}
		if(tabChangeEvent.getTab().getId().equalsIgnoreCase( "shiftTab" ) ){
			menuid = "shift";
		}
	}
	public void menuItemClicked(ActionEvent event){
		menuid = FacesUtils.getRequestParameter("menuid");
		if(menuid.equalsIgnoreCase("position")){
			activeMenuIndex = 0;
			includeViewPage = "ui/admin/position/positionHome.xhtml";
		}
		
		if(menuid.equalsIgnoreCase("shift")){
			activeMenuIndex = 1;
			
			includeViewPage ="ui/admin/shift/shiftHome.xhtml";
		}
		if(menuid.equalsIgnoreCase("passive")){
			activeMenuIndex = 2;
			//FacesUtils.redirectToPage("views/venue/client/passive");
		}
		//RequestContext.getCurrentInstance().update("toolForm:submenuViewId");
		//FacesContext.getCurrentInstance().responseComplete();
	}

	public Integer getActiveMenuIndex() {
		return activeMenuIndex;
	}

	public void setActiveMenuIndex(Integer activeMenuIndex) {
		this.activeMenuIndex = activeMenuIndex;
	}

	

	public String getMenuid() {
		return menuid;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public void displayPosition( AjaxBehaviorEvent event ){
        
        menuid   = FacesUtils.getRequestParameter( "menuid" );
        activeMenuIndex = 0;
        
    }

	public String getIncludeViewPage() {
		return includeViewPage;
	}
	
	public void setIncludeViewPage(String includeViewPage) {
		this.includeViewPage = includeViewPage;
	}

	
}
