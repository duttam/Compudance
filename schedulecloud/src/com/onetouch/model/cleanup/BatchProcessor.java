package com.onetouch.model.cleanup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class BatchProcessor implements Processor{
	
	@Autowired
	private ScheduleCleanup scheduleCleanup;

	@Scheduled(cron="0 0 0 * * ?")
	//@Scheduled(fixedDelay=10000)
	public void process() {
		
		scheduleCleanup.clearOldSchedule();
		//System.out.print("job started");
	}

}
