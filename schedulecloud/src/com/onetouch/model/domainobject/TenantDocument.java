package com.onetouch.model.domainobject;

import java.io.InputStream;
import java.io.Serializable;

import org.primefaces.model.StreamedContent;

public class TenantDocument implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String documentNote;
	private String documentName;
	private String documentType;
	private byte[] documentBytes;
	private InputStream inputStream;
	private String documentTypeImage="download.jpg";
	private StreamedContent file;
	private Tenant tenant;
	public TenantDocument() {
		
	}
	
	public TenantDocument(String documentName, String documentType,
			byte[] documentBytes) {
		
		this.documentName = documentName;
		this.documentType = documentType;
		this.documentBytes = documentBytes;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
	
	public byte[] getDocumentBytes() {
		return documentBytes;
	}
	public void setDocumentBytes(byte[] documentBytes) {
		this.documentBytes = documentBytes;
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public String getDocumentNote() {
		return documentNote;
	}

	public void setDocumentNote(String documentNote) {
		this.documentNote = documentNote;
	}
	

	public String getDocumentTypeImage(){
		if(documentType.equalsIgnoreCase("application/pdf"))
			documentTypeImage =  "downloadpdf.jpg";
		if(documentType.equalsIgnoreCase("application/vnd.ms-excel"))
			documentTypeImage ="downloadxls.jpg";
		if(documentType.equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
			documentTypeImage = "downloadmsword.jpg";
		
		return documentTypeImage;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	

	public StreamedContent getFile() {
		return file;
	}

	public void setFile(StreamedContent file) {
		this.file = file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TenantDocument other = (TenantDocument) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
}
