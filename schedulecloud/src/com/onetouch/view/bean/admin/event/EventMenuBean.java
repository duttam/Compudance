package com.onetouch.view.bean.admin.event;



import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;

import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.menu.MainMenuBean;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="eventMenuBean")
@RequestScoped
public class EventMenuBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private Integer activeMenuIndex;
	
	@ManagedProperty(value="#{mainMenuBean}")
	private MainMenuBean mainMenuBean;
	public EventMenuBean(){
		
	}
	
	@PostConstruct
	public void init(){
		activeMenuIndex = mainMenuBean.getEventActiveTabIndex();//supportBean.getEventActiveTabIndex();
	}
	
	public String menuItemClicked(String menuid){
		
		if(menuid.equalsIgnoreCase("eventHome")){
			mainMenuBean.setEventActiveTabIndex(0);
			
			return "/ui/admin/event/eventHome.jsf?faces-redirect=true";
		}
		
		if(menuid.equalsIgnoreCase("eventType")){
			mainMenuBean.setEventActiveTabIndex(1);
			
			return "/ui/admin/event/eventTypeHome.jsf?faces-redirect=true";
		}
		if(menuid.equalsIgnoreCase("dressCode")){
			mainMenuBean.setEventActiveTabIndex(2);
			
			return "/ui/admin/event/dressCodeHome.jsf?faces-redirect=true";
		}
		return "";
		
	}

	public Integer getActiveMenuIndex() {
		return activeMenuIndex;
	}

	public void setActiveMenuIndex(Integer activeMenuIndex) {
		this.activeMenuIndex = activeMenuIndex;
	}

	
	public MainMenuBean getMainMenuBean() {
		return mainMenuBean;
	}

	public void setMainMenuBean(MainMenuBean mainMenuBean) {
		this.mainMenuBean = mainMenuBean;
	}

	
	
}
