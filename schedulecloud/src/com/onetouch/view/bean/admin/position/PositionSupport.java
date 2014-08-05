package com.onetouch.view.bean.admin.position;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
@ManagedBean(name="positionSupport")
@ViewScoped
public class PositionSupport extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<Position> positions;
	private Tenant tenant;
	@PostConstruct
	public void initEventSupport(){
		tenant = tenantContext.getTenant();
		positions = getPositionService().getAllPosition(tenant);
		
	}
	public List<Position> getPositions() {
		return positions;
	}
	public void setPositions(List<Position> positions) {
		this.positions = positions;
	}
	
	
}