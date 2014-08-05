package com.onetouch.view.bean.admin.position;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import org.primefaces.context.RequestContext;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;


import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.login.LoginBean;
import com.onetouch.view.bean.menu.SubMenuBean;

import com.onetouch.view.util.FacesUtils;
import com.sun.corba.se.spi.orbutil.fsm.Action;

@ManagedBean(name="positionController")
@ViewScoped
public class PositionController extends BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Position position;
	private Tenant tenant;
	private String action;
	
	@ManagedProperty(value="#{subMenuBean}")
    private SubMenuBean subMenuBean;
	@PostConstruct
	public void initPositionController(){
		tenant = tenantContext.getTenant();
		position = new Position(tenant);
		/*action = FacesUtils.getRequestParameter("action");
		position = new Position(tenant);
		if(action.equalsIgnoreCase("addposition")){
			position = new Position(tenant);
		}
		if(action.equalsIgnoreCase("editposition")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			position = getPositionService().getPosition(id,tenant.getId());
			if(position!=null && position.getPositionColor()!=null){
				String color = position.getPositionColor();
				color = color.substring(1, color.length());
				position.setPositionColor(color);
				
			}
		}*/
	}
	public void clickAddPosition(ActionEvent event){
		action = FacesUtils.getRequestParameter("action");
		
		if(action.equalsIgnoreCase("addposition")){
			position = new Position(tenant);
		}
	}
	public void addPosition(ActionEvent event){
		try{
			getPositionService().addPosition(position);
			PositionHome positionHome = (PositionHome)FacesUtils.getManagedBean("positionHome");
			positionHome.getPositionList().add(0,position);
			position = new Position(tenant);
			FacesUtils.addInfoMessage("Position added Successfully");
			//RequestContext.getCurrentInstance().update(":toolForm:toolview:pgrowl");
		}catch (DataAccessException dae) {
			dae.printStackTrace();
		}
		//return "positionHome?faces-redirect=true";
	}
	public void clickEditPosition(ActionEvent event){
		action = FacesUtils.getRequestParameter("action");
		
		if(action.equalsIgnoreCase("editposition")){
			Integer id = Integer.parseInt(FacesUtils.getRequestParameter("id"));
			position = getPositionService().getPosition(id,tenant.getId());
			
		}
		if(position!=null){
			RequestContext.getCurrentInstance().update("toolForm:toolview:positionDialog");
		}
	}
	public void updatePosition(ActionEvent event){
		PositionHome positionHome = (PositionHome)FacesUtils.getManagedBean("positionHome");
		List<Position> positionList = positionHome.getPositionList();
		List<Position> replacePositionList = new ArrayList<Position>();
		Integer positionIndex = 0;
		for(int i =0;i<positionList.size();i++){
			Position tempPosition = positionList.get(i);
			if(position.getId().intValue() == tempPosition.getId().intValue()){
				positionIndex = i;
				continue;
			}
			else
				replacePositionList.add(tempPosition);
		}
		
		getPositionService().editPosition(position);
		replacePositionList.add(positionIndex, position);
		positionHome.setPositionList(replacePositionList);
		FacesUtils.addInfoMessage("Position updated Successfully");
	}
	public void updatePositionDisplayOrder(ActionEvent actionEvent){
		PositionHome positionHome = (PositionHome)FacesUtils.getManagedBean("positionHome");
		List<Position> positionList = positionHome.getPositionList();
		getPositionService().updatePositionsDisplayOrder(positionList);
	}
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	
	public SubMenuBean getSubMenuBean() {
		return subMenuBean;
	}
	public void setSubMenuBean(SubMenuBean subMenuBean) {
		this.subMenuBean = subMenuBean;
	}
	
	
}
