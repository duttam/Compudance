package com.onetouch.model.mapper.document;





import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.State;
import com.onetouch.model.domainobject.TenantDocument;


public class DocumentRowMapper implements RowMapper<TenantDocument> {
	
	@Override
	public TenantDocument mapRow(ResultSet rs, int row) throws SQLException {
		
		TenantDocument tenantDocument = new TenantDocument();
		tenantDocument.setId(rs.getInt("id"));
		tenantDocument.setDocumentName(rs.getString("documentname"));
		tenantDocument.setDocumentType(rs.getString("documenttype"));
		tenantDocument.setDocumentNote(rs.getString("notes"));
		tenantDocument.setInputStream(rs.getBinaryStream("documentbytes"));
		
		
		return tenantDocument;
	}

	}