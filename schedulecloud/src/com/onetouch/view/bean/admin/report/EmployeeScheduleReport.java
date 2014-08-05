package com.onetouch.view.bean.admin.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
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


import com.onetouch.model.domainobject.Employee;

import com.onetouch.model.domainobject.OneTouchReport;

import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.model.domainobject.TimeOffRequest;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.util.SupportBean;
@ManagedBean(name="employeeScheduleReport")
@ViewScoped
public class EmployeeScheduleReport extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OneTouchReport> employeeScheduleList;
	private Integer totalDaysWorked;
	private Integer totalHoursWorked;
	private Employee selectedEmployee;
	private Tenant tenant;
	private List<Employee> employeeList;
	private Map<String, List<TimeOffRequest>> vacationTimeOffMap;
	private Map<String, List<TimeOffRequest>> sickTimeOffMap;
	private String selectedMonth = "January";
	private String[] months;
	@ManagedProperty(value="#{supportBean}")
	private SupportBean supportBean;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@PostConstruct
	public void initEmployeeScheduleReport(){
		tenant = tenantContext.getTenant();
		
		employeeList = getEmployeeService().getAllEmployee(tenant, new String[]{"active"},regionBean.getSelectedRegion());
		selectedEmployee = employeeList.get(0);
		employeeScheduleList = getReportService().getAllScheduleByEmployee(selectedEmployee,tenant,selectedMonth);
		
		this.populateTimeoff(selectedEmployee);
		
		months = new String[]{"YTD","January","February","March","April","May","June","July","August","September","October","November","December"};
	}
	public void changeEmployee(){
		employeeScheduleList.clear();
		employeeScheduleList = getReportService().getAllScheduleByEmployee(selectedEmployee, tenant,selectedMonth);
		this.populateTimeoff(selectedEmployee);
	}
	public void changeMonth(){
		employeeScheduleList.clear();
		employeeScheduleList = getReportService().getAllScheduleByEmployee(selectedEmployee, tenant,selectedMonth);
		this.populateTimeoff(selectedEmployee);
	}
	private void populateTimeoff(Employee employee){
		List<TimeOffRequest> vacationTimeOffList = getTimeoffService().getAllVacationTimeOffRequestsByEmployee(employee,tenant,selectedMonth);
		List<TimeOffRequest> sickTimeOffList = getTimeoffService().getAllSickTimeOffRequestByEmployee(employee,tenant,selectedMonth);
		vacationTimeOffMap = new HashMap<String, List<TimeOffRequest>>();
		sickTimeOffMap = new HashMap<String, List<TimeOffRequest>>();
		sickTimeOffMap.put("Sick", sickTimeOffList);
		vacationTimeOffMap.put("Vacation", vacationTimeOffList);
	}
	
	public void preProcessorXLS(Object document){
		XSSFWorkbook workbook = (XSSFWorkbook) document;  
	    XSSFSheet sheet = workbook.getSheetAt(0);
	    XSSFFont boldFont = workbook.createFont();
	    boldFont.setBold(true);
	    boldFont.setColor(IndexedColors.ORANGE.getIndex()); //new XSSFColor(new java.awt.Color(247,190,129)
	    XSSFRow row = sheet.createRow(0);
	    XSSFRow row0 = sheet.createRow(1);


	    XSSFFont boldFont20 = workbook.createFont();
	    boldFont20.setBold(true);
	    boldFont20.setColor(IndexedColors.ORANGE.getIndex());
	    boldFont20.setFontHeight(20);

	    writeCell(workbook, row, 0, tenant.getName(), FormatType.TEXT, null, boldFont20);
	    writeCell(workbook, row0, 0, "Employee", FormatType.TEXT, null, boldFont);
	    writeCell(workbook, row0, 1, selectedEmployee.getFirstname()+" "+selectedEmployee.getLastname(), FormatType.TEXT, null, null);

			
		 
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
			            if(s.equalsIgnoreCase("Employee Schedule Report")){
			            	cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
			            	cell.setCellStyle(cellStyle);
			            }
			            if(s.equalsIgnoreCase("Total Hours")){
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
	public List<OneTouchReport> getEmployeeScheduleList() {
		return employeeScheduleList;
	}
	public void setEmployeeScheduleList(List<OneTouchReport> employeeScheduleList) {
		this.employeeScheduleList = employeeScheduleList;
	}
	public Integer getTotalDaysWorked() {
		return totalDaysWorked;
	}
	public void setTotalDaysWorked(Integer totalDaysWorked) {
		this.totalDaysWorked = totalDaysWorked;
	}
	public Integer getTotalHoursWorked() {
		return totalHoursWorked;
	}
	public void setTotalHoursWorked(Integer totalHoursWorked) {
		this.totalHoursWorked = totalHoursWorked;
	}
	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}
	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}
	public List<Employee> getEmployeeList() {
		return employeeList;
	}
	public void setEmployeeList(List<Employee> employeeList) {
		this.employeeList = employeeList;
	}
	public List getSickTimeOffMap(){
		List entrySet = new ArrayList(sickTimeOffMap.entrySet());
		
		return entrySet;
	}
	public List getVacationTimeOffMap(){
		List entrySet = new ArrayList(vacationTimeOffMap.entrySet());
		
		return entrySet;
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
	public SupportBean getSupportBean() {
		return supportBean;
	}
	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}
	public RegionBean getRegionBean() {
		return regionBean;
	}
	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}
	
}
