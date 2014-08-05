package com.onetouch.model.service.upload;

import java.util.List;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;

public interface IUploadService {
	public void uploadPolicyFile(String uploadedFileContent, String policyUploadLocation, Integer tenantId);
	public Integer saveDocument(TenantDocument tenantDocument);
	public List<TenantDocument> getTenantDocuments(Tenant tenant);
	public void updateDocumentNotes(TenantDocument tenantDocument);
	public void deleteDocument(TenantDocument deleteDocument);
}
