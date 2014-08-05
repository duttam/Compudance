package com.onetouch.view.bean.useremployee.schedule;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletContext;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;
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
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.CustomUserDetail;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.event.EventPositionPDFExporter;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.useremployee.availability.AvailabilityHome;
import com.onetouch.view.bean.util.EventDetailPDFBean;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageXofY;

@ManagedBean(name="employeeScheduleHome")
@ViewScoped
public class EmployeeScheduleHome extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ScheduleModel employeeScheduleModel;  
	private CustomUser user = null;
	private Employee employee = null;
	private List<Schedule> allSchedules;
	private Map<Integer,Schedule> allScheduleMap;
	private Schedule schedule;
	private Event selectedEvent;
	private Tenant tenant;
	
	private List<Employee> printEmployeeScheduleList;
	@ManagedProperty(value="#{availHome}")
    private AvailabilityHome availHome;
	
	//@ManagedProperty(value="#{eventDetailPDF}")
    private EventDetailPDFBean eventDetailPDF;
	/*public EmployeeScheduleHome() {  
		employeeScheduleModel = new DefaultScheduleModel();  
		employeeScheduleModel.addEvent(new DefaultScheduleEvent("Champions League Match", new Date(), new Date(),new Integer(1)));  
		employeeScheduleModel.addEvent(new DefaultScheduleEvent("Birthday Party", new Date(), new Date(),new Integer(2)));  
		employeeScheduleModel.addEvent(new DefaultScheduleEvent("Breakfast at Tiffanys", new Date(),new Date(),new Integer(3)));  
		employeeScheduleModel.addEvent(new DefaultScheduleEvent("Plant the new garden stuff", new Date(), new Date(),new Integer(4)));  
    }*/
	
	
	
	@PostConstruct
	public void initScheduleHome(){
		employeeScheduleModel = new DefaultScheduleModel();
		UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
		if(authenticationToken.isAuthenticated())
			user = (CustomUser)authenticationToken.getPrincipal();
		
		tenant = tenantContext.getTenant();
		String fromview = availHome.getFromview();
		if(fromview!=null && fromview.equalsIgnoreCase("admin")){
			Integer empId = availHome.getEmpId();
			employee = getEmployeeService().getEmplyeePositionById(empId, tenant);
		}
		else
			employee = getEmployeeService().getEmplyeePositionById(user.getEmp_id(), tenant);
		if(FacesUtils.isMobileRequest()){
			allSchedules = getScheduleService().getAllScheduledEventPositionsByDate(employee,tenant,"");//allScheduledShifts(employee,tenant);
			
		}else{
			
			allSchedules = getScheduleService().getAllScheduledEventPositions(employee,tenant);//allScheduledShifts(employee,tenant);
			allScheduleMap = new HashMap<Integer, Schedule>();
			for(Schedule schedule : allSchedules){
				Event event = schedule.getEvent();
				EventPosition eventPosition = schedule.getEventPosition();
				//Date eventStartTime =  DateUtil.getDateAndTime(event.getStartDate(), eventPosition.getStartTime());
				//Date eventEndTime = DateUtil.getDateAndTime(event.getEndDate(),eventPosition.getEndTime());
				Date scheduleStartTime =  DateUtil.getDateAndTime(event.getStartDate(), eventPosition.getStartTime());
				Date scheduleEndTime = DateUtil.getDateAndTime(event.getEndDate(),eventPosition.getEndTime());
				
				ScheduleEvent employeeSchedule = null;
				if(fromview!=null && fromview.equalsIgnoreCase("admin"))
					employeeSchedule = new DefaultScheduleEvent("",scheduleStartTime,scheduleEndTime,schedule.getId());
				else
					employeeSchedule = new DefaultScheduleEvent(schedule.getPosition().getName(),scheduleStartTime,scheduleEndTime,schedule.getId());
				employeeScheduleModel.addEvent(employeeSchedule);
				allScheduleMap.put(schedule.getId(),schedule);
			}
		}
		eventDetailPDF = new EventDetailPDFBean();
	}
	
	public void filterMobileSchedules(ActionEvent event){
		String mode = FacesUtils.getRequestParameter("mode");
		allSchedules = getScheduleService().getAllScheduledEventPositionsByDate(employee,tenant,mode);//allScheduledShifts(employee,tenant);
		
	}
	public void onScheduleEventSelect(SelectEvent selectEvent) {  
        ScheduleEvent scheduleEvent = (ScheduleEvent) selectEvent.getObject(); 
        if(scheduleEvent!=null){
	        Integer scheduleId = (Integer)scheduleEvent.getData();
	        schedule = allScheduleMap.get(scheduleId);
	        Position position = schedule.getPosition();
	        if(position.isViewReports()){
	        	Event event = getEventService().getEventDetail(schedule.getEvent().getId(), tenant);
	        	List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
	        	schedule.setEvent(event);
	        	printEmployeeScheduleList = eventDetailPDF.showEventDetailReport(allEventPositions);
	        }
        }else{
        	System.out.print(user.getUsername()+" "+scheduleEvent);
        }
        /*String contextRoot = FacesUtils.getServletContext().getContextPath();
        
        try {
			FacesContext.getCurrentInstance().getExternalContext().redirect( contextRoot +"/ui/useremployee/schedule/employeeScheduleDetail.jsf?faces-redirect=true&scheduleId="+scheduleId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
	public void goBack(ActionEvent actionEvent){
		this.schedule = null;
	}
	public String processMobilePDF(){
		Position position = schedule.getPosition();
        if(position.isViewReports()){
        	Event event = getEventService().getEventDetail(schedule.getEvent().getId(), tenant);
        	//List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
        	//schedule.setEvent(event);
        	//printEmployeeScheduleList = eventDetailPDF.showEventDetailReport(allEventPositions);
        
			Font smallBold = new Font(Font.TIMES_ROMAN, 12,Font.BOLD);
			Document pdf = new Document();  
			pdf.setPageSize(PageSize.A4.rotate());  
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			
		    try {
		    	
		    	PdfWriter writer = PdfWriter.getInstance(pdf, baos);
		    	this.addImage(pdf);
		    	PageXofY pageXofY = new PageXofY();
		        writer.setPageEvent(pageXofY);
	            pdf.open(); 
	                     
		            pdf.newPage();
		            this.addImage(pdf);
		
		            Paragraph eventDetail = new Paragraph();
				    // We add one empty line
				    //addEmptyLine(eventDetail, 1);
				    // Lets write a big header
				    eventDetail.add(new Paragraph("Title : "+event.getName()+"["+event.getId()+"]",smallBold));
				    
				    String event_time = "";
				    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
					SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
					event_time = event_time+ dateformat.format(event.getStartDate());
					event_time = event_time+ ", "+timeformat.format(event.getStartTime());
					event_time = event_time+ " to "+timeformat.format(event.getEndTime());
				    eventDetail.add(new Paragraph("Event Date/Time : "+event_time));
				    
				    Location location = event.getLocation();
				    String name = location.getName()+"["+location.getCode()+"], ";
				    String address = location.getAddress1();
				    String address2 = (location.getAddress2()==null)?"":(", "+location.getAddress2()); 
				    address = name+address+address2+", "+location.getCity()+", "+location.getState().getStateName()+", "+location.getZipcode();
				   	eventDetail.add(new Paragraph("Location : "+address));
				    
				   	//eventDetail.add(new Paragraph("Admin : "+event.getOwner().getFirstname()+event.getOwner().getLastname()));
				    //eventDetail.add(new Paragraph("Manager : "+event.getCoordinator().getFirstname()+event.getCoordinator().getLastname()));
				    //eventDetail.add(new Paragraph("Client/Host : "+event.getHostname()));
				    String s = "Admin : "+event.getOwner().getFirstname()+" "+event.getOwner().getLastname()+", "+" Manager : "+event.getCoordinator().getFirstname()+" "+event.getCoordinator().getLastname()+", "+" Client/Host : "+event.getHostname();
				    eventDetail.add(new Paragraph(s));
				    eventDetail.add(new Paragraph("Notes : "+ event.getNotes()));
				    eventDetail.add(new Paragraph("Description : "+event.getDescription()));
				    eventDetail.add(new Paragraph("DressCode : "+event.getDressCode()));
				    eventDetail.add(new Paragraph("Guest Count : "+event.getGuestCount()));
				    eventDetail.add(new Paragraph(""));
				    
				    pdf.add(eventDetail);
				    List<EventPosition> exportEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
				    pdf.add(exportPDFTable(exportEventPositions));
				    pdf.add( Chunk.NEWLINE );
				    EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
				    eventPositionPDFExporter.exportEventPositionPDFTable(pdf,exportEventPositions);
				    //pdf.add(employeeScheduleTable);
				    pdf.add(new Paragraph(""));
			    
	           
	            pdf.add( Chunk.NEWLINE );
	            
				pdf.close();
	            writePDFToResponse(FacesContext.getCurrentInstance().getExternalContext(), baos, "Roster_Schedule");
	
			} catch (BadElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        return null;
	}
	private void addImage(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "small_logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    logo_iamge.setAlignment(Image.MIDDLE);
	    logo_iamge.scaleAbsoluteHeight(30);
	    logo_iamge.scaleAbsoluteWidth(20);
	    logo_iamge.scalePercent(80);

	    /*float height =  logo_iamge.getScaledHeight()+40f;
	    float width = logo_iamge.getScaledWidth()+40f;
	    logo_iamge.setAbsolutePosition(PageSize.A4.getWidth() - width, PageSize.A4.getHeight() - height);
	    logo_iamge.scaleAbsolute(100, 30);*/
	    Chunk chunk = new Chunk(logo_iamge, 0, 0);
	    HeaderFooter header = new HeaderFooter(new Phrase(chunk), false);
	    header.setBorder(Rectangle.NO_BORDER); 
	    header.setAlignment(Element.ALIGN_CENTER);
	    pdf.setHeader(header);
	   // pdf.add(logo_iamge);  
	}
	protected PdfPTable exportPDFTable(List<EventPosition> exportEventPositions) {
    	
    	String[] headers = new String[] {"Position Name", "Start Time", "End Time", "Required", "Staffed", "Short"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	
        
    	
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            PdfPCell cell = new PdfPCell();
            cell.setGrayFill(0.6f);
            cell.setPhrase(new Phrase(header.toUpperCase(), new Font(Font.HELVETICA, 10, Font.BOLD)));
            pdfTable.addCell(cell);
        }
        pdfTable.completeRow();
     
        for(EventPosition eventPosition : exportEventPositions) { 
        	
        	String positionName = eventPosition.getPosition().getName();
            PdfPCell positionCell = new PdfPCell();
            positionCell.setPhrase(new Phrase(positionName.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(positionCell);
            
            SimpleDateFormat timeformat = new SimpleDateFormat("hh:mm a");
			
            String startTime = timeformat.format(eventPosition.getStartTime());
            PdfPCell stcell = new PdfPCell();
            stcell.setPhrase(new Phrase(startTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(stcell);
            
            String endTime = timeformat.format(eventPosition.getEndTime());
            PdfPCell etcell = new PdfPCell();
            etcell.setPhrase(new Phrase(endTime.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(etcell);
            
            String reqdCount = eventPosition.getReqdNumber().toString();
            PdfPCell reqdCntcell = new PdfPCell();
            reqdCntcell.setPhrase(new Phrase(reqdCount.toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(reqdCntcell);
            
            Integer staffed = eventPosition.getPosition().getAcceptedEmps().size();
            PdfPCell staffedCntcell = new PdfPCell();
            staffedCntcell.setPhrase(new Phrase(staffed.toString().toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(staffedCntcell);
            
            Integer toFill = eventPosition.getReqdNumber()- staffed;
            PdfPCell toFillcell = new PdfPCell();
            toFillcell.setPhrase(new Phrase(toFill.toString().toUpperCase(), new Font(Font.HELVETICA, 10, Font.NORMAL)));
            pdfTable.addCell(toFillcell);
            
            pdfTable.completeRow();
            
        }	
        pdfTable.completeRow();
    	return pdfTable;
	}
	protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName) throws IOException, DocumentException {
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
	/*public void empoyeeSchedulePreProcessorPDF(Object document){
		document =  eventDetailPDF.empoyeeSchedulePreProcessorPDF(schedule.getEvent(),tenant, document);
	}*/
	public String employeeSchedulePreProcessorPDF(String layout){
		Font smallBold = new Font(Font.TIMES_ROMAN, 12,Font.BOLD);
		Document pdf = new Document();  
		if(layout.equalsIgnoreCase("landscape"))
			pdf.setPageSize(PageSize.A4.rotate());
		else
			pdf.setPageSize(PageSize.A4);  
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
	    try {
	    	PdfWriter writer = PdfWriter.getInstance(pdf, baos);
	    	this.addImage(pdf);
	    
	        PageXofY pageXofY = new PageXofY();
	        writer.setPageEvent(pageXofY);
            pdf.open();
            pdf = eventDetailPDF.empoyeeSchedulePreProcessorPDF(schedule.getEvent(),tenant, pdf);
            pdf.add( Chunk.NEWLINE );
		    EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
		    List<EventPosition> allEventPositions = getScheduleService().getScheduleByEventPosition(schedule.getEvent(), tenant);
		    eventPositionPDFExporter.exportEventPositionPDFTable(pdf,allEventPositions);
		    pdf.newPage();
            eventPositionPDFExporter.getExtraStaffRpt(pdf);;
		    pdf.close();
		    writePDFToResponse(FacesContext.getCurrentInstance().getExternalContext(), baos, "Roster_Schedule");
		    

		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public ScheduleModel getEmployeeScheduleModel() {
		return employeeScheduleModel;
	}
	public void setEmployeeScheduleModel(ScheduleModel employeeScheduleModel) {
		this.employeeScheduleModel = employeeScheduleModel;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public AvailabilityHome getAvailHome() {
		return availHome;
	}

	public void setAvailHome(AvailabilityHome availHome) {
		this.availHome = availHome;
	}

	public List<Schedule> getAllSchedules() {
		return allSchedules;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public List<Employee> getPrintEmployeeScheduleList() {
		return printEmployeeScheduleList;
	}

	public void setPrintEmployeeScheduleList(
			List<Employee> printEmployeeScheduleList) {
		this.printEmployeeScheduleList = printEmployeeScheduleList;
	}

	public EventDetailPDFBean getEventDetailPDF() {
		return eventDetailPDF;
	}

	public void setEventDetailPDF(EventDetailPDFBean eventDetailPDF) {
		this.eventDetailPDF = eventDetailPDF;
	}
	
	
	
}
