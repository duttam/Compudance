package com.onetouch.model.service.position;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.position.PositionDAO;

import com.onetouch.model.domainobject.Event;

import com.onetouch.model.domainobject.Tenant;

import com.onetouch.model.domainobject.Position;
@Service
public class PositionService implements IPositionService {

	@Autowired
	PositionDAO positionDAO;
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getAllPosition(Tenant tenant) {
		
		List<Position> positionList = positionDAO.findAll(tenant.getId());
		
		return positionList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void addPosition(Position position) {
		Integer positionId = positionDAO.save(position);
		position.setId(positionId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void editPosition(Position position) {
		positionDAO.update(position);
		
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public Position getPosition(Integer id,Integer tenantId) {
		return positionDAO.find(id,tenantId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getAllPositionByEvent(Tenant tenant,
			Event selectedEvent) {
		
		return positionDAO.findByEvent(selectedEvent);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=true)
	public List<Position> getSelectedPositionsByEvent(Integer eventId) {
		
		return positionDAO.findPositionsByEvent(eventId);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updatePositionsDisplayOrder(List<Position> positionList) {
		for(int i=0;i<positionList.size();i++){
			Position p = positionList.get(i);
			p.setDisplayOrder(i);
		}
		positionDAO.updateDisplayOrder(positionList);
	}
	
	
}
