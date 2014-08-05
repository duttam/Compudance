package com.onetouch.view.bean.admin.report;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;
import javax.servlet.ServletContext;

import org.apache.commons.lang.exception.NestableException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.OneTouchReport;

import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.util.SupportBean;
@ManagedBean(name="reportHome")
@ViewScoped
public class ReportHome extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Tenant tenant;
	private Integer reportId;
	private String reportView;
	private String reportViewTarget;
	private String filename;
	private List<Employee> scheduledEmployees;
	private List<Employee> eventPosEmployees;
	private List<Event> publishedEventList;
	private List<SelectItem> eventListByMonth;
	private Event laborEvent;
	private CustomUser customUser;
	@ManagedProperty(value="#{supportBean}")
	private SupportBean supportBean;
	
	@PostConstruct
	public void initReportHome(){
		tenant = tenantContext.getTenant();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		this.reportView = "detailAvailabilityReport.xhtml";
		this.reportViewTarget = "availabilitytbl";
		this.filename = "Detailed Availability Report";
		
		
		
	}
	
	

	public String getReportViewTarget() {
		return reportViewTarget;
	}



	public void setReportViewTarget(String reportViewTarget) {
		this.reportViewTarget = reportViewTarget;
	}



	public String getReportView() {
		return reportView;
	}



	public void setReportView(String reportView) {
		this.reportView = reportView;
	}



	public SupportBean getSupportBean() {
		return supportBean;
	}
	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}
	
	
	
}
