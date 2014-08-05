package com.onetouch.model.service.email;





import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.CustomJdbcUserDetailsManager;
import com.onetouch.model.dao.email.EmailStatusDAO;
import com.onetouch.model.dao.employee.EmployeeDAO;
import com.onetouch.model.dao.position.PositionDAO;
import com.onetouch.model.dao.schedule.ScheduleDAO;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.EmailGroup;
import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.SalesPersonLocation;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.util.ApplicationContextProvider;
import com.onetouch.model.util.ApplicationData;
import com.onetouch.model.util.SpringUtil;

@Service
public class EmailStatusService implements IEmailStatusService{

	@Autowired
	private EmailStatusDAO emailStatusDAO; 
	
	@Autowired
	private CustomJdbcUserDetailsManager userDetailManager;
	
	@Autowired
	private ApplicationData applicationData;

	@Override
	public List<EmailStatus> getAllEmails(Tenant tenant, Region region) {
		
		return emailStatusDAO.findAllEmails(tenant,region);
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED,readOnly=false)
	public void updateEmailStatus(EmailStatus emailStatus,boolean updateToEmail,boolean updateStatus) {
		if(updateToEmail)
			emailStatusDAO.updateToEmail(emailStatus.getId(),emailStatus.getToEmail(),emailStatus.getCompanyId());
		else if(updateStatus)
			emailStatusDAO.updateStatus(emailStatus.getId(),"resend");
	}

	
	
	
}
