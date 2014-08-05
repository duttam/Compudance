package com.onetouch.view.bean.admin.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.event.SelectEvent;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.OneTouchReport;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.SignInOut;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.util.FacesUtils;

@ManagedBean(name="eventDetailReport")
@ViewScoped
public class EventDetailReport extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OneTouchReport> eventDetailList; 
	private Tenant tenant;
	private CustomUser customUser;
	private String selectedMonth = "January";
	private String[] months;
	private Date reportStartDate;
	private Date reportEndDate;
	@PostConstruct
	public void initBean(){
		tenant = tenantContext.getTenant();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
		
		}
		eventDetailList = getReportService().getAllPublishedEventsByMonth(tenant,customUser,true,selectedMonth);
		months = new String[]{"YTD","January","February","March","April","May","June","July","August","September","October","November","December"};
	}
	
	public void changeMonth(){
		eventDetailList.clear();
		eventDetailList = getReportService().getAllPublishedEventsByMonth(tenant,customUser,true,selectedMonth);
		
	}
	public void filterEventDetailReport(SelectEvent event){
		if(this.reportEndDate!=null && this.reportStartDate!=null){
			if(this.reportEndDate.getTime()<this.reportStartDate.getTime())
				FacesUtils.addErrorMessage("Enter a valid end date");
			else
				eventDetailList = getReportService().getAllPublishedEventsByDateRange(tenant,customUser,true,reportStartDate,reportEndDate);
		}
			
	}
	public void preProcessorXLS(Object document){
		XSSFWorkbook workbook = (XSSFWorkbook) document;  
		XSSFSheet sheet = workbook.getSheetAt(0);
		XSSFFont boldFont = workbook.createFont();
		boldFont.setBold(true);
		boldFont.setColor(IndexedColors.ORANGE.getIndex()); //new XSSFColor(new java.awt.Color(247,190,129)
		XSSFRow row = sheet.createRow(0);

		XSSFFont boldFont20 = workbook.createFont();
		boldFont20.setBold(true);
		boldFont20.setColor(IndexedColors.ORANGE.getIndex());
		boldFont20.setFontHeight(20);

		writeCell(workbook, row, 0, tenant.getName(), FormatType.TEXT, null, boldFont20);

	}
	
	public void postProcessXLS(Object document) {  
		XSSFWorkbook wb = (XSSFWorkbook) document;  
	    XSSFSheet sheet = wb.getSheetAt(0);  
	    for(int i=0; i<sheet.getPhysicalNumberOfRows();i++){
		    XSSFRow header = sheet.getRow(i);  
		    XSSFFont boldFont = wb.createFont();
		    boldFont.setBold(true);
		    boldFont.setFontHeight(14);
		    XSSFCellStyle cellStyle = wb.createCellStyle();    
		    cellStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(88,130,250)));  
		    cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);  
		    cellStyle.setFont(boldFont);
		    
		    if(header!=null){
			    for(int j=0; j < header.getPhysicalNumberOfCells();j++) {  
			        XSSFCell cell = header.getCell(j); 
			        int celltype = cell==null? Cell.CELL_TYPE_BLANK:cell.getCellType(); 
			       if( celltype == Cell.CELL_TYPE_STRING){
			            String s = cell.getStringCellValue();
			            if(s.equalsIgnoreCase("Monthly Event Detail")){
			            	cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			            	cell.setCellStyle(cellStyle);
			            }
			            if(s.equalsIgnoreCase("Total")){
			            	XSSFCellStyle style = wb.createCellStyle();
			            	style.setFont(boldFont);
			            	style.setAlignment(CellStyle.ALIGN_LEFT);
			            	cell.setCellStyle(style);
			            }
			       }
			          
			    } 
		    }
	    }
	}   
	
	private void writeCell(XSSFWorkbook workbook, XSSFRow row, int col, Object value, FormatType formatType,
			Short bgColor, XSSFFont font) {
		XSSFCell cell = row.createCell(col);
		if (value == null) {
			return;
		}

		if (font != null) {
			XSSFCellStyle style = workbook.createCellStyle();
			style.setFont(font);
			cell.setCellStyle(style);
		}

		switch (formatType) {
		case TEXT:
			cell.setCellValue(value.toString());
			break;
		
		case DATE:
			XSSFDataFormat df = workbook.createDataFormat();
			XSSFCellStyle cs = workbook.createCellStyle();
			cs.setDataFormat(df.getFormat("m/d/yy"));
			cell.setCellValue((Date) value);
			cell.setCellStyle(cs);
			
			break;
		
		}

		/*if (bgColor != null) {
			HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.FILL_FOREGROUND_COLOR, bgColor);
			HSSFCellUtil.setCellStyleProperty(cell, workbook, CellUtil.FILL_PATTERN, HSSFCellStyle.SOLID_FOREGROUND);
		}*/
	}
	public Tenant getTenant() {
		return tenant;
	}
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}



	public List<OneTouchReport> getEventDetailList() {
		return eventDetailList;
	}



	public void setEventDetailList(List<OneTouchReport> eventDetailList) {
		this.eventDetailList = eventDetailList;
	}

	public String getSelectedMonth() {
		return selectedMonth;
	}

	public void setSelectedMonth(String selectedMonth) {
		this.selectedMonth = selectedMonth;
	}

	public String[] getMonths() {
		return months;
	}

	public void setMonths(String[] months) {
		this.months = months;
	}

	public Date getReportStartDate() {
		return reportStartDate;
	}

	public void setReportStartDate(Date reportStartDate) {
		this.reportStartDate = reportStartDate;
	}

	public Date getReportEndDate() {
		return reportEndDate;
	}

	public void setReportEndDate(Date reportEndDate) {
		this.reportEndDate = reportEndDate;
	}

	
	
}
