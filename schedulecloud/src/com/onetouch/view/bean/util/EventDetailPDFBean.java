package com.onetouch.view.bean.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.NoneScoped;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.onetouch.model.domainobject.Employee;
import com.onetouch.model.domainobject.Event;
import com.onetouch.model.domainobject.EventPosition;
import com.onetouch.model.domainobject.Location;
import com.onetouch.model.domainobject.Position;
import com.onetouch.model.domainobject.Schedule;
import com.onetouch.model.domainobject.Tenant;
import com.onetouch.view.bean.BaseBean;
import com.onetouch.view.util.DateUtil;

@ManagedBean(name="eventDetailPDF")
//@ApplicationScoped
public class EventDetailPDFBean extends BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public List<Employee> showEventDetailReport(List<EventPosition> allEventPositions){
		List<Employee> printEmployeeScheduleList = new ArrayList<Employee>();
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
		
		
		return printEmployeeScheduleList;	
		
	}
	/**
	 * add event detail to pdf document 
	 * @param document
	 */
	public Document empoyeeSchedulePreProcessorPDF(Event event, Tenant tenant,Document pdf){
		Font smallBold = new Font(Font.TIMES_ROMAN, 12,Font.BOLD);
		/*Document pdf = (Document) document;  
		pdf.setPageSize(PageSize.A4.rotate()); 
	    pdf.open();
	    pdf.setPageSize(PageSize.A4);  
	    pdf.setMargins(1,1,1,1);*/
	    try {
			//this.addImage(pdf);
			Paragraph eventDetail = new Paragraph();
		    // We add one empty line
		    addEmptyLine(eventDetail, 1);
		    // Lets write a big header
		    eventDetail.add(new Paragraph("Title : "+event.getName(),smallBold));
		    
		    String event_time = "";
		    SimpleDateFormat dateformat = new SimpleDateFormat("MMM d, yyyy");
			SimpleDateFormat timeformat = new SimpleDateFormat("h:mm a");
			event_time = event_time+ dateformat.format(event.getStartDate());
			event_time = event_time+ ", "+timeformat.format(event.getStartTime());
			event_time = event_time+ ", "+timeformat.format(event.getEndTime());
		    eventDetail.add(new Paragraph("Event Time : "+event_time));
		    
		    Location location = event.getLocation();
		    String address = location.getAddress1();
		    String address2 = (location.getAddress2()==null)?"":(", "+location.getAddress2()); 
		    address = address+address2+", "+location.getCity()+", "+location.getState().getStateName()+", "+location.getZipcode();
		   	eventDetail.add(new Paragraph("Location : "+address));
		    
		   	//eventDetail.add(new Paragraph("Admin : "+event.getOwner().getFirstname()+event.getOwner().getLastname()));
		    //eventDetail.add(new Paragraph("Manager : "+event.getCoordinator().getFirstname()+event.getCoordinator().getLastname()));
		    //eventDetail.add(new Paragraph("Client/Host : "+event.getHostname()));
		    String s = "Admin : "+event.getOwner().getFirstname()+" "+event.getOwner().getLastname()+", "+" Manager : "+event.getCoordinator().getFirstname()+" "+event.getCoordinator().getLastname()+", "+" Client/Host : "+event.getHostname();
		    eventDetail.add(new Paragraph(s));
		    eventDetail.add(new Paragraph("Notes : "+ event.getNotes()));
		    eventDetail.add(new Paragraph("Description : "+event.getDescription()));
		    eventDetail.add(new Paragraph("DressCode : "+event.getDressCode()));
		    
		    addEmptyLine(eventDetail, 2);
		    pdf.add(eventDetail);
		    pdf.add(exportPDFTable(event,tenant));
		    pdf.add( Chunk.NEWLINE );
		} catch (BadElementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return pdf;
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
		pdf.setPageSize(PageSize.A4);  
		
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();  
	    String logo = servletContext.getRealPath("") + File.separator + "images" + File.separator + "logo.jpg";  
	    Image logo_iamge = Image.getInstance(logo);
	    //logo_iamge.scaleAbsolute(100, 30);
	    pdf.add(logo_iamge);  
	}
	protected PdfPTable exportPDFTable(Event event, Tenant tenant) {
    	
    	String[] headers = new String[] {"Position Name", "Start Time", "End Time", "Required", "Staffed", "Short"};
    	PdfPTable pdfTable = new PdfPTable(headers.length);
    	
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
	private static void addEmptyLine(Paragraph paragraph, int number) {
	    for (int i = 0; i < number; i++) {
	      paragraph.add(new Paragraph(" "));
	    }
	  }
	
}
