package com.onetouch.model.cleanup;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.schedule.OfferScheduleDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.domainobject.Schedule;

@Service
public class BatchScheduleCleanup implements ScheduleCleanup{

//	@Autowired
//	private ScheduleDAO scheduleDAO;
	@Autowired
	private OfferScheduleDAO offerScheduleDAO;
	
	@Override
	@Async @Transactional 
	public void clearOldSchedule() {
		List<Schedule> expiredSchedules = offerScheduleDAO.findAllExpiredSchedules();
		offerScheduleDAO.updateExpiredSchedules(expiredSchedules);
		
		List<Schedule> expiredOfferedSchedules = offerScheduleDAO.findAllExpiredOfferedSchedules();
		offerScheduleDAO.expireOfferedSchedules(expiredOfferedSchedules);
		
		System.out.print("deleted old schedules");
	}

}
