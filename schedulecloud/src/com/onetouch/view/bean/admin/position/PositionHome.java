package com.onetouch.view.bean.admin.position;





import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;

import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;



import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;


@ManagedBean(name="positionHome") 
@ViewScoped
public class PositionHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Position> positionList;
	private Tenant tenant;
	private String positionString="";
	public PositionHome(){
		
	}
	
	@PostConstruct
	public void initPositionHome(){
		
		tenant = tenantContext.getTenant();
		positionList = getPositionService().getAllPosition(tenant);
	}

	public List<Position> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<Position> positionList) {
		this.positionList = positionList;
	}

	public String getPositionString() {
		return positionString;
	}

	public void setPositionString(String positionString) {
		this.positionString = positionString;
	}
	
	public List<Position> getEventPositionList(){
		List<Position> eventPositionList = new ArrayList<Position>();
		for(Position position : positionList){
			if(position.getName().equalsIgnoreCase("Owner")||position.getName().equalsIgnoreCase("Coordinator"))
				continue;
			else
				eventPositionList.add(position);
		}
		
		return eventPositionList;
	}
	
	
}
