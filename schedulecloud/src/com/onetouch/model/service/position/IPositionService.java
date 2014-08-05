package com.onetouch.model.service.position;

import java.util.List;


import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;

public interface IPositionService {

	public List<Position> getAllPosition(Tenant tenant);
	
	public void addPosition(Position position);
	
	public void editPosition(Position position);
	
	public Position getPosition(Integer id,Integer tenantId);

	public List<Position> getAllPositionByEvent(Tenant tenant,Event selectedEvent);

	public List<Position> getSelectedPositionsByEvent(Integer eventId);

	public void updatePositionsDisplayOrder(List<Position> positionList);
}
