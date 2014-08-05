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

public class ReportHome_OLD extends BaseBean{

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
		
		reportId = 5;
		this.reportView = "detailAvailabilityReport.xhtml";
		this.reportViewTarget = "availabilitytbl";
		this.filename = "Detailed Availability Report";
		
		
		/*reportId = 1;
		this.reportView = "laborReport.xhtml";
		this.reportViewTarget = "forecastlabortbl,actuallabortbl";
		this.filename = "LABOR_FORECAST_VS_ACTUAL";*/
		
		scheduledEmployees = new ArrayList<Employee>();
		eventPosEmployees = new ArrayList<Employee>();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
			
		}
		publishedEventList = new ArrayList<Event>();//new api in eventdaogetEventService().getAllEvents(tenant, new String[]{"published"},customUser);
		if(publishedEventList!=null && publishedEventList.size()>0){
			Event signInOutEvent = publishedEventList.get(0);//order by event start time
			laborEvent = getEventService().getEventDetail(signInOutEvent.getId(), tenant);
			List<Position> scheduledPositions  = getScheduleService().getScheduleByEvent(signInOutEvent, tenant);
			this.populateScheduledEmployees(scheduledPositions,signInOutEvent);
			
			List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(signInOutEvent, tenant);
			this.populateEventPositionEmployees(allEventPositions,signInOutEvent);
		}
		populateEventsByMonth();
		
	}
	public void populateScheduledEmployees(List<Position> scheduledPositions,Event signInOutEvent){
		for(Position position : scheduledPositions){
			List<Employee> empList = position.getScheduledEmployees();
			for(Employee employee : empList){
				EmployeeRate employeeRate = getEmployeeService().getEmployeeRateById(signInOutEvent,employee.getId(), tenant);
				employee.setEmpRate(employeeRate);
				employee.setTenant(tenant);

				//BigDecimal working_hours = new BigDecimal(employee.getHours());
				/*if(employeeRate!=null)
					employeeRate.setRegPay(employeeRate.getHourlyRate().multiply(working_hours));*/
				employee.setPosition(position);
				employee.setCurrentScheduledEvent(signInOutEvent);
				scheduledEmployees.add(employee);
			}
		}
	}
	public void populateEventPositionEmployees(List<EventPosition> allEventPositions,Event signInOutEvent){
		for(EventPosition eventPosition : allEventPositions){
			
			Position position = eventPosition.getPosition();
			List<Employee> evpEmps = position.getScheduledEmployees();
			for(Employee employee : evpEmps){
				EmployeeRate employeeRate = getEmployeeService().getEmployeeRateById(signInOutEvent,employee.getId(), tenant);
				employee.setEmpRate(employeeRate);
				employee.setTenant(tenant);
				//BigDecimal working_hours = new BigDecimal(employee.getHours());
				/*if(employeeRate!=null)
					employeeRate.setRegPay(employeeRate.getHourlyRate().multiply(working_hours));*/
				employee.setPosition(position);
				employee.setShiftStartTime(eventPosition.getStartTime());
				employee.setShiftEndTime(eventPosition.getEndTime());
				
				employee.setCurrentScheduledEvent(signInOutEvent);
				eventPosEmployees.add(employee);
			}
	}
	}
	private void populateEventsByMonth() {
		eventListByMonth = new ArrayList<SelectItem>();
        
        List<OneTouchReport> list = getReportService().getAllPublishedEventsByMonth(tenant,customUser,false,null);
        for(OneTouchReport report : list){
        	List<Event> events = report.getEventList();
        	
        	SelectItemGroup sig = new SelectItemGroup(report.getMonth());
        	SelectItem[] selectItems = new SelectItem[events.size()]; 
        	for(int i = 0;i<events.size();i++){
        		Event event = events.get(i);
        		SelectItem item = new SelectItem(event,event.getName());
        		selectItems[i] = item;
        	}
            sig.setSelectItems(selectItems);
            eventListByMonth.add(sig);
        }
        
        
        
		
	}
	public void ChangeReportView(AjaxBehaviorEvent ajaxBehaviorEvent){
		if(reportId.intValue()==1){
			this.reportView = "laborReport.xhtml";
			this.reportViewTarget = "forecastlabortbl,actuallabortbl";
			this.filename = "LABOR FORECAST VS ACTUAL";
		}
		else if(reportId.intValue()==2){
			this.reportView = "employeeScheduleReport.xhtml";
			this.reportViewTarget = "empschreporttbl";
			this.filename = "Employee Schedule Report";
		}
		else if(reportId.intValue()==3){
			this.reportView = "timeoffReport.xhtml";
			this.reportViewTarget = "timeoffreporttbl";
			this.filename = "Timeoff Report";
		}
		else if(reportId.intValue()==4){
			this.reportView = "eventDetailReport.xhtml";
			this.reportViewTarget = "eventDetailtbl";
			this.filename = "Event Detail Report";
		}else if(reportId.intValue()==5){
			this.reportView = "detailAvailabilityReport.xhtml";
			this.reportViewTarget = "availabilitytbl";
			this.filename = "Detailed Availability Report";
		}
		else{
			
		}
	}
	public void changeReportEvent(){
		
	}
	
	
	public void preProcessPDF(Object document) throws IOException, BadElementException, DocumentException {  
	    Document pdf = (Document) document;  
	    pdf.open();  
	    pdf.setPageSize(PageSize.A4);  
	  
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "logo.jpg";  
	    Image image = Image.getInstance(logo);
        image.setAbsolutePosition(0f, 0f);
        
	    pdf.add(image);  
	}  
	public void changeReportTables(AjaxBehaviorEvent ajaxBehaviorEvent){
		scheduledEmployees = new ArrayList<Employee>();
		eventPosEmployees = new ArrayList<Employee>();
			for(Event event : publishedEventList){
				if(event.getId().intValue()==laborEvent.getId().intValue()){
					List<Position> scheduledPositions  = getScheduleService().getScheduleByEvent(event, tenant);
					this.populateScheduledEmployees(scheduledPositions,event);
					
					List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
					this.populateEventPositionEmployees(allEventPositions,event);
					laborEvent = getEventService().getEventDetail(event.getId(), tenant);
					return;
				}
			}
	}
	
	public void empoyeeSchedulePreProcessorXLS(Object document){
		XSSFWorkbook workbook = (XSSFWorkbook) document;  
	    XSSFSheet sheet = workbook.getSheetAt(0);
	    XSSFFont boldFont = workbook.createFont();
	    boldFont.setBold(true);
	    boldFont.setColor(IndexedColors.ORANGE.getIndex()); //new XSSFColor(new java.awt.Color(247,190,129)
	    XSSFRow row = sheet.createRow(0);
	    XSSFRow row0 = sheet.createRow(1);
	    XSSFRow row1 = sheet.createRow(2);
	    XSSFRow row2 = sheet.createRow(3);
	    
	    	XSSFFont boldFont20 = workbook.createFont();
	    	boldFont20.setBold(true);
	    	boldFont20.setColor(IndexedColors.ORANGE.getIndex());
	    	boldFont20.setFontHeight(20);
	    	
	    	writeCell(workbook, row, 0, tenant.getName(), FormatType.TEXT, null, boldFont20);
	    	writeCell(workbook, row0, 0, "Title", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row0, 1, laborEvent.getName(), FormatType.TEXT, null, null);
			
			writeCell(workbook, row1, 0, "Event Time", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row1, 1, laborEvent.getStartDate(), FormatType.DATE, null, null);
			//writeCell(workbook, row1, 2, laborEvent.getStartTime(), FormatType.DATE, null, null);
			//writeCell(workbook, row1, 3, laborEvent.getEndTime(), FormatType.DATE, null, null);
			
			Location location = laborEvent.getLocation();
			if(location.getId()!=null)
				location = getLocationService().getLocation(location.getId());
			
		    String address = location.getAddress1()+", "+location.getAddress2()+", "+location.getCity()+", "+location.getState().getStateName()+", "+location.getZipcode();
		   	writeCell(workbook, row2, 0, "Location", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row2, 1, address, FormatType.TEXT, null, null);
			//writeCell(workbook, row, 2, event.getName(), FormatType.TEXT, null, boldFont);
			//writeCell(workbook, row, 3, "title", FormatType.TEXT, null, boldFont);
			
			/*writeCell(workbook, row3, 0, "Admin", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row3, 1, event.getOwner(), FormatType.TEXT, null, null);
			
			writeCell(workbook, row4, 0, "Manager", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row4, 1, event.getCoordinator(), FormatType.TEXT, null, null);
			
			writeCell(workbook, row5, 0, "Client/Host", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row5, 1, event.getHostname(), FormatType.TEXT, null, null);
			
			writeCell(workbook, row6, 0, "Notes", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row6, 1, event.getNotes(), FormatType.TEXT, null, null);
			
			writeCell(workbook, row7, 0, "Dress", FormatType.TEXT, null, boldFont);
			writeCell(workbook, row7, 1, event.getDressCode(), FormatType.TEXT, null, null);*/
		 
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
			            if(s.equalsIgnoreCase("Actual Labor Report")){
			            	cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			            	cell.setCellStyle(cellStyle);
			            }
			            if(s.equalsIgnoreCase("Forecast Labor Report")){
			            	cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			            	cell.setCellStyle(cellStyle);
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
	public Integer getTotalActualHrs(){
		int totalActHrs = 0;
		int totalActMins = 0;
		for(Employee employee : scheduledEmployees){
			totalActHrs = totalActHrs+employee.getHours();
			totalActMins = totalActMins+employee.getMinutes();
		}
		if(totalActMins>60){
			int hourConvert = totalActMins/60;
			totalActHrs = totalActHrs+hourConvert;
		}
		return totalActHrs;
		
	}
	public Integer getTotalActualMinutes(){
		int totalActMins = 0;
		for(Employee employee : scheduledEmployees){
			totalActMins = totalActMins+employee.getMinutes();
		}
		if(totalActMins>60){
			int hourConvert = totalActMins/60;
			
			totalActMins = totalActMins - (60*hourConvert);
		}
		return totalActMins;
	}
	public BigDecimal getTotalActualPay(){
		BigDecimal totalActualPay = new BigDecimal(0.0);
		for(Employee employee : scheduledEmployees){
			totalActualPay = totalActualPay.add(employee.getTotalPay());
		}
		
		return totalActualPay;
	}
	public Integer getTotalForecastHrs(){
		int totalForecastHrs = 0;
		int totalForecastMins = 0;
		for(Employee employee : eventPosEmployees){
			totalForecastHrs = totalForecastHrs+employee.getHours();
			totalForecastMins = totalForecastMins+employee.getMinutes();
		}
		if(totalForecastMins>60){
			int hourConvert = totalForecastMins/60;
			
			totalForecastHrs = totalForecastHrs + hourConvert;
		}
		return totalForecastHrs;
		
	}
	public Integer getTotalForecastMinutes(){
		int totalForecastMins = 0;
		for(Employee employee : eventPosEmployees){
			totalForecastMins = totalForecastMins+employee.getMinutes();
		}
		if(totalForecastMins>60){
			int hourConvert = totalForecastMins/60;
			
			totalForecastMins = totalForecastMins - (60*hourConvert);
		}
		return totalForecastMins;
	}
	public BigDecimal getTotalForecastPay(){
		BigDecimal totalForecastPay = new BigDecimal(0.0);
		for(Employee employee : eventPosEmployees){
			totalForecastPay = totalForecastPay.add(employee.getTotalPay());
		}
		
		return totalForecastPay;
	}
	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}
	public String getReportView() {
		return reportView;
	}
	public void setReportView(String reportView) {
		this.reportView = reportView;
	}
	public String getReportViewTarget() {
		return reportViewTarget;
	}
	public void setReportViewTarget(String reportViewTarget) {
		this.reportViewTarget = reportViewTarget;
	}
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public List<Employee> getScheduledEmployees() {
		return scheduledEmployees;
	}
	public void setScheduledEmployees(List<Employee> scheduledEmployees) {
		this.scheduledEmployees = scheduledEmployees;
	}
	public List<Employee> getEventPosEmployees() {
		return eventPosEmployees;
	}
	public void setEventPosEmployees(List<Employee> eventPosEmployees) {
		this.eventPosEmployees = eventPosEmployees;
	}
	public List<SelectItem> getEventListByMonth() {
		return eventListByMonth;
	}
	public void setEventListByMonth(List<SelectItem> eventListByMonth) {
		this.eventListByMonth = eventListByMonth;
	}
	public Event getLaborEvent() {
		return laborEvent;
	}
	public void setLaborEvent(Event laborEvent) {
		this.laborEvent = laborEvent;
	}
	public SupportBean getSupportBean() {
		return supportBean;
	}
	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}
	
	
	
}
