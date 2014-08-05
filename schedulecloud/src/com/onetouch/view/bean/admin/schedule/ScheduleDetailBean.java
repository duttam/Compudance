package com.onetouch.view.bean.admin.schedule;



import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;

import org.primefaces.component.api.DynamicColumn;
import org.primefaces.component.api.UIColumn;
import org.primefaces.component.datatable.DataTable;

import org.primefaces.context.RequestContext;
import org.primefaces.event.CloseEvent;
import org.primefaces.expression.impl.PreviousExpressionResolver;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.util.Constants;

import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTable;
import com.lowagie.text.pdf.PdfWriter;

import com.onetouch.model.domainobject.Availability;
import com.onetouch.model.domainobject.CustomUser;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.EmployeeRate;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.FormatType;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;

import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.bean.admin.event.EventPositionPDFExporter;
import com.onetouch.view.bean.admin.event.EventSupport;
import com.onetouch.view.bean.admin.location.RegionBean;
import com.onetouch.view.bean.admin.report.EventPositionToExcel;

import com.onetouch.view.bean.util.DateBean;
import com.onetouch.view.bean.util.PopulateEmailAttributes;
import com.onetouch.view.bean.util.SupportBean;
import com.onetouch.view.comparator.EmployeeNameComparator;
import com.onetouch.view.context.TenantContextUtil;
import com.onetouch.view.util.DateUtil;
import com.onetouch.view.util.FacesUtils;
import com.onetouch.view.util.PageXofY;

@ManagedBean(name="scheduleDetailBean")
@ViewScoped
public class ScheduleDetailBean extends BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//@ManagedProperty(value="#{adminScheduleHome}")
	//private ScheduleHome scheduleHome;
	@ManagedProperty(value="#{supportBean}")
	private SupportBean supportBean;
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}
	@ManagedProperty(value="#{eventSupport}")
	private EventSupport eventSupport;
	private Event  event; // current event in view
	private Tenant tenant; // current tenant
	//in case of old events it holds scheduled employees and their sign in out detail
	//in case od new events it holds available employees for a position given a particular shift
	private List<Employee> allEmployees; 
	private String scheduleDetailSubject; // subject of the email
	private String scheduleDetailNotes;// content of note
	private List<Position> positionList;
	private List<Employee> scheduledEmployees; // collection that holds all selected employees for a position, edit position, ALL NOTIFIED EMPLOYEES 
	private List<EventPosition> allEventPositions; // all event positions that are scheduled
	private EventPosition scheduledEventPosition; // holder to keep track of event position when admin clicks edits position 
	private List<Employee> printEmployeeScheduleList; //collection used for printing schedule details 
	private List<Availability> viewEmployeeAvailability;
	private List<Schedule> notifiedEventPositions;
	private String editConfirmMessage;
	private String operation;
	@ManagedProperty(value="#{regionBean}")
	private RegionBean regionBean;
	
	public ScheduleDetailBean(){
		System.out.println("Schedule Detail Created");
	}
	@PostConstruct
	public void initScheduleDetailBean(){
		long startTime = System.currentTimeMillis();
		String eventId = FacesUtils.getRequestParameter("eventId");
		tenant = tenantContext.getTenant();
		event = getEventService().getEvent(Integer.parseInt(eventId), tenant,regionBean.getSelectedRegion());//getEventDetail(Integer.parseInt(eventId), tenant);
		eventSupport.setEvent(event);
		event.setTenant(tenant);
		if(DateUtil.isPreviousEvent(event.getStartDate(),new Date())){// for old events
			List<Position> schPositionList = getScheduleService().getScheduleByEvent(event, tenant);
			allEmployees = new ArrayList<Employee>();
			for(Position position : schPositionList){
				List<Employee> emps = position.getScheduledEmployees();
				for(Employee emp : emps ){
					allEmployees.add(emp);
				}
			}
			positionList = getPositionService().getAllPosition(tenant);
			allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
			
		}
		else{
			positionList = getPositionService().getAllPosition(tenant);
			allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
			if(event!=null){
				//allAvailableEmployee = getEmployeeService().getAllEmployeeAvailability(tenant,event.getStartDate()); //replace with this later
				// load images to disk for all employees
				//loadImagesToDisk(allAvailableEmployee);
				
				
			}
			allEmployees = new ArrayList<Employee>();
		}
		
		//showEventDetailReport();
		long endTime = System.currentTimeMillis();
		System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }
	@PreDestroy
	public void destroy()
	{
		System.out.println("Schedule Detail Destroy");
	}
	public String gotoRoster() {  
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    	String startDate = dateformat.format(event.getStartDate());
		String endDate = dateformat.format(event.getEndDate());
		return "/ui/admin/event/roster.jsf?faces-redirect=true&startDate="+startDate+"&endDate="+endDate+"&eventId="+event.getId();
        

    }
	public void loadImagesToDisk(List<Employee> allEmployee){
		//String path = FacesUtils.getServletContext().getRealPath("/images");
		String path = FacesContext.getCurrentInstance().getExternalContext().getRealPath("/images");
		File imageDir = new File(path+"/"+tenant.getCode());
		if (!imageDir.exists()) {
			if (imageDir.mkdir()) {
				System.out.println("Directory is created!");
			} else {
				System.out.println("Failed to create directory!");
			}
		}
		for(Employee employee : allEmployee){
			
				File file = new File(imageDir+"/"+employee.getImageName());
				OutputStream output = null;
				try {
					output = new FileOutputStream(file);
					IOUtils.copy(employee.getInputStream(), output);
				} catch (Exception e) {
					// TODO: handle exception
				}finally {
					IOUtils.closeQuietly(output);
				}
			
		}
	}
	public void sendEmailToEmployee(ActionEvent actionEvent){
		//List<Employee> employees = new ArrayList<Employee>(allEmployees.values());
		List<String> receipients = new ArrayList<String>();
		Map<Object,Object> attributes = new HashMap<Object, Object>();
		for(EventPosition eventPosition : allEventPositions){
			List<Employee> employees = eventPosition.getPosition().getScheduledEmployees();
			for(Employee employee : employees){
				Schedule schedule = employee.getSchedule();
				if(schedule.getSchedulestatus()<= 2){
					/*if(!receipients.contains(employee.getEmail()))
						receipients.add(employee.getEmail());*/
					Employee loginEmployee = supportBean.getCurrentLoggedEmployee();
					
					attributes.put("smtphost",tenant.getEmailSender());
					attributes.put("emailsubject", scheduleDetailSubject);
					attributes.put("scheduleNotes",scheduleDetailNotes);
					attributes.put("senderEmail", loginEmployee.getEmail());
					attributes.put("senderfirstname",loginEmployee.getFirstname() );
					attributes.put("senderlastname", loginEmployee.getLastname());
					
					try{
						getSheduleEmployeeSender().sendScheduleNotes(tenant, regionBean.getSelectedRegion(), attributes, employee.getEmail(),tenant.getEmailSender(), "schedule notes");
						
						//getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender());
						
					}catch (DataAccessException dae) {
						dae.printStackTrace();
					}catch (MailException me){
						me.printStackTrace();
						//getSender().sendScheduleNotes(scheduleNoteMessage, attributes, receipients,tenant.getEmailSender());
					}catch(Exception exception){
						FacesUtils.addErrorMessage("Error!");
						exception.getStackTrace();
					}
				}
				else
					continue;
			}
		}
		scheduleDetailSubject = "";
		scheduleDetailNotes="";
		
		
		FacesUtils.addInfoMessage("Message Saved Successfully, Email will be sent shortly ");
		
	}
	/**
	 * admin invokes new position event 
	 * @param actionEvent
	 */
	public String showNewEventPositionDialog(){
		return "/ui/admin/schedule/addEventPosition.jsf?faces-redirect=true&operation=add&includeViewParams=true";
		
	}
	
	
	/**
	 * compare two shifts to check if there is a time overlap
	 * @param selectedEventPosition
	 * @param eventPosition
	 * @return boolean
	 */
	public boolean isTimeOverlap(EventPosition selectedEventPosition,EventPosition eventPosition){
		int a = DateUtil.compareDateAndTime(selectedEventPosition.getStartDate(),selectedEventPosition.getStartTime(),eventPosition.getStartDate(), eventPosition.getStartTime());
		
		int b = DateUtil.compareDateAndTime(selectedEventPosition.getStartDate(),selectedEventPosition.getStartTime(),eventPosition.getEndDate(), eventPosition.getEndTime());
		if(a>=0 && b<=0) // selected start time falls between prev start and end time
			return true;
		int c = DateUtil.compareDateAndTime(selectedEventPosition.getEndDate(),selectedEventPosition.getEndTime(),eventPosition.getStartDate(), eventPosition.getStartTime());
		int d = DateUtil.compareDateAndTime(selectedEventPosition.getEndDate(),selectedEventPosition.getEndTime(),eventPosition.getEndDate(), eventPosition.getEndTime());
		if(c>=0 && d<=0) // selected end time falls between prev start and end time
			return true;
		if(a<0 && d>0)	// start time is smaller than prev start time and end time is greater than prev end time  
			return true;
		return false;
	}
	
	
	
	/**
	 * invoked when edit position is clicked
	 * @param eventPositionId
	 * @return
	 */
	public String editEventPosition(){
		
		eventSupport.setScheduleEventPosition(scheduledEventPosition);
		if(operation.equalsIgnoreCase("edit"))
			return "/ui/admin/schedule/scheduleEventPosition.jsf?faces-redirect=true&operation="+this.operation+"&includeViewParams=true";
		else if(operation.equalsIgnoreCase("override"))
			return "/ui/admin/schedule/overrideEventPosition.jsf?faces-redirect=true&operation="+this.operation+"&includeViewParams=true";
		else if(operation.equalsIgnoreCase("offer"))
			return "/ui/admin/schedule/offerEventPosition.jsf?faces-redirect=true&operation="+this.operation+"&includeViewParams=true";
		else
			return "";
		
	}
	
	public String editEventPositionNotes(Integer eventPositionId){
		for(EventPosition eventPosition : allEventPositions){
			if(eventPosition.getId().intValue()==eventPositionId.intValue()){
				scheduledEventPosition = eventPosition;// selected event position for editing
			}
		}
		
		RequestContext.getCurrentInstance().update("editNoteDialog");
		return null;
	}

	public String saveEventPositionNotes(){
		scheduledEventPosition.setRegion(regionBean.getSelectedRegion());
		scheduledEventPosition.setCompanyId(tenant.getId());
		scheduledEventPosition.setActive(true);
		getScheduleService().updateEventPositionNotes(scheduledEventPosition);
		
		return "scheduleDetail?faces-redirect=true&eventId="+event.getId();
	}

	
	
	/**
	 * filter already assigned employees if found for other position
	 * @param event_posId
	 */
	private void filterEmployeeIfScheduled(Integer event_posId) {
		for(EventPosition eventPosition : allEventPositions){
			if(eventPosition.getId().intValue()!=event_posId){
				Position position = eventPosition.getPosition();
				List<Employee> scheduledEmps = position.getScheduledEmployees();// get scheduled employees for other positions
				/*for(Employee employee : scheduledEmps){
					if(allEmployees.contains(employee))
						allEmployees.remove(employee);
				}*/
				
				Iterator<Employee> emp_iterator = allEmployees.iterator();
				while(emp_iterator.hasNext()){
					Employee availEmp = emp_iterator.next();
					if(scheduledEmps.contains(availEmp)){
						if(isTimeOverlap(scheduledEventPosition,eventPosition))// if time overlap remove from available employee list
							emp_iterator.remove();
					}
				}
				/*for(Employee availEmp : allEmployees){
					if(scheduledEmps.contains(availEmp)){
						if(isTimeOverlap(scheduledEventPosition,eventPosition))// if time overlap remove from available employee list
							availEmp.setAvailableLabel("NAvail.png");
							
					}
				}*/
				
			}
		}
	}
	
	public void displayNotifiedEventPosition(){
		FacesContext context = FacesContext.getCurrentInstance();
	    Map map = context.getExternalContext().getRequestParameterMap();
	    String empId = (String) map.get("empid");
		Integer employeeId = Integer.parseInt(empId);
		notifiedEventPositions = getScheduleService().getAllNotifiedEventPositionsByDate(new Employee(employeeId),tenant,event.getStartDate());
		RequestContext.getCurrentInstance().update("notifieddialog");
	}
	
	public void displayEmployeeAvailability(){
		FacesContext context = FacesContext.getCurrentInstance();
	    Map map = context.getExternalContext().getRequestParameterMap();
	    String empId = (String) map.get("empid");
		Integer employeeId = Integer.parseInt(empId);
		
		for(Employee employee : allEmployees){
			if(employee.getId().intValue()==employeeId.intValue()){
				viewEmployeeAvailability = getEmployeeAvailability().getAllAvailabilityByDate(tenant, employee, event.getStartDate());//employee.getAllAvailability();
			}
		}
		//Employee employee = employeeMap.get(employeeId);
		//viewEmployeeAvailability = employee.getAllAvailability();
		RequestContext.getCurrentInstance().update("availdialog");
	}
	
	
	public void handleAvailabilityClose(CloseEvent closeEvent) {  
		allEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
    }
	
	 
	
	/**
	 * Display event detail pdf on clicking the pdf icon 
	 */
	public void showEventDetailReport(){
		printEmployeeScheduleList = new ArrayList<Employee>();
		for(EventPosition eventPosition : allEventPositions){
				
				Position position = eventPosition.getPosition();
				 
				List<Employee> pos_scheduledEmps = position.getScheduledEmployees();
				if(pos_scheduledEmps!=null && (pos_scheduledEmps.size()>0)){
					for(Employee employee : pos_scheduledEmps){
						Schedule schedule = employee.getSchedule();
						if(schedule!=null && schedule.getSchedulestatus().intValue()== 2){
							/*Integer empId = employee.getId();
							EmployeeRate employeeRate = getEmployeeService().getEmployeeRateById(event,empId,tenant);
							employee.setEmpRate(employeeRate);*/
							employee.setPositionName(position.getName());
							employee.setShiftStartTime(eventPosition.getStartTime());
							employee.setShiftEndTime(eventPosition.getEndTime());
							printEmployeeScheduleList.add(employee);
						}
					}
				}
				//printEmployeeScheduleList.add(new Employee());
		}
		
		
			
		
	}
	
	/**
	 * add event detail to pdf document 
	 * @param document
	 */
	public void employeeSchedulePreProcessorPDF(String layout){
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
			Paragraph eventDetail = new Paragraph();
		    // We add one empty line
		    //addEmptyLine(eventDetail, 1);
		    // Lets write a big header
		    eventDetail.add(new Paragraph("Title : "+event.getName()+"["+event.getId()+"]",smallBold));
		    
		    String event_time = "";
		    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");;
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
		    
		    pdf.add(exportPDFTable());
		    pdf.add( Chunk.NEWLINE );
		    EventPositionPDFExporter eventPositionPDFExporter = new EventPositionPDFExporter();
		    eventPositionPDFExporter.exportEventPositionPDFTable(pdf,allEventPositions);
		    
		    pdf.newPage();
            eventPositionPDFExporter.getExtraStaffRpt(pdf);
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
	protected PdfPTable exportPDFTable() {
    	
    	String[] headers = new String[] {"Position Name", "Start Time", "End Time", "Required", "Staffed", "Short"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	pdfTable.setWidthPercentage(100);
        List<EventPosition> exportEventPositions = getScheduleService().getScheduleByEventPosition(event, tenant);
    	
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
            
            SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
			
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
    
	/**
	 * add image to pdf document
	 * @param pdf
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws DocumentException
	 * @throws IOException
	 */
	private void addImage(Document pdf) throws BadElementException, MalformedURLException, DocumentException, IOException {
		HeaderFooter footer = new HeaderFooter(new Phrase("Completed sheets must be emailed to "+tenantContext.getTenant().getCompanyEmail()+" or faxed to "+tenantContext.getTenant().getFax(), new Font()), false);
		footer.setBorder(Rectangle.NO_BORDER); 
	    footer.setAlignment(Element.ALIGN_CENTER);
	    pdf.setFooter(footer);
	   
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
	protected void writePDFToResponse(ExternalContext externalContext, ByteArrayOutputStream baos, String fileName)
    throws IOException, DocumentException {
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
	private static void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	  }
	
	public Event getEvent() {
		return event;
	}

	/*public ScheduleHome getScheduleHome() {
		return scheduleHome;
	}
	public void setScheduleHome(ScheduleHome scheduleHome) {
		this.scheduleHome = scheduleHome;
	}
*/
	
	public Tenant getTenant() {
		return tenant;
	}
	public String getScheduleDetailNotes() {
		return scheduleDetailNotes;
	}
	public void setScheduleDetailNotes(String scheduleDetailNotes) {
		this.scheduleDetailNotes = scheduleDetailNotes;
	}
	
	
	
	public List<Employee> getAllEmployees() {
		if(allEmployees!=null && allEmployees.size()>0)
			Collections.sort(allEmployees,new EmployeeNameComparator());
		return allEmployees;
	}
	public void setAllEmployees(List<Employee> allEmployees) {
		this.allEmployees = allEmployees;
	}
	public List<Employee> getScheduledEmployees() {
		return scheduledEmployees;
	}
	public void setScheduledEmployees(List<Employee> scheduledEmployees) {
		this.scheduledEmployees = scheduledEmployees;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	public List<Position> getPositionList() {
		return positionList;
	}
	public String getScheduleDetailSubject() {
		return scheduleDetailSubject;
	}
	public void setScheduleDetailSubject(String scheduleDetailSubject) {
		this.scheduleDetailSubject = scheduleDetailSubject;
	}
	public List<EventPosition> getAllEventPositions() {
		return allEventPositions;
	}
	public void setAllEventPositions(List<EventPosition> allEventPositions) {
		this.allEventPositions = allEventPositions;
	}
	public EventPosition getScheduledEventPosition() {
		return scheduledEventPosition;
	}
	public void setScheduledEventPosition(EventPosition scheduledEventPosition) {
		this.scheduledEventPosition = scheduledEventPosition;
	}
	public void setPositionList(List<Position> positionList) {
		this.positionList = positionList;
	}
	

	public List<Employee> getPrintEmployeeScheduleList() {
		return printEmployeeScheduleList;
	}

	public void setPrintEmployeeScheduleList(
			List<Employee> printEmployeeScheduleList) {
		this.printEmployeeScheduleList = printEmployeeScheduleList;
	}

	public SupportBean getSupportBean() {
		return supportBean;
	}

	public void setSupportBean(SupportBean supportBean) {
		this.supportBean = supportBean;
	}

	public List<Availability> getViewEmployeeAvailability() {
		return viewEmployeeAvailability;
	}

	public void setViewEmployeeAvailability(
			List<Availability> viewEmployeeAvailability) {
		this.viewEmployeeAvailability = viewEmployeeAvailability;
	}


	public RegionBean getRegionBean() {
		return regionBean;
	}

	public void setRegionBean(RegionBean regionBean) {
		this.regionBean = regionBean;
	}

	

	public List<Schedule> getNotifiedEventPositions() {
		return notifiedEventPositions;
	}

	public void setNotifiedEventPositions(List<Schedule> notifiedEventPositions) {
		this.notifiedEventPositions = notifiedEventPositions;
	}
	public String getEditConfirmMessage() {
		return editConfirmMessage;
	}
	public void setEditConfirmMessage(String editConfirmMessage) {
		this.editConfirmMessage = editConfirmMessage;
	}
	public EventSupport getEventSupport() {
		return eventSupport;
	}
	public void setEventSupport(EventSupport eventSupport) {
		this.eventSupport = eventSupport;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	
	
}
