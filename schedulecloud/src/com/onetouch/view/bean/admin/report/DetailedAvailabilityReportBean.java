package com.onetouch.view.bean.admin.report;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

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
import org.primefaces.event.data.FilterEvent;
import org.primefaces.util.Constants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.DetailedAvailReport;
import com.onetouch.model.domainobject.DetailedAvailReportByEmployee;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.OneTouchReport;

import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.employee.EmployeeHome;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.position.PositionHome;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageXofY;

import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.ServletContext;


@ManagedBean(name="detailedAvailReportBean")
@ViewScoped
public class DetailedAvailabilityReportBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<OneTouchReport> detailedAvailList; 
	private List<OneTouchReport> filterAvailList;
	private Tenant tenant;
	private CustomUser customUser;
	private String selectedMonth = "January";
	private String[] months;
    private Position selectedPosition;
    private List<Position> selectedPositions;
    private String positionList;
    private boolean allRegion;
    private Date reportStartDate;
	private Date reportEndDate;
	private String nameSearchValue;
	private String idSearchValue;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	@ManagedProperty(value="#{positionHome}")
	private PositionHome positionHome;
	private Employee selectedEmployee;
	private List<Employee> employees;
	private List<DetailedAvailReportByEmployee> empStatusResult;
	@PostConstruct
	public void initBean(){
		tenant = tenantContext.getTenant();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated()){
			customUser = (CustomUser)authenticationToken.getPrincipal();
		
		}
		reportStartDate = reportEndDate = new Date();
		positionHome = (PositionHome)FacesUtils.getManagedBean("positionHome");
		selectedPositions = positionHome.getPositionList();
	    detailedAvailList = getReportService().getDetailedAvailReportByEmployeeDateRange(selectedPositions,reportStartDate, reportEndDate,regionBean.getSelectedRegion(),tenant);
		
	}
	@PreDestroy
	public void destroy()
	{
		System.out.println("Report Destroy");
	}
	public void selectStartDate(SelectEvent selectevent){
		reportEndDate = reportStartDate;
	}
	public void selectEndDate(SelectEvent event){
		if(this.reportEndDate!=null && this.reportStartDate!=null){
			if(this.reportEndDate.getTime()<this.reportStartDate.getTime())
				FacesUtils.addErrorMessage("Enter a valid end date");
			else{
				
			}
		}
			
	}
	
	public void filterDetailAvailabilityReport(){
	if(allRegion)
		detailedAvailList = getReportService().getDetailedAvailReportByEmployeeDateRange( selectedPositions,reportStartDate, reportEndDate,null,tenant);
	else
       detailedAvailList = getReportService().getDetailedAvailReportByEmployeeDateRange( selectedPositions,reportStartDate, reportEndDate,regionBean.getSelectedRegion(),tenant);
       			
	}
	public void findAllEmployeeStatus(){
		if(allRegion)
			detailedAvailList = getReportService().getAllEmployeeStatusByDateRange( selectedPositions,reportStartDate, reportEndDate,null,tenant);
		else
			detailedAvailList = getReportService().getAllEmployeeStatusByDateRange( selectedPositions,reportStartDate, reportEndDate,regionBean.getSelectedRegion(),tenant);
	       			
		}
	public void filterListener(){
		//Map<String, String> filters =  filterEvent.getFilters();
		String s = nameSearchValue.toLowerCase();//filters.get("globalFilter").toLowerCase();
		if(!(s.contains(" ")||s.contains(","))){
			if(s.equalsIgnoreCase(""))
				detailedAvailList = getReportService().getDetailedAvailReportByEmployeeDateRange(selectedPositions,reportStartDate, reportEndDate,regionBean.getSelectedRegion(),tenant);
			else{
				filterAvailList = new ArrayList<OneTouchReport>();
				for(OneTouchReport report : detailedAvailList){
					List<DetailedAvailReportByEmployee> temp = new ArrayList<DetailedAvailReportByEmployee>();
					List<DetailedAvailReportByEmployee>  detailedAvailReports = report.getDetailedAvailReports();
					for(DetailedAvailReportByEmployee reportByEmployee : detailedAvailReports){
						String firstname = reportByEmployee.getEmployeeFirstname().toLowerCase();
						String lastname =  reportByEmployee.getEmployeeLastname().toLowerCase();
						boolean found = firstname.startsWith(s) || lastname.startsWith(s);
						if(found)
							temp.add(reportByEmployee);
							
					}
					if(temp.size()>0){
						OneTouchReport temp2 = new OneTouchReport();
						temp2.setDetailedAvailReports(temp);
						temp2.setReportDate(report.getReportDate());
						temp2.setPosition(report.getPosition());
						filterAvailList.add(temp2);
					}
				}
				detailedAvailList = filterAvailList;
			}
		}else{
			
		}
	}
	public void filterIdListener(){
		//Map<String, String> filters =  filterEvent.getFilters();
		String s = idSearchValue.toLowerCase();//filters.get("globalFilter").toLowerCase();
		if(s=="")
			detailedAvailList = getReportService().getDetailedAvailReportByEmployeeDateRange(selectedPositions,reportStartDate, reportEndDate,regionBean.getSelectedRegion(),tenant);
		else{
			filterAvailList = new ArrayList<OneTouchReport>();
			for(OneTouchReport report : detailedAvailList){
				List<DetailedAvailReportByEmployee> temp = new ArrayList<DetailedAvailReportByEmployee>();
				List<DetailedAvailReportByEmployee>  detailedAvailReports = report.getDetailedAvailReports();
				for(DetailedAvailReportByEmployee reportByEmployee : detailedAvailReports){
					String id = reportByEmployee.getEmployeeId().toString().toLowerCase();
					boolean found = id.startsWith(s);
					if(found)
						temp.add(reportByEmployee);
						
				}
				if(temp.size()>0){
					OneTouchReport temp2 = new OneTouchReport();
					temp2.setDetailedAvailReports(temp);
					temp2.setReportDate(report.getReportDate());
					temp2.setPosition(report.getPosition());
					filterAvailList.add(temp2);
				}
			}
			detailedAvailList = filterAvailList;
		}
	}
	/*public List<Employee> completeEmployer(String query) { 
		List<Employee> suggestions = new ArrayList<Employee>(); 
		if(employees==null || employees.size()<1)
			employees = getEmployeeService().getAllEmployee(tenant, new String[]{"active"},regionBean.getSelectedRegion());
		for(Employee emp : employees) 
		{ 
			String fullName = emp.getFirstname().toLowerCase()+" "+emp.getLastname().toLowerCase(); 
			String reverseFullName = emp.getLastname().toLowerCase()+" "+emp.getFirstname().toLowerCase();
			String querySearch = query.trim().toLowerCase();
			boolean found = fullName.startsWith(querySearch) || reverseFullName.startsWith(querySearch) || emp.getId().toString().startsWith(querySearch);
			if(found)
				suggestions.add(emp); 
		} 
		return suggestions; 
	}
	public void filterEmployeeListener(SelectEvent selectEvent){
			filterAvailList = new ArrayList<OneTouchReport>();
			for(OneTouchReport report : detailedAvailList){
				List<DetailedAvailReportByEmployee> temp = new ArrayList<DetailedAvailReportByEmployee>();
				List<DetailedAvailReportByEmployee>  detailedAvailReports = report.getDetailedAvailReports();
				for(DetailedAvailReportByEmployee reportByEmployee : detailedAvailReports){
					boolean found = reportByEmployee.getEmployeeId().equalsIgnoreCase(selectedEmployee.getId().toString());
					if(found)
						temp.add(reportByEmployee);
						
				}
				if(temp.size()>0){
					OneTouchReport temp2 = new OneTouchReport();
					temp2.setDetailedAvailReports(temp);
					temp2.setReportDate(report.getReportDate());
					temp2.setPosition(report.getPosition());
					filterAvailList.add(temp2);
				}
			}
			detailedAvailList = filterAvailList;
		
	}*/
	public String detailAvlPreProcessorPDF(){
		
		Document pdf = new Document();  
	    pdf.setPageSize(PageSize.A4.rotate());  
	    Font smallBold = new Font(Font.TIMES_ROMAN, 12,Font.BOLD);
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    try {
			PdfWriter writer = PdfWriter.getInstance(pdf, baos);
	    	PageXofY pageXofY = new PageXofY();
	        writer.setPageEvent(pageXofY);
            this.addFooter(pdf);
			pdf.open();
			
			Paragraph eventDetail = new Paragraph();
			eventDetail.setAlignment(Element.ALIGN_CENTER);
		    eventDetail.add(new Paragraph("Region : "+regionBean.getSelectedRegion().getName(),smallBold));
		    String event_time = "";
		    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");;
			event_time = event_time+ dateformat.format(reportStartDate);
			event_time = event_time+ " to "+ dateformat.format(reportEndDate);
			eventDetail.add(new Paragraph("Date : "+event_time));
		    
		    eventDetail.add(new Paragraph(""));
		    
		    pdf.add(eventDetail);
		    DetailAvailabilityPDFExporter availabilityPDFExporter = new DetailAvailabilityPDFExporter();
		    availabilityPDFExporter.exportAvailabilityPDFTable(pdf,detailedAvailList);
           /* for(OneTouchReport oneTouchReport : detailedAvailList) {
            	availabilityPDFExporter.exportAvailabilityPDFTable(document,oneTouchReport);
            }*/
            pdf.add( Chunk.NEWLINE );
            pdf.close();
		    writePDFToResponse(FacesContext.getCurrentInstance().getExternalContext(), baos, "Roster_Schedule");
	    }catch (BadElementException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private void addHeader(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "small_logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    logo_iamge.setAlignment(Image.MIDDLE);
	    logo_iamge.scaleAbsoluteHeight(30);
	    logo_iamge.scaleAbsoluteWidth(20);
	    logo_iamge.scalePercent(80);

	    
	    Chunk chunk = new Chunk(logo_iamge, 0, 0);
	    HeaderFooter header = new HeaderFooter(new Phrase(chunk), false);
	    header.setBorder(Rectangle.NO_BORDER); 
	    header.setAlignment(Element.ALIGN_CENTER);
	    pdf.setHeader(header);
	   
	}
	private void addFooter(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		HeaderFooter footer = new HeaderFooter(new Phrase("Schedule-cloud, all rights reserved"), false);
		footer.setBorder(Rectangle.NO_BORDER); 
	    footer.setAlignment(Element.ALIGN_CENTER);
	    pdf.setFooter(footer);
	   
  
	}
	protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName)throws IOException, DocumentException {
	externalContext.setResponseContentType("application/pdf");
	externalContext.setResponseHeader("Expires", "0");
	externalContext.setResponseHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	externalContext.setResponseHeader("Pragma", "public");
	externalContext.setResponseHeader("Content-disposition", "attachment;filename=" + fileName + ".pdf");
	externalContext.setResponseContentLength(baos.size());
	externalContext.addResponseCookie(Constants.DOWNLOAD_COOKIE, "true", Collections.<String, Object>emptyMap());
	OutputStream out = externalContext.getResponseOutputStream();
	baos.writeTo(out);
	externalContext.responseFlushBuffer();
	FacesContext.getCurrentInstance().responseComplete();
	}
	public List<OneTouchReport> getDetailedAvailList() {
		return detailedAvailList;
	}

	public void setDetailedAvailList(List<OneTouchReport> detailedAvailList) {
		this.detailedAvailList = detailedAvailList;
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
	public Position getSelectedPosition() {
		return selectedPosition;
	}

	public void setSelectedPosition(Position selectedPosition) {
		this.selectedPosition = selectedPosition;
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

	public boolean isAllRegion() {
		return allRegion;
	}

	public void setAllRegion(boolean allRegion) {
		this.allRegion = allRegion;
	}

	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	public List<OneTouchReport> getFilterAvailList() {
		return filterAvailList;
	}

	public void setFilterAvailList(List<OneTouchReport> filterAvailList) {
		this.filterAvailList = filterAvailList;
	}

	public List<Position> getSelectedPositions() {
		return selectedPositions;
	}

	public void setSelectedPositions(List<Position> selectedPositions) {
		this.selectedPositions = selectedPositions;
	}

	public String getPositionList() {
		if(selectedPositions!=null && selectedPositions.size()>0){
			if(selectedPositions.size()==positionHome.getPositionList().size())
				return "All Positions";
			else{
				/*String s = "[ ";
				for(Position position : selectedPositions){
					s=s+position.getName()+", ";
				}
				s=s.substring(0,s.length()-2);
				s=s+" ]";
				return s;*/
				return "Select Position";
			}
		}else
			return "Select Position";
	}

	public PositionHome getPositionHome() {
		return positionHome;
	}

	public void setPositionHome(PositionHome positionHome) {
		this.positionHome = positionHome;
	}

	public String getNameSearchValue() {
		return nameSearchValue;
	}

	public void setNameSearchValue(String nameSearchValue) {
		this.nameSearchValue = nameSearchValue;
	}

	public String getIdSearchValue() {
		return idSearchValue;
	}

	public void setIdSearchValue(String idSearchValue) {
		this.idSearchValue = idSearchValue;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	public Employee getSelectedEmployee() {
		return selectedEmployee;
	}

	public void setSelectedEmployee(Employee selectedEmployee) {
		this.selectedEmployee = selectedEmployee;
	}

	public List<DetailedAvailReportByEmployee> getEmpStatusResult() {
		return empStatusResult;
	}

	public void setEmpStatusResult(
			List<DetailedAvailReportByEmployee> empStatusResult) {
		this.empStatusResult = empStatusResult;
	}
	
	
	
}
