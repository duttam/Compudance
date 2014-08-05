package com.onetouch.model.service.email;

import java.util.List;

import com.onetouch.model.domainobject.EmailStatus;
import com.onetouch.model.domainobject.Region;
import com.onetouch.model.domainobject.Tenant;

public interface IEmailStatusService {
	public List<EmailStatus> getAllEmails(Tenant tenant, Region region);

	public void updateEmailStatus(EmailStatus cellEmailStatus,boolean updateToEmail,boolean updateStatus);
}
