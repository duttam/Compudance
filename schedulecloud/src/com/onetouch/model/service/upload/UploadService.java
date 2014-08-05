package com.onetouch.model.service.upload;



import java.io.BufferedWriter;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.onetouch.model.dao.tenant.TenantRepository;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;



@Service
public class UploadService implements IUploadService{
	
	@Autowired
	private TenantRepository tenantRepository;

	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void uploadPolicyFile(String uploadedFileContent, String policyUploadLocation, Integer tenantId) {
		File file = null;
		BufferedWriter output = null;
        String fileName = null;
        fileName = "Policies_"+tenantId+"." + "html";//prefix+ "." + suffix;
        
        if(uploadedFileContent!=null){
        	
        		//file = new File(policyUploadLocation+"\\"+fileName);
            	try {
					output = new BufferedWriter(new FileWriter(policyUploadLocation+"\\"+fileName));
					output.write(uploadedFileContent);
	            	output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
        }
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public Integer saveDocument(TenantDocument tenantDocument) {
		return tenantRepository.saveDocument(tenantDocument);
	}
	@Transactional(propagation=Propagation.REQUIRED, readOnly=true)
	public List<TenantDocument> getTenantDocuments(Tenant tenant){
		return tenantRepository.getDocuments(tenant.getId());
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void updateDocumentNotes(TenantDocument tenantDocument) {
		tenantRepository.updateDocument(tenantDocument);
	}
	@Override
	@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
	public void deleteDocument(TenantDocument deleteDocument) {
		tenantRepository.deleteDocument(deleteDocument);
		
	}
}
