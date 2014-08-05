package com.onetouch.view.bean.admin.upload;




import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;




import org.apache.commons.io.IOUtils;
import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TenantDocument;
import com.onetouch.model.util.ApplicationData;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.ValidationMessages;


@ManagedBean(name="uploadDocumentBean")
@ViewScoped
public class UploadDocumentBean extends BaseBean{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    @ManagedProperty(value="#{applicationData}")
    private ApplicationData applicationData;
    private Tenant tenant;
    private String policyUploadLocation;
    private String policyFileContent="";
    private UploadedFile uploadDocument;
    private String documentNote;
    private TenantDocument deleteDocument;
    private List<TenantDocument> tenantDocuments;
    @PostConstruct
    public void initTheme(){
    	tenant = tenantContext.getTenant();
    	this.policyUploadLocation = (String)applicationData.getPolicy_dir();
    	tenantDocuments = getUploadService().getTenantDocuments(tenant);
    	this.loadDocumentsToDisk(tenantDocuments);
    	for(TenantDocument document : tenantDocuments){
    		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/documents/"+tenant.getCode()+"/"+document.getDocumentName());  
    		StreamedContent file = new DefaultStreamedContent(stream, document.getDocumentType(), document.getDocumentName());
    		
    		document.setFile(file);
    	}
        /*FileInputStream inputStream = null;
        String fileName = null;
            try {
            	fileName = policyUploadLocation+"/"+"Policies_"+tenant.getCode()+"." + "html";
            	inputStream = new FileInputStream(fileName);
                policyFileContent = IOUtils.toString(inputStream);
            } catch (FileNotFoundException e) {
				logger.error("policy file  "+ fileName +" not found ");
			} catch (IOException e) {
				
				logger.error("cant open file  "+ fileName );
			} finally {
            	if(inputStream!=null)
					try {
						inputStream.close();
					} catch (IOException e) {
						logger.error("probem in closing file "+ fileName );
					}
            }*/

    }
    public void uploadDocument(){
    	
        if(uploadDocument!=null && uploadDocument.getFileName()!=null){
        		String documentname = uploadDocument.getFileName();
        		String documentType = uploadDocument.getContentType();
        		byte[] documentBytes = uploadDocument.getContents();
        		TenantDocument tenantDocument = new TenantDocument(documentname,documentType,documentBytes);
        		tenantDocument.setTenant(tenant);
        		tenantDocument.setDocumentNote(documentNote);
        		Integer documentId=getUploadService().saveDocument(tenantDocument);
        		tenantDocuments = getUploadService().getTenantDocuments(tenant);
            	this.loadDocumentsToDisk(tenantDocuments);
            	for(TenantDocument document : tenantDocuments){
            		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/documents/"+tenant.getCode()+"/"+document.getDocumentName());  
            		StreamedContent file = new DefaultStreamedContent(stream, document.getDocumentType(), document.getDocumentName());
            		
            		document.setFile(file);
            	}
        		/*InputStream stream=null;
				try {
					stream = uploadDocument.getInputstream();
					tenantDocument.setInputStream(stream);
					List<TenantDocument> newDoc = new ArrayList<TenantDocument>();
					newDoc.add(tenantDocument);
					this.loadDocumentsToDisk(newDoc);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/documents/"+tenant.getCode()+"/"+tenantDocument.getDocumentName());  
        		StreamedContent file = new DefaultStreamedContent(stream, tenantDocument.getDocumentType(), tenantDocument.getDocumentName());
        		tenantDocument.setId(documentId);
        		tenantDocument.setFile(file);
        		tenantDocuments.add(tenantDocument);*/
        		this.documentNote = null;
        	}
        	else{
        		FacesMessage message = ValidationMessages.getMessage(ValidationMessages.MESSAGE_RESOURCES, "policyUploadFailure", null);
        		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message.getDetail(), null));
        	}
        	
    }
    public void loadDocumentsToDisk(List<TenantDocument> allDocuments){
		//String path = FacesUtils.getServletContext().getRealPath("/images");
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/documents");
		File imageDir = new File(path+"/"+tenant.getCode());
		if (!imageDir.exists()) {
			if (imageDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		for(TenantDocument tenantDocument : allDocuments){
			
				File file = new File(imageDir+"/"+tenantDocument.getDocumentName());
				OutputStream output = null;
				try {
					output = new FileOutputStream(file);
					IOUtils.copy(tenantDocument.getInputStream(), output);
				} catch (Exception e) {
					// TODO: handle exception
				}finally {
					IOUtils.closeQuietly(output);
				}
			
		}
	}
    public void onEdit(RowEditEvent event) {  
    	TenantDocument tenantDocument = ((TenantDocument) event.getObject());  
    	tenantDocument.setTenant(tenant);
    	getUploadService().updateDocumentNotes(tenantDocument);
    	tenantDocuments = getUploadService().getTenantDocuments(tenant);
    	FacesMessage msg = new FacesMessage("Document Updated");  
    	FacesContext.getCurrentInstance().addMessage(null, msg);
    	this.loadDocumentsToDisk(tenantDocuments);
    	for(TenantDocument document : tenantDocuments){
    		InputStream stream = ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("/documents/"+tenant.getCode()+"/"+document.getDocumentName());  
    		StreamedContent file = new DefaultStreamedContent(stream, document.getDocumentType(), document.getDocumentName());
    		
    		document.setFile(file);
    	}
    }  
      
    public void onCancel(RowEditEvent event) {  
        FacesMessage msg = new FacesMessage("Document Update Cancelled");  
  
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    } 
    
    public void deleteDocument(ActionEvent  actionEvent) {  
    	//Integer docId = Integer.parseInt(FacesUtils.getRequestParameter("currentRowIndex"));
    	
    	/*for(TenantDocument document : tenantDocuments){
    		if(docId.intValue()==document.getId().intValue()){
    			deleteDocument = document;
    			break;
    		}
    	}*/
    	
    	deleteDocument.setTenant(tenant);
    	getUploadService().deleteDocument(this.deleteDocument);
    	tenantDocuments.remove(deleteDocument);
    	RequestContext.getCurrentInstance().update("toolForm:toolview:documentsTbl");
    }
    
    public ApplicationData getApplicationData() {
            return applicationData;
    }
    public void setApplicationData(ApplicationData applicationData) {
            this.applicationData = applicationData;
    }

	public String getPolicyFileContent() {
		return policyFileContent;
	}


	public void setPolicyFileContent(String policyFileContent) {
		this.policyFileContent = policyFileContent;
	}
	public String getDocumentNote() {
		return documentNote;
	}
	public void setDocumentNote(String documentNote) {
		this.documentNote = documentNote;
	}
	public List<TenantDocument> getTenantDocuments() {
		return tenantDocuments;
	}
	public void setTenantDocuments(List<TenantDocument> tenantDocuments) {
		this.tenantDocuments = tenantDocuments;
	}
	public UploadedFile getUploadDocument() {
		return uploadDocument;
	}
	public void setUploadDocument(UploadedFile uploadDocument) {
		this.uploadDocument = uploadDocument;
	}
	public TenantDocument getDeleteDocument() {
		return deleteDocument;
	}
	public void setDeleteDocument(TenantDocument deleteDocument) {
		this.deleteDocument = deleteDocument;
	}
	
    
}
