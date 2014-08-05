package com.onetouch.model.domainobject;

public class AuditLogFactory {

	public static AuditLog createAuditLog(Integer employeeId,Integer recordId,String objectType,String operation){
		AuditLog auditLog = new AuditLog();
		return auditLog;
		
	}
}
